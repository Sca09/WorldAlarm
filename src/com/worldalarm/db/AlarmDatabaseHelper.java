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
	public static final String COLUMN_NAME_CITY 			= "city";
	public static final String COLUMN_NAME_TIME_ZONE 		= "timeZone";
	
	private static final String DATABASE_NAME 		= "alarmset.db";
    private static final int DATABASE_VERSION 		= 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_NAME_TIME_IN_MILLIS +" INTEGER, "
    												+ COLUMN_NAME_CITY +" TEXT, "
    												+ COLUMN_NAME_TIME_ZONE +" TEXT);";
	
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

	public void saveAlarmAsync(Alarm alarm, SaveAlarmListener saveAlarmListener) {
		SaveAlarmTask task = new SaveAlarmTask(saveAlarmListener);
		
		task.execute(alarm);
	}
	
	public void getAllAlarmsAsync(ArrayAlarmListener arrayAlarmListener) {
		GetAllAlarmsTask task = new GetAllAlarmsTask(arrayAlarmListener);
		
		task.execute();
	}
	
	public void updateAlarmAsync(Alarm alarm, UpdateAlarmListener updateAlarmListener) {
		UpdateAlarmTask task = new UpdateAlarmTask(updateAlarmListener);
		
		task.execute(alarm);
	}
	
	public void removeAlarmAsync(Alarm alarm) {
		RemoveAlarmTask task = new RemoveAlarmTask();
		
		task.execute(alarm);
	}
	
	private class SaveAlarmTask extends AsyncTask<Alarm, Void, Alarm> {
		
		private SaveAlarmListener saveAlarmListener = null;
		
		public SaveAlarmTask(SaveAlarmListener saveAlarmListener) {
			this.saveAlarmListener = saveAlarmListener;
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
			this.saveAlarmListener.saveAlarm(alarm);
		}
	}
	
	private class GetAllAlarmsTask extends AsyncTask<Void, Void, List<Alarm>> {

		private ArrayAlarmListener arrayAlarmListener = null;
		
		public GetAllAlarmsTask(ArrayAlarmListener arrayAlarmListener) {
			this.arrayAlarmListener = arrayAlarmListener;
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
			String city = cursor.getString(2);
			String timeZone = cursor.getString(3);
			
			Alarm alarm = new Alarm(timeInMillis, city, timeZone);
			alarm.setId(id);
			
			return alarm;
		}
		
		@Override
		protected void onPostExecute(List<Alarm> listAlarm) {
			this.arrayAlarmListener.setArrayAlarm(listAlarm);
		}
	}
	
	private class UpdateAlarmTask extends AsyncTask<Alarm, Void, Alarm> {
		
		private UpdateAlarmListener updateAlarmListener = null;
		
		public UpdateAlarmTask(UpdateAlarmListener updateAlarmListener) {
			this.updateAlarmListener = updateAlarmListener;
		}
		
		@Override
		protected Alarm doInBackground(Alarm... params) {			
			ContentValues updateValues = params[0].getInsertContentValues();
			getWritableDatabase().update(TABLE_NAME, updateValues, COLUMN_ID +"="+ params[0].getId(), null);
			
			return params[0];
		}
		
		@Override
		protected void onPostExecute(Alarm alarm) {
			this.updateAlarmListener.updateAlarm(alarm);
		}
	}
	
	private class RemoveAlarmTask extends AsyncTask<Alarm, Void, Void> {
		
		@Override
		protected Void doInBackground(Alarm... params) {
			getWritableDatabase().delete(TABLE_NAME, COLUMN_ID +"="+ params[0].getId(), null);
			
			return null;
		}
	}
	
	public interface ArrayAlarmListener {
		void setArrayAlarm(List<Alarm> listAlarm);
	}
	
	public interface SaveAlarmListener {
		void saveAlarm(Alarm alarm);
	}
	
	public interface UpdateAlarmListener {
		void updateAlarm(Alarm alarm);
	}
}