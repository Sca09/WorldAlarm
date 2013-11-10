package com.worldalarm.fragments;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.worldalarm.R;
import com.worldalarm.adapters.AlarmAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.AlarmDatabaseHelper.OnRetrievedAllAlarmsByTZNameListener;

public class TZAlarmsFragment extends Fragment implements OnRetrievedAllAlarmsByTZNameListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NAME = "section_name";
	public static final String ARG_LIST_ALARMS = "list_alarms";

	RelativeLayout rootView;
	private ListView listViewAlarms;
	private Activity activity;
	private String timeZone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.list_alarms, container, false);
		
		String tzSelected = getArguments().getString(ARG_SECTION_NAME);
		
		if(tzSelected != null) {
			timeZone = tzSelected;
		}

//		AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsByTZName(tzSelected, this);
		AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsByTZName(this);

		return rootView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}
	
	
	@Override
	public void onRetrievedAllAlarmsByTZName(HashMap<String, List<Alarm>> listAlarms) {
		
		List<Alarm> timeZoneList = listAlarms.get(timeZone);
		
		Alarm[] data;
		
		if(timeZoneList != null) {
		
			data = new Alarm[timeZoneList.size()];
		
			int i = 0;
			for(Alarm alarm : timeZoneList) {
			
				data[i] = alarm;
			
				i++;
			}
		} else {
			data = new Alarm[0];
		}

		AlarmAdapter adapter = new AlarmAdapter(activity, R.layout.alarm, data);

		listViewAlarms = new ListView(activity);
		listViewAlarms.setDivider(null);
		
		listViewAlarms.setAdapter(adapter);
		
		rootView.addView(listViewAlarms);
	}
	
//	@Override
//	public void onRetrievedAllAlarmsByTZName(List<Alarm> listAlarm) {
//		HashMap<String, List<Alarm>> listAlarms
//		
//		Alarm[] data = new Alarm[listAlarm.size()];
//		
//		int i = 0;
//		for(Alarm alarm : listAlarm) {
//			
//			data[i] = alarm;
//			
//			i++;
//		}
//
//		AlarmAdapter adapter = new AlarmAdapter(activity, R.layout.alarm, data);
//
//		listAlarms = new ListView(activity);
//		listAlarms.setDivider(null);
//		
//		listAlarms.setAdapter(adapter);
//		
//		rootView.addView(listAlarms);
//	}
}
