package com.worldalarm.activities;

import java.util.List;
import java.util.zip.Inflater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fima.cardsui.views.CardUI;
import com.worldalarm.R;
import com.worldalarm.card.MyCard;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.TimeZoneDatabaseHelper;

public class ListAlarmsSwipeViewActivity extends FragmentActivity {

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
		
		Intent intent = getIntent();
		
		String timeZoneSelected = intent.getStringExtra("timeZoneSelected");
		
		int tzSelectedPosition = mSectionsPagerAdapter.getListTimeZones().lastIndexOf(timeZoneSelected);

		mViewPager.setCurrentItem(tzSelectedPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_alarms_swipe_view, menu);
		return true;
	}
	

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements TimeZoneDatabaseHelper.OnRetrievedAllTimeZonesListener {

		List<String> listTimeZones;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			
			TimeZoneDatabaseHelper.getAllTimeZones(getApplicationContext(), this);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new SectionFragment();
			Bundle args = new Bundle();
			args.putString(SectionFragment.ARG_SECTION_NAME, listTimeZones.get(position));
			fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public int getItemPosition(Object object) {
			
			SectionFragment fragment = (SectionFragment) object;
			
			fragment.getActivity();
			
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return listTimeZones.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return listTimeZones.get(position);
		}

		@Override
		public void OnRetrievedAllTimeZones(List<String> listTimeZones) {
			this.listTimeZones = listTimeZones;
		}
		
		public List<String> getListTimeZones() {
			return listTimeZones;
		}

		public void setListTimeZones(List<String> listTimeZones) {
			this.listTimeZones = listTimeZones;
		}
	}

	public static class SectionFragment extends Fragment implements AlarmDatabaseHelper.OnRetrievedAllAlarmsByTZNameListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NAME = "section_name";
		public static final String ARG_LIST_ALARMS = "list_alarms";
		
		private CardUI mCardView;
		private ViewPager mViewPager;
		private SectionsPagerAdapter mAdapter;
		
		public SectionFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_list_alarms_swipe_view, container,
					false);
			
			mViewPager = (ViewPager) container;
			mAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();
			
			mCardView = (CardUI) rootView.findViewById(R.id.cardsview);
			mCardView.setSwipeable(false);
			mCardView.clearCards();
			
			String tzSelected = getArguments().getString(ARG_SECTION_NAME);
			
			AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsByTZName(tzSelected, this);
				
			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, final Intent data) {
			switch (requestCode) {
			case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:				
				break;
				
			case REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM:
				if (resultCode == RESULT_OK) {
					mAdapter.notifyDataSetChanged();
					
					Alarm alamUpdated = (Alarm) data.getSerializableExtra("alamUpdated");
					int openTab = mAdapter.getListTimeZones().lastIndexOf(alamUpdated.getCity().getTimeZoneName());
					mViewPager.setCurrentItem(openTab);
					
					String tzSelected = getArguments().getString(ARG_SECTION_NAME);
					
					AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsByTZName(tzSelected, this);
				}
				break;	
			}
		}

		@Override
		public void onRetrievedAllAlarmsByTZName(List<Alarm> listAlarm) {
			mCardView.clearCards();
			
			for(Alarm alarm : listAlarm) {
				String tzSelected = getArguments().getString(ARG_SECTION_NAME);
				
				if(alarm != null && alarm.getCity().getTimeZoneName().equalsIgnoreCase(tzSelected)) {
					final MyCard newCard = new MyCard(alarm);
					newCard.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(), UpdateAlarmActivity.class);
							intent.putExtra("alamToUpdate", newCard.getAlarm());					
							startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
						}
					});
					
					mCardView.addCard(newCard);
				} 
			}
			
			mCardView.refresh();
		}
	}
}
