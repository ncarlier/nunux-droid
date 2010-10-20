/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.command.common;

import java.util.List;

/**
 * Command executor.
 *
 * @author Nicolas Carlier
 */
public interface ICommandExecutor {

    /**
     * Execute the command.
     *
     * @param parseResult the parse data for the command.
     */
    public void execute(List<String> args);
}
