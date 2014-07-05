package com.worldalarm.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationHelper {
	private static final int THRESHOLD_ACCURACY = 200;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private LocationHelper() {
	}

	public static Location getBestKnownLocation(final Context context) {
		return getBestKnownLocation((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
	}

	/**
	 * Retrieves best known location from all location providers available on
	 * this system.
	 * 
	 * @param locationManager
	 *            A {@link LocationManager}
	 * @return Last known location or <code>null</code> if it could not be
	 *         found.
	 */
	public static Location getBestKnownLocation(final LocationManager locationManager) {
		Location bestLocation = null;

		for (final String provider : locationManager.getProviders(false)) {
			final Location location = locationManager.getLastKnownLocation(provider);

			if ((location != null) && isBetterLocation(location, bestLocation)) {
				bestLocation = location;
			}
		}

		return bestLocation;
	}

	/**
	 * Determines the best {@link Location}.
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 * @return <code>true</code> if <code>location</code> is a better location,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isBetterLocation(final Location location, final Location currentBestLocation) {

		if (currentBestLocation == null)
			return true;

		final boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
		final float accuracyDelta = (location.getAccuracy() - currentBestLocation.getAccuracy());
		final boolean isLessAccurate = accuracyDelta > 0;
		final boolean isMoreAccurate = accuracyDelta < 0;
		final boolean isSignificantlyLessAccurate = accuracyDelta > THRESHOLD_ACCURACY;

		final long timeDelta = location.getTime() - currentBestLocation.getTime();
		final boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		final boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		final boolean isNewer = timeDelta > 0;

		if (isSignificantlyNewer)
			return true;
		else if (isSignificantlyOlder)
			return false;

		if (isMoreAccurate)
			return true;
		else if (isNewer && !isLessAccurate)
			return true;
		else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return true;

		return false;
	}

	private static boolean isSameProvider(final String provider1, final String provider2) {
		if (provider1 == null)
			return provider2 == null;

		return provider1.equals(provider2);
	}

	public static String getLatLon(Context context) {
		String latlon = null;
		try {
			Location loc = LocationHelper.getBestKnownLocation(context);
			if (loc != null) {
				latlon = loc.getLatitude() + "," + loc.getLongitude();
			}
		} catch (Exception e) {

		}
		return latlon;
	}
}