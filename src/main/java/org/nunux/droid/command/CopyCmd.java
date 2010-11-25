package org.nunux.droid.command;

import android.app.Service;
import android.text.ClipboardManager;
import android.util.Log;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * Copy to clipboard command.
 * @author Nicolas Carlier
 */
public class CopyCmd extends Command {

    public CopyCmd(final DroidService service) throws InvalidSyntaxException {
        super("^copy (.+)",
            "copy <text>\nCopy text into clipboard.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (args.size() > 0) {
                        String text = args.get(0);
                        try {
                            ClipboardManager clipboard = (ClipboardManager) service.getSystemService(Service.CLIPBOARD_SERVICE);
                            clipboard.setText(text);
                            service.send("Text copied into clipboard.");
                        }
                        catch(Exception ex) {
                            Log.e(DroidService.TAG, "Unable to access to clipboard.", ex);
                            service.send("Unable to access to clipboard: " + ex.getMessage());
                        }
                    }
                }
            }
        );
    }
}
