/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.w3c.dom.svg;

/**
 * This interface must be implemented in order to call Java code from
 * an SVG document.
 *
 * A <code>EventListenerInitializer</code> instance is called when
 * a 'script' element's 'type' attribute value is 'application/java-archive' and when
 * the manifest of the jar file referenced by the 'xlink:href' attribute contains
 * a 'SVG-Handler-Class' entry.  The value of this entry must be the classname of the
 * <code>EventListenerInitializer</code> to call.
 *
 * This classes implementing this interface must have a default
 * constructor.
 *
 * @version $Id$
 */
public interface EventListenerInitializer {

    /**
     * This method is called by the SVG viewer
     * when the scripts are loaded to register
     * the listener needed.
     * @param doc The current document.
     */
    public void initializeEventListeners(SVGDocument doc);
}
