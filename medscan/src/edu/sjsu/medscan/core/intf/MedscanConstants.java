package edu.sjsu.medscan.core.intf;

public interface MedscanConstants {

	//Model constants
	public String PATIENT = "Patient: ";
	public String MEDICINE = "Medicine: ";
	public String CONSUMPTION_DETAILS = "Consumption Details: ";
	public String QUANTITY = "Quantity: ";
	
	//Event creation constants
	public String DATE_START = "dtstart";
	public String DATE_END = "dtend";
	public String EVENT_TITLE = "title";
	public String EVENT_DESCRIPTION = "description";
	public String CALENDAR_ID = "calendar_id";
	public String EVENT_RULE = "rrule";
	public String HAS_ALARM = "hasAlarm";
	
	//URI Constants
	public String CALENDAR_URI = "content://com.android.calendar/calendars";
	public String EVENT_URI = "content://com.android.calendar/events";
	public String REMINDER_URI = "content://com.android.calendar/reminders";
	
	
}
