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

/**
 *
 * @author Nicolas Carlier
 */
public class HelpCmd extends Command {

    public HelpCmd(final XmppService service) throws InvalidSyntaxException {
        super("^help$",
            "help\nPrint help.",
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
