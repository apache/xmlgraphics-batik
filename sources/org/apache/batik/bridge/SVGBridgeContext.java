/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.util.SVGConstants;

/**
 * A bridge context initialized with all bridges needed by the SVG spec.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGBridgeContext extends ConcreteBridgeContext
        implements SVGConstants {

    /**
     * The global bridges.
     */
    protected static HashMap globalBridges;

    /**
     * Registers a new global bridge.
     */
    public static void registerGlobalBridge(String namespaceURI,
                                            String localName,
                                            Bridge bridge) {
        if (globalBridges == null) {
            globalBridges = new HashMap(11);
        }
        Map ns = (Map)globalBridges.get(namespaceURI);
        if (ns == null) {
            globalBridges.put(namespaceURI, ns = new HashMap(11));
        }
        ns.put(localName, bridge);
    }

    public SVGBridgeContext() {
        // Register the global bridges
        if (globalBridges != null) {
            Iterator it = globalBridges.keySet().iterator();
            while (it.hasNext()) {
                String ns = (String)it.next();
                Map m = (Map)globalBridges.get(ns);
                if (m != null) {
                    Iterator mit = m.keySet().iterator();
                    while (mit.hasNext()) {
                        String ln = (String)mit.next();
                        putBridge(ns, ln, (Bridge)m.get(ln));
                    }
                }
            }
        }

        // Register the standard bridges
        putBridge(SVG_NAMESPACE_URI, SVG_A_TAG,
                  new SVGAElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_CIRCLE_TAG,
                  new SVGCircleElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_CLIP_PATH_TAG,
                  new SVGClipPathElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_ELLIPSE_TAG,
                  new SVGEllipseElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_BLEND_TAG,
                  new SVGFeBlendElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_COLOR_MATRIX_TAG,
                  new SVGFeColorMatrixElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_COMPONENT_TRANSFER_TAG,
                  new SVGFeComponentTransferElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_COMPOSITE_TAG,
                  new SVGFeCompositeElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_CONVOLVE_MATRIX_TAG,
                  new SVGFeConvolveMatrixElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_DIFFUSE_LIGHTING_TAG,
                  new SVGFeDiffuseLightingElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_DISPLACEMENT_MAP_TAG,
                  new SVGFeDisplacementMapElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_FLOOD_TAG,
                  new SVGFeFloodElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_GAUSSIAN_BLUR_TAG,
                  new SVGFeGaussianBlurElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_IMAGE_TAG,
                  new SVGFeImageElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_MERGE_TAG,
                  new SVGFeMergeElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_MORPHOLOGY_TAG,
                  new SVGFeMorphologyElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_OFFSET_TAG,
                  new SVGFeOffsetElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_SPECULAR_LIGHTING_TAG,
                  new SVGFeSpecularLightingElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_TILE_TAG,
                  new SVGFeTileElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FE_TURBULENCE_TAG,
                  new SVGFeTurbulenceElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_FILTER_TAG,
                  new SVGFilterElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_G_TAG,
                  new SVGGElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_IMAGE_TAG,
                  new SVGImageElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_LINE,
                  new SVGLineElementBridge());

        putBridge(SVG_NAMESPACE_URI, TAG_LINEAR_GRADIENT,
                  new SVGLinearGradientBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_MARKER_TAG,
                  new SVGMarkerElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_MASK_TAG,
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

        putBridge(SVG_NAMESPACE_URI, SVG_SWITCH_TAG,
                  new SVGSwitchElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_TEXT_TAG,
                  new SVGTextElementBridge());

        putBridge(SVG_NAMESPACE_URI, SVG_USE_TAG,
                  new SVGUseElementBridge());
    }

}
