/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Defines a viewport for a <tt>UserAgent</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class UserAgentViewport implements Viewport {

    private float width;
    private float height;

    /**
     * Constructs a new viewport for the specified user agent.
     * @param userAgent the user agent that defines the viewport
     */
    public UserAgentViewport(UserAgent userAgent) {
        width = (float) userAgent.getViewportSize().getWidth();
        height = (float) userAgent.getViewportSize().getHeight();
    }

    /**
     * Returns the width of this viewport.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the height of this viewport.
     */
    public float getHeight() {
        return height;
    }
}
