/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;

import org.apache.batik.ext.awt.image.renderable.DistantLight;
import org.apache.batik.ext.awt.image.renderable.Light;
import org.apache.batik.ext.awt.image.renderable.PointLight;
import org.apache.batik.ext.awt.image.renderable.SpotLight;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;feDiffuseLighting> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class SVGFeAbstractLightingElementBridge
    extends SVGAbstractFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the lighting filter primitives.
     */
    protected SVGFeAbstractLightingElementBridge() {}

    /**
     * Returns the light from the specified lighting filter primitive
     * element or null if any
     *
     * @param filterElement the lighting filter primitive element
     * @param ctx the bridge context
     */
    protected static
        Light extractLight(Element filterElement, BridgeContext ctx) {

        Color color = CSSUtilities.convertLightingColor(filterElement, ctx);

        for (Node n = filterElement.getFirstChild();
             n != null;
             n = n.getNextSibling()) {

            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            Bridge bridge = ctx.getBridge(e);
            if (bridge == null ||
                !(bridge instanceof SVGFeAbstractLightElementBridge)) {
                continue;
            }
            return ((SVGFeAbstractLightElementBridge)bridge).createLight
                (ctx, filterElement, e, color);
        }
        return null;
    }

    /**
     * The base bridge class for light element.
     */
    protected static abstract class SVGFeAbstractLightElementBridge
        implements Bridge {

        /**
         * Creates a <tt>Light</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param filterElement the lighting filter primitive element
         * @param lightElement the element describing a light
         * @param color the color of the light
         */
        public abstract Light createLight(BridgeContext ctx,
                                          Element filterElement,
                                          Element lightElement,
                                          Color color);

        /**
         * Performs an update according to the specified event.
         *
         * @param evt the event describing the update to perform
         */
        public void update(BridgeMutationEvent evt) {
            throw new Error("Not implemented");
        }
    }

    /**
     * Bridge class for the &lt;feSpotLight> element.
     */
    public static class SVGFeSpotLightElementBridge
        extends SVGFeAbstractLightElementBridge {

        /**
         * Constructs a new bridge for a light element.
         */
        public SVGFeSpotLightElementBridge() {}

        /**
         * Creates a <tt>Light</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param filterElement the lighting filter primitive element
         * @param lightElement the element describing a light
         * @param color the color of the light
         */
        public Light createLight(BridgeContext ctx,
                                 Element filterElement,
                                 Element lightElement,
                                 Color color) {

            // 'x' attribute - default is 0
            double x = convertNumber(lightElement, SVG_X_ATTRIBUTE, 0);

            // 'y' attribute - default is 0
            double y = convertNumber(lightElement, SVG_Y_ATTRIBUTE, 0);

            // 'z' attribute - default is 0
            double z = convertNumber(lightElement, SVG_Z_ATTRIBUTE, 0);

            // 'pointsAtX' attribute - default is 0
            double px
                = convertNumber(lightElement, SVG_POINTS_AT_X_ATTRIBUTE, 0);

            // 'pointsAtY' attribute - default is 0
            double py
                = convertNumber(lightElement, SVG_POINTS_AT_Y_ATTRIBUTE, 0);

            // 'pointsAtZ' attribute - default is 0
            double pz
                = convertNumber(lightElement, SVG_POINTS_AT_Z_ATTRIBUTE, 0);

            // 'specularExponent' attribute - default is 1
            double specularExponent = convertNumber
                (lightElement, SVG_SPECULAR_EXPONENT_ATTRIBUTE, 1);

            // 'limitingConeAngle' attribute - default is 90
            double limitingConeAngle = convertNumber
                (lightElement, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE, 90);

            return new SpotLight(x, y, z,
                                 px, py, pz,
                                 specularExponent,
                                 limitingConeAngle,
                                 color);
        }
    }

    /**
     * Bridge class for the &lt;feDistantLight> element.
     */
    public static class SVGFeDistantLightElementBridge
        extends SVGFeAbstractLightElementBridge {

        /**
         * Constructs a new bridge for a light element.
         */
        public SVGFeDistantLightElementBridge() {}

        /**
         * Creates a <tt>Light</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param filterElement the lighting filter primitive element
         * @param lightElement the element describing a light
         * @param color the color of the light
         */
        public Light createLight(BridgeContext ctx,
                                 Element filterElement,
                                 Element lightElement,
                                 Color color) {

            // 'azimuth' attribute - default is 0
            double azimuth
                = convertNumber(lightElement, SVG_AZIMUTH_ATTRIBUTE, 0);

            // 'elevation' attribute - default is 0
            double elevation
                = convertNumber(lightElement, SVG_ELEVATION_ATTRIBUTE, 0);

            return new DistantLight(azimuth, elevation, color);
        }
    }

    /**
     * Bridge class for the &lt;fePointLight> element.
     */
    public static class SVGFePointLightElementBridge
        extends SVGFeAbstractLightElementBridge {

        /**
         * Constructs a new bridge for a light element.
         */
        public SVGFePointLightElementBridge() {}

        /**
         * Creates a <tt>Light</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param filterElement the lighting filter primitive element
         * @param lightElement the element describing a light
         * @param color the color of the light
         */
        public Light createLight(BridgeContext ctx,
                                 Element filterElement,
                                 Element lightElement,
                                 Color color) {

            // 'x' attribute - default is 0
            double x = convertNumber(lightElement, SVG_X_ATTRIBUTE, 0);

            // 'y' attribute - default is 0
            double y = convertNumber(lightElement, SVG_Y_ATTRIBUTE, 0);

            // 'z' attribute - default is 0
            double z = convertNumber(lightElement, SVG_Z_ATTRIBUTE, 0);

            return new PointLight(x, y, z, color);
        }
    }
}
