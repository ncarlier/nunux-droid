package org.nunux.droid.command;

import android.util.Log;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * IP command.
 * @author Nicolas Carlier
 */
public class IpCmd extends Command {

    public IpCmd(final DroidService service) throws InvalidSyntaxException {
        super("^ip$",
            "ip : Get the IP address.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    try {
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress()) {
                                    service.send(inetAddress.getHostAddress().toString());
                                }
                            }
                        }
                    } catch (SocketException ex) {
                        Log.e(DroidService.TAG, "Unable to get IP address.", ex);
                        service.send("Unable to get IP address.: " + ex.getMessage());
                    }
                }
            }
        );
    }
}
