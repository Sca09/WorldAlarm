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
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.AlarmDatabaseHelper.OnRetrievedAllAlarmsByTZNameListener;
import com.worldalarm.db.TimeZoneDatabaseHelper.OnRetrievedAllTimeZonesListener;

public class ExpandableAlarmsActivity extends Activity implements OnRetrievedAllAlarmsByTZNameListener, OnRetrievedAllTimeZonesListener {

	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    
    HashMap<String, List<Alarm>> listAlarms;
    List<String> listTimeZones;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable_alarms);
		
		expListView = (ExpandableListView) findViewById(R.id.expandableAlarmsView);
		
		AlarmDatabaseHelper.getAlarmsByTZInstance(this, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.expandable_alarms, menu);
		return true;
	}

	@Override
	public void onRetrievedAllAlarmsByTZName(HashMap<String, List<Alarm>> listAlarms) {
		
		this.listAlarms = listAlarms; 
		
		if(this.listTimeZones != null) {
			listAdapter = new ExpandableListAdapter(this, listTimeZones, listAlarms);

			expListView.setAdapter(listAdapter);
		}
	}

	@Override
	public void OnRetrievedAllTimeZones(List<String> listTimeZones) {
		this.listTimeZones = listTimeZones;
		
		if(this.listAlarms != null) {
			listAdapter = new ExpandableListAdapter(this, listTimeZones, listAlarms);

			expListView.setAdapter(listAdapter);			
		}
	}

}
