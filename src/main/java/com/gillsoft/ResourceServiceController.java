package com.gillsoft;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.abstract_rest_service.AbstractResourceService;
import com.gillsoft.model.Method;
import com.gillsoft.model.MethodType;
import com.gillsoft.model.Ping;
import com.gillsoft.model.Resource;

@RestController
public class ResourceServiceController extends AbstractResourceService {

	@Override
	public Resource getInfoResponse() {
		Resource resource = new Resource();
		resource.setCode("CODRIVE");
		resource.setName("КоДрайв (Inbus.ua)");
		return resource;
	}

	@Override
	public List<Method> getAvailableMethodsResponse() {
		List<Method> methods = new ArrayList<>();
		
		// resource
		addMethod(methods, "Resource activity check", Method.PING, MethodType.GET);
		addMethod(methods, "Information about resource", Method.INFO, MethodType.GET);
		addMethod(methods, "Available methods", Method.METHOD, MethodType.GET);
		
		// localities
		addMethod(methods, "All available resource localities", Method.LOCALITY_ALL, MethodType.POST);
		addMethod(methods, "All used resource localities", Method.LOCALITY_USED, MethodType.POST);
		addMethod(methods, "Binding from resource localities", Method.LOCALITY_BINDING, MethodType.POST);
		
		// search
		addMethod(methods, "Init search", Method.SEARCH, MethodType.POST);
		addMethod(methods, "Return search result", Method.SEARCH, MethodType.GET);
		addMethod(methods, "Return free seats on trip", Method.SEARCH_TRIP_SEATS, MethodType.GET);

		// order
		addMethod(methods, "Create new order", Method.ORDER, MethodType.POST);
		addMethod(methods, "Confirm order", Method.ORDER_CONFIRM, MethodType.POST);
		addMethod(methods, "Prepare order for return", Method.ORDER_CANCEL, MethodType.POST);
		addMethod(methods, "Confirm order return", Method.ORDER_RETURN_CONFIRM, MethodType.POST);
		return methods;
	}

	@Override
	public Ping pingResponse(String id) {
		return createPing(id);
	}

}
