package com.worldalarm.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ContentValues;

public class Alarm implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	private String id;
	private Calendar calendar;
	private City city;
	
	/**
	 * Alarm with local localization at current time
	 */
	public Alarm() {
		this.id = this.generateUniqueId();
		this.calendar = Calendar.getInstance();
		this.city = new City();
	}
	
	/**
	 * Alarm with local localization at specified time with the hour and minute picked
	 * @param hourPicked
	 * @param minutePicked
	 */
	public Alarm(int hourPicked, int minutePicked) {
		this.id = this.generateUniqueId();
		this.calendar = Calendar.getInstance();
		this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
		this.calendar.set(Calendar.MINUTE, minutePicked);
		
		if(this.calendar.equals(Calendar.getInstance()) || this.calendar.before(Calendar.getInstance())) {
			this.calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		this.city = new City();
	}
	
	/**
	 * Alarm with local localization at specified time in millis
	 * @param timeInMillis
	 */
	public Alarm(long timeInMillis) {
		this.id = this.generateUniqueId();
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeInMillis(timeInMillis);
		
		if(this.calendar.equals(Calendar.getInstance()) || this.calendar.before(Calendar.getInstance())) {
			this.calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		this.city = new City();
	}
	
	public Alarm(int hourPicked, int minutePicked, City cityPicked) {
		this.id = this.generateUniqueId();
		
		if(cityPicked != null) {
		
			this.calendar = new GregorianCalendar(TimeZone.getTimeZone(cityPicked.getTimeZoneID()));
			this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			this.calendar.set(Calendar.MINUTE, minutePicked);
			
			if(this.calendar.equals(new GregorianCalendar(TimeZone.getTimeZone(cityPicked.getTimeZoneID()))) || this.calendar.before(new GregorianCalendar(TimeZone.getTimeZone(cityPicked.getTimeZoneID())))) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}

			this.city = cityPicked;
			
		} else {
			this.calendar = Calendar.getInstance();
			this.calendar.set(Calendar.HOUR_OF_DAY, hourPicked);
			this.calendar.set(Calendar.MINUTE, minutePicked);
			
			if(this.calendar.equals(Calendar.getInstance()) || this.calendar.before(Calendar.getInstance())) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			this.city = new City();
		}
	}
	
	public Alarm(long timeInMillis, City cityPicked) {
		this.id = this.generateUniqueId();
		
		if(cityPicked != null) {
			this.calendar = new GregorianCalendar(TimeZone.getTimeZone(cityPicked.getTimeZoneID()));
			this.calendar.setTimeInMillis(timeInMillis);
		
			if(this.calendar.equals(Calendar.getInstance(TimeZone.getTimeZone(cityPicked.getTimeZoneID()))) || this.calendar.before(Calendar.getInstance(TimeZone.getTimeZone(cityPicked.getTimeZoneID())))) {
				this.calendar.add(Calendar.DAY_OF_MONTH, 1);
			}

			this.city = cityPicked;
			
		} else {
			this.calendar = Calendar.getInstance();
			this.calendar.setTimeInMillis(timeInMillis);
			
			if(this.calendar.equals(Calendar.getInstance()) || this.calendar.before(Calendar.getInstance())) {
				this.calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			this.city = new City();
		}
	}

	private String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public String toString() {	
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, MMM d");
		sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));
		
		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));
		
		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
		sdf.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));
		
		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getLocalDate() {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, MMM d");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.calendar.getTimeInMillis());
		
		if(calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getHourLocal() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.calendar.getTimeInMillis());
		
		if(calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return sdf.format(calendar.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getDateLocal() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.calendar.getTimeInMillis());
		
		if(calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return sdf.format(calendar.getTime());
	}
	
	public ContentValues getUpdateContentValues() {
		ContentValues updateValues = new ContentValues();
		updateValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_IN_MILLIS, getTimeInMillis());
		updateValues.put(AlarmDatabaseHelper.COLUMN_NAME_CITY, city.getCityName());
		updateValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_ZONE_ID, city.getTimeZoneID());
		updateValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_ZONE_NAME, city.getTimeZoneName());

		return updateValues;
	}
	
	public ContentValues getInsertContentValues() {
		ContentValues insertValues = new ContentValues();
		insertValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_IN_MILLIS, getTimeInMillis());
		insertValues.put(AlarmDatabaseHelper.COLUMN_NAME_CITY, city.getCityName());
		insertValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_ZONE_ID, city.getTimeZoneID());
		insertValues.put(AlarmDatabaseHelper.COLUMN_NAME_TIME_ZONE_NAME, city.getTimeZoneName());
		
		return insertValues;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		this.calendar.setTimeZone(TimeZone.getTimeZone(city.getTimeZoneID()));
	}
}
