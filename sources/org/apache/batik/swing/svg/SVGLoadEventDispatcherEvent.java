/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.util.EventObject;

import org.apache.batik.gvt.GraphicsNode;

/**
 * This class represents an event which indicate an event originated
 * from a SVGLoadEventDispatcher instance.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLoadEventDispatcherEvent extends EventObject {
    
    /**
     * The GVT root.
     */
    protected GraphicsNode gvtRoot;

    /**
     * Creates a new SVGLoadEventDispatcherEvent.
     * @param source the object that originated the event, ie. the
     *               SVGLoadEventDispatcher.
     * @param root   the GVT root.
     */
    public SVGLoadEventDispatcherEvent(Object source, GraphicsNode root) {
        super(source);
        gvtRoot = root;
    }

    /**
     * Returns the GVT tree root, or null if the gvt construction
     * was not completed or just started.
     */
    public GraphicsNode getGVTRoot() {
        return gvtRoot;
    }
}
