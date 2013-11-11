package com.worldalarm.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.AlarmDatabaseHelper.OnDeletedAlarmListener;

public class DeleteAlarmConfirmDialogFragment extends DialogFragment {

	OnDeleteAlarmDialogListener mCallback;
	
	public interface OnDeleteAlarmDialogListener {
		public Bundle getBundle();
	}

	public void setOnDeleteAlarmDialogListener(OnDeleteAlarmDialogListener onDeleteAlarmDialogListener) {
		this.mCallback = onDeleteAlarmDialogListener;
	} 
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		Bundle bundle = mCallback.getBundle();
		
		final Alarm alarm = (Alarm) bundle.getSerializable("alarmToDelete");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage(R.string.delete_confirm_question);
		builder.setPositiveButton(R.string.delete_text, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlarmDatabaseHelper.getInstance(getActivity()).removeAlarmAsync(alarm, (OnDeletedAlarmListener) getActivity()); 
						
						
//						new OnDeletedAlarmListener() {
//					
//					@Override
//					public void onDeletedAlarmListener() {
//						
//						OnDeletedAlarmListener activity = (OnDeletedAlarmListener) getActivity();
//						activity.onDeletedAlarmListener();
//						
//					}
//				});
				
			}
		});
		
		builder.setNegativeButton(android.R.string.cancel, null);

		return builder.create();
		
	}
}
