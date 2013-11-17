package com.worldalarm.db;

import java.io.Serializable;
import java.util.TimeZone;

import android.content.ContentValues;

public class City implements Serializable{


	private static final long serialVersionUID = 1L;

	private String cityName;
	private String timeZoneID;
	private String timeZoneName;

	public City() {
		this.timeZoneID = TimeZone.getDefault().getID();
		
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
		this.timeZoneName = timeZone.getDisplayName();
	}
	
	public City(String cityName, String timeZoneID, String timeZoneName) {
		this.cityName = cityName;
		this.timeZoneID = timeZoneID;
		this.timeZoneName = timeZoneName;
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getTimeZoneID() {
		return timeZoneID;
	}
	public void setTimeZoneID(String timeZoneID) {
		this.timeZoneID = timeZoneID;
	}
	
	public String getTimeZoneName() {
		return timeZoneName;
	}
	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}
	
	public ContentValues getInsertContentValues() {
		ContentValues insertValues = new ContentValues();
		insertValues.put(CityDatabaseHelper.COLUMN_NAME_CITY, cityName);
		insertValues.put(CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_ID, timeZoneID);
		insertValues.put(CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_NAME, timeZoneName);
		
		return insertValues;
	}
}