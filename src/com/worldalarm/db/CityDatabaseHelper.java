package com.worldalarm.db;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

public class CityDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME 					= "city";
	public static final String COLUMN_ID 					= "_id";
	public static final String COLUMN_NAME_CITY 			= "city";
	public static final String COLUMN_NAME_TIME_ZONE 		= "timeZone";
	
	private static final String DATABASE_NAME 		= "city.db";
    private static final int DATABASE_VERSION 		= 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_NAME_CITY +" TEXT, "
    												+ COLUMN_NAME_TIME_ZONE +" TEXT);";
	
    public static final String DATABASE_SELECT_ALL	= "SELECT * FROM "+ TABLE_NAME +";";
    
    private static CityDatabaseHelper singleton 	= null;
    
    public synchronized static CityDatabaseHelper getInstance(Context context) {
    	if(singleton == null) {
    		singleton = new CityDatabaseHelper(context.getApplicationContext());
    	}
    	
    	return singleton;
    }

    private static HashMap<String, String> citiesSingleton	= null;
    
    public synchronized static void getAllCities(Context context, OnRetrievedAllCitiesListener onRetrievedAllCitiesListener) {
    	if(citiesSingleton == null) {
    		getInstance(context).getAllCitiesAsync(onRetrievedAllCitiesListener);
    	} else {
    		onRetrievedAllCitiesListener.onRetrievedAllCities(citiesSingleton);
    	}
    }
    
    private CityDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL(DATABASE_CREATE);
			
			String[] availableIDs = TimeZone.getAvailableIDs();
			
			for(String ID : availableIDs) {
				String cityName = ID.substring(ID.lastIndexOf("/") + 1);
				ContentValues insertValues = new ContentValues();
				insertValues.put(COLUMN_NAME_CITY, cityName.replaceAll("_", " "));
				insertValues.put(COLUMN_NAME_TIME_ZONE, ID);
				db.insert(TABLE_NAME, null, insertValues);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CityDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "+ newVersion);
	}
	
	public void getAllCitiesAsync(OnRetrievedAllCitiesListener onRetrievedAllCitiesListener) {
		GetAllCitiesTask task = new GetAllCitiesTask(onRetrievedAllCitiesListener);
		
		task.execute();
	}
	
	public void addCityAsync(String city, String timeZone, OnAddedCityListener onAddedCityListener) {
		AddCityTask task = new AddCityTask(onAddedCityListener);
		
		String[] data = {city, timeZone};
		task.execute(data);
	}
	
	public void searchCityByNameAsync(String cityName, OnFoundCityByNameListener onFoundCityByNameListener) {
		SearchCityByNameTask task = new SearchCityByNameTask(onFoundCityByNameListener);
		
		task.execute(cityName);
	}
	
	private class GetAllCitiesTask extends AsyncTask<Void, Void, HashMap<String, String>> {

		private OnRetrievedAllCitiesListener onRetrievedAllCitiesListener = null;
		
		public GetAllCitiesTask(OnRetrievedAllCitiesListener onRetrievedAllCitiesListener) {
			this.onRetrievedAllCitiesListener = onRetrievedAllCitiesListener;
		}
		
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			
			HashMap<String, String> cities = new HashMap<String, String>();
			
			Cursor cursor = getReadableDatabase().rawQuery(DATABASE_SELECT_ALL, null);
						
			cursor.moveToFirst();
			
			while (!cursor.isAfterLast()) {
				
				cities.put(cursor.getString(1), cursor.getString(2));
				
				cursor.moveToNext();
			}
			cursor.close();
			
			citiesSingleton = cities;
			
			return cities;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> cities) {
			this.onRetrievedAllCitiesListener.onRetrievedAllCities(cities);
		}
	}
	
	private class AddCityTask extends AsyncTask<String[], Void, String[]> {
		
		private OnAddedCityListener onAddedCityListener = null;
		
		public AddCityTask(OnAddedCityListener onAddedCityListener) {
			this.onAddedCityListener = onAddedCityListener;
		}
		
		@Override
		protected String[] doInBackground(String[]... params) {
			ContentValues insertValues = new ContentValues();
			insertValues.put(COLUMN_NAME_CITY, params[0][0]);
			insertValues.put(COLUMN_NAME_TIME_ZONE, params[0][1]);
			getWritableDatabase().insert(TABLE_NAME, null, insertValues);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(String[] data) {
			this.onAddedCityListener.onAddedCity(data);
		}
	}
	
	private class SearchCityByNameTask extends AsyncTask<String, Void, String[]> {

		private OnFoundCityByNameListener onFoundCityByNameListener = null;
		
		public SearchCityByNameTask(OnFoundCityByNameListener onFoundCityByNameListener) {
			this.onFoundCityByNameListener = onFoundCityByNameListener;
		}
		
		@Override
		protected String[] doInBackground(String... params) {
			
			try {
				URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(params[0], "UTF-8") +"&sensor=false");
				String res = IOUtils.toString(url);
				
				if(res != null && res.length() > 0) {
					
					JSONObject jObject = new JSONObject(res);
					JSONArray jArray = jObject.getJSONArray("results");
					if(jArray.length() > 0){
						if(jArray.length() == 1) {
							JSONObject jObjectGeo = jArray.getJSONObject(0);
							JSONArray jArrayAddress = jObjectGeo.getJSONArray("address_components");
							JSONObject jObjectCity = jArrayAddress.getJSONObject(0);
							String cityLongName = jObjectCity.getString("long_name");
							
							JSONObject jObjectGeometry = jObjectGeo.getJSONObject("geometry");
							JSONObject jObjectLocation = jObjectGeometry.getJSONObject("location");
							double lat = jObjectLocation.getDouble("lat");
							double lng = jObjectLocation.getDouble("lng");
							
							url = new URL("https://maps.googleapis.com/maps/api/timezone/json?location="+ lat +","+ lng +"&timestamp="+ (Calendar.getInstance().getTimeInMillis() / 1000) +"&sensor=false");
							res = IOUtils.toString(url);
							JSONObject jObjectTZ = new JSONObject(res);
							String timeZoneId = jObjectTZ.getString("timeZoneId");
							
							if(cityLongName != null && cityLongName.length() > 0 && timeZoneId != null && timeZoneId.length() > 0) {
								ContentValues insertValues = new ContentValues();
								insertValues.put(COLUMN_NAME_CITY, cityLongName);
								insertValues.put(COLUMN_NAME_TIME_ZONE, timeZoneId);
								getWritableDatabase().insert(TABLE_NAME, null, insertValues);
								
								citiesSingleton.put(cityLongName, timeZoneId);
								
								String[] result = {cityLongName, timeZoneId}; 
								
								return result;
							}
						} else {
							//TODO: implement case for more than one city found
						}
					}
				}
				
				Log.d("CityDatabaseHelper", "res["+ res +"]");
				
			} catch (Exception e) {
				Log.e("CityDatabaseHelper", e.getMessage());
				
			}

			return null;
		}

		@Override
		protected void onPostExecute(String[] data) {
			this.onFoundCityByNameListener.onFoundCityByName(data);
		}
	}
	
	public interface OnRetrievedAllCitiesListener {
		void onRetrievedAllCities(HashMap<String, String> cities);
	}
	
	public interface OnAddedCityListener {
		void onAddedCity(String[] data);
	}
	
	public interface OnFoundCityByNameListener {
		void onFoundCityByName(String[] data);
	}
}
