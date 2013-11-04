package com.worldalarm.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fima.cardsui.views.CardUI;
import com.worldalarm.R;
import com.worldalarm.activities.UpdateAlarmActivity;
import com.worldalarm.card.MyCard;
import com.worldalarm.db.Alarm;
import com.worldalarm.db.AlarmDatabaseHelper;
import com.worldalarm.db.AlarmDatabaseHelper.OnRetrievedAllAlarmsListener;

public class AllAlarmsFragment extends Fragment implements OnRetrievedAllAlarmsListener {

	private CardUI mCardView;

	private static final int REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM = 6000;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.list_alarms, container, false);
		
		mCardView = (CardUI) rootView.findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);
		mCardView.clearCards();
		
		AlarmDatabaseHelper.getInstance(getActivity()).getAllAlarmsAsync(this);

		return rootView;
	}

	@Override
	public void onRetrievedAllAlarms(List<Alarm> listAlarm) {
		mCardView.clearCards();
		
		for(Alarm alarm : listAlarm) {
			if(alarm != null) {
				final MyCard newCard = new MyCard(alarm);
				newCard.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), UpdateAlarmActivity.class);
						intent.putExtra("alamToUpdate", newCard.getAlarm());
						getActivity().startActivityForResult(intent, REQUEST_CODE_RESOLVE_ERR_UPDATE_ALARM);
					}
				});
				
				mCardView.addCard(newCard);
			} 
		}
		
		mCardView.refresh();	
	}
}
