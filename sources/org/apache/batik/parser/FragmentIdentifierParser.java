/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface represents an event-based parser for the SVG
 * fragment identifier (used with polyline and polygon elements).
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface FragmentIdentifierParser extends Parser {
    /**
     * Allows an application to register a fragment identifier handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    void setPointsHandler(FragmentIdentifierHandler handler);

    /**
     * Returns the points handler in use.
     */
    FragmentIdentifierHandler getPointsHandler();
}
