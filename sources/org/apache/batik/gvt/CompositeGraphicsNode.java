/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.util.List;

import org.apache.batik.gvt.event.CompositeGraphicsNodeListener;

/**
 * A CompositeGraphicsNode is a graphics node that can contain graphics nodes.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface CompositeGraphicsNode extends GraphicsNode {

    /**
     * Returns the list of children or null if any.
     */
    List getChildren();

    /**
     * Adds the specified composite graphics node listener to receive
     * composite graphics node events from this node.
     * @param l the composite graphics node listener to add 
     */
    void addCompositeGraphicsNodeListener(CompositeGraphicsNodeListener l);

    /**
     * Removes the specified composite graphics node listener so that it
     * no longer receives composite graphics node events from this node.
     * @param l the composite graphics node listener to remove
     */
    void removeCompositeGraphicsNodeListener(CompositeGraphicsNodeListener l);

    /**
     * This constant is used when the Background Rect is not provided
     * and hence it defaults to the union of the bounding rects of all
     * graphics or the viewport which ever is smaller.
     */
    static Rectangle2D VIEWPORT = new Rectangle(0, 0, 0, 0);
    
    /**
     * If <tt>bgRgn == VIEWPORT</tt> then background enable is
     * activated for the entire viewable region.  If bgRgn is null
     * then background enable is <tt>accumulate</tt>.  If bgRgn is any
     * other Rectangle2D then it defines the bounds in the user coord
     * system for which drawing is enabled.
     */
    public void setBackgroundEnable(Rectangle2D bgRgn);

    /**
     * Returns the Rectangle defined as background.  If this is null
     * then you need to check the parents background-enable.
     */
    public Rectangle2D getBackgroundEnable();
}
