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

    protected UserAgent userAgent;

    public UserAgentViewport(UserAgent ua) {
        this.userAgent = ua;
    }

    public float getWidth() {
        return (float) userAgent.getViewportSize().getWidth();
    }

    public float getHeight() {
        return (float) userAgent.getViewportSize().getHeight();
    }
}
