package org.nunux.droid.command;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * SMS command.
 * @author Nicolas Carlier
 */
public class SmsCmd extends Command {

    public SmsCmd(final DroidService service) throws InvalidSyntaxException {
        super("^sms ([0-9]+) (.+)",
            "sms <num> <text> : Send a SMS to a phone number.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (args.size() > 1) {
                        service.send("Sending SMS...");
                        PendingIntent sentIntent = PendingIntent.getBroadcast(
                                service, 0, new Intent("SMS_SENT"), 0);
                        PendingIntent deliverIntent = PendingIntent.getBroadcast(
                                service, 0, new Intent("SMS_DELIVERED"), 0);
                        
                        SmsManager sms = SmsManager.getDefault();
                        List<String> messages = sms.divideMessage(args.get(1));
                        for (String message : messages) {
                            sms.sendTextMessage(args.get(0), null, message, sentIntent, deliverIntent);
                            service.send("SMS sended.");
                        }
                    }
                }
            }
        );
    }
}
