package com.worldalarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
	
	Context context; 
    int layoutResourceId;    
    Alarm data[] = null;
    
    public AlarmAdapter(Context context, int layoutResourceId, Alarm data[]){
    	super(context, layoutResourceId, data);
    	this.context = context;
    	this.layoutResourceId = layoutResourceId;
    	this.data = data;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AlarmHolder holder = null;
		
		if(row == null) {
			
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
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
		
		Alarm alarm = data[position];
		holder.alarmId.setText(String.valueOf(alarm.getId()));
		holder.alarmHour.setText(alarm.getHour());
		holder.alarmDate.setText(alarm.getDate());
		holder.alarmCity.setText(alarm.getCity().getCityName());
		holder.alarmHourLocal.setText(alarm.getHourLocal());
		holder.alarmDateLocal.setText(alarm.getDateLocal());
		
		
		return row;
	}

    
    static class AlarmHolder
    {
    	TextView alarmId;
        TextView alarmHour;
        TextView alarmDate;
        TextView alarmCity;
        TextView alarmHourLocal;
        TextView alarmDateLocal;
    }
}
