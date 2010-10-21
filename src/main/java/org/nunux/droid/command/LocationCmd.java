/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command;

import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.XmppService;
import org.nunux.droid.tools.LocationHandle;

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
                        String url = LocationHandle.getInstance().getGoogleMapUrl();
                        if (url == null) {
                            service.send("Location unavailabe.");
                        }
                        else {
                            service.send(url);
                        }
                    }
                });
    }
}
