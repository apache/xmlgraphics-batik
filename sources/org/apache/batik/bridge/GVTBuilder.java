/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Document;

/**
 * This class is responsible on building a GVT tree using an SVG document.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GVTBuilder {

    /**
     * Builds a GVT tree using the specified context and SVG document.
     * @param ctx the context to use
     * @param svgDocument the DOM tree that represents an SVG document
     */
    GraphicsNode build(BridgeContext ctx, Document svgDocument);

}
