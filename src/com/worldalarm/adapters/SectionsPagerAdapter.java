package com.worldalarm.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.worldalarm.db.TimeZoneDatabaseHelper;
import com.worldalarm.fragments.AlarmsListFragment;
import com.worldalarm.fragments.AllAlarmsFragment;
import com.worldalarm.fragments.TZAlarmsFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements TimeZoneDatabaseHelper.OnRetrievedAllTimeZonesListener {

	private Context context; 
	
	List<String> listTimeZones = new ArrayList<String>();
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
		
		TimeZoneDatabaseHelper.getAllTimeZones(this.context, this);
	}

	@Override
	public Fragment getItem(int position) {
		
		if(position == 0) {
//			Fragment fragment = new AlarmsListFragment();
			Fragment fragment = new AllAlarmsFragment();
			return fragment;
		} else {
			Fragment fragment = new AlarmsListFragment();
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
