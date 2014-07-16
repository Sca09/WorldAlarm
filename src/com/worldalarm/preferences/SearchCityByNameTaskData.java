package com.worldalarm.preferences;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.worldalarm.db.City;

public class SearchCityByNameTaskData extends AsyncTask<String, Void, List<City>> {
	public Context context;
	public OnFoundCityByNameListener onFoundCityByNameListener;

	public SearchCityByNameTaskData(Context context, OnFoundCityByNameListener onFoundCityByNameListener) {
		this.context = context;
		this.onFoundCityByNameListener = onFoundCityByNameListener;
	}
	
	@Override
	protected List<City> doInBackground(String... params)  {
		try {
			List<City> cityList = new ArrayList<City>();
			
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
							CityPreferences.addCity(city, context);
							cityList.add(city);
							
							return cityList;
						}
					} else {
						for(int i=0; i<jArray.length(); i++){
							JSONObject jObjectGeo = jArray.getJSONObject(i);
							JSONArray jArrayAddress = jObjectGeo.getJSONArray("address_components");
							JSONObject jObjectCity = jArrayAddress.getJSONObject(0);
							String cityLongName = jObjectCity.getString("long_name");
							String administrativeArea = "";
							String country = "";
							
							for(int j=1; j<jArrayAddress.length(); j++) {
								JSONObject jObjectCityExtra = jArrayAddress.getJSONObject(j);
								JSONArray types = jObjectCityExtra.getJSONArray("types");
								for(int k=0; k<types.length(); k++) {
									if(types.get(k).equals("administrative_area_level_1")) {
										administrativeArea = jObjectCityExtra.getString("short_name");
									} else if(types.get(k).equals("country")) {
										country = jObjectCityExtra.getString("short_name");
									}
								}
							}
							cityLongName = cityLongName +", "+ administrativeArea +", "+ country;
							
							cityLongName = normalizeCityName(cityLongName);
							
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
								CityPreferences.addCity(city, context);
								cityList.add(city);
							}
						}
						
					return cityList;
					}
				}
			}
			
			Log.d("CityDatabaseHelper", "res["+ res +"]");
			
		} catch (Exception e) {
			Log.e("CityDatabaseHelper", e.getMessage());
			
		}

		return null;
	}

	private String normalizeCityName(String cityLongName) {
		return cityLongName.replace("á", "a")
				.replace("é", "e")
				.replace("í", "i")
				.replace("ó", "o")
				.replace("ú", "u");
	}

	@Override
	protected void onPostExecute(List<City> cityList) {
		this.onFoundCityByNameListener.onFoundCityByName(cityList);
	}
	
	public interface OnFoundCityByNameListener {
		void onFoundCityByName(List<City> cityList);
	}
	
}