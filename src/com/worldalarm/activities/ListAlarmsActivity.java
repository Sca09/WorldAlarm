package com.worldalarm.activities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.Card.OnCardSwiped;
import com.fima.cardsui.views.CardUI;
import com.worldalarm.R;
import com.worldalarm.card.MyCard;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;

public class ListAlarmsActivity extends Activity implements View.OnClickListener, AlarmDatabaseHelper.ArrayAlarmListener {

	private CardUI mCardView;
	private static final int REQUEST_CODE_RESOLVE_ERR_NEW_ALARM = 5000;
	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_alarms);
		
		findViewById(R.id.addAlarmButton).setOnClickListener(this);
		
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		
		AlarmDatabaseHelper.getInstance(this).getAllAlarmsAsync(this);
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
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR_NEW_ALARM:
			if (resultCode == RESULT_OK) {
            	Alarm alarm = (Alarm) data.getSerializableExtra("newAlarm");
            	
            	if(alarm != null) {
            		final MyCard newCard = new MyCard(alarm);
            		newCard.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(), UpdateAlarmActivity.class);
							intent.putExtra("alamToUpdate", newCard.getAlarm());
							
							startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
						}
					});
            		
            		newCard.setOnCardSwipedListener(new OnCardSwiped() {
						
						@Override
						public void onCardSwiped(Card card, View layout) {
							AlarmDatabaseHelper.getInstance(layout.getContext()).removeAlarmAsync(newCard.getAlarm());	
							
							Toast toastAlert = Toast.makeText(layout.getContext(), "Removed alarm at "+ newCard.getAlarm() +" in "+ newCard.getAlarm().getCity(), Toast.LENGTH_LONG);
							toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
							toastAlert.show();
						}
					});
            		
            		mCardView.addCard(newCard);
    			}
            	
            	mCardView.refresh();
            	mCardView.scrollToCard(mCardView.getLastCardStackPosition());
            }
			
			break;
			
		case REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM:
			if (resultCode == RESULT_OK) {
				AlarmDatabaseHelper.getInstance(this).getAllAlarmsAsync(this);
			}
			
			break;

		default:
			break;
		}
	}
	
	@Override
	public void setArrayAlarm(List<Alarm> listAlarm) {
		mCardView.clearCards();
		
		for(Alarm alarm : listAlarm) {
			if(alarm != null) {
				final MyCard newCard = new MyCard(alarm);
				newCard.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), UpdateAlarmActivity.class);
						intent.putExtra("alamToUpdate", newCard.getAlarm());
						
						startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
					}
				});
				
				newCard.setOnCardSwipedListener(new OnCardSwiped() {
					
					@Override
					public void onCardSwiped(Card card, View layout) {
						AlarmDatabaseHelper.getInstance(layout.getContext()).removeAlarmAsync(newCard.getAlarm());
						
				    	Toast toastAlert = Toast.makeText(layout.getContext(), "Removed alarm at "+ newCard.getAlarm() +" in "+ newCard.getAlarm().getCity(), Toast.LENGTH_LONG);
						toastAlert.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
						toastAlert.show();
					}
				});
				
				mCardView.addCard(newCard);
			} 
		}
		
		mCardView.refresh();	
	}
}
