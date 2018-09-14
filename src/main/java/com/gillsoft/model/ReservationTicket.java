package com.gillsoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationTicket implements Serializable {

	private static final long serialVersionUID = 2198577971557441814L;

	@JsonProperty("Place")
	private String place;

	@JsonProperty("Dispatch")
	private String from;

	@JsonProperty("Arrival")
	private String to;

	@JsonProperty("DispatchDateTime")
	private String dispatchDateTime;

	@JsonProperty("ArrivalDateTime")
	private String arrivalDateTime;
	
	public ReservationTicket() {

	}
	
	public ReservationTicket(String from, String to, String dispatchDateTime, String arrivalDateTime) {
		this.from = from;
		this.to= to;
		this.dispatchDateTime = dispatchDateTime;
		this.arrivalDateTime = arrivalDateTime;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
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

	public String getDispatchDateTime() {
		return dispatchDateTime;
	}

	public void setDispatchDateTime(String dispatchDateTime) {
		this.dispatchDateTime = dispatchDateTime;
	}

	public String getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(String arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}
	
}
