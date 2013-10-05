package com.worldalarm.db;

import java.io.Serializable;

public class AlarmSet implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	public Alarm localAlarm;
	public Alarm remoteAlarm;
	
	public AlarmSet(int hourPicked, int minutePicked, String cityPicked, String timeZonePicked) {
		
		if(cityPicked != null && cityPicked.length() > 0 && timeZonePicked != null && timeZonePicked.length() > 0) {
			
			remoteAlarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
			
			localAlarm = new Alarm();
			
			localAlarm.setTimeInMillis(remoteAlarm.getTimeInMillis());
			
		} else {
			
			localAlarm = new Alarm(hourPicked, minutePicked);
		}
	}
	
	public AlarmSet(long timeInMillis, String cityPicked, String timeZonePicked) {
		
		if(cityPicked != null && cityPicked.length() > 0 && timeZonePicked != null && timeZonePicked.length() > 0) {
			
			remoteAlarm = new Alarm(timeInMillis, cityPicked, timeZonePicked);
			
			localAlarm = new Alarm();
			
			localAlarm.setTimeInMillis(remoteAlarm.getTimeInMillis());
			
		} else {
			
			localAlarm = new Alarm(timeInMillis);
		}
	}
	
	public Alarm getLocalAlarm() {
		return localAlarm;
	}
	public void setLocalAlarm(Alarm localAlarm) {
		this.localAlarm = localAlarm;
	}
	public Alarm getRemoteAlarm() {
		return remoteAlarm;
	}
	public void setRemoteAlarm(Alarm remoteAlarm) {
		this.remoteAlarm = remoteAlarm;
	}

	@Override
	public String toString() {
		String text = "";
    	if(remoteAlarm != null) {
    		text = "Remote alarm ["+ remoteAlarm +"] in ["+ remoteAlarm.getTimeZone() +"]"
    				+ "\nLocal  alarm ["+ localAlarm +"] in ["+ localAlarm.getTimeZone() +"]";
    	} else {
    		text = "Local  alarm ["+ localAlarm +"] in ["+ localAlarm.getTimeZone() +"]";
    	}
    	
    	return text;
	}
	
	public String[] getDatabaseParams() {
				
		if(remoteAlarm != null) {
			String[] params = {String.valueOf(localAlarm.getTimeInMillis()), localAlarm.getTimeZone(), remoteAlarm.getCity(), remoteAlarm.getTimeZone()};
			return params;
		} else {
			String[] params = {String.valueOf(localAlarm.getTimeInMillis()), localAlarm.getTimeZone(), "", ""};
			return params;
		}
	}
}