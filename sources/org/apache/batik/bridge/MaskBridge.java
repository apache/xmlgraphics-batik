/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.Mask;
import org.w3c.dom.Element;

/**
 * Factory class for vending <tt>Mask</tt> objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface MaskBridge extends Bridge {

    /**
     * Creates a <tt>Mask</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param maskElement the element that defines the mask
     * @param maskedElement the element that references the mask element
     * @param maskedNode the graphics node to mask
     */
    Mask createMask(BridgeContext ctx,
                    Element maskElement,
                    Element maskedElement,
                    GraphicsNode maskedNode);

    /**
     * Updates the <tt>Mask</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the mask.
     *
     * @param evt the event that describes the modification to perform
     */
    void update(BridgeMutationEvent evt);

}
