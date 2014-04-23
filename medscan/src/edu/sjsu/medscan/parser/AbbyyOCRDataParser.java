package edu.sjsu.medscan.parser;

import android.util.Log;
import edu.sjsu.medscan.core.intf.IParser;
import edu.sjsu.medscan.core.model.Data;
import edu.sjsu.medscan.core.model.Doctor;
import edu.sjsu.medscan.core.model.Medicine;
import edu.sjsu.medscan.core.model.Patient;
import edu.sjsu.medscan.core.model.Patient2Medicine;

public class AbbyyOCRDataParser implements IParser{

	private String ocrData;
	private Data data;

	public AbbyyOCRDataParser(String ocrData) {
		this.ocrData = ocrData;
		Log.d("OCR Data", ocrData);
	}

	public Data parse() {
		data = new Data();
		String [] lines = ocrData.split("\n");
		for (int i = 0; i < lines.length; i++) {
			switch (i){
			case 0: processPatientDetails(lines[i]);
			break;
			case 1: processMedicineDetails(lines[i]);
			break;
			case 3: processConsumptionDetails(lines[i]);
			break;
			case 4: processDoctorDetails(lines[i]);
			break;
			case 5: processQuantity(lines[i]);
			break;
			case 7: processExpiry(lines[i]);
			break;
			}
		}
		return data;
	}

	private void processPatientDetails(String string) {
		Patient p = new Patient();
		p.setName(string.trim());
		data.setPatient(p);

	}

	private void processMedicineDetails(String string) {
		Medicine m = new Medicine();
		m.setMedicine(string.trim());
		data.setMedicine(m);	
	}

	private void processExpiry(String string) {
		data.getPatient2Medicine().setEndDate(string.split(" ")[2].trim());
	}

	private void processQuantity(String string) {
		int quantity = Integer.parseInt(string.split(" ")[1].trim());
		data.getMedicine().setQuantity(quantity);
	}

	private void processDoctorDetails(String string) {
		int index = string.indexOf("DR");
		Doctor d = new Doctor();
		d.setName(string.substring(index, string.length() - 1));
		data.setDoctor(d);
	}

	private void processConsumptionDetails(String string) {
		String[] consumptionDetails = string.split(" ");
		Patient2Medicine p2m = new Patient2Medicine();
		p2m.setQuantityPerIntake(Integer.parseInt(consumptionDetails[1]));
		p2m.setMode(consumptionDetails[4].trim());
		p2m.setFrequencyOfIntake(consumptionDetails[5].trim());
		data.setPatient2Medicine(p2m);
	}

}
