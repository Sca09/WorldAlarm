package com.worldalarm.activities;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.fima.cardsui.views.CardUI;
import com.worldalarm.R;
import com.worldalarm.card.MyCard;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmSet;
import com.worldalarm.db.AlarmSetDatabaseHelper;

public class ListAlarmsActivity extends Activity implements View.OnClickListener, AlarmSetDatabaseHelper.ArrayAlarmSetListener {

	private CardUI mCardView;
	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarms_list);
		
		findViewById(R.id.addAlarmButton).setOnClickListener(this);
		
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		
		AlarmSetDatabaseHelper.getInstance(this).GetAllAlarmsSetAsync(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarms_list, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.addAlarmButton:			
			Intent newAlarmIntent = new Intent(this, NewAlarmActivity.class);
			this.startActivityForResult(newAlarmIntent, REQUEST_CODE_RESOLVE_ERR_NEW_ALARM);
			
			break;
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_RESOLVE_ERR_NEW_ALARM) {
            if (resultCode == RESULT_OK) {
            	
            	AlarmSet alarmSet = (AlarmSet) data.getSerializableExtra("newAlarm");
            	
            	if(alarmSet != null && alarmSet.getRemoteAlarm() != null) {
    				mCardView.addCard(new MyCard(alarmSet.getRemoteAlarm().getCity(), alarmSet.toString()));
    			} else if(alarmSet != null) {
    				mCardView.addCard(new MyCard("Local alarm", alarmSet.toString()));
    			}
            	
            	mCardView.refresh();
            	
            	Log.d("ListAlarmsActivity", alarmSet.toString());
            }
        }
	}
	
	@Override
	public void setArrayAlarmSet(List<AlarmSet> listAlarmSet) {
		mCardView.clearCards();
		
		for(AlarmSet alarmSet : listAlarmSet) {
			
			if(alarmSet.getRemoteAlarm() != null) {
				mCardView.addCard(new MyCard(alarmSet.getRemoteAlarm().getCity(), alarmSet.toString()));
			} else {
				mCardView.addCard(new MyCard("Local alarm", alarmSet.toString()));
			}
		}
		
		mCardView.refresh();	
	}
}
