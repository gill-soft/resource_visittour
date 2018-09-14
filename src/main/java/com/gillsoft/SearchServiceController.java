package com.gillsoft;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.gillsoft.abstract_rest_service.SimpleAbstractTripSearchService;
import com.gillsoft.cache.CacheHandler;
import com.gillsoft.client.Config;
import com.gillsoft.client.ResponseError;
import com.gillsoft.client.RestClient;
import com.gillsoft.client.TripIdModel;
import com.gillsoft.client.TripPackage;
import com.gillsoft.model.Currency;
import com.gillsoft.model.Document;
import com.gillsoft.model.Locality;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Price;
import com.gillsoft.model.RequiredField;
import com.gillsoft.model.RestError;
import com.gillsoft.model.ReturnCondition;
import com.gillsoft.model.Route;
import com.gillsoft.model.RoutePrice;
import com.gillsoft.model.Seat;
import com.gillsoft.model.SeatsScheme;
import com.gillsoft.model.Segment;
import com.gillsoft.model.Tariff;
import com.gillsoft.model.Trip;
import com.gillsoft.model.TripContainer;
import com.gillsoft.model.Vehicle;
import com.gillsoft.model.request.TripSearchRequest;
import com.gillsoft.model.response.TripSearchResponse;
import com.gillsoft.util.StringUtil;

@RestController
public class SearchServiceController extends SimpleAbstractTripSearchService<TripPackage> {
	
	@Autowired
	private RestClient client;
	
	@Autowired
	@Qualifier("MemoryCacheHandler")
	private CacheHandler cache;

