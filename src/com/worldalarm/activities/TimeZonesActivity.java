package com.worldalarm.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.CityDatabaseHelper;
import com.worldalarm.db.CityDatabaseHelper.OnRetrievedTimeZoneNamesListener;
import com.worldalarm.db.TimeZoneDatabaseHelper;
import com.worldalarm.fragments.TimeZonesDialogFragment;
import com.worldalarm.fragments.TimeZonesDialogFragment.OnAddTimeZoneListener;

public class TimeZonesActivity extends FragmentActivity implements View.OnClickListener, TimeZoneDatabaseHelper.OnRetrievedAllTimeZonesListener, TimeZoneDatabaseHelper.OnAddedTimeZoneListener {

	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_TIME_ZONE = 7000;
	
	SimpleAdapter mAdapter;
	List<String> listTimeZones;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_zones);
		
		findViewById(R.id.NewAlarmButton).setOnClickListener(this);
		findViewById(R.id.NewTimeZoneButton).setOnClickListener(this);
		
		TimeZoneDatabaseHelper.getAllTimeZones(getApplicationContext(), this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.refreshTimeZoneList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_zones, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.NewAlarmButton:
			Intent newAlarmIntent = new Intent(this, NewAlarmActivity.class);
			this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			break;
			
		case R.id.NewTimeZoneButton:
			
			CityDatabaseHelper.getInstance(this).getTimeZoneNamesAsync(new OnRetrievedTimeZoneNamesListener() {
				
				@Override
				public void onRetrievedTimeZoneNames(final String[] timeZoneNames) {
					
					TimeZonesDialogFragment fragment = new TimeZonesDialogFragment();
					
					fragment.setOnAddTimeZoneListener(new OnAddTimeZoneListener() {
						
						@Override
						public Bundle getBundle() {
							Bundle bundle = new Bundle();
							bundle.putStringArray("timeZones", timeZoneNames);
							
							return bundle;
						}
					});
					
					fragment.show(getSupportFragmentManager(), "timeZones");
				}
			});
			
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:
			if (resultCode == RESULT_OK) {
				this.refreshTimeZoneList();
				
				Alarm newAlarm = (Alarm) data.getSerializableExtra("newAlarm");
				
				Intent listAlarmsIntent = new Intent(getApplicationContext(), ListAlarmsSwipeViewActivity.class).putExtra("timeZoneSelected", newAlarm.getCity().getTimeZoneName());
				startActivity(listAlarmsIntent);
				
			}
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_NEW_TIME_ZONE:
			// TODO: to get new timeZone from Dialog
			TimeZoneDatabaseHelper.getInstance(this).addTimeZoneAsync("", this);
			break;
		}
	}

	@Override
	public void OnRetrievedAllTimeZones(List<String> listTimeZones) {
		
		this.listTimeZones = listTimeZones;
		
		this.refreshTimeZoneList();
	}
	
	@Override
	public void OnAddedTimeZone(List<String> listTimeZones) {
		
		this.listTimeZones = listTimeZones;
		
		this.refreshTimeZoneList();
		
	}
	
	private void refreshTimeZoneList() {
		List<Map<String, String>> timeZoneList = new ArrayList<Map<String,String>>();
		
		if(listTimeZones != null) {
			for(String timeZone : listTimeZones) {
				HashMap<String, String> aux = new HashMap<String, String>();
				aux.put("timeZone", timeZone);
			
				timeZoneList.add(aux);
			}
			
			mAdapter = new SimpleAdapter(this, timeZoneList, android.R.layout.simple_list_item_1, new String[]{"timeZone"}, new int[]{android.R.id.text1});
			
			ListView lv = (ListView) findViewById(android.R.id.list);
			lv.setAdapter(mAdapter);
			
			lv.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					@SuppressWarnings("unchecked")
					HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
					
					Intent listAlarmsIntent = new Intent(getApplicationContext(), ListAlarmsSwipeViewActivity.class).putExtra("timeZoneSelected", map.get("timeZone"));
					
					startActivity(listAlarmsIntent);
				}
			});
		}
	}
}