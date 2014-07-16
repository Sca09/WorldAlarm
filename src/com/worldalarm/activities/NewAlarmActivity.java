package com.worldalarm.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.worldalarm.R;
import com.worldalarm.broadcast.AlarmManagerBroadcastReceiver;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.City;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.CityPreferences;
import com.worldalarm.preferences.SearchCityByNameTaskData;
import com.worldalarm.preferences.TimeZonePreferences;
import com.worldalarm.preferences.UserPreferences;

public class NewAlarmActivity extends Activity implements View.OnClickListener, SearchCityByNameTaskData.OnFoundCityByNameListener {

	HashMap<String, City> cityTimeZonesNames = new HashMap<String, City>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	City currentCity;

	private AlarmManagerBroadcastReceiver alarmManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_alarm);
		
		this.getCurrentCityLocation();
		
		this.initTimePicker();
		this.getAllCities();
		
		findViewById(R.id.setAlarmButton).setOnClickListener(this);
		findViewById(R.id.cancelButton).setOnClickListener(this);
		
		findViewById(R.id.repeat_day_toggle_sun).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_mon).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_tue).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_wed).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_thu).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_fri).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_sat).setOnClickListener(this);
		
		alarmManager = new AlarmManagerBroadcastReceiver();
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
			
		case R.id.cancelButton:
			finish();
			break;
			
		case R.id.repeat_day_toggle_sun:
		case R.id.repeat_day_toggle_mon:
		case R.id.repeat_day_toggle_tue:
		case R.id.repeat_day_toggle_wed:
		case R.id.repeat_day_toggle_thu:
		case R.id.repeat_day_toggle_fri:
		case R.id.repeat_day_toggle_sat:
			ToggleButton button = (ToggleButton) view;
			
			if(button.isChecked()) {
				button.setTypeface(Typeface.DEFAULT_BOLD);
			} else {
				button.setTypeface(Typeface.DEFAULT);
			}
			
			break;
		}
	}
	
	public void setAlarm(final View view) {
		int hourPicked = timePicker.getCurrentHour();
		int minutePicked = timePicker.getCurrentMinute();	
		String cityPicked = cityPickerAutoComplete.getText().toString();
		List<Integer> repeatDays = this.getCheckedRepeatDays();
		if(cityPicked.equals("")) { //User didn't pick a city > Using the current one
			cityPicked = currentCity.getCityName();
			
			City city = cityTimeZonesNames.get(cityPicked);
			if(city == null) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				cityTimeZonesNames = CityPreferences.addCity(currentCity, this);
				city = cityTimeZonesNames.get(cityPicked);
			}
			
			Alarm newAlarm = new Alarm(hourPicked, minutePicked, city);
			newAlarm.setRepeatDays(repeatDays);
			this.saveAlarm(newAlarm);
			

		} else {
			City city = cityTimeZonesNames.get(cityPicked);
			
			if(city == null) { //City chosen is not in TZ database
				SearchCityByNameTaskData task = new SearchCityByNameTaskData(this, this);
				task.execute(cityPicked);
			} else {
				Alarm newAlarm = new Alarm(hourPicked, minutePicked, city);
				newAlarm.setRepeatDays(repeatDays);
				this.saveAlarm(newAlarm);
			}
		}
	}

	private List<Integer> getCheckedRepeatDays() {
		List<Integer> repeatDays = new ArrayList<Integer>();
		
		ToggleButton sun = (ToggleButton) findViewById(R.id.repeat_day_toggle_sun);
		if(sun.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_SUN);
		}
		
		ToggleButton mon = (ToggleButton) findViewById(R.id.repeat_day_toggle_mon);
		if(mon.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_MON);
		}
		
		ToggleButton tue = (ToggleButton) findViewById(R.id.repeat_day_toggle_tue);
		if(tue.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_TUE);
		}
		
		ToggleButton wed = (ToggleButton) findViewById(R.id.repeat_day_toggle_wed);
		if(wed.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_WED);
		}
		
		ToggleButton thu = (ToggleButton) findViewById(R.id.repeat_day_toggle_thu);
		if(thu.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_THU);
		}
		
		ToggleButton fri = (ToggleButton) findViewById(R.id.repeat_day_toggle_fri);
		if(fri.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_FRI);
		}
		
		ToggleButton sat = (ToggleButton) findViewById(R.id.repeat_day_toggle_sat);
		if(sat.isChecked()) {
			repeatDays.add(Alarm.REPEAT_DAY_SAT);
		}
		
		return repeatDays;
	}
	
	private void getCurrentCityLocation() {
		currentCity = UserPreferences.getUserInstance(this).getCurrentCity();
		
//		try {
//			String currentTimeZoneID = TimeZone.getDefault().getID();
//			String currentCityNameNotFormatted = currentTimeZoneID.substring(currentTimeZoneID.lastIndexOf("/") + 1);
//			String currentCityName = currentCityNameNotFormatted.replaceAll("_", " ");
//			
//			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//						
//			boolean gpsEnabled = false;
//			boolean networkEnabled = false;
//			try {
//				gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//			} catch (Exception ex) {}
//			try {
//				networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//			} catch (Exception ex) {}
//						
//			Location location = null;
//			if (gpsEnabled)
//				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			if (networkEnabled)
//				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//			Geocoder gcd = new Geocoder(this, Locale.getDefault());
//			
//			if(location != null) {
//				double latitude = location.getLatitude();
//				double longitude = location.getLongitude();
//
//				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
//				if (addresses.size() > 0) { 
//					currentCityName = addresses.get(0).getLocality();
//				}
//			} else {
//				if (gpsEnabled)
//					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//				if (networkEnabled)
//					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//			}
//			
//			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZoneID);
//			String currentTimeZoneName = timeZone.getDisplayName();
//			
//			currentCity = new City(currentCityName, currentTimeZoneID, currentTimeZoneName);
//		} catch (Exception e) {
//			Log.d("NewAlarmActivity", "No location obtained from device");
//		}
	}

