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
import com.worldalarm.db.City;
import com.worldalarm.db.CityDatabaseHelper;

public class UpdateAlarmActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.UpdateAlarmListener, CityDatabaseHelper.OnRetrievedAllCitiesListener, CityDatabaseHelper.OnAddedCityListener, CityDatabaseHelper.OnFoundCityByNameListener {

	HashMap<String, City> cityTimeZonesNames = new HashMap<String, City>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	Alarm alarm;
	
	City currentCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_alarm);
		
		this.getCurrentCityLocation();
		
		Intent intent = this.getIntent();
		alarm = (Alarm) intent.getSerializableExtra("alamToUpdate");
		
		this.initTimePicker();
		CityDatabaseHelper.getAllCities(this, this);
		
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
		if(cityPicked.equals("")) { //User didn't pick a city > Using the current one
			cityPicked = currentCity.getCityName();
			
			City city = cityTimeZonesNames.get(cityPicked);
			
			if(city == null) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				CityDatabaseHelper.getInstance(this).addCityAsync(city, this);
			}
			
			Alarm alarm = new Alarm(hourPicked, minutePicked, city);
	    	this.alarm.setCalendar(alarm.getCalendar());
	    	this.alarm.setCity(alarm.getCity());
	    	AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
			
		} else {
			City city = cityTimeZonesNames.get(cityPicked);
			
			if(city == null) { //City chosen is not in TZ database
				CityDatabaseHelper.getInstance(this).searchCityByNameAsync(cityPicked, this);
					
			} else {
				Alarm alarm = new Alarm(hourPicked, minutePicked, city);
				this.alarm.setCalendar(alarm.getCalendar());
				this.alarm.setCity(alarm.getCity());
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
			String currentTimeZoneID = TimeZone.getDefault().getID();
			String currentCityNameNotFormatted = currentTimeZoneID.substring(currentTimeZoneID.lastIndexOf("/") + 1);
			String currentCityName = currentCityNameNotFormatted.replaceAll("_", " ");
			
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
					currentCityName = addresses.get(0).getLocality();
				}
			} else {
				if (gpsEnabled)
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        if (networkEnabled)
		        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			}
			
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZoneID);
			String currentTimeZoneName = timeZone.getDisplayName();
			
			currentCity = new City(currentCityName, currentTimeZoneID, currentTimeZoneName);
			

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
					String currentCityName = addresses.get(0).getLocality();
					String currentTimeZoneID = TimeZone.getDefault().getID();
					
					TimeZone timeZone = TimeZone.getTimeZone(currentTimeZoneID);
					String currentTimeZoneName = timeZone.getDisplayName();
					
					currentCity = new City(currentCityName, currentTimeZoneID, currentTimeZoneName);
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
	public void onRetrievedAllCities(HashMap<String, City> cities) {
		cityTimeZonesNames = cities;
		
		String[] citiesArray = cityTimeZonesNames.keySet().toArray(new String[cityTimeZonesNames.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, citiesArray);
        cityPickerAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityPickerAutoComplete);
        cityPickerAutoComplete.setAdapter(adapter);
        if(currentCity != null && currentCity.getCityName() != null && currentCity.getCityName().length() > 0) {
        	cityPickerAutoComplete.setHint(currentCity.getCityName() +" "+ getString(R.string.by_default));
        } else {
        	cityPickerAutoComplete.setHint(R.string.choose_city);
        }
        
        Log.d("UpdateAlarmActivity", "hint["+ cityPickerAutoComplete.getHint() +"]");
        
        cityPickerAutoComplete.setText(alarm.getCity().getCityName());
        cityPickerAutoComplete.clearFocus();
	}

	@Override
	public void onAddedCity(City city) {
		// Nothing to do here
	}

	@Override
	public void onFoundCityByName(City city) {
		if(city != null) {
			Alarm alarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), city);
			this.alarm.setCalendar(alarm.getCalendar());
			this.alarm.setCity(alarm.getCity());
			AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
		} else {
			Toast toastAlert = Toast.makeText(this, "City not found, please try again", Toast.LENGTH_LONG);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		}
	}
}
