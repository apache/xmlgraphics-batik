/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

/**
 * An abstract adapter class for receiving graphics node change
 * events. The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 *
 * <p>Extend this class to create a <tt>GraphicsNodeChangeEvent</tt>
 * listener and override the methods for the events of interest. (If
 * you implement the <tt>GraphicsNodeChangeListener</tt> interface, you
 * have to define all of the methods in it. This abstract class
 * defines null methods for them all, so you can only have to define
 * methods for events you care about.)
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public abstract class GraphicsNodeChangeAdapter
        implements GraphicsNodeChangeListener {
    
    /**
     * Invoked when a change has started on a graphics node, but before
     * any changes occure in the graphics node it's self.
     * @param evt the graphics node change event
     */
    public void changeStarted  (GraphicsNodeChangeEvent gnce) { }

    /**
     * Invoked when a change on a graphics node has completed
     * @param evt the graphics node change event
     */
    public void changeCompleted(GraphicsNodeChangeEvent gnce) { }
}
