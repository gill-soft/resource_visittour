package com.gillsoft.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.model.AbstractJsonModel;
import com.gillsoft.model.Response;

@JsonInclude(Include.NON_NULL)
public class ServiceIdModel extends AbstractJsonModel {
	
	private static final long serialVersionUID = 7614884907411639503L;

	private Response response;
	
	public ServiceIdModel() {
		
	}

	public ServiceIdModel(Response response) {
		this.setResponse(response);
	}

	@Override
	public ServiceIdModel create(String json) {
		return (ServiceIdModel) super.create(json);
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
		this.response.setStatus(null);
	}
	
}