//	LocationListener locationListener = new LocationListener() {
//
//		@Override
//		public void onLocationChanged(Location location) {
//			double latitude = location.getLatitude();
//			double longitude = location.getLongitude();
//
//			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			Geocoder gcd = new Geocoder(getApplicationContext(), Locale.ENGLISH);
//			
//			locationManager.removeUpdates(this);
//			
//			try {
//				List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
//				
//				if (addresses.size() > 0) { 
//					String currentCityName = addresses.get(0).getLocality();
//					String currentTimeZoneID = TimeZone.getDefault().getID();
//					
//					TimeZone timeZone = TimeZone.getTimeZone(currentTimeZoneID);
//					String currentTimeZoneName = timeZone.getDisplayName();
//					
//					currentCity = new City(currentCityName, currentTimeZoneID, currentTimeZoneName);
//				}
//			} catch (Exception e) {}
//		}
//		
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {}
//		
//		@Override
//		public void onProviderEnabled(String provider) {}
//
//		@Override
//		public void onProviderDisabled(String provider) {}
//	};

	public void getAllCities() {
		cityTimeZonesNames = CityPreferences.getCitiesInstance(this);
		
		String[] citiesArray = cityTimeZonesNames.keySet().toArray(new String[cityTimeZonesNames.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, citiesArray);
		cityPickerAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityPickerAutoComplete);
		cityPickerAutoComplete.setAdapter(adapter);
		if(currentCity != null && currentCity.getCityName() != null && currentCity.getCityName().length() > 0) {
			cityPickerAutoComplete.setHint(currentCity.getCityName() +" "+ getString(R.string.by_default));
		} else {
			cityPickerAutoComplete.setHint(R.string.choose_city);
		}
	}

	@Override
	public void onFoundCityByName(final List<City> cityList) {
		if(cityList != null && cityList.size() == 1) {
			Alarm newAlarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), cityList.get(0));
			this.saveAlarm(newAlarm);
		} else {
			if(cityList.size() > 0) {
				
				final String[] cityKeys = new String[cityList.size()];
				int i = 0;
				for(City city : cityList) {
					cityKeys[i] = city.getCityName();
					i++;
				}
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle("Choose City");
				alertDialog.setItems(cityKeys, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String cityName = cityKeys[which];
						
						for(City city : cityList) {
							if(city.getCityName().equals(cityName)) {
								Alarm newAlarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), city);
								NewAlarmActivity.this.saveAlarm(newAlarm);
							}
						}
					}
				});
				
				alertDialog.show();
			}
		}	
	}
	
	public void saveAlarm(Alarm alarm) {
		alarmManager.setOnetimeTimer(this.getApplicationContext(), alarm);
		
		this.addAlarmPreference(alarm);
		this.addTimeZone(alarm.getCity().getTimeZoneName());
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("newAlarm", alarm);
		
		setResult(RESULT_OK, returnIntent);

		finish();
	}
	
	public void addAlarmPreference(Alarm alarm) {
		AlarmPreferences.addAlarm(alarm, this);
	}
	
	public void addTimeZone(String timeZone) {
		TimeZonePreferences.addTimeZone(timeZone, this);
	}
}
