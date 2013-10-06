package com.worldalarm.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.worldalarm.R;

public class MyCard extends Card {

	private String alarmInfo;
	private String alarmInfoLocal;
	
//	public MyCard(String title, String desc){
//		super(title, desc, -1);
//	}
	
	public MyCard(String alarmInfo, String alarmInfoLocal){
		super(alarmInfo, alarmInfoLocal, -1);
		this.alarmInfo = alarmInfo;
		this.alarmInfoLocal = alarmInfoLocal;
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_ex, null);

//		((TextView) view.findViewById(R.id.title)).setText(title);
//		((TextView) view.findViewById(R.id.description)).setText(desc);
		
		((TextView) view.findViewById(R.id.alarmInfo)).setText(alarmInfo);
		((TextView) view.findViewById(R.id.alarmInfoLocal)).setText(alarmInfoLocal);
		
		return view;
	}

	
	
	
}
