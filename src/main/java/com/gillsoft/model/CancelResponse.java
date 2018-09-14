package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CancelResponse extends RoutePrice implements Serializable {

	private static final long serialVersionUID = -8225570694845737326L;
	
	private static final ParameterizedTypeReference<CancelResponse> typeRef = new ParameterizedTypeReference<CancelResponse>() { };

    private BigDecimal refundAmount;

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public static ParameterizedTypeReference<CancelResponse> getTypeReference() {
		return typeRef;
	}

}
