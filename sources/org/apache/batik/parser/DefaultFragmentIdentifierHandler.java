/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This class provides an adapter for FragmentIdentifierHandler.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultFragmentIdentifierHandler
    extends DefaultPreserveAspectRatioHandler
    implements FragmentIdentifierHandler {

    /**
     * The only instance of this class.
     */
    public final static FragmentIdentifierHandler INSTANCE
        = new DefaultFragmentIdentifierHandler();

    /**
     * This class does not need to be instantiated.
     */
    protected DefaultFragmentIdentifierHandler() {
    }

    /**
     * Implements {@link FragmentIdentifierHandler#startFragmentIdentifier()}.
     */
    public void startFragmentIdentifier() throws ParseException {
    }

    /**
     * Invoked when an ID has been parsed.
     * @param s The string that represents the parsed ID.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    public void idReference(String s) throws ParseException {

    }

    /**
     * Invoked when 'viewBox(x,y,width,height)' has been parsed.
     * @param x&nbsp;y&nbsp;width&nbsp;height the coordinates of the viewbox.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    public void viewBox(float x, float y, float width, float height)
        throws ParseException {

    }

    /**
     * Invoked when 'viewTarget(name)' has been parsed.
     * @param name the target name.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    public void viewTarget(String name) throws ParseException {

    }

    /**
     * Implements {@link FragmentIdentifierHandler#endFragmentIdentifier()}.
     */
    public void endFragmentIdentifier() throws ParseException {
    }
}
