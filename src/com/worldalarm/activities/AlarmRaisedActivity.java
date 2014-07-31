package com.worldalarm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.worldalarm.R;
import com.worldalarm.db.Alarm;
import com.worldalarm.preferences.AlarmPreferences;

public class AlarmRaisedActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_raised);
		
		Intent intent = this.getIntent();
		String alarmId = intent.getStringExtra("alarmId");
		
		Alarm alarm = AlarmPreferences.getAlarmById(this, alarmId);
		
		ImageView background_img = (ImageView) findViewById(R.id.background_img);
		TextView alarmHour = (TextView) findViewById(R.id.alarmHour);
		TextView alarmCity = (TextView) findViewById(R.id.alarmCity);
		
		alarmHour.setText(alarm.getFormattedHour());
		alarmCity.setText(alarm.getCity().getCityName());
		
		if(alarm != null) {
			Picasso.with(this).load(alarm.getCityPicUrl()).resize(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels).centerCrop().into(background_img);
		}
		
		findViewById(R.id.dismissButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_raised, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.dismissButton:
			Intent main = new Intent(this, ListAlarmsSwipeViewActivity.class);
			startActivity(main);
			break;
		}
		
	}
}
