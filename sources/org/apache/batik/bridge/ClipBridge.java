/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Factory class for vending <tt>Shape</tt> objects that represents a
 * clipping area.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface ClipBridge extends Bridge {

    /**
     * Creates a <tt>Clip</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param clipElement the element that defines the clip
     * @param clipedElement the element that references the clip element
     * @param clipedNode the graphics node to clip
     */
    ClipRable createClip(BridgeContext ctx,
                         Element clipElement,
                         Element clipedElement,
                         GraphicsNode clipedNode);
}
