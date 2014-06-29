package com.worldalarm.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class City implements Serializable{


	private static final long serialVersionUID = 1L;

	private String cityName;
	private String timeZoneID;
	private String timeZoneName;
	private String picUrl;
	private List<String> listPicUrls;

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

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public List<String> getListPicUrls() {
		return listPicUrls;
	}

	public void setListPicUrls(List<String> listPicUrls) {
		this.listPicUrls = listPicUrls;
	}
	
	public void addPicUrl(String picUrl) {
		if(this.listPicUrls == null) {
			this.listPicUrls = new ArrayList<String>();
		}

		this.listPicUrls.add(picUrl);
	}
	
	public String getRandomPicUrl() {
		int randomPicUrlIndex = new Random().nextInt(this.listPicUrls.size());
		return this.listPicUrls.get(randomPicUrlIndex);
	}
}
