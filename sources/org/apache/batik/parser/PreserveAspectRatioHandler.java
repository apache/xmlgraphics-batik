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
 * handler of a <code>PreserveAspectRatioParser</code> instance in order to
 * be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface PreserveAspectRatioHandler {
    /**
     * Invoked when the PreserveAspectRatio parsing starts.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void startPreserveAspectRatio() throws ParseException;

    /**
     * Invoked when 'none' been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void none() throws ParseException;

    /**
     * Invoked when 'xMaxYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMax() throws ParseException;

    /**
     * Invoked when 'xMaxYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMid() throws ParseException;

    /**
     * Invoked when 'xMaxYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMin() throws ParseException;

    /**
     * Invoked when 'xMidYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMax() throws ParseException;

    /**
     * Invoked when 'xMidYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMid() throws ParseException;

    /**
     * Invoked when 'xMidYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMin() throws ParseException;

    /**
     * Invoked when 'xMinYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMax() throws ParseException;

    /**
     * Invoked when 'xMinYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMid() throws ParseException;

    /**
     * Invoked when 'xMinYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMin() throws ParseException;

    /**
     * Invoked when 'meet' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void meet() throws ParseException;

    /**
     * Invoked when 'slice' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void slice() throws ParseException;

    /**
     * Invoked when the PreserveAspectRatio parsing ends.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio
     */
    void endPreserveAspectRatio() throws ParseException;
}
