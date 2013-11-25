package com.worldalarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.worldalarm.R;

public class TimeZoneAdapter extends ArrayAdapter<String> {

	Context context; 
    int layoutResourceId;
    String data[] = null;
    
    public TimeZoneAdapter(Context context, int layoutResourceId, String data[]) {
    	super(context, layoutResourceId, data);
    	this.context = context;
    	this.layoutResourceId = layoutResourceId;
    	this.data = data;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TimeZoneHolder holder = null;
		
		if(row == null) {
			
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new TimeZoneHolder();
			holder.timeZoneTitle = (TextView)row.findViewById(R.id.timeZoneTitle);
			
			row.setTag(holder);
			
		} else {
			holder = (TimeZoneHolder)row.getTag();
		}
		
		String title = data[position];
		holder.timeZoneTitle.setText(title);
		
		return row;
	}
	
    static class TimeZoneHolder {
    	TextView timeZoneTitle;
    }
}
