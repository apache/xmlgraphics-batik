/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.bridge.BridgeContext;

/**
 * A bridge context initialized with all bridges needed by the SVG spec.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGBridgeContext extends ConcreteBridgeContext
        implements SVGConstants {

    public SVGBridgeContext() {
        putBridge(SVG_NAMESPACE_URI, TAG_CIRCLE, new SVGCircleElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_ELLIPSE,
                  new SVGEllipseElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_FILTER, new SVGFilterElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_FE_FLOOD,
                  new SVGFeFloodElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_FE_GAUSSIAN_BLUR,
                  new SVGFeGaussianBlurElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_FE_TURBULENCE,
                  new SVGFeTurbulenceElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_G, new SVGGElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_LINE, new SVGLineElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_PATH, new SVGPathElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_POLYLINE,
                  new SVGPolylineElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_POLYGON,
                  new SVGPolygonElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_RECT, new SVGRectElementBridge()); 
        putBridge(SVG_NAMESPACE_URI, TAG_SVG, new SVGSVGElementBridge());
        putBridge(SVG_NAMESPACE_URI, TAG_TEXT, new SVGTextElementBridge());
    }

}
