package com.gillsoft.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class Ticket implements Serializable {

	private static final long serialVersionUID = 24449107648129095L;

	@JsonProperty("Place")
	private int place;

	private String ticketId;

	private String firstName;

	private String lastName;

	private String thirdName;

	private String firstNameLatin;

	private String lastNameLatin;

	private String phone;

	private String email;

	private String gender;

	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date birthDate;

	private String docNumber;

	private String docType;

	private String citizenship;

	private String info;

	@JsonProperty("BasePrice")
	private String basePrice;

	@JsonProperty("Price")
	private String price;

	@JsonProperty("Currency")
	private String currency;

	/*"Place": 7,
	"ticketId": "1759410",
	"firstName": "",
	"lastName": "",
	"thirdName": "",
	"firstNameLatin": "Test1",
	"lastNameLatin": "Test1",
	"phone": "",
	"email": "sergey.trushin@gillsoft.tech",
	"gender": "m",
	"birthDate": "1984-01-09",
	"docNumber": "",
	"docType": "",
	"citizenship": "",
	"info": "",
	"BasePrice": "20",
	"Price": "25",
	"Currency": "EUR"*/

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getThirdName() {
		return thirdName;
	}

	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}

	public String getFirstNameLatin() {
		return firstNameLatin;
	}

	public void setFirstNameLatin(String firstNameLatin) {
		this.firstNameLatin = firstNameLatin;
	}

	public String getLastNameLatin() {
		return lastNameLatin;
	}

	public void setLastNameLatin(String lastNameLatin) {
		this.lastNameLatin = lastNameLatin;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
		this.basePrice = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
