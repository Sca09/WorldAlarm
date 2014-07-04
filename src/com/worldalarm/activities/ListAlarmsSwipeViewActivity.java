package com.worldalarm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.worldalarm.R;
import com.worldalarm.adapters.SectionsPagerAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.utils.Constants;

public class ListAlarmsSwipeViewActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	ImageButton imageButton;
	
	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	private static final int REQUEST_CODE_RESOLVE_ERR_TIME_ZONE_CONF = 7000;
	
	public static final String ARG_SECTION_NAME = "section_name";
	public static final String ARG_LIST_ALARMS = "list_alarms";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_alarms_swipe_view);

		addListenerOnButton();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_alarms_swipe_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:			
			Intent newAlarmIntent = new Intent(this, NewAlarmActivity.class);
			this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			break;
			
//		case R.id.action_home:
//			mViewPager.setCurrentItem(0);
//			break;
			
		case R.id.action_settings_time_zones_conf:
			Intent timeZonesConf = new Intent(this, TimeZonesActivity.class);
			this.startActivityForResult(timeZonesConf, REQUEST_CODE_RESOLVE_ERR_TIME_ZONE_CONF);
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:
			if (resultCode == RESULT_OK) {
				mSectionsPagerAdapter.notifyDataSetChanged();
			}
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM:
			if (resultCode == Activity.RESULT_OK) {
				Alarm alarmUpdated = (Alarm) data.getSerializableExtra("alamUpdated");
				broadcastChanges(alarmUpdated);
			} 
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_TIME_ZONE_CONF:
			mSectionsPagerAdapter.notifyDataSetChanged();
			mSectionsPagerAdapter.getAllTimeZones();
			mViewPager.setCurrentItem(0);
			break;
		}
	}

	public void openTab(String timeZoneSelected) {
		int tzSelectedPosition = mSectionsPagerAdapter.getListTimeZones().lastIndexOf(timeZoneSelected);
		mViewPager.setCurrentItem(tzSelectedPosition + 1);
	}
	
	public SectionsPagerAdapter getmSectionsPagerAdapter() {
		return mSectionsPagerAdapter;
	}

	public void setmSectionsPagerAdapter(SectionsPagerAdapter mSectionsPagerAdapter) {
		this.mSectionsPagerAdapter = mSectionsPagerAdapter;
	}
	
	public void addListenerOnButton() {
		 
		imageButton = (ImageButton) findViewById(R.id.addBottomButton);
 
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAlarmIntent = new Intent(getApplicationContext(), NewAlarmActivity.class);
				ListAlarmsSwipeViewActivity.this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			}
		});
	}
	
	private void broadcastChanges(Alarm alarm) {
		Intent intent = new Intent(Constants.BROADCAST_FILTER_ALARM_UPDATE);
		intent.putExtra("alarmId", alarm.getId());
		sendBroadcast(intent);
	}
}
