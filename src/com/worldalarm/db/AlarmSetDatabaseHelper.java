package com.worldalarm.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

public class AlarmSetDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME 					= "alarmset";
	public static final String COLUMN_ID 					= "_id";
	public static final String COLUMN_ALARM_TIME_IN_MILLIS 	= "alarmTimeInMillis";
	public static final String COLUMN_LOCAL_ALARM_TZ 		= "localAlarmTZ";
	public static final String COLUMN_REMOTE_ALARM_CITY 	= "remoteAlarmCity";
	public static final String COLUMN_REMOTE_ALARM_TZ 		= "remoteAlarmTZ";
	
	private static final String DATABASE_NAME 		= "alarmset.db";
    private static final int DATABASE_VERSION 		= 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_ALARM_TIME_IN_MILLIS +" INTEGER, "
    												+ COLUMN_LOCAL_ALARM_TZ +" TEXT, "
    												+ COLUMN_REMOTE_ALARM_CITY +" TEXT, "
    												+ COLUMN_REMOTE_ALARM_TZ +" TEXT);";
	
    public static final String DATABASE_INSERT		= "INSERT INTO "+ TABLE_NAME+ " ("
    												+ COLUMN_ALARM_TIME_IN_MILLIS +", "
    												+ COLUMN_LOCAL_ALARM_TZ +", "
    												+ COLUMN_REMOTE_ALARM_CITY +", "
    												+ COLUMN_REMOTE_ALARM_TZ +") "
    												+ "VALUES (?, ?, ?, ?);";
    
    public static final String DATABASE_SELECT_ALL	= "SELECT * FROM "+ TABLE_NAME +";";
    
    private static AlarmSetDatabaseHelper singleton 	= null;
    
    public synchronized static AlarmSetDatabaseHelper getInstance(Context context) {
    	if(singleton == null) {
    		singleton = new AlarmSetDatabaseHelper(context.getApplicationContext());
    	}
    	
    	return singleton;
    }
    
    private AlarmSetDatabaseHelper(Context context) {
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
		Log.w(AlarmSetDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "+ newVersion);
	}

	public void saveAlarmSetAsync(AlarmSet alarmSet) {
		SaveAlarmSetTask task = new SaveAlarmSetTask();
		
		task.execute(alarmSet);
	}
	
	private class SaveAlarmSetTask extends AsyncTask<AlarmSet, Void, AlarmSet> {
		
		@Override
		protected AlarmSet doInBackground(AlarmSet... params) {
			
			String[] paramsAlarmSet = params[0].getDatabaseParams();
			
			getWritableDatabase().execSQL(DATABASE_INSERT, paramsAlarmSet);
			
			return null;
		}
	}
	
	public void GetAllAlarmsSetAsync(ArrayAlarmSetListener arrayAlarmSetListener) {
		GetAllAlarmsSetTask task = new GetAllAlarmsSetTask(arrayAlarmSetListener);
		
		task.execute();
	}
	
	private class GetAllAlarmsSetTask extends AsyncTask<Void, Void, List<AlarmSet>> {

		private ArrayAlarmSetListener arrayAlarmSetListener = null;
		
		public GetAllAlarmsSetTask(ArrayAlarmSetListener arrayAlarmSetListener) {
			this.arrayAlarmSetListener = arrayAlarmSetListener;
		}
		
		@Override
		protected List<AlarmSet> doInBackground(Void... params) {
			
			 List<AlarmSet> listAlarmSet = new ArrayList<AlarmSet>();
			
			Cursor cursor = getReadableDatabase().rawQuery(DATABASE_SELECT_ALL, null);
			
//			int count = cursor.getCount();
			
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				
				AlarmSet alarmSet = cursorToAlarmSet(cursor);
				
				listAlarmSet.add(alarmSet);
				
				cursor.moveToNext();
			}
			cursor.close();
			
			return listAlarmSet;
		}

		private AlarmSet cursorToAlarmSet(Cursor cursor) {
			long timeInMillis = cursor.getLong(1);
			String localTZ = cursor.getString(2);
			String remoteCity = cursor.getString(3);
			String remoteTZ = cursor.getString(4);
			
			AlarmSet alarmSet = new AlarmSet(timeInMillis, remoteCity, remoteTZ);
			
			return alarmSet;
		}
		
		@Override
		protected void onPostExecute(List<AlarmSet> listAlarmSet) {
			this.arrayAlarmSetListener.setArrayAlarmSet(listAlarmSet);
		}
	}
	
	public interface ArrayAlarmSetListener {
		void setArrayAlarmSet(List<AlarmSet> listAlarmSet);
	}
	
}
