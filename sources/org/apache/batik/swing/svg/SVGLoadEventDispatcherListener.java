/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * This interface represents a listener to the
 * SVGLoadEventDispatcherEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGLoadEventDispatcherListener {

    /**
     * Called when a onload event dispatch started.
     */
    void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent e);

    /**
     * Called when a onload event dispatch was completed.
     */
    void svgLoadEventDispatchCompleted(SVGLoadEventDispatcherEvent e);

    /**
     * Called when a onload event dispatch was cancelled.
     */
    void svgLoadEventDispatchCancelled(SVGLoadEventDispatcherEvent e);

    /**
     * Called when a onload event dispatch failed.
     */
    void svgLoadEventDispatchFailed(SVGLoadEventDispatcherEvent e);

}
