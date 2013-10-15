package com.worldalarm.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME 					= "alarm";
	public static final String COLUMN_ID 					= "_id";
	public static final String COLUMN_NAME_TIME_IN_MILLIS 	= "timeInMillis";
	public static final String COLUMN_NAME_CITY				= CityDatabaseHelper.COLUMN_NAME_CITY;
	public static final String COLUMN_NAME_TIME_ZONE_ID		= CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_ID;
	public static final String COLUMN_NAME_TIME_ZONE_NAME	= CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_NAME;
	
	private static final String DATABASE_NAME 		= "alarmset.db";
    private static final int DATABASE_VERSION 		= 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_NAME_TIME_IN_MILLIS +" INTEGER, "
    												+ COLUMN_NAME_CITY +" TEXT, "
    												+ CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_ID +" TEXT, "
    												+ CityDatabaseHelper.COLUMN_NAME_TIME_ZONE_NAME +" TEXT);";
	
    public static final String DATABASE_SELECT_ALL	= "SELECT * FROM "+ TABLE_NAME +";";
    
    private static AlarmDatabaseHelper singleton 	= null;
    
    public synchronized static AlarmDatabaseHelper getInstance(Context context) {
    	if(singleton == null) {
    		singleton = new AlarmDatabaseHelper(context.getApplicationContext());
    	}
    	
    	return singleton;
    }
    
    private AlarmDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL(DATABASE_CREATE);
			db.setTransactionSuccessful();
		
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(AlarmDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "+ newVersion);
	}

	public void saveAlarmAsync(Alarm alarm, OnSavedAlarmListener onSavedAlarmListener) {
		SaveAlarmTask task = new SaveAlarmTask(onSavedAlarmListener);
		
		task.execute(alarm);
	}
	
	public void getAllAlarmsAsync(OnRetrievedAllAlarmsListener onRetrievedAllAlarmsListener) {
		GetAllAlarmsTask task = new GetAllAlarmsTask(onRetrievedAllAlarmsListener);
		
		task.execute();
	}
	
	public void getAllAlarmsByTZName(String timeZoneName, OnRetrievedAllAlarmsByTZNameListener onRetrievedAllAlarmsByTZNameListener) {
		GetAllAlarmsByTZNameTask task = new GetAllAlarmsByTZNameTask(onRetrievedAllAlarmsByTZNameListener);
		
		task.execute(timeZoneName);
	}
	
	public void updateAlarmAsync(Alarm alarm, OnUpdatedAlarmListener onUpdatedAlarmListener) {
		UpdateAlarmTask task = new UpdateAlarmTask(onUpdatedAlarmListener);
		
		task.execute(alarm);
	}
	
	public void removeAlarmAsync(Alarm alarm) {
		RemoveAlarmTask task = new RemoveAlarmTask();
		
		task.execute(alarm);
	}
	
	private class SaveAlarmTask extends AsyncTask<Alarm, Void, Alarm> {
		
		private OnSavedAlarmListener onSavedAlarmListener = null;
		
		public SaveAlarmTask(OnSavedAlarmListener onSavedAlarmListener) {
			this.onSavedAlarmListener = onSavedAlarmListener;
		}
		
		@Override
		protected Alarm doInBackground(Alarm... params) {
			ContentValues insertValues = params[0].getInsertContentValues();
			long row = getWritableDatabase().insert(TABLE_NAME, null, insertValues);
			
			params[0].setId(row);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(Alarm alarm) {
			this.onSavedAlarmListener.onSavedAlarm(alarm);
		}
	}
	
	private class GetAllAlarmsTask extends AsyncTask<Void, Void, List<Alarm>> {

		private OnRetrievedAllAlarmsListener onRetrievedAllAlarmsListener = null;
		
		public GetAllAlarmsTask(OnRetrievedAllAlarmsListener onRetrievedAllAlarmsListener) {
			this.onRetrievedAllAlarmsListener = onRetrievedAllAlarmsListener;
		}
		
		@Override
		protected List<Alarm> doInBackground(Void... params) {
			
			List<Alarm> listAlarm = new ArrayList<Alarm>();
			
			Cursor cursor = getReadableDatabase().rawQuery(DATABASE_SELECT_ALL, null);
						
			cursor.moveToFirst();
			
			while (!cursor.isAfterLast()) {
				
				Alarm alarm = cursorToAlarm(cursor);
				
				listAlarm.add(alarm);
				
				cursor.moveToNext();
			}
			cursor.close();
			
			return listAlarm;
		}

		private Alarm cursorToAlarm(Cursor cursor) {
			long id = cursor.getLong(0);
			long timeInMillis = cursor.getLong(1);
			String cityName = cursor.getString(2);
			String timeZoneID = cursor.getString(3);
			String timeZoneName = cursor.getString(4);
			
			City city = new City(cityName, timeZoneID, timeZoneName);
			
			Alarm alarm = new Alarm(timeInMillis, city);
			alarm.setId(id);
			
			return alarm;
		}
		
		@Override
		protected void onPostExecute(List<Alarm> listAlarm) {
			this.onRetrievedAllAlarmsListener.onRetrievedAllAlarms(listAlarm);
		}
	}
	
	private class GetAllAlarmsByTZNameTask extends AsyncTask<String, Void, List<Alarm>> {

		private OnRetrievedAllAlarmsByTZNameListener onRetrievedAllAlarmsByTZNameListener = null;
		
		public GetAllAlarmsByTZNameTask(OnRetrievedAllAlarmsByTZNameListener onRetrievedAllAlarmsByTZNameListener) {
			this.onRetrievedAllAlarmsByTZNameListener = onRetrievedAllAlarmsByTZNameListener;
		}
		
		@Override
		protected List<Alarm> doInBackground(String... params) {
			
			List<Alarm> listAlarm = new ArrayList<Alarm>();
			
			String selection = COLUMN_NAME_TIME_ZONE_NAME +"= ?";
			String[] selectionArgs = {params[0]};
			
			Cursor cursor = getReadableDatabase().query(false, TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
						
			cursor.moveToFirst();
			
			while (!cursor.isAfterLast()) {
				
				Alarm alarm = cursorToAlarm(cursor);
				
				listAlarm.add(alarm);
				
				cursor.moveToNext();
			}
			cursor.close();
			
			return listAlarm;
		}

		private Alarm cursorToAlarm(Cursor cursor) {
			long id = cursor.getLong(0);
			long timeInMillis = cursor.getLong(1);
			String cityName = cursor.getString(2);
			String timeZoneID = cursor.getString(3);
			String timeZoneName = cursor.getString(4);
			
			City city = new City(cityName, timeZoneID, timeZoneName);
			
			Alarm alarm = new Alarm(timeInMillis, city);
			alarm.setId(id);
			
			return alarm;
		}
		
		@Override
		protected void onPostExecute(List<Alarm> listAlarm) {
			this.onRetrievedAllAlarmsByTZNameListener.onRetrievedAllAlarmsByTZName(listAlarm);
		}
	}
	
	private class UpdateAlarmTask extends AsyncTask<Alarm, Void, Alarm> {
		
		private OnUpdatedAlarmListener onUpdatedAlarmListener = null;
		
		public UpdateAlarmTask(OnUpdatedAlarmListener onUpdatedAlarmListener) {
			this.onUpdatedAlarmListener = onUpdatedAlarmListener;
		}
		
		@Override
		protected Alarm doInBackground(Alarm... params) {			
			ContentValues updateValues = params[0].getUpdateContentValues();
			getWritableDatabase().update(TABLE_NAME, updateValues, COLUMN_ID +"="+ params[0].getId(), null);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(Alarm alarm) {
			this.onUpdatedAlarmListener.onUpdatedAlarm(alarm);
		}
	}
	
	private class RemoveAlarmTask extends AsyncTask<Alarm, Void, Void> {
		
		@Override
		protected Void doInBackground(Alarm... params) {
			getWritableDatabase().delete(TABLE_NAME, COLUMN_ID +"="+ params[0].getId(), null);
			
			return null;
		}
	}
	
	public interface OnRetrievedAllAlarmsListener {
		void onRetrievedAllAlarms(List<Alarm> listAlarm);
	}
	
	public interface OnRetrievedAllAlarmsByTZNameListener {
		void onRetrievedAllAlarmsByTZName(List<Alarm> listAlarm);
	}
	
	public interface OnSavedAlarmListener {
		void onSavedAlarm(Alarm alarm);
	}
	
	public interface OnUpdatedAlarmListener {
		void onUpdatedAlarm(Alarm alarm);
	}
}
