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
 * handler of a <code>PreserveAspectRatioParser</code> instance
 * in order to be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface FragmentIdentifierHandler {
    /**
     * Invoked when the fragment identifier attribute starts.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void startFragmentIdentifier() throws ParseException;

    /**
     * Invoked when an ID has been parsed.
     * @param s The string that represents the parsed ID.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void idReference(String s) throws ParseException;

    /**
     * Invoked when 'viewBox(x,y,width,height)' has been parsed.
     * @param x&nbsp;y&nbsp;width&nbsp;height the coordinates of the viewbox.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void viewBox(float x, float y, float width, float height)
        throws ParseException;

    /**
     * Invoked when 'viewTarget(name)' has been parsed.
     * @param name the target name.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void viewTarget(String name) throws ParseException;

    /**
     * Invoked when the points attribute ends.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void endFragmentIdentifier() throws ParseException;
}
