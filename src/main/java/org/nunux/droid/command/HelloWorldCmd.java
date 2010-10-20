/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nunux.droid.command;

import org.naturalcli.Command;
import org.naturalcli.ICommandExecutor;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.ParseResult;
import org.nunux.droid.service.XmppService;

/**
 *
 * @author Nicolas Carlier
 */
public class HelloWorldCmd extends Command {

    public HelloWorldCmd(final XmppService service) throws InvalidSyntaxException {
        super("hello world <name:string>",
            "Says hello to the world and especially to some one.",
            new ICommandExecutor() {
                public void execute(ParseResult pr) {
                    service.send("Hello world! And hello especially to " + pr.getParameterValue(0));
                }
            }
        );
    }
}
