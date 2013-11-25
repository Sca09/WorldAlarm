package com.worldalarm.preferences;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldalarm.db.City;

public class CityPreferences {

	private static String preferences_file_city = "city_file";
	private static String preferences_name = "city_set";
	
	private static HashMap<String, City> citiesSingleton = null;
	
	public synchronized static HashMap<String, City> getCitiesInstance(Context context) {
		
		if(citiesSingleton == null) {
			citiesSingleton = getCitiesFromPreferences(context);
		}
		
		return citiesSingleton;
	}
	
	public static HashMap<String, City> getCitiesFromPreferences(Context context){
		
		HashMap<String, City> citiesList = new HashMap<String, City>();
		
		SharedPreferences prefs = context.getSharedPreferences(preferences_file_city, Context.MODE_PRIVATE);
		
		String citiesSet = prefs.getString(preferences_name, "");
		
		if(citiesSet != "") {
			Gson gson = new Gson();
			
			Type type = new TypeToken<HashMap<String, City>>(){}.getType();
			citiesList = gson.fromJson(citiesSet, type);
		} 
		
		String[] availableIDs = TimeZone.getAvailableIDs();
		
		TimeZone timeZone = null;
		
		City city = null;
		
		for(String ID : availableIDs) {
			String cityName = ID.substring(ID.lastIndexOf("/") + 1);
			
			timeZone = TimeZone.getTimeZone(ID);
			
			city = new City(cityName.replaceAll("_", " "), ID, timeZone.getDisplayName());
			
			citiesList.put(city.getCityName(), city);
		}
		
		
		citiesSingleton = citiesList;
		
		return citiesSingleton;
	}
	
	public synchronized static HashMap<String, City> addCity(City city, Context context) {
		
		if(citiesSingleton == null) {
			citiesSingleton = getCitiesFromPreferences(context);
		}
		
		citiesSingleton.put(city.getCityName(), city);
		
		savePreferences(city, context);
		
		return citiesSingleton;
	}
	
	private static void savePreferences(City city, Context context) {
		HashMap<String, City> citiesList = new HashMap<String, City>();
		
		SharedPreferences prefs = context.getSharedPreferences(preferences_file_city, Context.MODE_PRIVATE);
		
		String citiesSet = prefs.getString(preferences_name, "");
		
		if(citiesSet != "") {
			Gson gson = new Gson();
			
			Type type = new TypeToken<HashMap<String, City>>(){}.getType();
			citiesList = gson.fromJson(citiesSet, type);
		}
		
		citiesList.put(city.getCityName(), city);
		
		Gson gson = new Gson();
		String listAlarmsJson = gson.toJson(citiesList);

		SharedPreferences.Editor editor = context.getSharedPreferences(preferences_file_city, Context.MODE_PRIVATE).edit();
		editor.putString(preferences_name, listAlarmsJson);
		editor.commit();
	}
}
