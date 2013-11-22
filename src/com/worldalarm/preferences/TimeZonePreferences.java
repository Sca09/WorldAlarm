package com.worldalarm.preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TimeZonePreferences {

	private static String preferences_file_alarm = "time_zone_set_file";
	private static String preferences_name = "time_zone_set";
	
	private static List<String> timeZonesSingleton = null;
	
	public static List<String> getAllTimeZones(Context context) {
    	
    	if(timeZonesSingleton == null) {
    		timeZonesSingleton = getTimeZonesFromPreferences(context);
    	} 
    	
    	return timeZonesSingleton;
    }
	
	public static List<String> getTimeZonesFromPreferences(Context context) {
		
		List<String> timeZoneList = new ArrayList<String>();
		
		SharedPreferences prefs = context.getSharedPreferences(preferences_file_alarm, Context.MODE_PRIVATE);
		
		String timeZoneSet = prefs.getString(preferences_name, "");
		
		if(timeZoneSet != "") {
			Gson gson = new Gson();
			
			Type type = new TypeToken<List<String>>(){}.getType();
			timeZoneList = gson.fromJson(timeZoneSet, type);
		}
		
		if(timeZoneList.size() == 0) {
			TimeZone timeZone = TimeZone.getDefault();
			
			timeZoneList.add(timeZone.getDisplayName());
		}
		
		return timeZoneList;
	}
	
	public static List<String> addTimeZone(String timeZone, Context context) {
		
		if(!timeZonesSingleton.contains(timeZone)) {
			timeZonesSingleton.add(timeZone);
			
			savePreferences(context);
		}
		
		return timeZonesSingleton;
	}
	
	public static List<String> deleteTimeZone(String timeZone, Context context) {
		
		if(timeZonesSingleton.contains(timeZone)) {
			timeZonesSingleton.remove(timeZone);
			
			savePreferences(context);
		}
		
		return timeZonesSingleton;
	}
	
	public static List<String> saveTimeZone(List<String> timeZonesList, Context context) {
		timeZonesSingleton = timeZonesList;
				
		savePreferences(context);
		
		return timeZonesSingleton;
	}
	
	private static void savePreferences(Context context) {
		Gson gson = new Gson();
		String listTimeZonesJson = gson.toJson(timeZonesSingleton);
		
		SharedPreferences.Editor editor = context.getSharedPreferences(preferences_file_alarm, Context.MODE_PRIVATE).edit();
		editor.putString(preferences_name, listTimeZonesJson);
		editor.commit();
	}
}
