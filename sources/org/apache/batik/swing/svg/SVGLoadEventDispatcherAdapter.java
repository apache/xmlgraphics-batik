/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * This class provides an adapter for the SVGLoadEventDispatcherListener
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLoadEventDispatcherAdapter
    implements SVGLoadEventDispatcherListener {

    /**
     * Called when a onload event dispatch started.
     */
    public void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent e) {
    }

    /**
     * Called when a onload event dispatch was completed.
     */
    public void svgLoadEventDispatchCompleted(SVGLoadEventDispatcherEvent e) {
    }

    /**
     * Called when a onload event dispatch was cancelled.
     */
    public void svgLoadEventDispatchCancelled(SVGLoadEventDispatcherEvent e) {
    }

    /**
     * Called when a onload event dispatch failed.
     */
    public void svgLoadEventDispatchFailed(SVGLoadEventDispatcherEvent e) {
    }
}
