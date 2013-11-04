package com.worldalarm.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.CityDatabaseHelper;
import com.worldalarm.db.CityDatabaseHelper.OnRetrievedTimeZoneNamesListener;
import com.worldalarm.db.TimeZoneDatabaseHelper;
import com.worldalarm.db.TimeZoneDatabaseHelper.OnAddedTimeZoneListener;
import com.worldalarm.fragments.AllAlarmsFragment;
import com.worldalarm.fragments.TZAlarmsFragment;
import com.worldalarm.fragments.TimeZonesDialogFragment;
import com.worldalarm.fragments.TimeZonesDialogFragment.OnAddTimeZoneListener;

public class ListAlarmsSwipeViewActivity extends FragmentActivity implements View.OnClickListener, TimeZonesDialogFragment.NewTimeZoneListener {

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
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		findViewById(R.id.NewAlarmButton).setOnClickListener(this);
		findViewById(R.id.NewTimeZoneButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_alarms_swipe_view, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.NewAlarmButton:
			Intent newAlarmIntent = new Intent(this, NewAlarmActivity.class);
			this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			break;
			
		case R.id.NewTimeZoneButton:
			
			CityDatabaseHelper.getInstance(this).getTimeZoneNamesAsync(new OnRetrievedTimeZoneNamesListener() {
				
				@Override
				public void onRetrievedTimeZoneNames(final String[] timeZoneNames) {
					
					TimeZonesDialogFragment fragment = new TimeZonesDialogFragment();
					
					fragment.setOnAddTimeZoneListener(new OnAddTimeZoneListener() {
						
						@Override
						public Bundle getBundle() {
							Bundle bundle = new Bundle();
							bundle.putStringArray("timeZones", timeZoneNames);
							
							return bundle;
						}
					});
					
					fragment.show(getSupportFragmentManager().beginTransaction(), "timeZones");
				}
			});
			
			break;
		}
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
				
				String timeZoneSelected = alamUpdated.getCity().getTimeZoneName();
				openTab(timeZoneSelected);
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements TimeZoneDatabaseHelper.OnRetrievedAllTimeZonesListener {

		List<String> listTimeZones = new ArrayList<String>();
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			
			TimeZoneDatabaseHelper.getAllTimeZones(getApplicationContext(), this);
		}

		@Override
		public Fragment getItem(int position) {
			
			if(position == 0) {
				Fragment fragment = new AllAlarmsFragment();
				return fragment;
			} else {
				Fragment fragment = new TZAlarmsFragment();
				Bundle args = new Bundle();
				args.putString(TZAlarmsFragment.ARG_SECTION_NAME, listTimeZones.get(position - 1));
				fragment.setArguments(args);
				return fragment;
			}
		}
		
		@Override
		public int getItemPosition(Object object) {
			
			Fragment fragment = (Fragment) object;
			
			fragment.getActivity();
			
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return listTimeZones.size() + 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(position == 0) {
				return "All Alarms";
			} else {
				return listTimeZones.get(position - 1);
			}
		}

		@Override
		public void OnRetrievedAllTimeZones(List<String> listTimeZones) {
			this.listTimeZones = listTimeZones;
			notifyDataSetChanged();
		}
		
		public List<String> getListTimeZones() {
			return listTimeZones;
		}

		public void setListTimeZones(List<String> listTimeZones) {
			this.listTimeZones = listTimeZones;
			notifyDataSetChanged();
		}
	}	
}
