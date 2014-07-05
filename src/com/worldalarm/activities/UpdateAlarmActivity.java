package com.worldalarm.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.worldalarm.R;
import com.worldalarm.broadcast.AlarmManagerBroadcastReceiver;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.City;
import com.worldalarm.fragments.DeleteAlarmConfirmDialogFragment;
import com.worldalarm.fragments.DeleteAlarmConfirmDialogFragment.OnDeleteAlarmDialogListener;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.CityPreferences;
import com.worldalarm.preferences.SearchCityByNameTaskData;
import com.worldalarm.preferences.TimeZonePreferences;
import com.worldalarm.preferences.UserPreferences;

public class UpdateAlarmActivity extends FragmentActivity implements View.OnClickListener, SearchCityByNameTaskData.OnFoundCityByNameListener {

	HashMap<String, City> cityTimeZonesNames = new HashMap<String, City>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	Alarm alarm;
	
	City currentCity;
	
	private AlarmManagerBroadcastReceiver alarmManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_alarm);
		
		this.getCurrentCityLocation();
		
		Intent intent = this.getIntent();
		alarm = (Alarm) intent.getSerializableExtra("alamToUpdate");
		
		this.initTimePicker();
		this.getAllCities();
		
		findViewById(R.id.updateAlarmButton).setOnClickListener(this);
		findViewById(R.id.deleteButton).setOnClickListener(this);
		
		findViewById(R.id.repeat_day_toggle_sun).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_mon).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_tue).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_wed).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_thu).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_fri).setOnClickListener(this);
		findViewById(R.id.repeat_day_toggle_sat).setOnClickListener(this);
		
		alarmManager = new AlarmManagerBroadcastReceiver();
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
		case R.id.updateAlarmButton:
			this.updateAlarm(view);
			break;
		
		case R.id.deleteButton:
			DeleteAlarmConfirmDialogFragment dialog = new DeleteAlarmConfirmDialogFragment();
			
			dialog.setOnDeleteAlarmDialogListener(new OnDeleteAlarmDialogListener() {
				
				@Override
				public Bundle getBundle() {
					Bundle bundle = new Bundle();
					bundle.putSerializable("alarmToDelete", alarm);
					
					return bundle;
				}
			});

			dialog.show(getSupportFragmentManager().beginTransaction(), "deleteAlarm");
			
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
	
	private void initTimePicker() {
		timePicker = (TimePicker) findViewById(R.id.alarmPicker);
		timePicker.setCurrentHour(alarm.getHour());
		timePicker.setCurrentMinute(alarm.getMinute());
		
		ToggleButton repeatDay_Sun = (ToggleButton) findViewById(R.id.repeat_day_toggle_sun);
		ToggleButton repeatDay_Mon = (ToggleButton) findViewById(R.id.repeat_day_toggle_mon);
		ToggleButton repeatDay_Tue = (ToggleButton) findViewById(R.id.repeat_day_toggle_tue);
		ToggleButton repeatDay_Wed = (ToggleButton) findViewById(R.id.repeat_day_toggle_wed);
		ToggleButton repeatDay_Thu = (ToggleButton) findViewById(R.id.repeat_day_toggle_thu);
		ToggleButton repeatDay_Fri = (ToggleButton) findViewById(R.id.repeat_day_toggle_fri);
		ToggleButton repeatDay_Sat = (ToggleButton) findViewById(R.id.repeat_day_toggle_sat);
		
		List<Integer> repeatDays = this.alarm.getRepeatDays();
		
		for(Integer day : repeatDays) {
			if(Alarm.REPEAT_DAY_SUN.equals(day)) {
				repeatDay_Sun.setChecked(true);
				repeatDay_Sun.setTypeface(Typeface.DEFAULT_BOLD);
			} else if(Alarm.REPEAT_DAY_MON.equals(day)) {
				repeatDay_Mon.setChecked(true);
				repeatDay_Mon.setTypeface(Typeface.DEFAULT_BOLD);
			} if(Alarm.REPEAT_DAY_TUE.equals(day)) {
				repeatDay_Tue.setChecked(true);
				repeatDay_Tue.setTypeface(Typeface.DEFAULT_BOLD);
			} else if(Alarm.REPEAT_DAY_WED.equals(day)) {
				repeatDay_Wed.setChecked(true);
				repeatDay_Wed.setTypeface(Typeface.DEFAULT_BOLD);
			} else if(Alarm.REPEAT_DAY_THU.equals(day)) {
				repeatDay_Thu.setChecked(true);
				repeatDay_Thu.setTypeface(Typeface.DEFAULT_BOLD);
			} else if(Alarm.REPEAT_DAY_FRI.equals(day)) {
				repeatDay_Fri.setChecked(true);
				repeatDay_Fri.setTypeface(Typeface.DEFAULT_BOLD);
			} else if(Alarm.REPEAT_DAY_SAT.equals(day)) {
				repeatDay_Sat.setChecked(true);
				repeatDay_Sat.setTypeface(Typeface.DEFAULT_BOLD);
			}
		}
	}

	public void updateAlarm(View view) {
		int hourPicked 			= timePicker.getCurrentHour();
		int minutePicked 		= timePicker.getCurrentMinute();	
		String cityPicked 		= cityPickerAutoComplete.getText().toString();
		List<Integer> repeatDays = this.getCheckedRepeatDays();
		if(cityPicked.equals("")) { //User didn't pick a city > Using the current one
			cityPicked = currentCity.getCityName();
			
			City city = cityTimeZonesNames.get(cityPicked);
			
			if(city == null) { //Current city is not in TZ database > using default TZ and saving new pair city-TZ
				cityTimeZonesNames = CityPreferences.addCity(currentCity, this);
				
				city = cityTimeZonesNames.get(cityPicked);
			}
			
			Alarm alarm = new Alarm(hourPicked, minutePicked, city);
			this.alarm.setHour(alarm.getHour());
			this.alarm.setMinute(alarm.getMinute());
			if(this.alarm.getCity() != alarm.getCity()) {
				this.alarm.setCity(alarm.getCity());
				this.alarm.setCityPicUrl(null);
			}
			this.alarm.setRepeatDays(repeatDays);
			this.alarm.setActive(Boolean.TRUE);
			this.updateAlarm(this.alarm);
			
		} else {
			City city = cityTimeZonesNames.get(cityPicked);
			
			if(city == null) { //City chosen is not in TZ database
				SearchCityByNameTaskData task = new SearchCityByNameTaskData(this);
				task.execute(cityPicked);
					
			} else {
				Alarm alarm = new Alarm(hourPicked, minutePicked, city);
				this.alarm.setHour(alarm.getHour());
				this.alarm.setMinute(alarm.getMinute());
				if(this.alarm.getCity() != alarm.getCity()) {
					this.alarm.setCity(alarm.getCity());
					this.alarm.setCityPicUrl(null);
				}
				this.alarm.setRepeatDays(repeatDays);
				this.alarm.setActive(Boolean.TRUE);
				this.updateAlarm(this.alarm);
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
//			
//
//		} catch (Exception e) {
//	
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
		
		cityPickerAutoComplete.setText(alarm.getCity().getCityName());
		cityPickerAutoComplete.clearFocus();
	}

	@Override
	public void onFoundCityByName(City city) {
		if(city != null) {
			Alarm alarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), city);
			this.alarm.setHour(alarm.getHour());
			this.alarm.setMinute(alarm.getMinute());
			if(this.alarm.getCity() != alarm.getCity()) {
				this.alarm.setCity(alarm.getCity());
				this.alarm.setCityPicUrl(null);
			}
			this.alarm.setActive(Boolean.TRUE);
			this.updateAlarm(this.alarm);
		} else {
			Toast toastAlert = Toast.makeText(this, "City not found, please try again", Toast.LENGTH_LONG);
			toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
			toastAlert.show();
		}
	}

	public void updateAlarm(Alarm alarm) {
		alarmManager.setOnetimeTimer(this.getApplicationContext(), alarm);
		
		this.updateAlarmPreference(alarm);
		this.addTimeZone(alarm.getCity().getTimeZoneName());
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("alamUpdated", alarm);
		
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	private void updateAlarmPreference(Alarm Alarm) {
		AlarmPreferences.updateAlarm(alarm, this);
	}
	
	private void addTimeZone(String timeZone) {
		TimeZonePreferences.addTimeZone(timeZone, this);
	}
}
