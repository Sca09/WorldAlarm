package com.worldalarm.db;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Alarm implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	private Calendar calendar;
	private String city;
	private String timeZone;

	public Alarm() {
		this.calendar = Calendar.getInstance();
		this.timeZone = TimeZone.getDefault().getID();
	}
	
	public Alarm(int hourPicked, int minutePicked) {
		this.calendar = Calendar.getInstance();
		this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
		this.calendar.set(Calendar.MINUTE, minutePicked);
		
		if(this.calendar.before(Calendar.getInstance())) {
			this.calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		this.timeZone = TimeZone.getDefault().getID();
	}
	
	public Alarm(long timeInMillis) {
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeInMillis(timeInMillis);
		
		if(this.calendar.before(Calendar.getInstance())) {
			this.calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		this.timeZone = TimeZone.getDefault().getID();
	}
	
	public Alarm(int hourPicked, int minutePicked, String cityPicked, String timeZonePicked) {
		
		if(cityPicked != null && cityPicked.length() > 0 && timeZonePicked != null && timeZonePicked.length() > 0) {
		
			this.calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZonePicked));
			this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			this.calendar.set(Calendar.MINUTE, minutePicked);
			
			if(this.calendar.before(new GregorianCalendar(TimeZone.getTimeZone(timeZonePicked)))) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			this.setCity(cityPicked);
			this.setTimeZone(timeZonePicked);
			
		} else {
			this.calendar = Calendar.getInstance();
			this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			this.calendar.set(Calendar.MINUTE, minutePicked);
			
			if(this.calendar.before(Calendar.getInstance())) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			this.timeZone = TimeZone.getDefault().getID();
		}
	}
	
	public Alarm(long timeInMillis, String cityPicked, String timeZonePicked) {
		if(cityPicked != null && cityPicked.length() > 0 && timeZonePicked != null && timeZonePicked.length() > 0) {
			this.calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZonePicked));
			this.calendar.setTimeInMillis(timeInMillis);
		
			if(this.calendar.before(Calendar.getInstance())) {
				this.calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		
			this.setCity(cityPicked);
			this.setTimeZone(timeZonePicked);
			
		} else {
			this.calendar = Calendar.getInstance();
			this.calendar.setTimeInMillis(timeInMillis);
			
			if(this.calendar.before(Calendar.getInstance())) {
				this.calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			this.timeZone = TimeZone.getDefault().getID();
		}
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
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public String toString() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		
		return sdf.format(calendar.getTime());
	}
	
	public String[] getDatabaseParams() {
		String[] params = {String.valueOf(calendar.getTimeInMillis()), city, timeZone}; 
		
		return params;
	}
}
