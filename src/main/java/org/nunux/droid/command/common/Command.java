package org.nunux.droid.command.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic command.
 * @author Nicolas Carlier
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
     * @param syntax the syntax for the command.
     * @param helpthe help help of the command.
     * @param ce command executor.
     * @throws InvalidSyntaxDefinionException.
     */
    protected final void prepare(String syntax, String help, ICommandExecutor ce) throws InvalidSyntaxException {
        if (syntax == null || syntax.length() == 0) {
            throw new IllegalArgumentException("Syntax cannot be empty.");
        }
        this.help = help;
        this.syntaxPattern = Pattern.compile(syntax);
        this.executor = ce;
    }

    /**
     * Check if the command line match the current command.
     * @return <code>true</code> if it's matches, <code>false</code> if not.
     */
    protected final boolean matches(String commandLine) {
        return this.syntaxPattern.matcher(commandLine).matches();
    }

    /**
     * Extract parameters from command line.
     * @param commandLine command line
     * @return list of parameter
     * @throws InvalidSyntaxException
     */
    protected final List<String> extractParameters(String commandLine) throws InvalidSyntaxException {
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
     * @return The help for the command.
     */
    public final String getHelp() {
        return help;
    }

    /**
     * Get the executor for the command.
     * @return the executor.
     */
    public final ICommandExecutor getExecutor() {
        return executor;
    }

     /**
     * Set the executor for the command.
     * @param executor the executor.
     */
    protected final void setCommandExecutor(ICommandExecutor executor) {
        this.executor = executor;
    }
}
