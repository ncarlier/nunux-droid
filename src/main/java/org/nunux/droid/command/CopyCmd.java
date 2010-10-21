/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nunux.droid.command;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.XmppService;

/**
 *
 * @author Nicolas Carlier
 */
public class CopyCmd extends Command {

    public CopyCmd(final XmppService service) throws InvalidSyntaxException {
        super("^copy (.+)",
            "copy <text>\nCopy text into clipboard.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (args.size() > 0) {
                        String text = args.get(0);
                        try {
                            ClipboardManager clipboard = (ClipboardManager) service.getSystemService(Service.CLIPBOARD_SERVICE);
                            clipboard.setText(text);
                            service.send("Text copied.");
                        }
                        catch(Exception ex) {
                            service.send("Unable to access to clipboard: " + ex.getMessage());
                        }
                    }
                }
            }
        );
    }
}
