package com.worldalarm.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class City implements Serializable{


	private static final long serialVersionUID = 1L;

	private String cityName;
	private String timeZoneID;
	private String timeZoneName;
	private List<String> listPicUrls;
	private Long nextPicCheckTime;

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

	public long getNextPicCheckTime() {
		return nextPicCheckTime;
	}

	public void setNextPicCheckTime(Long nextPicCheck) {
		this.nextPicCheckTime = nextPicCheck;
	}
	
	public void setNextPicCheckTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 5);
		Long currentTimeInMillis = calendar.getTimeInMillis();
		setNextPicCheckTime(currentTimeInMillis);
	}
	
	public boolean isNextPicCheckTime() {
		if(nextPicCheckTime != null) {
			Calendar calendar = Calendar.getInstance();
			Long currentTimeInMillis = calendar.getTimeInMillis();
		
			return (currentTimeInMillis > nextPicCheckTime);
		} else {
			return true;
		}
	}
}
