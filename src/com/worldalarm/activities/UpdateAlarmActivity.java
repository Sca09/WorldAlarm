package com.worldalarm.activities;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.CityDatabaseHelper;

public class UpdateAlarmActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.UpdateAlarmListener, CityDatabaseHelper.AllCitiesListener {

	HashMap<String, String> timeZonesNames = new HashMap<String, String>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	Alarm alarm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_alarm);
		
		Intent intent = this.getIntent();
		alarm = (Alarm) intent.getSerializableExtra("alamToUpdate");
		
		this.initTimePicker();
		CityDatabaseHelper.getInstance(this).getAllCitiesAsync(this);
		
		findViewById(R.id.setAlarmButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_alarm, menu);
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
		String timeZonePicked 	= timeZonesNames.get(cityPicked);
		    	
    	Alarm alarm = new Alarm(hourPicked, minutePicked, cityPicked, timeZonePicked);
    	this.alarm.setCalendar(alarm.getCalendar());
    	this.alarm.setCity(alarm.getCity());
    	this.alarm.setTimeZone(alarm.getTimeZone());
    	AlarmDatabaseHelper.getInstance(this).updateAlarmAsync(this.alarm, this);
	}

	@Override
	public void updateAlarm(Alarm alarm) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("alamUpdated", alarm);
		
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
        
        cityPickerAutoComplete.setText(alarm.getCity());
        cityPickerAutoComplete.clearFocus();
	}
}
