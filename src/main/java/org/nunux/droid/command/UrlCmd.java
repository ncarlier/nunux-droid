package org.nunux.droid.command;

import android.content.Intent;
import android.net.Uri;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * URL command.
 * @author Nicolas Carlier
 */
public class UrlCmd extends Command {

    public UrlCmd(final DroidService service) throws InvalidSyntaxException {
        super("^(https?://.+)$",
            "<URL> : Open URL into device browser.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (args.size() > 0) {
                        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(args.get(0)));
                        Intent intent = Intent.createChooser(target, "Droid: choose an activity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        service.startActivity(intent);
                    }
                }
            }
        );
    }
}
