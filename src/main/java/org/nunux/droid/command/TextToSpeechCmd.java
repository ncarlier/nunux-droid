/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command;

import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.tts.TextToSpeechHandle;

/**
 *
 * @author Nicolas Carlier
 */
public class TextToSpeechCmd extends Command {

    public TextToSpeechCmd(final TextToSpeechHandle handle) throws InvalidSyntaxException {
        super("^tell (.+)",
                "Use TTS phone feature to tell something.",
                new ICommandExecutor() {
                    public void execute(List<String> args) {
                        if (args.size() > 0) {
                            handle.tell(args.get(0));
                        }
                    }
                });
    }
}
