package com.worldalarm.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.worldalarm.R;
import com.worldalarm.activities.TimeZonesActivity;

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
		
		final String[] timeZones = bundle.getStringArray("timeZones");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.choose_time_zone);
		builder.setNegativeButton(android.R.string.cancel, null);
		
		builder.setItems(timeZones, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newTimeZone = timeZones[which];
				
				TimeZonesActivity activity = (TimeZonesActivity) getActivity();
				activity.addTimeZone(newTimeZone);
			}
		});
		
		return builder.create();
	}
	
	public interface NewTimeZoneListener {
		void addTimeZone(String timeZone);
	}
}
