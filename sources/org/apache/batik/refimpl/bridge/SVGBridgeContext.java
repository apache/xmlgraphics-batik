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
        putBridge(SVG_NAMESPACE_URI, TAG_A,
                  new SVGAElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_CIRCLE,
                  new SVGCircleElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_CLIP_PATH,
                  new SVGClipPathElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_ELLIPSE,
                  new SVGEllipseElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_COLOR_MATRIX,
                  new SVGFeColorMatrixElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_COMPONENT_TRANSFER,
                  new SVGFeComponentTransferElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_COMPOSITE,
                  new SVGFeCompositeElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_FLOOD,
                  new SVGFeFloodElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_GAUSSIAN_BLUR,
                  new SVGFeGaussianBlurElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_IMAGE,
                  new SVGFeImageElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_MERGE,
                  new SVGFeMergeElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_MORPHOLOGY,
                  new SVGFeMorphologyElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_OFFSET,
                  new SVGFeOffsetElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_TILE,
                  new SVGFeTileElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FE_TURBULENCE,
                  new SVGFeTurbulenceElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_FILTER,
                  new SVGFilterElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_G,
                  new SVGGElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_IMAGE,
                  new SVGImageElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_LINE,
                  new SVGLineElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_LINEAR_GRADIENT,
                  new SVGLinearGradientBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_MASK,
                  new SVGMaskElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_PATH,
                  new SVGPathElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_PATTERN,
                  new SVGPatternElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_POLYLINE,
                  new SVGPolylineElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_POLYGON,
                  new SVGPolygonElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_RADIAL_GRADIENT,
                  new SVGRadialGradientBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_RECT,
                  new SVGRectElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_SVG,
                  new SVGSVGElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_SWITCH,
                  new SVGSwitchElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_TEXT,
                  new SVGTextElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_TEXT_PATH,
                  new SVGTextPathElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_USE,
                  new SVGUseElementBridge());
    }

}
