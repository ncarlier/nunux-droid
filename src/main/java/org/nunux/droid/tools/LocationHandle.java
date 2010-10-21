/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.tools;

import android.location.Location;

/**
 *
 * @author fr23972
 */
public class LocationHandle {
    
    private Location currentLocation = null;

    private static LocationHandle instance = null;

    protected LocationHandle() {
        // Exists only to defeat instantiation.
    }

    public static LocationHandle getInstance() {
        if (instance == null) {
            instance = new LocationHandle();
        }
        return instance;
    }

    public synchronized void updateCurrentLocation(Location location) {
        currentLocation = location;
    }

    public String getGoogleMapUrl() {
        if (currentLocation != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("http://maps.google.com/maps?q=")
                   .append(currentLocation.getLatitude()).append(",")
                   .append(currentLocation.getLongitude()).append(" (")
                   .append("accuracy: ").append(currentLocation.getAccuracy()).append("m ")
                   .append("altitude: ").append(currentLocation.getAltitude()).append(" ")
                   .append("speed: ").append(currentLocation.getSpeed()).append("m/s ")
                   .append("provider: ").append(currentLocation.getProvider()).append(")");
            return builder.toString();
        }
        return null;
    }

}
