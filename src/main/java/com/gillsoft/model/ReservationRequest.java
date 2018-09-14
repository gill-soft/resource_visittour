package com.gillsoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationRequest implements Serializable {

	private static final long serialVersionUID = 3560663619322271528L;

	@JsonProperty("RaceId")
	private String raceId;

	@JsonProperty("Tickets")
	private List<ReservationTicket> tickets;

	public ReservationRequest() {
		
	}

	public ReservationRequest(String raceId) {
		this.raceId = raceId;
	}

	public ReservationRequest(String raceId, ReservationTicket ticket) {
		this.raceId = raceId;
		this.tickets = Arrays.asList(new ReservationTicket[] { ticket });
	}

	public ReservationRequest(String raceId, List<ReservationTicket> tickets) {
		this.raceId = raceId;
		this.tickets = tickets;
	}

	public String getRaceId() {
		return raceId;
	}

	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}

	public List<ReservationTicket> getTickets() {
		if (tickets == null) {
			tickets = new ArrayList<>();
		}
		return tickets;
	}
}
