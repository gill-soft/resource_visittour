package com.gillsoft.client;

import com.gillsoft.model.AbstractJsonModel;
import com.gillsoft.model.RoutePrice;

public class TripIdModel extends AbstractJsonModel {

	private static final long serialVersionUID = -8580474896771951668L;

	private String routeId;

	private String from;

	private String to;

	private String dateDispatch;

	private String dateArrival;
	
	private RoutePrice price;
	
	public TripIdModel() {

	}

	public TripIdModel(String routeId, String from, String to, String dateDispatch, String dateArrival, RoutePrice price) {
		this.routeId = routeId;
		this.from = from;
		this.to = to;
		this.dateDispatch = dateDispatch;
		this.dateArrival = dateArrival;
		this.price = price;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDateDispatch() {
		return dateDispatch;
	}

	public void setDateDispatch(String dateDispatch) {
		this.dateDispatch = dateDispatch;
	}

	public String getDateArrival() {
		return dateArrival;
	}

	public void setDateArrival(String dateArrival) {
		this.dateArrival = dateArrival;
	}

	public RoutePrice getPrice() {
		return price;
	}

	public void setPrice(RoutePrice price) {
		this.price = price;
	}

}
