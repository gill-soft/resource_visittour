package com.gillsoft.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class Route implements Serializable {

	private static final long serialVersionUID = 3690971933961672020L;
	
	private static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
	public final static FastDateFormat timeFormat = FastDateFormat.getInstance(datePattern);
	
	private static final ParameterizedTypeReference<Route[]> typeRef = new ParameterizedTypeReference<Route[]>() { };

	@JsonProperty("id")
	private String id;

	@JsonProperty("dispatchStationName")
	private String dispatchStationName;

	@JsonProperty("arrivalStationName")
	private String arrivalStationName;

	@JsonProperty("dispatchAddress")
	private String dispatchAddress;

	@JsonProperty("artivalAddress")
	private String artivalAddress;

	@JsonProperty("name")
	private String name;

	@JsonProperty("dispatchDate")
	@JsonFormat(shape = Shape.STRING, pattern = datePattern)
	private Date dispatchDate;

	@JsonProperty("arrivalDate")
	@JsonFormat(shape = Shape.STRING, pattern = datePattern)
	private Date arrivalDate;

	@JsonProperty("carrierName")
	private String carrierName;

	@JsonProperty("busInfo")
	private String busInfo;

	@JsonProperty("routeReisId")
	private Integer routeReisId;

	@JsonProperty("price")
	private List<RoutePrice> price = null;

	@JsonProperty("discounts")
	private List<RouteDiscount> discounts = null;
	
	private int freeSeatCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDispatchStationName() {
		return dispatchStationName;
	}

	public void setDispatchStationName(String dispatchStationName) {
		this.dispatchStationName = dispatchStationName;
	}

	public String getArrivalStationName() {
		return arrivalStationName;
	}

	public void setArrivalStationName(String arrivalStationName) {
		this.arrivalStationName = arrivalStationName;
	}

	public String getDispatchAddress() {
		return dispatchAddress;
	}

	public void setDispatchAddress(String dispatchAddress) {
		this.dispatchAddress = dispatchAddress;
	}

	public String getArtivalAddress() {
		return artivalAddress;
	}

	public void setArtivalAddress(String artivalAddress) {
		this.artivalAddress = artivalAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}
	
	public String getDispatchDateAsString() {
		return timeFormat.format(dispatchDate);
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}
	
	public String getArrivalDateAsString() {
		return timeFormat.format(arrivalDate);
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getBusInfo() {
		return busInfo;
	}

	public void setBusInfo(String busInfo) {
		this.busInfo = busInfo;
	}

	public Integer getRouteReisId() {
		return routeReisId;
	}

	public void setRouteReisId(Integer routeReisId) {
		this.routeReisId = routeReisId;
	}

	public List<RoutePrice> getPrice() {
		return price;
	}

	public void setPrice(List<RoutePrice> price) {
		this.price = price;
	}

	public List<RouteDiscount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<RouteDiscount> discounts) {
		this.discounts = discounts;
	}

	public int getFreeSeatCount() {
		return freeSeatCount;
	}

	public void setFreeSeatCount(int freeSeatCount) {
		this.freeSeatCount = freeSeatCount;
	}

	public static ParameterizedTypeReference<Route[]> getTypeReference() {
		return typeRef;
	}

}