package com.worldalarm.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.worldalarm.R;
import com.worldalarm.adapters.SectionsPagerAdapter;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.TimeZoneDatabaseHelper;
import com.worldalarm.db.TimeZoneDatabaseHelper.OnAddedTimeZoneListener;
import com.worldalarm.fragments.TimeZonesDialogFragment;

public class ListAlarmsSwipeViewActivity extends FragmentActivity implements TimeZonesDialogFragment.NewTimeZoneListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	
	public static final String ARG_SECTION_NAME = "section_name";
	public static final String ARG_LIST_ALARMS = "list_alarms";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_alarms_swipe_view);

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
			
		case R.id.action_home:
			mViewPager.setCurrentItem(0);
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:
			if (resultCode == RESULT_OK) {
				mViewPager.getAdapter().notifyDataSetChanged();
				
				Alarm newAlarm = (Alarm) data.getSerializableExtra("newAlarm");
				
				String timeZoneSelected = newAlarm.getCity().getTimeZoneName();
				openTab(timeZoneSelected);
			}
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM:
			if (resultCode == Activity.RESULT_OK) {
				mViewPager.getAdapter().notifyDataSetChanged();
				
				Alarm alamUpdated = (Alarm) data.getSerializableExtra("alamUpdated");
				
				if(alamUpdated != null) {
					String timeZoneSelected = alamUpdated.getCity().getTimeZoneName();
					openTab(timeZoneSelected);
				} else {
					// Alarm deleted
					mViewPager.setCurrentItem(0);
				}
			} 
			break;
		}
	}

	@Override
	public void addTimeZone(final String timeZone) {
		TimeZoneDatabaseHelper.getInstance(this).addTimeZoneAsync(timeZone, new OnAddedTimeZoneListener() {
			
			@Override
			public void OnAddedTimeZone(List<String> listTimeZones) {
				mViewPager.getAdapter().notifyDataSetChanged();
				openTab(timeZone);
			}
		});
	}
	
	private void openTab(String timeZoneSelected) {
		int tzSelectedPosition = mSectionsPagerAdapter.getListTimeZones().lastIndexOf(timeZoneSelected);
		mViewPager.setCurrentItem(tzSelectedPosition + 1);
	}
}
