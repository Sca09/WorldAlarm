package com.worldalarm.preferences;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.worldalarm.db.City;

public class SearchCityByNameTaskData extends AsyncTask<String, Void, City> {
	public OnFoundCityByNameListener onFoundCityByNameListener;

	public SearchCityByNameTaskData(OnFoundCityByNameListener onFoundCityByNameListener) {
		this.onFoundCityByNameListener = onFoundCityByNameListener;
	}
	
	@Override
	protected City doInBackground(String... params)  {
		try {
			URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(params[0], "UTF-8") +"&sensor=false");
			String res = IOUtils.toString(url);
			
			if(res != null && res.length() > 0) {
				
				JSONObject jObject = new JSONObject(res);
				JSONArray jArray = jObject.getJSONArray("results");
				if(jArray.length() > 0){
					if(jArray.length() == 1) {
						JSONObject jObjectGeo = jArray.getJSONObject(0);
						JSONArray jArrayAddress = jObjectGeo.getJSONArray("address_components");
						JSONObject jObjectCity = jArrayAddress.getJSONObject(0);
						String cityLongName = jObjectCity.getString("long_name");
						
						JSONObject jObjectGeometry = jObjectGeo.getJSONObject("geometry");
						JSONObject jObjectLocation = jObjectGeometry.getJSONObject("location");
						double lat = jObjectLocation.getDouble("lat");
						double lng = jObjectLocation.getDouble("lng");
						
						url = new URL("https://maps.googleapis.com/maps/api/timezone/json?location="+ lat +","+ lng +"&timestamp="+ (Calendar.getInstance().getTimeInMillis() / 1000) +"&sensor=false");
						res = IOUtils.toString(url);
						JSONObject jObjectTZ = new JSONObject(res);
						String timeZoneId = jObjectTZ.getString("timeZoneId");
						
						if(cityLongName != null && cityLongName.length() > 0 && timeZoneId != null && timeZoneId.length() > 0) {
							TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
							String timeZoneName = timeZone.getDisplayName();
							
							City city = new City(cityLongName, timeZoneId, timeZoneName);

							return city;
						}
					} else {
						//TODO: implement case for more than one city found
					}
				}
			}
			
			Log.d("CityDatabaseHelper", "res["+ res +"]");
			
		} catch (Exception e) {
			Log.e("CityDatabaseHelper", e.getMessage());
			
		}

		return null;
	}

	@Override
	protected void onPostExecute(City city) {
		this.onFoundCityByNameListener.onFoundCityByName(city);
	}
	
	public interface OnFoundCityByNameListener {
		void onFoundCityByName(City city);
	}
	
}