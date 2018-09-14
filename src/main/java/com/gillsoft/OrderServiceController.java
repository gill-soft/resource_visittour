package com.gillsoft;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.abstract_rest_service.AbstractOrderService;
import com.gillsoft.client.OrderIdModel;
import com.gillsoft.client.RestClient;
import com.gillsoft.client.ServiceIdModel;
import com.gillsoft.model.CancelResponse;
import com.gillsoft.model.Customer;
import com.gillsoft.model.RestError;
import com.gillsoft.model.ServiceItem;
import com.gillsoft.model.request.OrderRequest;
import com.gillsoft.model.response.OrderResponse;
import com.gillsoft.model.Response;

@RestController
public class OrderServiceController extends AbstractOrderService {
	
	@Autowired
	private RestClient client;

	@Override
	public OrderResponse addServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse bookingResponse(String orderId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse cancelResponse(String orderId) {
		List<ServiceItem> resultItems = new ArrayList<>();
		OrderResponse response = new OrderResponse();
		// преобразовываем ид заказа в объект
		OrderIdModel orderIdModel = new OrderIdModel().create(orderId);
		response.setServices(new ArrayList<>(orderIdModel.getServices().size()));
		for (ServiceIdModel service : orderIdModel.getServices()) {
			try {
				CancelResponse cancelResponse = client.cancel(String.valueOf(service.getResponse().getTicketId()));
				cancelResponse.setPrice(cancelResponse.getRefundAmount());
				addServiceItem(resultItems, service.getId(), true, null);
			} catch (Exception e) {
				addServiceItem(resultItems, service.getId(), false, new RestError(e.getMessage()));
			}
		}
		response.setOrderId(orderId);
		response.setServices(resultItems);
		return response;
	}

	@Override
	public OrderResponse createResponse(OrderRequest request) {
		// формируем ответ
		OrderResponse response = new OrderResponse();
		response.setCustomers(request.getCustomers());
		
		// копия для определения пассажиров
		List<ServiceItem> items = new ArrayList<>();
		items.addAll(request.getServices());
		
		List<ServiceItem> resultItems = new ArrayList<>();
		
		// список билетов
		OrderIdModel orderId = new OrderIdModel();
		
		for (ServiceItem service : request.getServices()) {
			try {
				Customer customer = request.getCustomers().get(service.getCustomer().getId());
				if (customer != null) {
					Response reserveResponse = client.reserve(service);
					if (reserveResponse != null) {
						reserveResponse.setCustomer(customer);
						orderId.getServices().add(new ServiceIdModel(reserveResponse));
						service.setCustomer(customer);
						service.setSegment(service.getSegment());
						service.setSeat(service.getSeat());
					} else {
						throw new Exception("Response error");
					}
				} else {
					throw new Exception("Customer not found - " + service.getCustomer().getId());
				}
			} catch (Exception e) {
				service.setError(new RestError(e.getMessage()));
				service.setConfirmed(false);
			}
			resultItems.add(service);
		}
		response.setOrderId(orderId.asString());
		response.setServices(resultItems);
		return response;
	}

	@Override
	public OrderResponse getPdfDocumentsResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse getResponse(String orderId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse getServiceResponse(String serviceId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse confirmResponse(String orderId) {
		// формируем ответ
		OrderResponse response = new OrderResponse();
		List<ServiceItem> resultItems = new ArrayList<>();
		// преобразовываем ид заказа в объект
		OrderIdModel orderIdModel = new OrderIdModel().create(orderId);
		// выкупаем заказы и формируем ответ
		for (ServiceIdModel service : orderIdModel.getServices()) {
			try {
				client.booking(service.getResponse());
				addServiceItem(resultItems, String.valueOf(service.getResponse().getTicketId()), true, null);
			} catch (Exception e) {
				addServiceItem(resultItems, String.valueOf(service.getResponse().getTicketId()), false, new RestError(e.getMessage()));
			}
		}
		response.setOrderId(orderId);
		response.setServices(resultItems);
		return response;
	}
	
	private void addServiceItem(List<ServiceItem> resultItems, String ticketId, boolean confirmed, RestError error) {
		ServiceItem serviceItem = new ServiceItem();
		serviceItem.setId(ticketId);
		serviceItem.setConfirmed(confirmed);
		serviceItem.setError(error);
		resultItems.add(serviceItem);
	}

	@Override
	public OrderResponse removeServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse returnServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse updateCustomersResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse prepareReturnServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

}
