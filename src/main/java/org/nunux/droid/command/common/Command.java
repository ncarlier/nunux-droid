/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author fr23972
 */
public class Command {

    /** Help message */
    private String help;
    /** Command executor */
    private ICommandExecutor executor;
    /** Syntax definition  */
    private Pattern syntaxPattern;

    /**
     * Constructs a new command.
     *
     * @param syntax the syntax for the command.
     * @param helpthe help help of the command.
     * @param ce command executor.
     * @throws InvalidSyntaxDefinionException.
     */
    public Command(String syntax, String help, ICommandExecutor ce) throws InvalidSyntaxException {
        this.prepare(syntax, help, ce);
    }

    /**
     * Default constructor only for inheritors.
     */
    protected Command() {
    }

    /**
     * Initialize the command.
     *
     * @param syntax the syntax for the command.
     * @param helpthe help help of the command.
     * @param ce command executor.
     * @throws InvalidSyntaxDefinionException.
     */
    protected void prepare(String syntax, String help, ICommandExecutor ce) throws InvalidSyntaxException {
        if (syntax == null || syntax.length() == 0) {
            throw new IllegalArgumentException("Syntax cannot be empty.");
        }
        if (ce == null) {
            throw new IllegalArgumentException("Command executor cannot be null.");
        }
        this.help = help;
        this.syntaxPattern = Pattern.compile(syntax);
        this.executor = ce;
    }

    /**
     * Determine if this is a hidden command.
     *
     * @return <code>true</code> if it's a hidden command, <code>false</code> if not.
     */
    protected boolean matches(String commandLine) {
        return this.syntaxPattern.matcher(commandLine).matches();
    }

    protected List<String> extractParameters(String commandLine) throws InvalidSyntaxException {
        if (!matches(commandLine)) {
            throw new InvalidSyntaxException("Command does not matches: " + commandLine);
        }
        
        List<String> args = new ArrayList<String>();
        Matcher matcher = this.syntaxPattern.matcher(commandLine);

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                args.add(matcher.group(i));
            }
        }
        return args;
    }

    /**
     * Returns the help for the commend.
     *
     * @return The help for the command.
     */
    public String getHelp() {
        return help;
    }

    /**
     * Get the executor for the command.
     *
     * @return the executor.
     */
    public ICommandExecutor getExecutor() {
        return executor;
    }
}
