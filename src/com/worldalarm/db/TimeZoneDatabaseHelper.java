package com.worldalarm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

public class TimeZoneDatabaseHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "timezone";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	
	private static final String DATABASE_NAME = "timezone.db";
    private static final int DATABASE_VERSION = 1;
	
    private static final String DATABASE_CREATE		= "CREATE TABLE "+ TABLE_NAME +" (" 
    												+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
    												+ COLUMN_NAME +" TEXT);";
	
    public static final String DATABASE_SELECT_ALL	= "SELECT * FROM "+ TABLE_NAME +";";
	
    private static TimeZoneDatabaseHelper singleton = null;
    
    public synchronized static TimeZoneDatabaseHelper getInstance(Context context) {
    	if(singleton == null) {
    		singleton = new TimeZoneDatabaseHelper(context.getApplicationContext());
    	}
    	
    	return singleton;
    }
    
    private static List<String> listTimeZonesSingleton = null;
    
    public static void getAllTimeZones(Context context, OnRetrievedAllTimeZonesListener onRetrievedAllTimeZonesListener) {
    	
    	if(listTimeZonesSingleton == null) {
    		getInstance(context).getAllTimeZonesAsync(onRetrievedAllTimeZonesListener);
    	} else {
    		onRetrievedAllTimeZonesListener.OnRetrievedAllTimeZones(listTimeZonesSingleton);
    	}
    }
    
    private TimeZoneDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL(DATABASE_CREATE);
			
			TimeZone timeZone = TimeZone.getDefault();
			
			ContentValues insertValues = new ContentValues();
			insertValues.put(COLUMN_NAME, timeZone.getDisplayName());
			db.insert(TABLE_NAME, null, insertValues);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TimeZoneDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "+ newVersion);
	}
	
	public void getAllTimeZonesAsync(OnRetrievedAllTimeZonesListener onRetrievedAllTimeZonesListener) {
		GetAllTimeZonesTask task = new GetAllTimeZonesTask(onRetrievedAllTimeZonesListener);
		
		task.execute();
	}
	
	public void addTimeZoneAsync(String timeZone, OnAddedTimeZoneListener onAddedTimeZoneListener) {
		AddTimeZoneTask task = new AddTimeZoneTask(onAddedTimeZoneListener);
		
		task.execute(timeZone);
	}
	
	public void deleteTimeZoneAsync(String timeZone, OnDeletedTimeZoneListener onDeletedTimeZoneListener) {
		DeleteTimeZoneTask task = new DeleteTimeZoneTask(onDeletedTimeZoneListener);
		
		task.execute(timeZone);
	}
	
	private class GetAllTimeZonesTask extends AsyncTask<Void, Void, List<String>> {

		private OnRetrievedAllTimeZonesListener onRetrievedAllTimeZonesListener;
		
		public GetAllTimeZonesTask(OnRetrievedAllTimeZonesListener onRetrievedAllTimeZonesListener) {
			this.onRetrievedAllTimeZonesListener = onRetrievedAllTimeZonesListener;
		}
		
		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> listTimeZones = new ArrayList<String>();
			
			Cursor cursor = getReadableDatabase().rawQuery(DATABASE_SELECT_ALL, null);
			
			cursor.moveToFirst();
			
			while (!cursor.isAfterLast()) {
				listTimeZones.add(cursor.getString(1));
			}
			cursor.close();
			
			listTimeZonesSingleton = listTimeZones;
			
			return listTimeZones;
		}

		@Override
		protected void onPostExecute(List<String> listTimeZones) {
			this.onRetrievedAllTimeZonesListener.OnRetrievedAllTimeZones(listTimeZones);
		}
	}
	
	private class AddTimeZoneTask extends AsyncTask<String, Void, Void> {

		private OnAddedTimeZoneListener onAddedTimeZoneListener;
		
		public AddTimeZoneTask(OnAddedTimeZoneListener onAddedTimeZoneListener) {
			this.onAddedTimeZoneListener = onAddedTimeZoneListener;
		}
		
		@Override
		protected Void doInBackground(String... params) {
			String newTimeZone = params[0];
			
			if(!listTimeZonesSingleton.contains(newTimeZone)) {
				ContentValues insertValues = new ContentValues();
				insertValues.put(COLUMN_NAME, newTimeZone);
				getWritableDatabase().insert(TABLE_NAME, null, insertValues);
			
				listTimeZonesSingleton.add(newTimeZone);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			this.onAddedTimeZoneListener.OnAddedTimeZone(listTimeZonesSingleton);
		}
	}
	
	private class DeleteTimeZoneTask extends AsyncTask<String, Void, Void> {

		private OnDeletedTimeZoneListener onDeletedTimeZoneListener;
		
		public DeleteTimeZoneTask(OnDeletedTimeZoneListener onDeletedTimeZoneListener) {
			this.onDeletedTimeZoneListener = onDeletedTimeZoneListener;
		}

		@Override
		protected Void doInBackground(String... params) {
			String timeZoneToDelete = params[0];
			
			ContentValues deleteValues = new ContentValues();
			deleteValues.put(COLUMN_NAME, timeZoneToDelete);
			getWritableDatabase().delete(TABLE_NAME, COLUMN_NAME +" = "+ timeZoneToDelete, null);
			
			listTimeZonesSingleton.remove(timeZoneToDelete);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			this.onDeletedTimeZoneListener.OnDeletedTimeZone(listTimeZonesSingleton);
		}
	}
	
	
	public interface OnRetrievedAllTimeZonesListener {
		void OnRetrievedAllTimeZones(List<String> listTimeZones);
	}
	
	public interface OnAddedTimeZoneListener {
		void OnAddedTimeZone(List<String> listTimeZones);
	}
	
	public interface OnDeletedTimeZoneListener {
		void OnDeletedTimeZone(List<String> listTimeZones);
	}
}
