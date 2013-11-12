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
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.AlarmDatabaseHelper.OnRetrievedAllAlarmsByTZNameListener;

public class AllAlarmsFragment extends Fragment implements OnRetrievedAllAlarmsByTZNameListener {

	RelativeLayout rootView;

	private Activity activity;
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = (RelativeLayout)inflater.inflate(R.layout.activity_expandable_alarms, container, false);
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.expandableAlarmsView);
		
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
		listAdapter = new ExpandableListAdapter(activity, listAlarms);

		expListView.setAdapter(listAdapter);
		
		this.expandAll();
	}
	
	public void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i=0; i<count ; i++){
			expListView.expandGroup(i);
		}
	}
}
