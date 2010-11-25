package org.nunux.droid.command.common;

import java.util.List;

/**
 * Command-line interface.
 * @author Nicolas Carlier
 */
public class CommandCLI {

    private List<Command> commands;

    /**
     * Creates a new instance.
     * @param commands the list of commands that can be executed.
     */
    public CommandCLI(List<Command> commands) {
        this.commands = commands;
    }

    /**
     * Runs a command based on the command line.
     * @param commandLine the command line
     * @throws InvalidSyntaxException
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
        // Execute the command
        command.getExecutor().execute(command.extractParameters(commandLine));
    }
}
