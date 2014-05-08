package edu.sjsu.medscan;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.sjsu.medscan.core.intf.IParser;
import edu.sjsu.medscan.core.intf.MedscanConstants;
import edu.sjsu.medscan.core.model.Data;
import edu.sjsu.medscan.core.model.Patient2Medicine;
import edu.sjsu.medscan.dao.MedicinesDataSource;
import edu.sjsu.medscan.parser.AbbyyOCRDataParser;

/**
 * This activity manages the creation of new medicine 
 * after the cloud service downloads the parsed data. 
 *
 */
public class NewPatientActivity extends Activity implements MedscanConstants{
	
	private static final int DURATION =  60 * 60 * 1000;
	private Data ocrData;
	private String[] projection = new String[] { "_id", "name" };
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		Intent intent = getIntent();
		String data = intent.getStringExtra(MedScanActivity.OCR_RESULT); /*this.data;*/
		
		parseAndPopulate(data);
	}

	/**
	 * Parses the OCR data and populates the view elements
	 * @param in
	 */
	
	private void parseAndPopulate(String in) {
		IParser parser = new AbbyyOCRDataParser(in);
		ocrData = parser.parse();
		Store.addData(ocrData);
		
		EditText np = (EditText)findViewById(R.id.patient_et);
		EditText nm = (EditText)findViewById(R.id.medicine_et);
		EditText cd = (EditText)findViewById(R.id.consumption_et);
		
		Patient2Medicine p2m = ocrData.getPatient2Medicine();
		np.setText(ocrData.getPatient().getName());
		nm.setText(ocrData.getMedicine().getMedicine());
		cd.setText(p2m.getFrequencyOfIntake()
				+ " " + p2m.getQuantityPerIntake() + " by " + p2m.getMode());
		
	}
	
	/**
	 * Saves patient details and creates calendar event for the medicine.
	 */
	public void savePatientDetails(View view){
		saveToSQLite();
        createCalendarEntry();
		Toast.makeText(this, "Saved. Reminder created.", Toast.LENGTH_SHORT).show();
	}
	
	
	private void saveToSQLite() {
		MedicinesDataSource datasource = new MedicinesDataSource(this);
		datasource.open();
		
		String medicine = ocrData.getMedicine().getMedicine();
		String consumption = "Take " + ocrData.getPatient2Medicine().getFrequencyOfIntake() 
				+ " by " + ocrData.getPatient2Medicine().getMode();
		datasource.createMedicine(medicine, consumption);
		datasource.close();
	}

	/**
	 * Creates a calendar event for new medicine.
	 */
	@SuppressWarnings("deprecation")
	private void createCalendarEntry(){
		
		Uri calendars = Uri.parse(CALENDAR_URI);
		     
		Cursor managedCursor = managedQuery(calendars, projection, null, null, null);
		String calName = ""; 
		String calId = ""; 
		if (managedCursor.moveToFirst()) {
			 
			 int idColumn = managedCursor.getColumnIndex(projection[0]);
			 int nameColumn = managedCursor.getColumnIndex(projection[1]); 
			 
			 do {
			    calName = managedCursor.getString(nameColumn);
			    calId = managedCursor.getString(idColumn);
			    if (calName.contains("gmail"))
			    	break;
			 } while (managedCursor.moveToNext());
	     }
		
		long start = System.currentTimeMillis() + 120000;
		long duration = DURATION + start;
		
		ContentValues values = new ContentValues();
		values.put(DATE_START, start);
		values.put(DATE_END, duration);
		values.put(EVENT_TITLE, ocrData.getMedicine().getMedicine());
		values.put(EVENT_DESCRIPTION, "Take " + ocrData.getPatient2Medicine().getFrequencyOfIntake() 
				+ " by " + ocrData.getPatient2Medicine().getMode());
		values.put(CALENDAR_ID, calId);
		values.put(EVENT_RULE, "FREQ=" + ocrData.getPatient2Medicine().getFrequencyOfIntake() + ";");
		values.put(HAS_ALARM, 1);
		
		ContentResolver cr = getContentResolver();
		Uri event = cr.insert(Uri.parse(EVENT_URI), values);
		
		values = new ContentValues();
		values.put("event_id", Long.parseLong(event.getLastPathSegment()));
		values.put("method", 1);
		values.put("minutes", 10 );
		cr.insert(Uri.parse(REMINDER_URI), values);
	}
}




/*"QAMER GHAFOORI\n"+
"AMLODIPINE BESYLATE 5MG TABLETS\n"+
"VIFG LUPIN\n"+
"TAKF 1 TABLET BY MOUTH DAILY\n"+
"rx 2280181 -00900	DR *RA\"REZ\n"+
"QTV 30\n"+
"PARTIAL REFILL BEFORE 07/02/14\n"+
"USE BEFORE 07/02/14\n";*/