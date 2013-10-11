package com.worldalarm.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.worldalarm.R;
import com.worldalarm.db.Alarm;

public class MyCard extends Card {
	
	private Alarm alarm;
	
	public MyCard(Alarm alarm){
		this.alarm = alarm;
	}
	
	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_ex, null);

		((TextView) view.findViewById(R.id.alarmInfo)).setText(alarm.getCity().getCityName() +" - "+ alarm.toString());
		((TextView) view.findViewById(R.id.alarmInfoLocal)).setText("Local date - "+ alarm.getLocalDate());
		
		return view;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}
}
