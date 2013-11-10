package com.worldalarm.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.worldalarm.R;
import com.worldalarm.adapters.AlarmAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;

public class ListAlarmsActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.OnRetrievedAllAlarmsListener {

	private ListView listAlarms;
	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_alarms);
						
		AlarmDatabaseHelper.getInstance(this).getAllAlarmsAsync(this);
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
		
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:			
			Intent newAlarmIntent = new Intent(this, NewAlarmActivity.class);
			this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			
			break;
		}
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:
			if (resultCode == RESULT_OK) {
				AlarmDatabaseHelper.getInstance(this).getAllAlarmsAsync(this);
			}
			
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM:
			if (resultCode == RESULT_OK) {
				AlarmDatabaseHelper.getInstance(this).getAllAlarmsAsync(this);
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onRetrievedAllAlarms(List<Alarm> listAlarm) {
		
		Alarm[] data = new Alarm[listAlarm.size()];
		
		int i = 0;
		for(Alarm alarm : listAlarm) {
			
			data[i] = alarm;
			
			i++;
		}		
		
		AlarmAdapter adapter = new AlarmAdapter(this, R.layout.alarm, data);
		
		listAlarms = new ListView(this);
		
		listAlarms.setAdapter(adapter);
		
		RelativeLayout view = (RelativeLayout)findViewById(R.layout.list_alarms);
		view.addView(listAlarms);
	}
}
