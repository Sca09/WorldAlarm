package com.worldalarm.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.CityDatabaseHelper;

public class NewAlarmActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.SaveAlarmListener, CityDatabaseHelper.AllCitiesListener, CityDatabaseHelper.AddCityListener {

	HashMap<String, String> timeZonesNames = new HashMap<String, String>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	String currentCity;
	String currentTimeZone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		
		this.initTimePicker();
		CityDatabaseHelper.getInstance(this).getAllCitiesAsync(this);
		
		this.getCurrentCityLocation();
		
		findViewById(R.id.setAlarmButton).setOnClickListener(this);
	}

	private void initTimePicker() {
		timePicker = (TimePicker) findViewById(R.id.alarmPicker);
//		timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.setAlarmButton:			
			this.setAlarm(view);
			break;
		}
	}
	
	public void setAlarm(View view) {
	
		int hourPicked 			= timePicker.getCurrentHour();
		int minutePicked 		= timePicker.getCurrentMinute();	
		String cityPicked 		= cityPickerAutoComplete.getText().toString();
		String timeZonePicked 	= "";
		if(cityPicked.equals("")) { //User didn't pick a city > Using the current one
//			cityPicked = this.getCurrentCity();
			cityPicked = currentCity;
			
			timeZonePicked 	= timeZonesNames.get(cityPicked);
			if(timeZonePicked == null || timeZonePicked.equals("")) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				timeZonePicked = currentTimeZone;
				
				CityDatabaseHelper.getInstance(this).addCityAsync(cityPicked, timeZonePicked, this);
			}
		} else {
			timeZonePicked 	= timeZonesNames.get(cityPicked);
			
			if(timeZonePicked == "") { //City chosen is not in TZ database
				//TODO: get the TZ from the City Name
				timeZonePicked = TimeZone.getDefault().toString();
				
				CityDatabaseHelper.getInstance(this).addCityAsync(cityPicked, timeZonePicked, this);
			}
		}
		    	
    	Alarm newAlarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
    	AlarmDatabaseHelper.getInstance(this).saveAlarmAsync(newAlarm, this);
	}

	@Override
	public void saveAlarm(Alarm alarm) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("newAlarm", alarm);
		
		setResult(RESULT_OK, returnIntent);     
		finish();
	}

	@Override
	public void getCities(HashMap<String, String> cities) {
		
		timeZonesNames = cities;
		
		String[] citiesArray = timeZonesNames.keySet().toArray(new String[timeZonesNames.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, citiesArray);
        cityPickerAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityPickerAutoComplete);
        cityPickerAutoComplete.setAdapter(adapter);
        cityPickerAutoComplete.setHint(R.string.choose_city);
	}
	
	private String getCurrentCity() {
		
		String currentCity = "";
		
		try {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);

			if(location != null) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
        
				Geocoder gcd = new Geocoder(this, Locale.getDefault());
        
				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
				if (addresses.size() > 0) { 
					currentCity = addresses.get(0).getLocality();
				}
			} else {
				String TZId = TimeZone.getDefault().getID();
				currentCity = TZId.substring(TZId.lastIndexOf("/") + 1);
			}
        	
        } catch (Exception e) {
        	
        	Log.d("NewAlarmActivity", "No location obtained from device");
        }	
        
        return currentCity;
	}
	
	private void getCurrentCityLocation() {		
		try {
			currentTimeZone = TimeZone.getDefault().getID();
			String cityName = currentTimeZone.substring(currentTimeZone.lastIndexOf("/") + 1);
			currentCity = cityName.replaceAll("_", " ");
			
			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(provider);
			
			Geocoder gcd = new Geocoder(this, Locale.getDefault());
			
			if(location != null) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
        
				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
				if (addresses.size() > 0) { 
					currentCity = addresses.get(0).getLocality();
				}
			} else {
				locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
			}
			

        } catch (Exception e) {
        	
        	Log.d("NewAlarmActivity", "No location obtained from device");
        }
	}

	@Override
	public void addCity(String[] data) {
		// Nothing to do here
	}
	
	
	LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
    
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Geocoder gcd = new Geocoder(getApplicationContext(), Locale.ENGLISH);
			
			locationManager.removeUpdates(this);
			
			try {
				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
				
				if (addresses.size() > 0) { 
					currentCity = addresses.get(0).getLocality();
				}
			} catch (Exception e) {}
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	};
}
