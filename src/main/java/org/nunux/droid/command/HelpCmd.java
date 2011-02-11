package org.nunux.droid.command;

import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * Help command.
 * @author Nicolas Carlier
 */
public class HelpCmd extends Command {

    public HelpCmd(final DroidService service) throws InvalidSyntaxException {
        super("^help$",
            "help : Print help.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    List<Command> commands = service.getCommands();
                    for (Command command : commands) {
                        service.send(command.getHelp());
                    }
                }
            }
        );
    }
}