	@Override
	public List<ReturnCondition> getConditionsResponse(String arg0, String arg1) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<Document> getDocumentsResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<Tariff> getTariffsResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<RequiredField> getRequiredFieldsResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public Route getRouteResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public SeatsScheme getSeatsSchemeResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}
	
	@Override
	public List<Seat> updateSeatsResponse(String arg0, List<Seat> arg1) {
		throw RestClient.createUnavailableMethod();
	}
	
	@Override
	public List<Seat> getSeatsResponse(String tripId) {
		try {
			TripIdModel idModel = (TripIdModel) new TripIdModel().create(tripId);
			return client.getSeats(idModel.getRouteId());
		} catch (Exception e) {
			throw new RestClientException(e.getMessage());
		}
	}

	@Override
	public TripSearchResponse initSearchResponse(TripSearchRequest request) {
		return simpleInitSearchResponse(cache, request);
	}

	@Override
	public void addInitSearchCallables(List<Callable<TripPackage>> callables, String[] pair, Date date) {
		callables.add(() -> {
			try {
				validateSearchParams(pair, date);
				TripPackage tripPackage = client.getCachedTrips(pair[0], pair[1], date);
				if (tripPackage == null) {
					throw new ResponseError("Empty result");
				}
				tripPackage.setRequest(TripSearchRequest.createRequest(pair, date));
				return tripPackage;
			} catch (ResponseError e) {
				TripPackage tripPackage = new TripPackage();
				tripPackage.setError(e);
				tripPackage.setRequest(TripSearchRequest.createRequest(pair, date));
				return tripPackage;
			} catch (Exception e) {
				return null;
			}
		});
	}

	private static void validateSearchParams(String[] pair, Date date) throws ResponseError {
		if (date == null
				|| date.getTime() < DateUtils.truncate(new Date(), Calendar.DATE).getTime()) {
			throw new ResponseError("Invalid parameter \"date\"");
		}
		if (pair == null || pair.length < 2) {
			throw new ResponseError("Invalid parameter \"pair\"");
		}
	}

	@Override
	public TripSearchResponse getSearchResultResponse(String searchId) {
		return simpleGetSearchResponse(cache, searchId);
	}

	@Override
	public void addNextGetSearchCallablesAndResult(List<Callable<TripPackage>> callables, Map<String, Vehicle> vehicles,
			Map<String, Locality> localities, Map<String, Organisation> organisations, Map<String, Segment> segments,
			List<TripContainer> containers, TripPackage tripPackage) {
		if (!tripPackage.isContinueSearch()) {
			addResult(vehicles, localities, segments, containers, tripPackage);
		} else if (tripPackage.getRequest() != null) {
			addInitSearchCallables(callables, tripPackage.getRequest().getLocalityPairs().get(0),
					tripPackage.getRequest().getDates().get(0));
		}
	}

	private void addResult(Map<String, Vehicle> vehicles, Map<String, Locality> localities,
			Map<String, Segment> segments, List<TripContainer> containers, TripPackage tripPackage) {
		TripContainer container = new TripContainer();
		container.setRequest(tripPackage.getRequest());
		if (tripPackage != null
				&& tripPackage.getRouteList() != null) {
			List<Trip> trips = new ArrayList<>();
			for (com.gillsoft.model.Route route : tripPackage.getRouteList()) {
				RoutePrice routePrice = getCarrierPrice(route.getPrice());
				Trip tmpTrip = new Trip();
				tmpTrip.setId(new TripIdModel(route.getId(), tripPackage.getFrom(), tripPackage.getTo(),
						route.getDispatchDateAsString(), route.getArrivalDateAsString(), routePrice).asString());
				trips.add(tmpTrip);

				String segmentId = tmpTrip.getId();
				Segment segment = segments.get(segmentId);
				if (segment == null) {
					segment = new Segment();
					segment.setId(segmentId);
					segment.setNumber(route.getName());
					try {
						segment.setDepartureDate(route.getDispatchDate());
						segment.setArrivalDate(route.getArrivalDate());
					} catch (Exception e) {}
					segment.setFreeSeatsCount(route.getFreeSeatCount());
					segments.put(segmentId, segment);
				}
				segment.setDeparture(addStation(localities, tripPackage.getFrom()));
				segment.setArrival(addStation(localities, tripPackage.getTo()));
				segment.setPrice(getPrice(routePrice));
			}
			container.setTrips(trips);
		}
		if (tripPackage.getError() != null) {
			container.setError(new RestError(tripPackage.getError().getMessage()));
		}
		containers.add(container);
	}

	private RoutePrice getCarrierPrice(List<RoutePrice> priceList) {
		Optional<RoutePrice> defaultPrice = priceList.stream().filter(f -> f.getCurrency().equals(Config.getCurrency())).findFirst();
		if (defaultPrice.isPresent()) {
			return defaultPrice.get();
		} else {
			return priceList.get(0);
		}
	}

	public static Price getPrice(RoutePrice price) {
		Price tripPrice = new Price();
		Tariff tariff = new Tariff();
		tariff.setValue(price.getPrice());
		tripPrice.setCurrency(Currency.valueOf(price.getCurrency()));
		tripPrice.setAmount(tariff.getValue());
		tripPrice.setTariff(tariff);
		return tripPrice;
	}

	public static void addVehicle(Map<String, Vehicle> vehicles, Segment segment, String model) {
		String vehicleKey = StringUtil.md5(model);
		Vehicle vehicle = vehicles.get(vehicleKey);
		if (vehicle == null) {
			vehicle = new Vehicle();
			vehicle.setModel(model);
			vehicles.put(vehicleKey, vehicle);
		}
		segment.setVehicle(new Vehicle(vehicleKey));
	}

	public static Locality addStation(Map<String, Locality> localities, String id) {
		Locality locality = LocalityServiceController.getLocality(id);
		if (locality == null) {
			return null;
		}
		String localityId = locality.getId();
		try {
			locality = locality.clone();
			locality.setId(null);
		} catch (CloneNotSupportedException e) {
		}
		if (!localities.containsKey(localityId)) {
			localities.put(localityId, locality);
		}
		return new Locality(localityId);
	}

}
