package com.worldalarm.db;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Alarm {

	private Calendar calendar;
	
	private boolean isLocalTime;
		
	private String city;
	private String timeZone;

	public Alarm() {
		createLocalAlarm(true);
	}
	
	public Alarm(int hourPicked, int minutePicked) {
		createLocalAlarm(true);
		calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
		calendar.set(Calendar.MINUTE, minutePicked);
		
		if(this.calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	public Alarm(int hourPicked, int minutePicked, String cityPicked, String timeZonePicked) {
		
		this.isLocalTime = false;
		
		if(cityPicked != null && cityPicked.length() > 0 && timeZonePicked != null && timeZonePicked.length() > 0) {
		
			calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZonePicked));
			calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			calendar.set(Calendar.MINUTE, minutePicked);
			
			this.setCity(cityPicked);
			this.setTimeZone(timeZonePicked);
			
			if(this.calendar.before(new GregorianCalendar(TimeZone.getTimeZone(timeZonePicked)))) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else {
			createLocalAlarm(false);
			calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			calendar.set(Calendar.MINUTE, minutePicked);
			
			if(this.calendar.before(Calendar.getInstance())) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
	}
	
	private void createLocalAlarm(boolean isLocalTime) {
		
		this.isLocalTime = isLocalTime;
		this.calendar = Calendar.getInstance();
		this.timeZone = TimeZone.getDefault().getID();
	}
	
	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}
	
	public void setTimeInMillis(long milliseconds) {
		calendar.setTimeInMillis(milliseconds);
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	public boolean isLocalTime() {
		return isLocalTime;
	}

	public void setLocalTime(boolean isLocalTime) {
		this.isLocalTime = isLocalTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		this.timeZone = calendar.getTimeZone().getID();
	}
	
	@Override
	public String toString() {
		
		String result = String.valueOf(calendar.get(Calendar.YEAR));
		
		if((calendar.get(Calendar.MONTH) + 1) < 10) {
			result += "-0"+ (calendar.get(Calendar.MONTH) + 1);
		} else {
			result += "-"+ (calendar.get(Calendar.MONTH) + 1);
		}
		
		if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			result += "-0"+ calendar.get(Calendar.DAY_OF_MONTH);
		} else {
			result += "-"+ calendar.get(Calendar.DAY_OF_MONTH);
		}
				
		if(calendar.get(Calendar.HOUR) < 10) {
			result += " 0"+ calendar.get(Calendar.HOUR);
		} else {
			result += " "+ calendar.get(Calendar.HOUR);
		}
		
		if(calendar.get(Calendar.MINUTE) < 10) {
			result += ":0"+ calendar.get(Calendar.MINUTE);
		} else {
			result += ":"+ calendar.get(Calendar.MINUTE);
		}
		
		if(calendar.get(Calendar.AM_PM) == Calendar.AM) {
			result += " AM";
		} else {
			result += " PM";
		}
		
		return result;
	}
}
