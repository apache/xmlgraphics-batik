/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGUserAgent;

/**
 * This class represents a general-purpose SVG component.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGCanvas extends JSVGComponent {
    
    /**
     * Creates a new JSVGCanvas.
     */
    public JSVGCanvas() {
        this(null, false, false);
    }

    /**
     * Creates a new JSVGCanvas.
     * @param ua a SVGUserAgent instance or null.
     * @param eventEnabled Whether the GVT tree should be reactive
     *        to mouse and key events.
     * @param selectableText Whether the text should be selectable.
     */
    public JSVGCanvas(SVGUserAgent ua, boolean eventsEnabled,
                      boolean selectableText) {
        super(ua, eventsEnabled, selectableText);
    }
}
