/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

/**
 * An abstract adapter class for receiving graphics node mouse
 * events. The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 *
 * <p>Extend this class to create a <tt>GraphicsNodeMouseEvent</tt>
 * listener and override the methods for the events of interest. (If
 * you implement the <tt>GraphicsNodeMouseListener</tt> interface, you
 * have to define all of the methods in it. This abstract class
 * defines null methods for them all, so you can only have to define
 * methods for events you care about.)
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class GraphicsNodeMouseAdapter
        implements GraphicsNodeMouseListener {

    public void mouseClicked(GraphicsNodeMouseEvent evt) {}

    public void mousePressed(GraphicsNodeMouseEvent evt) {}

    public void mouseReleased(GraphicsNodeMouseEvent evt) {}

    public void mouseEntered(GraphicsNodeMouseEvent evt) {}

    public void mouseExited(GraphicsNodeMouseEvent evt) {}

    public void mouseDragged(GraphicsNodeMouseEvent evt) {}

    public void mouseMoved(GraphicsNodeMouseEvent evt) {}

}
