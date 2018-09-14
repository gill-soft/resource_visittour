package com.gillsoft.client;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.core.ParameterizedTypeReference;

import com.gillsoft.model.request.TripSearchRequest;

public class TripPackage implements Serializable {

	private static final long serialVersionUID = -4074866420438921827L;

	private static final ParameterizedTypeReference<TripPackage> typeRef = new ParameterizedTypeReference<TripPackage>() { };

	private TripSearchRequest request;

	private boolean inProgress = true;

	private List<ScheduleTrip> schedule;

	private CopyOnWriteArrayList<Trip> trips;

	private ResponseError error;

	private boolean continueSearch = false;

	private List<com.gillsoft.model.Route> routeList;

	private String from;

	private String to;
	
	public TripSearchRequest getRequest() {
		return request;
	}

	public void setRequest(TripSearchRequest request) {
		this.request = request;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public List<ScheduleTrip> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<ScheduleTrip> schedule) {
		this.schedule = schedule;
	}

	public CopyOnWriteArrayList<Trip> getTrips() {
		return trips;
	}

	public void setTrips(CopyOnWriteArrayList<Trip> trips) {
		this.trips = trips;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

	public boolean isContinueSearch() {
		return continueSearch;
	}

	public void setContinueSearch(boolean continueSearch) {
		this.continueSearch = continueSearch;
	}
	
	public List<com.gillsoft.model.Route> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<com.gillsoft.model.Route> routeList) {
		this.routeList = routeList;
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

	public static ParameterizedTypeReference<TripPackage> getTypeReference() {
		return typeRef;
	}

}
