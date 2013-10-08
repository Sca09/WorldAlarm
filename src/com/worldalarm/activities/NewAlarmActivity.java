package com.worldalarm.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.CityDatabaseHelper;

public class NewAlarmActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.SaveAlarmListener, CityDatabaseHelper.AllCitiesListener, CityDatabaseHelper.AddCityListener, CityDatabaseHelper.FoundCityByNameListener {

	HashMap<String, String> timeZonesNames = new HashMap<String, String>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	String currentCity;
	String currentTimeZone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		
		this.getCurrentCityLocation();
		
		this.initTimePicker();
		CityDatabaseHelper.getInstance(this).getAllCitiesAsync(this);
		
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
			cityPicked = currentCity;
			
			timeZonePicked 	= timeZonesNames.get(cityPicked);
			if(timeZonePicked == null || timeZonePicked.equals("")) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				timeZonePicked = currentTimeZone;
				
				CityDatabaseHelper.getInstance(this).addCityAsync(cityPicked, timeZonePicked, this);
			}
			
			Alarm newAlarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
	    	AlarmDatabaseHelper.getInstance(this).saveAlarmAsync(newAlarm, this);
	    	
		} else {
			timeZonePicked 	= timeZonesNames.get(cityPicked);
			
			if(timeZonePicked == null || timeZonePicked == "") { //City chosen is not in TZ database
				CityDatabaseHelper.getInstance(this).searchCityByNameAsync(cityPicked, this);
				
			} else {
				Alarm newAlarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
		    	AlarmDatabaseHelper.getInstance(this).saveAlarmAsync(newAlarm, this);
			}
		}
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
        if(currentCity != null && currentCity.length() > 0) {
        	cityPickerAutoComplete.setHint(currentCity +" "+ getString(R.string.by_default));
        } else {
        	cityPickerAutoComplete.setHint(R.string.choose_city);
        }
	}

	private void getCurrentCityLocation() {		
		try {
			currentTimeZone = TimeZone.getDefault().getID();
			String cityName = currentTimeZone.substring(currentTimeZone.lastIndexOf("/") + 1);
			currentCity = cityName.replaceAll("_", " ");
			
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						
			boolean gpsEnabled = false;
		    boolean networkEnabled = false;
			try {
				gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        } catch (Exception ex) {}
	        try {
	        	networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        } catch (Exception ex) {}
						
			Location location = null;
	        if (gpsEnabled)
	        	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        if (networkEnabled)
	        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


			Geocoder gcd = new Geocoder(this, Locale.getDefault());
			
			if(location != null) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
        
				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
				if (addresses.size() > 0) { 
					currentCity = addresses.get(0).getLocality();
				}
			} else {
				if (gpsEnabled)
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        if (networkEnabled)
		        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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

	@Override
	public void addCityByName(String[] data) {
		if(data != null) {
			Alarm newAlarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), data[0], data[1]);
			AlarmDatabaseHelper.getInstance(getApplicationContext()).saveAlarmAsync(newAlarm, this);
		} else {
			Toast toastAlert = Toast.makeText(this, "City not found, please try again", Toast.LENGTH_LONG);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		}
		
	}
	
	
}
