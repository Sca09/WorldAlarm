package com.worldalarm.activities;

import java.util.HashMap;
import java.util.TimeZone;

import android.app.Activity;
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
import com.worldalarm.db.AlarmSet;

public class MainActivity extends Activity implements View.OnClickListener {

	HashMap<String, String> timeZonesNames = new HashMap<String, String>();
	TimePicker timePicker;
	AutoCompleteTextView cityPickerAutoComplete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.initTimePicker();
		this.fillCityPickerAutoComplete();
		
		findViewById(R.id.setAlarmButton).setOnClickListener(this);
	}

	private void initTimePicker() {
		timePicker = (TimePicker) findViewById(R.id.alarmPicker);
//		timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
		

	}

	private void fillCityPickerAutoComplete() {
		String[] availableIDs = TimeZone.getAvailableIDs();
		
		for(String ID : availableIDs) {
			String cityName = ID.substring(ID.indexOf("/") + 1);
			timeZonesNames.put(cityName.replaceAll("_", " "), ID);
		}
		
		String[] cities = timeZonesNames.keySet().toArray(new String[timeZonesNames.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);
        cityPickerAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityPickerAutoComplete);
        cityPickerAutoComplete.setAdapter(adapter);
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
		String timeZonePicked 	= timeZonesNames.get(cityPicked);
		
		AlarmSet alarmSet = new AlarmSet(hourPicked, minutePicked, cityPicked, timeZonePicked);
		
    	Log.d("MainActivity", alarmSet.toString());
		
    	Toast toastAlert = Toast.makeText(this, alarmSet.toString(), Toast.LENGTH_LONG);
		toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
		toastAlert.show();
	}

	public AlarmSet getAlarmSet() {
		
		int hourPicked = -1;
		int minutePicked = -1;
		String cityPicked = "";
		String timeZonePicked = "";
		
		hourPicked = timePicker.getCurrentHour();
		minutePicked = timePicker.getCurrentMinute();
		
		cityPicked = cityPickerAutoComplete.getText().toString();
		if(cityPicked != null && cityPicked.length() > 0) {
			timeZonePicked = timeZonesNames.get(timeZonePicked);
		}
		
		AlarmSet alarmSet = new AlarmSet(hourPicked, minutePicked, cityPicked, timeZonePicked);
		
		return alarmSet;
	}
}
