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
 * handler of a <code>LengthParser</code> instance in order to be
 * notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface LengthHandler {
    /**
     * Invoked when the length attribute starts.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void startLength() throws ParseException;

    /**
     * Invoked when a float value has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void lengthValue(float v) throws ParseException;

    /**
     * Invoked when 'em' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void em() throws ParseException;

    /**
     * Invoked when 'ex' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void ex() throws ParseException;

    /**
     * Invoked when 'in' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void in() throws ParseException;

    /**
     * Invoked when 'cm' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void cm() throws ParseException;

    /**
     * Invoked when 'mm' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void mm() throws ParseException;

    /**
     * Invoked when 'pc' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void pc() throws ParseException;

    /**
     * Invoked when 'pt' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void pt() throws ParseException;

    /**
     * Invoked when 'px' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void px() throws ParseException;

    /**
     * Invoked when '%' has been parsed.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void percentage() throws ParseException;

    /**
     * Invoked when the length attribute ends.
     * @exception ParseException if an error occures while processing
     *                           the length
     */
    void endLength() throws ParseException;
}
