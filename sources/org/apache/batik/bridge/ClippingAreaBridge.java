/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import java.awt.Shape;

/**
 * Factory class for vending clipping areas as <tt>Shape</tt> objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface ClippingAreaBridge extends Bridge {

    /**
     * Creates a <tt>Shape</tt> representing the clipping area using
     * of the element.
     * @param ctx the context to use
     * @param element the Element with the 'clip' attribute
     */
    Shape createClippingArea(BridgeContext ctx, Element element);

    /**
     * Updates an Element coresponding to the specified BridgeMutationEvent.
     * @param evt the event that describes the modification to perform
     */
    void update(BridgeMutationEvent evt);

}
