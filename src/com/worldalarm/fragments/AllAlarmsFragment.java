package com.worldalarm.fragments;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.worldalarm.R;
import com.worldalarm.adapters.ExpandableListAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.TimeZonePreferences;

public class AllAlarmsFragment extends Fragment {

	RelativeLayout rootView;

	private Activity activity;
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    
    HashMap<String, List<Alarm>> listAlarms;
    List<String> listTimeZones;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.activity_expandable_alarms, container, false);
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.expandableAlarmsView);

		this.getAllAlarmsByTZName();
		this.getAllTimeZones();
		
		if(this.listAlarms != null && this.listTimeZones != null) {
			listAdapter = new ExpandableListAdapter(activity, this.listTimeZones, this.listAlarms); 
			
			expListView.setAdapter(listAdapter);
		
			this.expandAll();
		}
		
		return rootView;
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated");
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume");
		this.expandAll();
	}



	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart");
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
			System.out.println("2 - groupPosition["+ i +"] - Expanded["+  expListView.isGroupExpanded(i) +"]");
		}
	}
}
