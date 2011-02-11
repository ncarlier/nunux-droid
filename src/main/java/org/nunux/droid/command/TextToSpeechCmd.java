package org.nunux.droid.command;

import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;
import org.nunux.droid.tools.TextToSpeechHandle;

/**
 * TTS command.
 * @author Nicolas Carlier
 */
public class TextToSpeechCmd extends Command {

    public TextToSpeechCmd(final DroidService service) throws InvalidSyntaxException {
        super("^tell (.+)",
                "tell <text> : Use TTS phone feature to tell something.",
                null);

        final TextToSpeechHandle handle = new TextToSpeechHandle(service);
        setCommandExecutor(new ICommandExecutor() {
            public void execute(List<String> args) {
                if (args.size() > 0) {
                    handle.tell(args.get(0));
                }
            }
        });
    }
}
