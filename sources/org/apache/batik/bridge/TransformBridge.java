/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import java.awt.geom.AffineTransform;

/**
 * Factory class for vending <tt>AffineTransform</tt> objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface TransformBridge extends Bridge {

    /**
     * Creates a <tt>AffineTransform</tt> using the specified context
     * and element.
     * @param ctx the context to use
     * @param element the Element with the 'transform' attribute
     */
    AffineTransform createTransform(BridgeContext ctx, Element element);

}
