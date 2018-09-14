package com.gillsoft.model;

import java.io.Serializable;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response implements Serializable {

	private static final long serialVersionUID = -3624696360520714739L;

	private static final ParameterizedTypeReference<Response[]> typeRef = new ParameterizedTypeReference<Response[]>() { };

	private ResponseStatus status;

	private Long raceId;

	private Long ticketId;

	private Integer placeNumber;

	private Customer customer;

	private RoutePrice price;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public Long getRaceId() {
		return raceId;
	}

	public void setRaceId(Long raceId) {
		this.raceId = raceId;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public Integer getPlaceNumber() {
		return placeNumber;
	}

	public void setPlaceNumber(Integer placeNumber) {
		this.placeNumber = placeNumber;
	}

	public static ParameterizedTypeReference<Response[]> getTypeReference() {
		return typeRef;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public RoutePrice getPrice() {
		return price;
	}

	public void setPrice(RoutePrice price) {
		this.price = price;
	}

}
