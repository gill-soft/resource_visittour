package com.gillsoft.model;

import java.io.Serializable;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Location implements Serializable {

	private static final long serialVersionUID = -1779007026776669326L;
	
	private static final ParameterizedTypeReference<Location[]> typeRef = new ParameterizedTypeReference<Location[]>() { };

	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static ParameterizedTypeReference<Location[]> getTypeReference() {
		return typeRef;
	}

}