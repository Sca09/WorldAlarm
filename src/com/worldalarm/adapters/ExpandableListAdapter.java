package com.worldalarm.adapters;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.worldalarm.R;
import com.worldalarm.adapters.AlarmAdapter.AlarmHolder;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.TimeZonePreferences;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> listTimeZones;
	private HashMap<String, List<Alarm>> listAlarms;
	
	public ExpandableListAdapter(Context context, List<String> listTimeZones, HashMap<String, List<Alarm>> listAlarms) {
		this.context = context;
		this.listTimeZones = listTimeZones;
		this.listAlarms = listAlarms;
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		listTimeZones = TimeZonePreferences.getAllTimeZones(context);
		listAlarms = AlarmPreferences.getAlarmsByTZInstance(context);
		
		List<Alarm> list = listAlarms.get(listTimeZones.get(groupPosition));
		return list.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		AlarmHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.alarm, parent, false);
			holder = new AlarmHolder(context, convertView);
			convertView.setTag(holder);
		}
		else {
			holder = (AlarmHolder) convertView.getTag();
		}
		
		final Alarm alarm = (Alarm) getChild(groupPosition, childPosition);
		holder.populateWithAlarm(alarm);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<Alarm> list = listAlarms.get(listTimeZones.get(groupPosition));
		
		if(list != null) {
			return list.size();
		} else {
			return 0;
		}
		
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listTimeZones.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return listTimeZones.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		String headerTitle = (String) getGroup(groupPosition);
		
		View row = convertView;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.expandable_group_header_alarms, parent, false);
		}
		
		TextView headerTextView = (TextView) row.findViewById(R.id.expandableHeader);
		headerTextView.setText(headerTitle);
		
		return row;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
