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
public class HelloWorldCmd extends Command {

    public HelloWorldCmd(final XmppService service) throws InvalidSyntaxException {
        super("^hello (.+)",
            "Says hello to the world and especially to some one.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (args.size() > 0) {
                        service.send("Hello world! And hello especially to " + args.get(0));
                    }
                }
            }
        );
    }
}
