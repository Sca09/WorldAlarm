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
	public static final String COLUMN_NAME_TIME_ZONE_ID		= "timeZoneID";
	public static final String COLUMN_NAME_TIME_ZONE_NAME	= "timeZoneName";
	
	private static final String DATABASE_NAME 		= "city.db";
    private static final int DATABASE_VERSION 		= 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_NAME_CITY +" TEXT, "
    												+ COLUMN_NAME_TIME_ZONE_ID +" TEXT, "
    												+ COLUMN_NAME_TIME_ZONE_NAME +" TEXT);";
	
    public static final String DATABASE_SELECT_ALL	= "SELECT * FROM "+ TABLE_NAME +";";
    
    private static CityDatabaseHelper singleton 	= null;
    
    public synchronized static CityDatabaseHelper getInstance(Context context) {
    	if(singleton == null) {
    		singleton = new CityDatabaseHelper(context.getApplicationContext());
    	}
    	
    	return singleton;
    }

    private static HashMap<String, City> citiesSingleton	= null;
    
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
			
			TimeZone timeZone = null;
			
			City city = null;
			
			for(String ID : availableIDs) {
				String cityName = ID.substring(ID.lastIndexOf("/") + 1);
				
				timeZone = TimeZone.getTimeZone(ID);
				
				city = new City(cityName.replaceAll("_", " "), ID, timeZone.getDisplayName());
				
				ContentValues insertValues = city.getInsertContentValues();
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
	
	public void addCityAsync(City city, OnAddedCityListener onAddedCityListener) {
		AddCityTask task = new AddCityTask(onAddedCityListener);
		
		task.execute(city);
	}
	
	public void searchCityByNameAsync(String cityName, OnFoundCityByNameListener onFoundCityByNameListener) {
		SearchCityByNameTask task = new SearchCityByNameTask(onFoundCityByNameListener);
		
		task.execute(cityName);
	}
	
	private class GetAllCitiesTask extends AsyncTask<Void, Void, HashMap<String, City>> {

		private OnRetrievedAllCitiesListener onRetrievedAllCitiesListener = null;
		
		public GetAllCitiesTask(OnRetrievedAllCitiesListener onRetrievedAllCitiesListener) {
			this.onRetrievedAllCitiesListener = onRetrievedAllCitiesListener;
		}
		
		@Override
		protected HashMap<String, City> doInBackground(Void... params) {
			
			HashMap<String, City> cities = new HashMap<String, City>();
			
			Cursor cursor = getReadableDatabase().rawQuery(DATABASE_SELECT_ALL, null);
						
			cursor.moveToFirst();
			
			City city = null;
			
			while (!cursor.isAfterLast()) {
				
				city = new City(cursor.getString(1), cursor.getString(2), cursor.getString(3));
				
				cities.put(cursor.getString(1), city);
				
				cursor.moveToNext();
			}
			cursor.close();
			
			citiesSingleton = cities;
			
			return cities;
		}

		@Override
		protected void onPostExecute(HashMap<String, City> cities) {
			this.onRetrievedAllCitiesListener.onRetrievedAllCities(cities);
		}
	}
	
	private class AddCityTask extends AsyncTask<City, Void, City> {
		
		private OnAddedCityListener onAddedCityListener = null;
		
		public AddCityTask(OnAddedCityListener onAddedCityListener) {
			this.onAddedCityListener = onAddedCityListener;
		}
		
		@Override
		protected City doInBackground(City... params) {
			ContentValues insertValues = new ContentValues();
			insertValues.put(COLUMN_NAME_CITY, params[0].getCityName());
			insertValues.put(COLUMN_NAME_TIME_ZONE_ID, params[0].getTimeZoneID());
			insertValues.put(COLUMN_NAME_TIME_ZONE_NAME, params[0].getTimeZoneName());
			getWritableDatabase().insert(TABLE_NAME, null, insertValues);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(City city) {
			this.onAddedCityListener.onAddedCity(city);
		}
	}
	
	private class SearchCityByNameTask extends AsyncTask<String, Void, City> {

		private OnFoundCityByNameListener onFoundCityByNameListener = null;
		
		public SearchCityByNameTask(OnFoundCityByNameListener onFoundCityByNameListener) {
			this.onFoundCityByNameListener = onFoundCityByNameListener;
		}
		
		@Override
		protected City doInBackground(String... params) {
			
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
								TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
								String timeZoneName = timeZone.getDisplayName();
								
								ContentValues insertValues = new ContentValues();
								insertValues.put(COLUMN_NAME_CITY, cityLongName);
								insertValues.put(COLUMN_NAME_TIME_ZONE_ID, timeZoneId);
								insertValues.put(COLUMN_NAME_TIME_ZONE_NAME, timeZoneName);
								getWritableDatabase().insert(TABLE_NAME, null, insertValues);
								
								City city = new City(cityLongName, timeZoneId, timeZoneName);
								
								citiesSingleton.put(cityLongName, city);
								
								return city;
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
		protected void onPostExecute(City city) {
			this.onFoundCityByNameListener.onFoundCityByName(city);
		}
	}
	
	public interface OnRetrievedAllCitiesListener {
		void onRetrievedAllCities(HashMap<String, City> cities);
	}
	
	public interface OnAddedCityListener {
		void onAddedCity(City city);
	}
	
	public interface OnFoundCityByNameListener {
		void onFoundCityByName(City city);
	}
}
