package com.worldalarm.adapters;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.worldalarm.R;
import com.worldalarm.activities.UpdateAlarmActivity;
import com.worldalarm.adapters.AlarmAdapter.AlarmHolder;
import com.worldalarm.db.Alarm;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> listTimeZones;
	private HashMap<String, List<Alarm>> listAlarms;
	
	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	
	public ExpandableListAdapter(Context context, List<String> listTimeZones, HashMap<String, List<Alarm>> listAlarms) {
		this.context = context;
		this.listTimeZones = listTimeZones;
		this.listAlarms = listAlarms;
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return listAlarms.get(listTimeZones.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View row = convertView;
		AlarmHolder holder = null;
		
		if(row == null) {
			
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(R.layout.alarm, parent, false);
			
			holder = new AlarmHolder();
			holder.alarmId = (TextView)row.findViewById(R.id.alarmId);
			holder.alarmHour = (TextView)row.findViewById(R.id.alarmHour);
			holder.alarmDate = (TextView)row.findViewById(R.id.alarmDate);
			holder.alarmCity = (TextView)row.findViewById(R.id.alarmCity);
			holder.alarmHourLocal = (TextView)row.findViewById(R.id.alarmHourLocal);
			holder.alarmDateLocal = (TextView)row.findViewById(R.id.alarmDateLocal);
			
			row.setTag(holder);
			
		} else {
			holder = (AlarmHolder)row.getTag();
		}
		
		final Alarm alarm = (Alarm) getChild(groupPosition, childPosition);
		
		holder.alarmId.setText(String.valueOf(alarm.getId()));
		holder.alarmHour.setText(alarm.getHour());
		holder.alarmDate.setText(alarm.getDate());
		holder.alarmCity.setText(alarm.getCity().getCityName());
		holder.alarmHourLocal.setText(alarm.getHourLocal());
		holder.alarmDateLocal.setText(alarm.getDateLocal());
		
		row.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, UpdateAlarmActivity.class);
				intent.putExtra("alamToUpdate", alarm);
				
				((Activity)context).startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
			}
		});
		
		row.setPadding(25, 0, 0, 0);
		
		return row;
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
