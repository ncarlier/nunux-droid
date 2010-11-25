package org.nunux.droid.command.common;

import java.util.List;

/**
 * Command executor interface.
 * @author Nicolas Carlier
 */
public interface ICommandExecutor {

    /**
     * Execute the command.
     * @param args arguments for the command.
     */
    public void execute(List<String> args);
}
