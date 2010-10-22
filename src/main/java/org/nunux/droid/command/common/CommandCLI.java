/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command.common;

import android.util.Log;
import java.util.List;

/**
 *
 * @author fr23972
 */
public class CommandCLI {

    private List<Command> commands;

    /**
     * Creates a new instance.
     *
     * @param commands the list of commands that can be executed.
     */
    public CommandCLI(List<Command> commands) {
        this.commands = commands;
    }

    /**
     * Runs a command based on the arguments.
     *
     * @param args  the arguments to be parsed
     * @param first the index on <code>args</code> of the first string for the arguments.
     * @throws ExecutionException
     */
    public void execute(String commandLine) throws InvalidSyntaxException {
        if (commandLine == null) {
            throw new IllegalArgumentException("The command line cannot be null.");
        }
        if (commandLine.length() == 0) {
            return;
        }
        // Look for the command that the parse works
        Command command = null;
        for (Command c : commands) {
            if (c.matches(commandLine)) {
                command = c;
                break;
            }
        }
        if (command == null) {
            throw new InvalidSyntaxException("No command matches: " + commandLine);
        }
        Log.d("Droid", "Command received: " + commandLine);
        // Execute the command
        command.getExecutor().execute(command.extractParameters(commandLine));
    }
}
