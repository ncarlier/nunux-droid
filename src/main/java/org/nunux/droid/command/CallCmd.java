package org.nunux.droid.command;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * Call command.
 * @author Nicolas Carlier
 */
public class CallCmd extends Command {

    public CallCmd(final DroidService service) throws InvalidSyntaxException {
        super("^call ([0-9]+)$",
            "call <number> : Make a call phone.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if(args.size() > 0) {
                        String number = args.get(0);
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            service.startActivity(intent);
                        } catch (Exception ex) {
                            Log.e(DroidService.TAG, "Unable to make the call.", ex);
                            service.send("Unable to make the call: " + ex.getMessage());
                            return;
                        }
                        service.send("Calling " + number + "...");
                    }
                }
            }
        );
    }
}
