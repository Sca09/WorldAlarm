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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.squareup.picasso.Picasso;
import com.worldalarm.R;
import com.worldalarm.activities.UpdateAlarmActivity;
import com.worldalarm.broadcast.AlarmManagerBroadcastReceiver;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.City;
import com.worldalarm.preferences.AlarmPreferences;
import com.worldalarm.preferences.CityPreferences;
import com.worldalarm.utils.Constants;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
	
	Context context; 
	List<Alarm> data = null;

	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;

	public AlarmAdapter(Context context, List<Alarm> data){
		super(context, R.layout.alarm, data);
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			
		AlarmHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.alarm, parent, false);
			holder = new AlarmHolder(context, convertView);
			convertView.setTag(holder);
		}
		else {
			holder = (AlarmHolder) convertView.getTag();
		}
		
		Alarm alarm = data.get(position);
		holder.populateWithAlarm(alarm);
				
		return convertView;
	}

	static class AlarmHolder
	{
		Context context;
		ImageView background_img;
		TextView alarmId;
		TextView alarmHour;
		TextView alarmDate;
		TextView alarmCity;
		TextView alarmHourLocal;
		TextView alarmDateLocal;
		Switch alarmSwitchButton;
		ToggleButton repeatDay_Sun;
		ToggleButton repeatDay_Mon;
		ToggleButton repeatDay_Tue;
		ToggleButton repeatDay_Wed;
		ToggleButton repeatDay_Thu;
		ToggleButton repeatDay_Fri;
		ToggleButton repeatDay_Sat;
		
		Alarm alarm;
		
		public AlarmHolder(final Context context, final View row) {
			this.context = context;
			background_img = (ImageView)row.findViewById(R.id.background_img);
			
			alarmId = (TextView)row.findViewById(R.id.alarmId);
			alarmHour = (TextView)row.findViewById(R.id.alarmHour);
			alarmDate = (TextView)row.findViewById(R.id.alarmDate);
			alarmCity = (TextView)row.findViewById(R.id.alarmCity);
			alarmHourLocal = (TextView)row.findViewById(R.id.alarmHourLocal);
			alarmDateLocal = (TextView)row.findViewById(R.id.alarmDateLocal);
			alarmSwitchButton = (Switch)row.findViewById(R.id.alarmSwitchButton);
			repeatDay_Sun = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_sun);
			repeatDay_Mon = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_mon);
			repeatDay_Tue = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_tue);
			repeatDay_Wed = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_wed);
			repeatDay_Thu = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_thu);
			repeatDay_Fri = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_fri);
			repeatDay_Sat = (ToggleButton)row.findViewById(R.id.repeat_day_toggle_sat);

			alarmSwitchButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean isChecked = ((Switch)v).isChecked();

					alarm.setActive(isChecked);
					
					AlarmManagerBroadcastReceiver alarmManager = new AlarmManagerBroadcastReceiver();
					
					if(isChecked){
						alarmManager.setOnetimeTimer(context, alarm);
					} else {
						alarmManager.cancelAlarm(context, alarm);
					}
					
					AlarmPreferences.updateAlarm(alarm, context);
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Sun.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Mon.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Tue.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Wed.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Thu.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			repeatDay_Fri.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});
			
			repeatDay_Sat.setOnClickListener(new OnClickListener() {
				
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
					broadcastChanges(context);
//					((ListAlarmsSwipeViewActivity) context).getmSectionsPagerAdapter().notifyDataSetChanged(alarm.getCity().getTimeZoneName());
				}
			});

			row.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, UpdateAlarmActivity.class);
					intent.putExtra("alamToUpdate", alarm);
					
					((Activity)context).startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
				}
			});

		}
		
		public void populateWithAlarm(final Alarm alarm) {
			this.alarm = alarm;

			background_img.setImageDrawable(null);
			if(alarm.getCity().getListPicUrls() == null && alarm.getCity().isNextPicCheckTime()) {
				final City cityForThread = alarm.getCity();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							alarm.getCity().setNextPicCheckTime();
							
							Flickr flickr = new Flickr(Constants.FLICKR_KEY, Constants.FLICKR_SECRET);
							
							SearchParameters searchParams = new SearchParameters();
							searchParams.setText(cityForThread.getCityName() +" downtown");
							searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
							
							PhotoList photoList = flickr.getPhotosInterface().search(searchParams, 5, 0);
							if(photoList.size() > 0) {
								for(Photo photo : photoList) {
									alarm.getCity().addPicUrl(photo.getMediumUrl());
								}
								CityPreferences.updateCity(alarm.getCity(), context);
							} else {
								
							}
							
							broadcastChanges(alarm, context);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
			String picUrl = alarm.getCityPicUrl();
			if(picUrl == null && alarm.getCity().getListPicUrls() != null){
				picUrl = alarm.getCity().getRandomPicUrl();
				alarm.setCityPicUrl(picUrl);
				AlarmPreferences.updateAlarm(alarm, context);
			}
			Picasso.with(context).load(picUrl).resize(context.getResources().getDisplayMetrics().widthPixels, 600).centerCrop().into(background_img);
			
			alarmId.setText(String.valueOf(alarm.getId()));
			alarmHour.setText(alarm.getFormattedHour());
			alarmCity.setText(alarm.getCity().getCityName());
			alarmHourLocal.setText(alarm.getFormattedLocalHour());
			alarmSwitchButton.setChecked(alarm.isActive());
			
			List<Integer> repeatDays = alarm.getRepeatDays();
			
			// TODO: Needs Refactor
			if(repeatDays.contains(Alarm.REPEAT_DAY_SUN)) {
				repeatDay_Sun.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Sun.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Sun.setTypeface(Typeface.DEFAULT);
				repeatDay_Sun.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_MON)) {
				repeatDay_Mon.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Mon.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Mon.setTypeface(Typeface.DEFAULT);
				repeatDay_Mon.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_TUE)) {
				repeatDay_Tue.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Tue.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Tue.setTypeface(Typeface.DEFAULT);
				repeatDay_Tue.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_WED)) {
				repeatDay_Wed.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Wed.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Wed.setTypeface(Typeface.DEFAULT);
				repeatDay_Wed.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_THU)) {
				repeatDay_Thu.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Thu.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Thu.setTypeface(Typeface.DEFAULT);
				repeatDay_Thu.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_FRI)) {
				repeatDay_Fri.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Fri.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Fri.setTypeface(Typeface.DEFAULT);
				repeatDay_Fri.setChecked(Boolean.FALSE);
			}
			
			if(repeatDays.contains(Alarm.REPEAT_DAY_SAT)) {
				repeatDay_Sat.setTypeface(Typeface.DEFAULT_BOLD);
				repeatDay_Sat.setChecked(Boolean.TRUE);
			} else {
				repeatDay_Sat.setTypeface(Typeface.DEFAULT);
				repeatDay_Sat.setChecked(Boolean.FALSE);
			}
		}
		
		private void broadcastChanges(Context context) {
			broadcastChanges(alarm, context);
		}
		
		private void broadcastChanges(Alarm alarm, Context context) {
			Intent intent = new Intent(Constants.BROADCAST_FILTER_ALARM_UPDATE);
			intent.putExtra("alarmId", alarm.getId());
			context.sendBroadcast(intent);
		}
	}
}
