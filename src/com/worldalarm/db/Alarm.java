package com.worldalarm.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.annotation.SuppressLint;

public class Alarm implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	private String id;
	private int hour;
	private int minute;
	private City city;
	private boolean active;
	private List<Integer> repeat_days = new ArrayList<Integer>();
	
	public static final Integer REPEAT_DAY_SUN		= 0;
	public static final Integer REPEAT_DAY_MON		= 1;
	public static final Integer REPEAT_DAY_TUE		= 2;
	public static final Integer REPEAT_DAY_WED		= 3;
	public static final Integer REPEAT_DAY_THU		= 4;
	public static final Integer REPEAT_DAY_FRI		= 5;
	public static final Integer REPEAT_DAY_SAT		= 6;
	
	/**
	 * Alarm with local localization at current time
	 */
	public Alarm() {
		this.id = this.generateUniqueId();
		
		Calendar calendar = Calendar.getInstance();
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		
		this.city = new City();
		this.active = Boolean.TRUE;
	}
	
	/**
	 * Alarm with local localization at specified time with the hour and minute picked
	 * @param hourPicked
	 * @param minutePicked
	 */
	public Alarm(int hourPicked, int minutePicked) {
		this.id = this.generateUniqueId();
		this.hour = hourPicked;
		this.minute = minutePicked;
		this.city = new City();
		this.active = Boolean.TRUE;
	}
	
	/**
	 * Alarm with local localization at specified time in millis
	 * @param timeInMillis
	 */
	public Alarm(long timeInMillis) {
		this.id = this.generateUniqueId();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		
		this.city = new City();
		this.active = Boolean.TRUE;
	}
	
	public Alarm(int hourPicked, int minutePicked, City cityPicked) {
		this.id = this.generateUniqueId();
		
		if(cityPicked == null) {
			this.city = new City();
		} else {
			this.city = cityPicked;
		}
			
		this.hour = hourPicked;
		this.minute = minutePicked;
		this.active = Boolean.TRUE;
	}
	
	public Alarm(long timeInMillis, City cityPicked) {
		this.id = this.generateUniqueId();
		
		if(cityPicked == null) {
			this.city = new City();
		} else {
			this.city = cityPicked;
		}
		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(cityPicked.getTimeZoneID()));
		calendar.setTimeInMillis(timeInMillis);
		
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		this.active = Boolean.TRUE;
	}

	private String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public String toString() {	
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, MMM d");
		sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));
		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(this.city.getTimeZoneID()));
		calendar.set(Calendar.HOUR_OF_DAY, this.hour);
		calendar.set(Calendar.MINUTE, this.minute);
		
		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getFormattedHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(this.city.getTimeZoneID()));
		calendar.set(Calendar.HOUR_OF_DAY, this.hour);
		calendar.set(Calendar.MINUTE, this.minute);

		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getFormattedLocalHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(this.city.getTimeZoneID()));
		calendar.set(Calendar.HOUR_OF_DAY, this.hour);
		calendar.set(Calendar.MINUTE, this.minute);
		
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTimeInMillis(calendar.getTimeInMillis());

		return sdf.format(calendar.getTime());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Integer> getRepeatDays() {
		return repeat_days;
	}

	public void setRepeatDays(List<Integer> repeat_days) {
		this.repeat_days = repeat_days;
	}
}
