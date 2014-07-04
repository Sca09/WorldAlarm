package com.worldalarm.fragments;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldalarm.R;
import com.worldalarm.activities.ListAlarmsSwipeViewActivity;
import com.worldalarm.adapters.ExpandableListAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.TimeZonePreferences;
import com.worldalarm.utils.Constants;

public class AllAlarmsFragment extends Fragment {

	RelativeLayout rootView;

	private Activity activity;
	
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;

	HashMap<String, List<Alarm>> listAlarms;
	List<String> listTimeZones;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().registerReceiver(updateReceiver, new IntentFilter(Constants.BROADCAST_FILTER_ALARM_UPDATE));
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.activity_expandable_alarms, container, false);
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.expandableAlarmsView);

		this.getAllAlarmsByTZName();
		this.getAllTimeZones();
		
		if(this.listAlarms != null && this.listTimeZones != null) {
			listAdapter = new ExpandableListAdapter(activity, this.listTimeZones, this.listAlarms); 
			
			expListView.setAdapter(listAdapter);
			
			expListView.setOnGroupClickListener(new OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					
					expListView.expandGroup(groupPosition);
					
					((ListAlarmsSwipeViewActivity) getActivity()).openTab(listTimeZones.get(groupPosition));
					
					return true;
				}
			});
		
			this.expandAll();
		}
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.expandAll();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	public void getAllAlarmsByTZName() {
		this.listAlarms = AlarmPreferences.getAlarmsByTZInstance(getActivity());
	}
	
	public void getAllTimeZones() {
		this.listTimeZones = TimeZonePreferences.getAllTimeZones(getActivity());
	}
	
	public void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i=0; i<count ; i++){
			expListView.expandGroup(i);
		}
	}
	
	BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String updateId = intent.getStringExtra("alarmId");
			
			ExpandableListView expandableListView = (ExpandableListView) getActivity().findViewById(R.id.expandableAlarmsView);
			
			int start = expandableListView.getFirstVisiblePosition();
			for(int i=start; i<=expandableListView.getLastVisiblePosition(); i++) {
				
				View view = expandableListView.getChildAt(i-start);
				TextView textViewId = (TextView) view.findViewById(R.id.alarmId);
				
				if(textViewId != null) {
					String id = textViewId.getText().toString();
					
					if(updateId.equalsIgnoreCase(id)){
						expandableListView.getAdapter().getView(i, view, expandableListView);
						break;
					}
				}
			}
		}
	};

	public void onDestroy() {
		if (updateReceiver != null) {
			getActivity().unregisterReceiver(updateReceiver);
		}
		super.onDestroy();
	}
}
