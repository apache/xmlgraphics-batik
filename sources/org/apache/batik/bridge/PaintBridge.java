/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Paint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.w3c.dom.Element;


/**
 * Factory class for vending <tt>Paint</tt> objects used to fill or
 * draw the outline of a <tt>Shape</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface PaintBridge extends Bridge {

    /**
     * Creates a <tt>Paint</tt> used to draw the outline of a
     * <tt>Shape</tt> of a <tt>ShapeNode</tt>.
     *
     * @param ctx the context to use
     * @param paintedNode the GraphicsNode for which a Paint is created
     * @param paintedElement the Element with 'stroke' and
     * 'stroke-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    Paint createStrokePaint(BridgeContext ctx,
                            GraphicsNode paintedNode,
                            Element paintedElement,
                            Element paintElement);
    /**
     * Creates a <tt>Paint</tt> used to fill a <tt>Shape</tt> of a
     * <tt>ShapeNode</tt>.
     *
     * @param ctx the context to use
     * @param paintedNode the GraphicsNode for which a Paint is created
     * @param paintedElement the Element with 'fill' and
     * 'fill-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    Paint createFillPaint(BridgeContext ctx,
                          GraphicsNode paintedNode,
                          Element paintedElement,
                          Element paintElement);

}
