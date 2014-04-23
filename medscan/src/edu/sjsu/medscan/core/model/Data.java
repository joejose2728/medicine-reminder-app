package edu.sjsu.medscan.core.model;

public class Data {
	private Medicine medicine;
	private Patient patient;
	private Doctor doctor;
	private Patient2Medicine patient2Medicine;
	
	public Data() {
	}

	public Medicine getMedicine() {
		return medicine;
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Patient2Medicine getPatient2Medicine() {
		return patient2Medicine;
	}

	public void setPatient2Medicine(Patient2Medicine patient2Medicine) {
		this.patient2Medicine = patient2Medicine;
	}
	
}
