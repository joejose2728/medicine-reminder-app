package edu.sjsu.medscan.core.model;

public class Patient2Medicine {

	private String mode;
	private int quantityPerIntake;
	private String frequencyOfIntake;
	private String startDate;
	private String endDate;
	
	public int getQuantityPerIntake() {
		return quantityPerIntake;
	}
	public void setQuantityPerIntake(int quantityPerIntake) {
		this.quantityPerIntake = quantityPerIntake;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getFrequencyOfIntake() {
		return frequencyOfIntake;
	}
	public void setFrequencyOfIntake(String frequencyOfIntake) {
		this.frequencyOfIntake = frequencyOfIntake;
	}
	
}
