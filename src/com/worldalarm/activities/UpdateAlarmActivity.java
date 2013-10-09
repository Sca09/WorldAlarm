package com.worldalarm.activities;

import java.util.Calendar;
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

public class UpdateAlarmActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.UpdateAlarmListener, CityDatabaseHelper.OnRetrievedAllCitiesListener, CityDatabaseHelper.OnAddedCityListener, CityDatabaseHelper.OnFoundCityByNameListener {

	HashMap<String, String> cityTimeZonesNames = new HashMap<String, String>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	Alarm alarm;
	
	String currentCity;
	String currentTimeZone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_alarm);
		
		Intent intent = this.getIntent();
		alarm = (Alarm) intent.getSerializableExtra("alamToUpdate");
		
		this.initTimePicker();
//		CityDatabaseHelper.getInstance(this).getAllCitiesAsync(this);
		CityDatabaseHelper.getAllCities(this, this);
		
		this.getCurrentCityLocation();
		
		findViewById(R.id.setAlarmButton).setOnClickListener(this);
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
			this.updateAlarm(view);
			break;
		}
	}
	
	private void initTimePicker() {
		timePicker = (TimePicker) findViewById(R.id.alarmPicker);
//		timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
		
		timePicker.setCurrentHour(alarm.getCalendar().get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(alarm.getCalendar().get(Calendar.MINUTE));
		
	}

	public void updateAlarm(View view) {
		
		int hourPicked 			= timePicker.getCurrentHour();
		int minutePicked 		= timePicker.getCurrentMinute();	
		String cityPicked 		= cityPickerAutoComplete.getText().toString();
		String timeZonePicked 	= "";
		if(cityPicked.equals("")) { //User didn't pick a city > Using the current one
			cityPicked = currentCity;
			
			timeZonePicked 	= cityTimeZonesNames.get(cityPicked);
			if(timeZonePicked == null || timeZonePicked.equals("")) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				timeZonePicked = currentTimeZone;
				
				CityDatabaseHelper.getInstance(this).addCityAsync(cityPicked, timeZonePicked, this);
			}
			
			Alarm alarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
	    	this.alarm.setCalendar(alarm.getCalendar());
	    	this.alarm.setCity(alarm.getCity());
	    	this.alarm.setTimeZone(alarm.getTimeZone());
	    	AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
			
		} else {
			timeZonePicked 	= cityTimeZonesNames.get(cityPicked);
			
			if(timeZonePicked == null || timeZonePicked == "") { //City chosen is not in TZ database
				CityDatabaseHelper.getInstance(this).searchCityByNameAsync(cityPicked, this);
					
			} else {
				Alarm alarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
				this.alarm.setCalendar(alarm.getCalendar());
				this.alarm.setCity(alarm.getCity());
				this.alarm.setTimeZone(alarm.getTimeZone());
				AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
			}
		}
	}

	@Override
	public void updateAlarm(Alarm alarm) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("alamUpdated", alarm);
		
		setResult(RESULT_OK, returnIntent);     
		finish();
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
	public void onRetrievedAllCities(HashMap<String, String> cities) {
		cityTimeZonesNames = cities;
		
		String[] citiesArray = cityTimeZonesNames.keySet().toArray(new String[cityTimeZonesNames.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, citiesArray);
        cityPickerAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityPickerAutoComplete);
        cityPickerAutoComplete.setAdapter(adapter);
        if(currentCity != null && currentCity.length() > 0) {
        	cityPickerAutoComplete.setHint(currentCity +" "+ getString(R.string.by_default));
        } else {
        	cityPickerAutoComplete.setHint(R.string.choose_city);
        }
        
        cityPickerAutoComplete.setText(alarm.getCity());
        cityPickerAutoComplete.clearFocus();
	}

	@Override
	public void onAddedCity(String[] data) {
		// Nothing to do here
	}

	@Override
	public void onFoundCityByName(String[] data) {
		if(data != null) {
			Alarm alarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), data[0], data[1]);
			this.alarm.setCalendar(alarm.getCalendar());
			this.alarm.setCity(alarm.getCity());
			this.alarm.setTimeZone(alarm.getTimeZone());
			AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
		} else {
			Toast toastAlert = Toast.makeText(this, "City not found, please try again", Toast.LENGTH_LONG);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		}
	}
}
