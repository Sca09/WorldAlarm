package com.worldalarm.activities;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;

import com.worldalarm.R;
import com.worldalarm.adapters.ExpandableListAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.TimeZonePreferences;

public class ExpandableAlarmsActivity extends Activity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

	HashMap<String, List<Alarm>> listAlarms;
	List<String> listTimeZones;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable_alarms);
		
		expListView = (ExpandableListView) findViewById(R.id.expandableAlarmsView);
		
		this.getAllAlarmsByTZName();
		this.getAllTimeZones();
		
		if(this.listTimeZones != null && this.listAlarms != null) {
			listAdapter = new ExpandableListAdapter(this, listTimeZones, listAlarms);

			expListView.setAdapter(listAdapter);
		}
		
		this.expandAll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.expandable_alarms, menu);
		return true;
	}

	public void getAllAlarmsByTZName() {
		this.listAlarms = AlarmPreferences.getAlarmsByTZInstance(this);
	}
	
	private void getAllTimeZones() {
		this.listTimeZones = TimeZonePreferences.getAllTimeZones(this);
	}
	
	public void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i=0; i<count ; i++){
			expListView.expandGroup(i);
			System.out.println("3 - groupPosition["+ i +"] - Expanded["+  expListView.isGroupExpanded(i) +"]");
		}
	}
	
}
