package com.gillsoft.client;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.SearchServiceController;
import com.gillsoft.cache.CacheHandler;
import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.logging.SimpleRequestResponseLoggingInterceptor;
import com.gillsoft.model.BookingRequest;
import com.gillsoft.model.CancelResponse;
import com.gillsoft.model.IdentificationDocumentType;
import com.gillsoft.model.Response;
//import com.gillsoft.model.Invoice;
import com.gillsoft.model.Lang;
import com.gillsoft.model.Locality;
import com.gillsoft.model.Location;
import com.gillsoft.model.ReservationRequest;
import com.gillsoft.model.ReservationTicket;
import com.gillsoft.model.Seat;
import com.gillsoft.model.SeatStatus;
import com.gillsoft.model.SeatType;
import com.gillsoft.model.ServiceItem;
import com.gillsoft.model.Ticket;
import com.gillsoft.model.request.Request;
import com.gillsoft.util.RestTemplateUtil;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestClient {

	public static final String STATIONS_CACHE_KEY = "visittour.stations.";
	public static final String TRIPS_CACHE_KEY = "visittour.trips.";

	public static final String CODE_DEPARTURE = "D";
	public static final String CODE_ARRIVE = "A";

	private static final String NAME_REGEX = "[^a-zA-Z0-9\u0430-\u044F\u0410-\u042F\u0456\u0406\u0457\u0407\u0454\u0404-]";

	private static final String COUNTRIES = "countries";
	private static final String RACES = "races";
	private static final String TICKETS = "tickets";

	private static HttpHeaders headers = new HttpHeaders();

    @Autowired
    @Qualifier("RedisMemoryCache")
	private CacheHandler cache;

    static {
		headers.add("Authorization", "Bearer " + Config.getToken());
    	headers.add("Content-Type", "application/json; charset=utf-8");
    }

	private RestTemplate template;

	// для запросов поиска с меньшим таймаутом
	private RestTemplate searchTemplate;

	public RestClient() {
		template = createNewPoolingTemplate(Config.getRequestTimeout());
		searchTemplate = createNewPoolingTemplate(Config.getSearchRequestTimeout());
	}

	public RestTemplate createNewPoolingTemplate(int requestTimeout) {
		HttpComponentsClientHttpRequestFactory factory = (HttpComponentsClientHttpRequestFactory) RestTemplateUtil
				.createPoolingFactory(Config.getUrl(), 300, requestTimeout);
		factory.setReadTimeout(Config.getReadTimeout());
		RestTemplate template = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		template.setInterceptors(Collections.singletonList(new SimpleRequestResponseLoggingInterceptor()));
		return template;
	}

	/****************** STATIONS ********************/
	@SuppressWarnings("unchecked")
	public List<Locality> getCachedStations() throws IOCacheException {
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, RestClient.STATIONS_CACHE_KEY);
		params.put(RedisMemoryCache.IGNORE_AGE, true);
		params.put(RedisMemoryCache.UPDATE_DELAY, Config.getCacheStationsUpdateDelay());
		params.put(RedisMemoryCache.UPDATE_TASK, new UpdateStationsTask());
		return (List<Locality>) cache.read(params);
	}

	public List<Locality> getAllStations() throws ResponseError {
		Map<BigDecimal, Locality> localities = new HashMap<>();
		try {
			Location[] countries = getResult(template, null, COUNTRIES, HttpMethod.GET,
					Location.getTypeReference());
			if (countries != null && countries.length != 0) {
				for (Location country : countries) {
					/*Locality countryLocality = new Locality();
					countryLocality.setId(String.valueOf(country.getId()));
					countryLocality.setName(Lang.EN, country.getName());
					localities.put(new BigDecimal(country.getId()), countryLocality);
					Locality parentLocality = new Locality(countryLocality.getId());*/
					Location[] cities = getResult(template, null, COUNTRIES + '/' + country.getId(), HttpMethod.GET,
							Location.getTypeReference());
					if (cities != null && cities.length != 0) {
						for (Location city : cities) {
							if (localities.containsKey(String.valueOf(city.getId()))) {
								System.out.println(localities.get(String.valueOf(city.getId())).getName(Lang.EN.toString()) + '\t' + city.getName());
							}
							Locality cityLocality = new Locality();
							cityLocality.setId(String.valueOf(city.getId()));
							cityLocality.setName(Lang.EN, city.getName());
							//cityLocality.setParent(parentLocality);
							localities.put(new BigDecimal(city.getId()), cityLocality);
						}
					}
				}
			}
			return new ArrayList<>(localities.values());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/****************** SEATS ********************/
	public List<Seat> getSeats(String routeId) throws ResponseError {
		List<Seat> seats = new ArrayList<>();
		int[] freeSeats = getResult(searchTemplate, null, RACES + '/' + routeId, HttpMethod.GET, new ParameterizedTypeReference<int[]>() { });
		if (freeSeats.length != 0) {
			for (int seatId : freeSeats) {
				seats.add(createSeat(String.valueOf(seatId)));
			}
		}
		return seats;
	}

	private Seat createSeat(String seatId) {
		Seat newSeat = new Seat();
		newSeat.setId(seatId);
		newSeat.setNumber(seatId);
		newSeat.setStatus(SeatStatus.FREE);
		newSeat.setType(SeatType.SEAT);
		return newSeat;
	}

	/****************** TRIPS ********************/
	public TripPackage getCachedTrips(String from, String to, Date dispatch) throws ResponseError {
		URI uri = UriComponentsBuilder.fromUriString(RACES).queryParam("DispatchPointId", from)
				.queryParam("ArrivalPointId", to)
				.queryParam("DispatchDate", com.gillsoft.util.Date.dateFormat.format(dispatch)).build().toUri();
		String key = uri.toString();
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, key);
		params.put(RedisMemoryCache.UPDATE_TASK, new UpdateTripsTask(key));
		try {
			return (TripPackage) cache.read(params);
		} catch (IOCacheException e) {
			e.printStackTrace();
			// ставим пометку, что кэш еще формируется
			TripPackage tripPackage = new TripPackage();
			tripPackage.setContinueSearch(true);
			return tripPackage;
		} catch (Exception e) {
			throw new ResponseError(e.getMessage());
		}
	}

	public TripPackage getTrips(String key) throws ResponseError {
		com.gillsoft.model.Route[] routes = getResult(searchTemplate, null, key, HttpMethod.GET, com.gillsoft.model.Route.getTypeReference());
		TripPackage tripPackage = new TripPackage();
		UriComponents components = UriComponentsBuilder.fromUri(URI.create(key)).build();
		tripPackage.setFrom(components.getQueryParams().getFirst("DispatchPointId"));
		tripPackage.setTo(components.getQueryParams().getFirst("ArrivalPointId"));
		tripPackage.setRouteList(Arrays.asList(routes));
		if (routes.length != 0) {
			for (com.gillsoft.model.Route route : routes) {
				route.setFreeSeatCount(getSeats(route.getId()).size());
			}
		}
		return tripPackage;
	}

	/****************** BILL/PAY/CANCEL ********************/
	private Response[] getResponse(RequestEntity<?> requestEntity) throws ResponseError {
		return template.exchange(requestEntity, Response[].class).getBody();
	}

	public Response reserve(ServiceItem service) throws ResponseError {
		TripIdModel trip = (TripIdModel) new TripIdModel().create(service.getSegment().getId());
		ReservationTicket reservationTicket = new ReservationTicket(trip.getFrom(), trip.getTo(),
				trip.getDateDispatch(), trip.getDateArrival());
		if (service.getSeat() != null && (service.getSeat().getId() != null || service.getSeat().getNumber() != null)) {
			reservationTicket.setPlace(service.getSeat().getId() != null ? service.getSeat().getId() : service.getSeat().getNumber());
		} else {
			List<Seat> freeSeats = getSeats(trip.getRouteId());
			if (freeSeats != null && !freeSeats.isEmpty()) {
				reservationTicket.setPlace(freeSeats.get(0).getNumber());
			} else {
				throw new ResponseError("No free seats left");
			}
		}
		ReservationRequest reservationRequest = new ReservationRequest(trip.getRouteId(), reservationTicket);
		Response[] response = getResponse(getRequestEntity(reservationRequest, HttpMethod.POST, TICKETS));
		if (response != null && response.length != 0) {
			if (response[0].getStatus() != null && response[0].getStatus().getCode() != null && response[0].getStatus().getCode() != 0) {
				throw new ResponseError(response[0].getStatus().getMessage());
			}
			service.setId(String.valueOf(response[0].getTicketId()));
			service.setPrice(SearchServiceController.getPrice(trip.getPrice()));
			response[0].setPrice(trip.getPrice());
			return response[0];
		}
		return null;
	}

	public Response booking(Response reserveResponse) throws ResponseError {
		BookingRequest bookingRequest = new BookingRequest(getTicket(reserveResponse));
		Response[] response = getResponse(getRequestEntity(bookingRequest, HttpMethod.PUT, TICKETS + '/' + reserveResponse.getTicketId()));
		if (response != null && response.length != 0) {
			if (response[0].getStatus() != null && response[0].getStatus().getCode() != null && response[0].getStatus().getCode() != 0) {
				throw new ResponseError(response[0].getStatus().getMessage());
			}
			return response[0];
		}
		throw new ResponseError("Response error");
	}

	private Ticket getTicket(Response reserveResponse) {
		Ticket ticket = new Ticket();
		ticket.setPlace(reserveResponse.getPlaceNumber());
		ticket.setTicketId(String.valueOf(reserveResponse.getTicketId()));
		ticket.setFirstName(reserveResponse.getCustomer().getName());
		ticket.setLastName(reserveResponse.getCustomer().getSurname());
		ticket.setThirdName(reserveResponse.getCustomer().getPatronymic() != null ? reserveResponse.getCustomer().getPatronymic() : "NA");
		ticket.setFirstNameLatin(reserveResponse.getCustomer().getName());
		ticket.setLastNameLatin(reserveResponse.getCustomer().getSurname());
		ticket.setPhone(reserveResponse.getCustomer().getPhone());
		ticket.setEmail(reserveResponse.getCustomer().getEmail());
		ticket.setGender(String.valueOf(reserveResponse.getCustomer().getGender()).toLowerCase());
		ticket.setBirthDate(reserveResponse.getCustomer().getBirthday());
		ticket.setDocNumber(reserveResponse.getCustomer().getDocumentSeries() != null
				&& reserveResponse.getCustomer().getDocumentNumber() != null
						? reserveResponse.getCustomer().getDocumentSeries()
								+ reserveResponse.getCustomer().getDocumentNumber()
						: null);
		ticket.setDocType(getDocTypeCode(reserveResponse.getCustomer().getDocumentType()));
		ticket.setCitizenship(reserveResponse.getCustomer().getCitizenship() != null
				? reserveResponse.getCustomer().getCitizenship().getAlfa3Code() : null);
		ticket.setBasePrice(String.valueOf(reserveResponse.getPrice().getPrice()));
		ticket.setPrice(String.valueOf(reserveResponse.getPrice().getPrice()));
		ticket.setCurrency(reserveResponse.getPrice().getCurrency());
		return ticket;
	}

	private String getDocTypeCode(IdentificationDocumentType documentType) {
		if (documentType != null) {
			switch (documentType) {
			case FOREIGN_PASSPORT:
				return "2";
			case PASSPORT:
				return "0";
			case SEAMAN:
				return "1";
			case BIRTH_CERTIFICATE:
				return "4";
			case MILITARY_ID:
				return "5";
			case WITHOUT_CITIZENSHIP_ID:
				return "6";
			case TEMPORARY_CARD:
				return "7";
			case MILITARY_CARD:
				return "8";
			case RESIDENCE_PERMIT:
				return "9";
			case RELEASE_CERTIFICATE:
				return "10";
			case SSSR_PASSPORT:
				return "11";
			case DIPLOMATIC_PASSPORT:
				return "12";
			case SERVICE_PASSPORT:
				return "13";
			case CIS_RETURN_CERTIFICATE:
				return "14";
			case LOSS_CERTIFICATE:
				return "15";
			default:
				return "99";
			}
		}
		return null;
	}

	public CancelResponse cancel(String ticketId) throws ResponseError {
		return getResult(searchTemplate, null, TICKETS + '/' + ticketId, HttpMethod.DELETE, CancelResponse.getTypeReference());
	}

	/*************************************************/
	private <T> T getResult(RestTemplate template, Request request, String method, HttpMethod httpMethod,
			ParameterizedTypeReference<T> type) throws ResponseError {
		URI uri = UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri();
		RequestEntity<Request> requestEntity = new RequestEntity<Request>(request, headers, httpMethod, uri);
		ResponseEntity<T> response = template.exchange(requestEntity, type);
		return response.getBody();
	}

	private <T> RequestEntity<T> getRequestEntity(T request, HttpMethod httpMethod, String method) {
		return new RequestEntity<T>(request, headers, httpMethod,
				UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri());
	}

	public CacheHandler getCache() {
		return cache;
	}

	public static RestClientException createUnavailableMethod() {
		return new RestClientException("Method is unavailable");
	}

}
