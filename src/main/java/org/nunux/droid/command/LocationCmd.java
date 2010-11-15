/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command;

import android.location.Location;
import android.os.RemoteException;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.XmppService;

/**
 *
 * @author Nicolas Carlier
 */
public class LocationCmd extends Command {

    public LocationCmd(final XmppService service) throws InvalidSyntaxException {
        super("^location$",
                "location\nLocate the phone and return google maps link.",
                new ICommandExecutor() {
                    public void execute(List<String> args) {
                        try {
                            Location location = service.getLocationService().getCurrentLocation();
                             String url = LocationCmd.getGoogleMapUrl(location);
                             service.send(url != null ? url : "Location unavailabe.");
                        } catch (RemoteException ex) {
                            service.send("Location unavailabe: " + ex.getMessage());
                        }
                    }
                });
    }

    public static String getGoogleMapUrl(Location location) {
        if (location != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("http://maps.google.com/maps?q=")
                   .append(location.getLatitude()).append(",")
                   .append(location.getLongitude()).append(" (")
                   .append("accuracy: ").append(location.getAccuracy()).append("m ")
                   .append("altitude: ").append(location.getAltitude()).append(" ")
                   .append("speed: ").append(location.getSpeed()).append("m/s ")
                   .append("provider: ").append(location.getProvider()).append(")");
            return builder.toString();
        }
        return null;
    }

}
