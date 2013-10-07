package com.worldalarm.db;

import java.util.HashMap;
import java.util.TimeZone;

import com.worldalarm.db.AlarmDatabaseHelper.SaveAlarmListener;

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
    
    private CityDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL(DATABASE_CREATE);
			db.setTransactionSuccessful();
			
			String[] availableIDs = TimeZone.getAvailableIDs();
			
			for(String ID : availableIDs) {
				String cityName = ID.substring(ID.lastIndexOf("/") + 1);
				ContentValues insertValues = new ContentValues();
				insertValues.put(COLUMN_NAME_CITY, cityName.replaceAll("_", " "));
				insertValues.put(COLUMN_NAME_TIME_ZONE, ID);
				db.insert(TABLE_NAME, null, insertValues);
			}
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CityDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "+ newVersion);
	}
	
	public void getAllCitiesAsync(AllCitiesListener allCitiesListener) {
		GetAllCitiesTask task = new GetAllCitiesTask(allCitiesListener);
		
		task.execute();
	}
	
	public void addCityAsync(String city, String timeZone, AddCityListener addCityListener) {
		AddCityTask task = new AddCityTask(addCityListener);
		
		String[] data = {city, timeZone};
		task.execute(data);
	}
	
	private class GetAllCitiesTask extends AsyncTask<Void, Void, HashMap<String, String>> {

		private AllCitiesListener allCitiesListener = null;
		
		public GetAllCitiesTask(AllCitiesListener allCitiesListener) {
			this.allCitiesListener = allCitiesListener;
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
			
			return cities;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> cities) {
			this.allCitiesListener.getCities(cities);
		}
	}
	
	private class AddCityTask extends AsyncTask<String[], Void, String[]> {
		
		private AddCityListener addCityListener = null;
		
		public AddCityTask(AddCityListener addCityListener) {
			this.addCityListener = addCityListener;
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
			this.addCityListener.addCity(data);
		}
	}
	
	public interface AllCitiesListener {
		void getCities(HashMap<String, String> cities);
	}
	
	public interface AddCityListener {
		void addCity(String[] data);
	}
}
