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
 * handler of a <code>LengthListParser</code> instance in order to be
 * notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public interface LengthListHandler extends LengthHandler {
    /**
     * Invoked when the length list attribute starts.
     * @exception ParseException if an error occures while processing the
     *                           length list.
     */
    void startLengthList() throws ParseException;

    /**
     * Invoked when the length attribute ends.
     * @exception ParseException if an error occures while processing the
     *                           length list.
     */
    void endLengthList() throws ParseException;
}
