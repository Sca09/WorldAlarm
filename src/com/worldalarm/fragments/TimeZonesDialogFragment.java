package com.worldalarm.fragments;

import com.worldalarm.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class TimeZonesDialogFragment extends DialogFragment {

	OnAddTimeZoneListener mCallback;
	
	public interface OnAddTimeZoneListener {
		public Bundle getBundle();
	}

	public void setOnAddTimeZoneListener(OnAddTimeZoneListener onAddTimeZoneListener) {
		this.mCallback = onAddTimeZoneListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		Bundle bundle = mCallback.getBundle();
		
		String[] timeZones = bundle.getStringArray("timeZones");
		
		String test = bundle.getString("test");
		
		Log.d("TimeZonesDialogFragment", "timeZones["+ timeZones +"]");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.choose_time_zone);
		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		builder.setItems(timeZones, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder.create();
	}
	
	
}
