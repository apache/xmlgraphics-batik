/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface must be implemented and then registred as the
 * handler of a <code>ClockParser</code> instance in order to be
 * notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ClockHandler {
    /**
     * Invoked when the clock attribute parsing starts.
     * @exception ParseException if an error occured while processing the clock
     */
    void startClock() throws ParseException;

    /**
     * Invoked when an int value has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void intValue(int v) throws ParseException;

    /**
     * Invoked when ':' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void colon() throws ParseException;

    /**
     * Invoked when '.' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void dot() throws ParseException;

    /**
     * Invoked when 'h' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void h() throws ParseException;

    /**
     * Invoked when 'min' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void min() throws ParseException;

    /**
     * Invoked when 's' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void s() throws ParseException;

    /**
     * Invoked when 'ms' has been parsed.
     * @exception ParseException if an error occured while processing the clock
     */
    void ms() throws ParseException;

    /**
     * Invoked when the clock attribute parsing ends.
     * @exception ParseException if an error occured while processing the clock
     */
    void endClock() throws ParseException;
}
