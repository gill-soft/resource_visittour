package com.gillsoft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingRequest implements Serializable {

	private static final long serialVersionUID = -4363121058574859155L;

	@JsonProperty("Tickets")
	private List<Ticket> tickets;
	
	public BookingRequest() {

	}

	public BookingRequest(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	
	public BookingRequest(Ticket ticket) {
		this(Arrays.asList(new Ticket[] { ticket }));
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
}
