/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving keyboard focus events on a
 * graphics node.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeFocusListener extends EventListener {

    /**
     * Invoked when a graphics node gains the keyboard focus.
     */
    void focusGained(GraphicsNodeFocusEvent evt);

    /**
     * Invoked when a graphics node loses the keyboard focus.
     */
    void focusLost(GraphicsNodeFocusEvent evt);

}
