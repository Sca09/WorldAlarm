package com.worldalarm.db;


public class AlarmSet {

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
}
