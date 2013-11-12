package com.worldalarm.fragments;

import java.util.ArrayList;
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

public class AlarmsListFragment extends Fragment implements OnRetrievedAllAlarmsByTZNameListener {
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
		
		if(getArguments() != null) {
			
			String tzSelected = getArguments().getString(ARG_SECTION_NAME);
		
			if(tzSelected != null) {
				timeZone = tzSelected;
			}
		}

		AlarmDatabaseHelper.getAlarmsByTZInstance(getActivity(), this);

		return rootView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}
	
	
	@Override
	public void onRetrievedAllAlarmsByTZName(HashMap<String, List<Alarm>> listAlarms) {
		
		List<Alarm> timeZoneList = this.getListAlarms(listAlarms);
		
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
	
	private List<Alarm> getListAlarms(HashMap<String, List<Alarm>> listAlarms) {
		
		if(timeZone != null && timeZone.length() > 0) {
			return listAlarms.get(timeZone);
		} else {
			
			List<Alarm> allAlarms = new ArrayList<Alarm>();
			
			for(String timeZone : listAlarms.keySet()) {
				allAlarms.addAll(listAlarms.get(timeZone));
			}
			
			return allAlarms;
		}
	}	
}