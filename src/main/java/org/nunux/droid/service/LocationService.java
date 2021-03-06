package org.nunux.droid.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * Location service.
 * @author Nicolas Carlier
 */
public class LocationService extends Service {

    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    private Location currentBestLocation = null;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    @Override
    public IBinder onBind(Intent arg0) {
        handleStart();
        return new ILocationService.Stub() {
            public Location getCurrentLocation() throws RemoteException {
                return currentBestLocation;
            }
        };
    }

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean getGPSStatus() {
        String allowedLocationProviders =
                Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (allowedLocationProviders == null) {
            allowedLocationProviders = "";
        }
        return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
    }

    private void setGPSStatus(boolean pNewGPSStatus) {
        String allowedLocationProviders =
                Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (allowedLocationProviders == null) {
            allowedLocationProviders = "";
        }
        boolean networkProviderStatus =
                allowedLocationProviders.contains(LocationManager.NETWORK_PROVIDER);
        allowedLocationProviders = "";
        if (networkProviderStatus == true) {
            allowedLocationProviders += LocationManager.NETWORK_PROVIDER;
        }
        if (pNewGPSStatus == true) {
            allowedLocationProviders += "," + LocationManager.GPS_PROVIDER;
        }
        Settings.Secure.putString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED, allowedLocationProviders);
        try {
            Method m =
                    mLocationManager.getClass().getMethod("updateProviders", new Class[]{});
            m.setAccessible(true);
            m.invoke(mLocationManager, new Object[]{});
        } catch (Exception e) {
        }
        return;
    }

    @Override
    public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);
        handleStart();
    }

    public void handleStart() {
        Log.i(DroidService.TAG, "Starting LocationService...");

        try {
            if (!getGPSStatus()) {
                setGPSStatus(true);
            }
        } catch (Exception e) {
            Log.e(DroidService.TAG, "Unable to get GPS status.", e);
        }
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                if (isBetterLocation(location, currentBestLocation)) {
                    currentBestLocation = location;
                }
            }

            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            }

            public void onProviderDisabled(String arg0) {
            }

            public void onProviderEnabled(String arg0) {
            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                if (isBetterLocation(location, currentBestLocation)) {
                    currentBestLocation = location;
                }
            }
        }
        Log.i(DroidService.TAG, "LoactionService started.");
    }

    @Override
    public void onDestroy() {
        destroy();
    }

    private void destroy() {
        if (mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;
            mLocationListener = null;
        }
    }

    /** From the SDK documentation. Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
