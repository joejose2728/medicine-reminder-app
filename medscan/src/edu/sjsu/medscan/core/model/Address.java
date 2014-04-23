package edu.sjsu.medscan.core.model;

public class Address {

	private String street;
	private String apt;
	private String city;
	private String state;
	private String zip;
	
	public static final String[] states =  {"AL", "CA", "BA"};

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getApt() {
		return apt;
	}

	public void setApt(String apt) {
		this.apt = apt;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String toString(){
		return apt + ", " + street + ", "
				+ city + ", " + state + " " + zip;
	}
}
