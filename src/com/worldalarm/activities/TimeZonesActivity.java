package com.worldalarm.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.mobeta.android.dslv.DragSortListView;
import com.worldalarm.R;
import com.worldalarm.adapters.TimeZoneAdapter;
import com.worldalarm.fragments.TimeZonesDialogFragment;
import com.worldalarm.fragments.TimeZonesDialogFragment.OnAddTimeZoneListener;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.TimeZonePreferences;

public class TimeZonesActivity extends FragmentActivity implements View.OnClickListener {
	
	private ArrayAdapter<String> adapter;
	private DragSortListView listView;
	
	List<String> listTimeZones = new ArrayList<String>();
	List<String> listTimeZonesToSave = new ArrayList<String>();
	List<String> listTimeZonesToDelete = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_zones);
		
		listView = (DragSortListView)findViewById(android.R.id.list);
		
		findViewById(R.id.saveTimeZoneConf).setOnClickListener(this);
		findViewById(R.id.cancelTimeZoneConf).setOnClickListener(this);

		this.getAllTimeZones();
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
		case R.id.saveTimeZoneConf:
			TimeZonePreferences.saveTimeZone(listTimeZonesToSave, this);
			if(listTimeZonesToDelete.size() > 0) {
				AlarmPreferences.deleteAlarmByTZ(listTimeZonesToDelete, this);
			}
			finish();
			break;
		case R.id.cancelTimeZoneConf:
			finish();
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			
			TimeZonesDialogFragment fragment = new TimeZonesDialogFragment();
			
			fragment.setOnAddTimeZoneListener(new OnAddTimeZoneListener() {
				
				@Override
				public Bundle getBundle() {
					
					String[] availableIDs = TimeZone.getAvailableIDs();
					
					List<String> timeZoneNames = new ArrayList<String>();
					
					for(String tzId : availableIDs) {
						
						String tzName = TimeZone.getTimeZone(tzId).getDisplayName();
						
						if(!timeZoneNames.contains(tzName)) {
							timeZoneNames.add(tzName);
						}
					}
					
					String[] timeZonesArray = new String[timeZoneNames.size()];
					timeZonesArray = timeZoneNames.toArray(timeZonesArray);
					
					Bundle bundle = new Bundle();
					bundle.putStringArray("timeZones", timeZonesArray);
					
					return bundle;
				}
			});
			
			fragment.show(getSupportFragmentManager(), "timeZones");
			
			break;
			
		default:
			break;
		}
		
		return true;
	}

	
	public void getAllTimeZones() {
		this.listTimeZones = TimeZonePreferences.getAllTimeZones(this);
		
		this.listTimeZonesToSave.addAll(listTimeZones);
		
		this.refreshTimeZoneList();
	}
	
	public void addTimeZone(String timeZone) {
		if(!listTimeZonesToSave.contains(timeZone)) {
			listTimeZonesToSave.add(timeZone);
		}
		
		this.refreshTimeZoneList();
	}
	
	private void refreshTimeZoneList() {
		if(listTimeZonesToSave != null) {
			String[] data;
			
			if(listTimeZonesToSave != null) {
				data = new String[listTimeZonesToSave.size()];
				
				int i = 0;
				for(String timeZone : listTimeZonesToSave) {
					data[i] = timeZone;
					
					i++;
				}
			} else {
				data = new String[0];
			}
			
			adapter = new TimeZoneAdapter(this, R.layout.time_zone_row, data);
	
			listView.setDropListener(onDrop);
			listView.setRemoveListener(onRemove);
			listView.setDragScrollProfile(ssProfile);
			
//	        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_right, R.id.text, listTimeZones);
	        adapter = new ArrayAdapter<String>(this, R.layout.list_item_handle_left, R.id.text, listTimeZonesToSave);

	        listView.setAdapter(adapter);
		}
	}
	
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item=adapter.getItem(from);

			adapter.notifyDataSetChanged();
			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			listTimeZonesToDelete.add(adapter.getItem(which));
			adapter.remove(adapter.getItem(which));
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};
}