package com.worldalarm.fragments;

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
import com.worldalarm.db.AlarmDatabaseHelper.OnRetrievedAllAlarmsListener;

public class AllAlarmsFragment extends Fragment implements OnRetrievedAllAlarmsListener {

	RelativeLayout rootView;

	private ListView listAlarms;
	private Activity activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.list_alarms, container, false);
		
		AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsAsync(this);

		return rootView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	
	@Override
	public void onRetrievedAllAlarms(List<Alarm> listAlarm) {
		
		Alarm[] data = new Alarm[listAlarm.size()];
		
		int i = 0;
		for(Alarm alarm : listAlarm) {
			
			data[i] = alarm;
			
			i++;
		}

		AlarmAdapter adapter = new AlarmAdapter(activity, R.layout.alarm, data);
		
//		listAlarms = (ListView)activity.findViewById(android.R.id.list);
		listAlarms = new ListView(activity);
		listAlarms.setDivider(null);
				
		listAlarms.setAdapter(adapter);
		
		rootView.addView(listAlarms);	
	}
}
