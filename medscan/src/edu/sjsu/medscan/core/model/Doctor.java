package edu.sjsu.medscan.core.model;

public class Doctor {

	private String name;
	private Address address;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getAddress(){
		return address.toString();
	}
}
