/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.event.InputEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This interface represents an object which interacts with a GVT component.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Interactor
    extends KeyListener,
            MouseListener,
            MouseMotionListener {

    /**
     * Tells whether the given event will start the interactor.
     */
    boolean startInteraction(InputEvent ie);

    /**
     * Tells whether the interaction has finished.
     */
    boolean endInteraction();
}
