/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

/**
 * This interface represents the 'window' object defined in the global
 * environment of a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Window {

    /**
     * Evaluates the given string repeatedly after the given amount of
     * time.  This method does not stall the script: the evaluation is
     * scheduled and the script continues its execution.
     * @return an object representing the interval created.
     */
    Object setInterval(String script, long interval);

    /**
     * Calls the 'run' method of the given Runnable repeatedly after
     * the given amount of time.  This method does not stall the
     * script: the evaluation is scheduled and the script continues
     * its execution.
     * @return an object representing the interval created.
     */
    Object setInterval(Runnable r, long interval);

    /**
     * Cancels an interval that was set by a call to 'setInterval'.
     */
    void clearInterval(Object interval);

    /**
     * Evaluates the given string after the given amount of time.
     * This method does not stall the script: the evaluation is
     * scheduled and the script continues its execution.
     * @return an object representing the timeout created.
     */
    Object setTimeout(String script, long timeout);

    /**
     * Calls the 'run' method of the given Runnable after the given
     * amount of time.  This method does not stall the script: the
     * evaluation is scheduled and the script continues its execution.
     * @return an object representing the timeout created.
     */
    Object setTimeout(Runnable r, long timeout);

    /**
     * Cancels an timeout that was set by a call to 'setTimeout'.
     */
    void clearTimeout(Object timeout);

    /**
     * Displays an alert dialog box.
     */
    void alert(String message);

    /**
     * Displays a confirm dialog box.
     */
    boolean confirm(String message);

    /**
     * Displays an input dialog box.
     * @return The input of the user, or null if the dialog was cancelled.
     */
    String prompt(String message);

    /**
     * Displays an input dialog box, given the default value.
     * @return The input of the user, or null if the dialog was cancelled.
     */
    String prompt(String message, String defVal);

    /**
     * Returns the associated interpreter.
     */
    Interpreter getInterpreter();
}
