package com.worldalarm.preferences;

import java.lang.reflect.Type;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldalarm.db.City;
import com.worldalarm.db.User;

public class UserPreferences {

	private static String preferences_file_user = "user_file";
	private static String preferences_name = "user_set";
	
	private static User userSingleton = null;
	
	public synchronized static User getUserInstance(Context context) {
	
		if(userSingleton == null) {
			userSingleton = getUserFromPreferences(context);
		}
		
		return userSingleton;
	}
	
	private static User getUserFromPreferences(Context context) {
		User user = new User();
		
		SharedPreferences prefs = context.getSharedPreferences(preferences_file_user, Context.MODE_PRIVATE);
		
		String userSet = prefs.getString(preferences_name, "");
		
		if(userSet != "") {
			Gson gson = new Gson();
			
			Type type = new TypeToken<User>(){}.getType();
			
			user = gson.fromJson(userSet, type);
		}
		
		return user;
	}
	
	public synchronized static User updateUser(User user, Context context) {
		if(userSingleton == null) {
			userSingleton = getUserFromPreferences(context);
		}
		
		userSingleton = user;

		savePreferences(context);
		
		return userSingleton;
	}
	
	public synchronized static User addCurrentCity(City currentCity, Context context) {
		if(userSingleton == null) {
			userSingleton = getUserFromPreferences(context);
		}
		
		userSingleton.setCurrentCity(currentCity);
		
		savePreferences(context);
		
		return userSingleton;
	}
	
	private static void savePreferences(Context context) {
		Gson gson = new Gson();
		String listAlarmsJson = gson.toJson(userSingleton);
		
		SharedPreferences.Editor editor = context.getSharedPreferences(preferences_file_user, Context.MODE_PRIVATE).edit();
		editor.putString(preferences_name, listAlarmsJson);
		editor.commit();
	}
}
