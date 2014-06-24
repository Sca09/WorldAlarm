package com.worldalarm.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.worldalarm.R;
import com.worldalarm.activities.ListAlarmsSwipeViewActivity;
import com.worldalarm.activities.UpdateAlarmActivity;
import com.worldalarm.broadcast.AlarmManagerBroadcastReceiver;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
	
	Context context; 
	int layoutResourceId;
	Alarm data[] = null;

	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;

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
			
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		
		holder = new AlarmHolder();
		holder.background_img = (ImageView)row.findViewById(R.id.background_img);
		//holder.background_img.setScaleType(ScaleType.CENTER_CROP);
		
		holder.alarmId = (TextView)row.findViewById(R.id.alarmId);
		holder.alarmHour = (TextView)row.findViewById(R.id.alarmHour);
		holder.alarmDate = (TextView)row.findViewById(R.id.alarmDate);
		holder.alarmCity = (TextView)row.findViewById(R.id.alarmCity);
		holder.alarmHourLocal = (TextView)row.findViewById(R.id.alarmHourLocal);
		holder.alarmDateLocal = (TextView)row.findViewById(R.id.alarmDateLocal);
		holder.alarmSwitchButton = (Switch)row.findViewById(R.id.alarmSwitchButton);
//		holder.repeatDay_Sun = (TextView)row.findViewById(R.id.repeat_day_sun);
//		holder.repeatDay_Mon = (TextView)row.findViewById(R.id.repeat_day_mon);
//		holder.repeatDay_Tue = (TextView)row.findViewById(R.id.repeat_day_tue);
//		holder.repeatDay_Wed = (TextView)row.findViewById(R.id.repeat_day_wed);
//		holder.repeatDay_Thu = (TextView)row.findViewById(R.id.repeat_day_thu);
//		holder.repeatDay_Fri = (TextView)row.findViewById(R.id.repeat_day_fri);
//		holder.repeatDay_Sat = (TextView)row.findViewById(R.id.repeat_day_sat);
		holder.repeatDay_Sun = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_sun);
		holder.repeatDay_Mon = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_mon);
		holder.repeatDay_Tue = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_tue);
		holder.repeatDay_Wed = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_wed);
		holder.repeatDay_Thu = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_thu);
		holder.repeatDay_Fri = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_fri);
		holder.repeatDay_Sat = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_sat);
		row.setTag(holder);
					
		final Alarm alarm = data[position];
		
		String imageUrl = alarm.getCity().getPicUrl();
		Picasso.with(context).load(imageUrl).resize(context.getResources().getDisplayMetrics().widthPixels, 400).centerCrop().into(holder.background_img);
		
		holder.alarmId.setText(String.valueOf(alarm.getId()));
		holder.alarmHour.setText(alarm.getFormattedHour());
		holder.alarmCity.setText(alarm.getCity().getCityName());
		holder.alarmHourLocal.setText(alarm.getFormattedLocalHour());
		holder.alarmSwitchButton.setOnCheckedChangeListener(null);
		holder.alarmSwitchButton.setChecked(alarm.isActive());
		
		holder.alarmSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alarm.setActive(isChecked);
				
				AlarmManagerBroadcastReceiver alarmManager = new AlarmManagerBroadcastReceiver();
				
				if(isChecked){
					alarmManager.setOnetimeTimer(context, alarm);
				} else {
					alarmManager.cancelAlarm(context, alarm);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Sun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_SUN);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_SUN);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Mon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_MON);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_MON);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Tue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_TUE);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_TUE);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Wed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_WED);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_WED);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Thu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_THU);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_THU);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Fri.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_FRI);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_FRI);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		holder.repeatDay_Sat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ToggleButton button = (ToggleButton) view;
				List<Integer> repeatDays = alarm.getRepeatDays();
				
				if(button.isChecked()) {
					repeatDays.add(Alarm.REPEAT_DAY_SAT);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT_BOLD);
				} else {
					repeatDays.remove(Alarm.REPEAT_DAY_SAT);
					alarm.setRepeatDays(repeatDays);
					button.setTypeface(Typeface.DEFAULT);
				}
				
				AlarmPreferences.updateAlarm(alarm, context);
				((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
			}
		});
		
		List<Integer> repeatDays = alarm.getRepeatDays();
		
		for(Integer day : repeatDays) {
		
			if(Alarm.REPEAT_DAY_SUN.equals(day)) {
				holder.repeatDay_Sun.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Sun.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Sun.setChecked(Boolean.TRUE);
			} else if(Alarm.REPEAT_DAY_MON.equals(day)) {
				holder.repeatDay_Mon.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Mon.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Mon.setChecked(Boolean.TRUE);
			} if(Alarm.REPEAT_DAY_TUE.equals(day)) {
				holder.repeatDay_Tue.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Tue.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Tue.setChecked(Boolean.TRUE);
			} else if(Alarm.REPEAT_DAY_WED.equals(day)) {
				holder.repeatDay_Wed.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Wed.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Wed.setChecked(Boolean.TRUE);
			} else if(Alarm.REPEAT_DAY_THU.equals(day)) {
				holder.repeatDay_Thu.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Thu.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Thu.setChecked(Boolean.TRUE);
			} else if(Alarm.REPEAT_DAY_FRI.equals(day)) {
				holder.repeatDay_Fri.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Fri.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Fri.setChecked(Boolean.TRUE);
			} else if(Alarm.REPEAT_DAY_SAT.equals(day)) {
				holder.repeatDay_Sat.setTextColor(context.getResources().getColor(R.color.blue_light));
				holder.repeatDay_Sat.setTypeface(Typeface.DEFAULT_BOLD);
				holder.repeatDay_Sat.setChecked(Boolean.TRUE);
			}
		}
		

		row.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, UpdateAlarmActivity.class);
				intent.putExtra("alamToUpdate", alarm);
				
				((Activity)context).startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
			}
		});
		
		return row;
	}

	static class AlarmHolder
	{
		ImageView background_img;
		TextView alarmId;
		TextView alarmHour;
		TextView alarmDate;
		TextView alarmCity;
		TextView alarmHourLocal;
		TextView alarmDateLocal;
		Switch alarmSwitchButton;
//		TextView repeatDay_Sun;
//		TextView repeatDay_Mon;
//		TextView repeatDay_Tue;
//		TextView repeatDay_Wed;
//		TextView repeatDay_Thu;
//		TextView repeatDay_Fri;
//		TextView repeatDay_Sat;
		ToggleButton repeatDay_Sun;
		ToggleButton repeatDay_Mon;
		ToggleButton repeatDay_Tue;
		ToggleButton repeatDay_Wed;
		ToggleButton repeatDay_Thu;
		ToggleButton repeatDay_Fri;
		ToggleButton repeatDay_Sat;
	}
}
