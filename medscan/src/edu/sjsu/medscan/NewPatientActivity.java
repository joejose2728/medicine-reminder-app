package edu.sjsu.medscan;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.sjsu.medscan.core.intf.IParser;
import edu.sjsu.medscan.core.intf.MedscanConstants;
import edu.sjsu.medscan.core.model.Data;
import edu.sjsu.medscan.core.model.Patient2Medicine;
import edu.sjsu.medscan.parser.AbbyyOCRDataParser;


public class NewPatientActivity extends Activity implements MedscanConstants{

/*"QAMER GHAFOORI\n"+
"AMLODIPINE BESYLATE 5MG TABLETS\n"+
"VIFG LUPIN\n"+
"TAKF 1 TABLET BY MOUTH DAILY\n"+
"rx 2280181 -00900	DR *RA\"REZ\n"+
"QTV 30\n"+
"PARTIAL REFILL BEFORE 07/02/14\n"+
"USE BEFORE 07/02/14\n";*/
	
	private Data ocrData;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		Intent intent = getIntent();
		String data = /*this.data;*/intent.getStringExtra(MedScanActivity.OCR_RESULT);
		
		parseAndPopulate(data);
	}

	private void parseAndPopulate(String in) {
		IParser parser = new AbbyyOCRDataParser(in);
		ocrData = parser.parse();
		Store.addData(ocrData);
		
		TextView np = (TextView)findViewById(R.id.new_patient);
		TextView nm = (TextView)findViewById(R.id.new_medicine);
		TextView cd = (TextView)findViewById(R.id.new_consume_details);
		
		Patient2Medicine p2m = ocrData.getPatient2Medicine();
		np.setText(PATIENT + ocrData.getPatient().getName());
		nm.setText(MEDICINE + ocrData.getMedicine().getMedicine());
		cd.setText(CONSUMPTION_DETAILS + p2m.getFrequencyOfIntake()
				+ " " + p2m.getQuantityPerIntake() + " by " + p2m.getMode());
		
	}
	
	public void savePatientDetails(View view){
        createCalendarEntry();
		Toast.makeText(this, "Saved. Reminder created.", Toast.LENGTH_SHORT).show();
	}
	
	private void createCalendarEntry(){
		String[] projection = new String[] { "_id", "name" };
		Uri calendars = Uri.parse("content://com.android.calendar/calendars");
		     
		Cursor managedCursor =
		  managedQuery(calendars, projection, null, null, null);
		String calName = ""; 
		String calId = ""; 
		if (managedCursor.moveToFirst()) {
			 
			 int nameColumn = managedCursor.getColumnIndex("name"); 
			 int idColumn = managedCursor.getColumnIndex("_id");
			 do {
			    calName = managedCursor.getString(nameColumn);
			    calId = managedCursor.getString(idColumn);
			    if (calName.contains("gmail"))
			    	break;
			 } while (managedCursor.moveToNext());
	     }
		
		long start = System.currentTimeMillis() + 120000;
		long duration = (/*ocrData.getMedicine().getQuantity() * 24 **/ 60 * 60 * 1000) + start;
		
		ContentValues values = new ContentValues();
		values.put("dtstart"/*Events.DTSTART*/, start);
		values.put("dtend"/*Events.DTEND*/, duration);
		values.put("title"/*Events.TITLE*/, ocrData.getMedicine().getMedicine());
		values.put("description", "Take " + ocrData.getPatient2Medicine().getFrequencyOfIntake() 
				+ " by " + ocrData.getPatient2Medicine().getMode());
		values.put("calendar_id"/*Events.CALENDAR_ID*/, calId);
		values.put("rrule", "FREQ=" + ocrData.getPatient2Medicine().getFrequencyOfIntake() + ";");
		values.put("hasAlarm", 1);
		
		ContentResolver cr = getContentResolver();
		Uri event = cr.insert(Uri.parse("content://com.android.calendar/events"), values);
		
		values = new ContentValues();
		values.put("event_id", Long.parseLong(event.getLastPathSegment()));
		values.put("method", 1);
		values.put("minutes", 10 );
		cr.insert(Uri.parse("content://com.android.calendar/reminders"), values);
	}
}
