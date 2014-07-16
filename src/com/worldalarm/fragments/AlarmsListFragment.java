package com.worldalarm.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldalarm.R;
import com.worldalarm.adapters.AlarmAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.utils.Constants;

public class AlarmsListFragment extends ListFragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NAME = "section_name";
	public static final String ARG_LIST_ALARMS = "list_alarms";

	RelativeLayout rootView;
	private ListView listViewAlarms;
	private Activity activity;
	private String timeZone;
	List<Alarm> data;
	AlarmAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().registerReceiver(updateReceiver, new IntentFilter(Constants.BROADCAST_FILTER_ALARM_UPDATE));
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.list_alarms, container, false);
		listViewAlarms = (ListView) rootView.findViewById(android.R.id.list);
		if(getArguments() != null) {
			
			String tzSelected = getArguments().getString(ARG_SECTION_NAME);
		
			if(tzSelected != null) {
				timeZone = tzSelected;
			}
		}

		this.getAllAlarmsByTZName();
		
		return rootView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	public void getAllAlarmsByTZName() {
		HashMap<String, List<Alarm>> listAlarms = AlarmPreferences.getAlarmsByTZInstance(getActivity());

		data = this.getListAlarms(listAlarms);

		adapter = new AlarmAdapter(activity, data);
		listViewAlarms.setDivider(null);
		listViewAlarms.setAdapter(adapter);
	}
	
	private List<Alarm> getListAlarms(HashMap<String, List<Alarm>> listAlarms) {
		List<Alarm> alarmsForTimeZone = new ArrayList<Alarm>();
		
		if(timeZone != null && timeZone.length() > 0) {
			List<Alarm> listAlarmsByZone = listAlarms.get(timeZone);
			
			if(listAlarmsByZone != null){
				alarmsForTimeZone.addAll(listAlarms.get(timeZone));
			}
		} else {
			for(String timeZone : listAlarms.keySet()) {
				alarmsForTimeZone.addAll(listAlarms.get(timeZone));
			}
		}
		
		return alarmsForTimeZone;
	}
	
	BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getAllAlarmsByTZName();

			String updateId = intent.getStringExtra("alarmId");
			
			ListView list = getListView();
			int start = list.getFirstVisiblePosition();
			for(int i=start;i<=list.getLastVisiblePosition();i++) {
				
				View view = list.getChildAt(i-start);
				TextView textViewId = (TextView) view.findViewById(R.id.alarmId);
				String id = textViewId.getText().toString();
				
				if(updateId.equalsIgnoreCase(id)){
					list.getAdapter().getView(i, view, list);
					break;
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