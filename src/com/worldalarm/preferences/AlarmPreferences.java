package com.worldalarm.preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldalarm.db.Alarm;

public class AlarmPreferences {
	
	private static String preferences_file_alarm = "alarms_set_file";
	private static String preferences_name = "alarms_set";
	
	private static HashMap<String, List<Alarm>> alarmsByTZSingleton	= null;
	
	public synchronized static HashMap<String, List<Alarm>> getAlarmsByTZInstance(Context context) {
		
		if(alarmsByTZSingleton == null) {
			alarmsByTZSingleton = getAlarmsFromPreferences(context);
		}
		
		return alarmsByTZSingleton;
	}
	
	public static HashMap<String, List<Alarm>> getAlarmsFromPreferences(Context context){
		
		HashMap<String, List<Alarm>> alarmsList = new HashMap<String, List<Alarm>>();
		
		SharedPreferences prefs = context.getSharedPreferences(preferences_file_alarm, Context.MODE_PRIVATE);
		
		String alarmSet = prefs.getString(preferences_name, "");
		
		if(alarmSet != "") {
			Gson gson = new Gson();
			
			Type type = new TypeToken<HashMap<String, List<Alarm>>>(){}.getType();
			alarmsList = gson.fromJson(alarmSet, type);
		}
		
		alarmsByTZSingleton = alarmsList;
		
		return alarmsByTZSingleton;
	}
	
	public synchronized static HashMap<String, List<Alarm>> addAlarm(Alarm alarm, Context context) {
		
		if(alarmsByTZSingleton == null) {
			alarmsByTZSingleton = getAlarmsFromPreferences(context);
		}
		
		List<Alarm> listAlarms = alarmsByTZSingleton.get(alarm.getCity().getTimeZoneName());
		
		if(listAlarms == null) {
			listAlarms = new ArrayList<Alarm>();
		}
		
		listAlarms.add(alarm);
		
		alarmsByTZSingleton.put(alarm.getCity().getTimeZoneName(), listAlarms);
		
		savePreferences(context);
		
		return alarmsByTZSingleton;
	}
	
	public synchronized static HashMap<String, List<Alarm>> updateAlarm(Alarm alarm, Context context) {
		if(alarmsByTZSingleton == null) {
			alarmsByTZSingleton = getAlarmsFromPreferences(context);
		}
		
		for(String timeZoneName : alarmsByTZSingleton.keySet()) {
			List<Alarm> list = alarmsByTZSingleton.get(timeZoneName);
			
			if(list.size() > 0) {
				for(Alarm currentAlarm : list) {
					if(currentAlarm.getId().equals(alarm.getId())) {
						if(currentAlarm.getCity().getTimeZoneName().equals(alarm.getCity().getTimeZoneName())) {
							list.set(list.indexOf(currentAlarm), alarm);
						} else {
							list.remove(currentAlarm);
							
							List<Alarm> listAlarms = alarmsByTZSingleton.get(alarm.getCity().getTimeZoneName());
							
							if(listAlarms == null) {
								listAlarms = new ArrayList<Alarm>();
							}
							
							listAlarms.add(alarm);
							
							alarmsByTZSingleton.put(alarm.getCity().getTimeZoneName(), listAlarms);
						}
						break;
					}
				}
			}
		}
		
		savePreferences(context);
		
		return alarmsByTZSingleton;
	}
	
	public synchronized static HashMap<String, List<Alarm>> deleteAlarm(Alarm alarm, Context context) {
		if(alarmsByTZSingleton == null) {
			alarmsByTZSingleton = getAlarmsFromPreferences(context);
		}
		
		List<Alarm> listAlarms = alarmsByTZSingleton.get(alarm.getCity().getTimeZoneName());
		
		if(listAlarms != null) {
			for(Alarm currentAlarm : listAlarms) {
				if(currentAlarm.getId().equals(alarm.getId())) {
					listAlarms.remove(currentAlarm);
					break;
				}
			}
		}
		
		alarmsByTZSingleton.put(alarm.getCity().getTimeZoneName(), listAlarms);
		
		savePreferences(context);
		
		return alarmsByTZSingleton;
	}

	public synchronized static HashMap<String, List<Alarm>> deleteAlarmByTZ(List<String> listTimeZonesToDelete, Context context) {
		if(alarmsByTZSingleton == null) {
			alarmsByTZSingleton = getAlarmsFromPreferences(context);
		}
		
		for(String timeZone : listTimeZonesToDelete) {
			alarmsByTZSingleton.remove(timeZone);
		}
		
		savePreferences(context);
		
		return alarmsByTZSingleton;
	}
	
	private static void savePreferences(Context context) {
		Gson gson = new Gson();
		String listAlarmsJson = gson.toJson(alarmsByTZSingleton);
		
		SharedPreferences.Editor editor = context.getSharedPreferences(preferences_file_alarm, Context.MODE_PRIVATE).edit();
		editor.putString(preferences_name, listAlarmsJson);
		editor.commit();
	}
	
}
