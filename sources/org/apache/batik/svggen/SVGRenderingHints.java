/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a RenderingHins object into
 * a set of SVG properties. Here is how individual hints
 * are converted.
 * + RENDERING -> sets all other hints to
 *                initial value.
 * + FRACTIONAL_METRICS -> sets initial values for
 *                         text-rendering and shape-rendering.
 * + ALPHA_INTERPOLATION -> Not mapped
 * + ANTIALIASING -> shape-rendering and text-rendering
 * + COLOR_RENDERING -> color-rendering
 * + DITHERING -> not mapped
 * + INTERPOLATION -> image-rendering
 * + TEXT_ANTIALIASING -> text-rendering
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRenderingHints extends AbstractSVGConverter{
    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        return toSVG(gc.getRenderingHints());
    }

    /**
     * @param hints RenderingHints object which should be converted
     *              to a set of SVG attributes.
     * @return map Map of attribute values that describe the hints
     */
    public static SVGHintsDescriptor toSVG(RenderingHints hints){
        String colorInterpolation = VALUE_SRGB;
        String colorRendering = VALUE_AUTO;
        String textRendering = VALUE_AUTO;
        String shapeRendering = VALUE_AUTO;
        String imageRendering = VALUE_AUTO;

        //
        // RENDERING
        //
        if(hints != null){
            Object rendering = hints.get(hints.KEY_RENDERING);
            if(rendering == hints.VALUE_RENDER_DEFAULT){
                colorInterpolation = VALUE_AUTO;
                colorRendering = VALUE_AUTO;
                textRendering = VALUE_AUTO;
                shapeRendering = VALUE_AUTO;
                imageRendering = VALUE_AUTO;
            }
            else if(rendering == hints.VALUE_RENDER_SPEED){
                colorInterpolation = VALUE_SRGB;
                colorRendering = VALUE_OPTIMIZE_SPEED;
                textRendering = VALUE_OPTIMIZE_SPEED;
                shapeRendering = VALUE_OPTIMIZE_SPEED;
                imageRendering = VALUE_OPTIMIZE_SPEED;
            }
            else if(rendering == hints.VALUE_RENDER_QUALITY){
                colorInterpolation = VALUE_LINEAR_RGB;
                colorRendering = VALUE_OPTIMIZE_QUALITY;
                textRendering = VALUE_OPTIMIZE_QUALITY;
                shapeRendering = VALUE_OPTIMIZE_QUALITY;
                imageRendering = VALUE_OPTIMIZE_QUALITY;
            }

            //
            // Fractional Metrics
            //
            Object fractionalMetrics = hints.get(hints.KEY_FRACTIONALMETRICS);
            if(fractionalMetrics == hints.VALUE_FRACTIONALMETRICS_ON){
                textRendering = VALUE_OPTIMIZE_QUALITY;
                shapeRendering = VALUE_OPTIMIZE_QUALITY;
            }
            else if(fractionalMetrics == hints.VALUE_FRACTIONALMETRICS_OFF){
                textRendering = VALUE_OPTIMIZE_SPEED;
                shapeRendering = VALUE_OPTIMIZE_SPEED;
            }
            else if(fractionalMetrics == hints.VALUE_FRACTIONALMETRICS_DEFAULT){
                textRendering = VALUE_AUTO;
                shapeRendering = VALUE_AUTO;
            }

            //
            // Antialiasing
            //
            Object antialiasing = hints.get(hints.KEY_ANTIALIASING);
            if(antialiasing == hints.VALUE_ANTIALIAS_ON){
                textRendering = VALUE_GEOMETRIC_PRECISION;
                shapeRendering = VALUE_GEOMETRIC_PRECISION;
            }
            else if(antialiasing == hints.VALUE_ANTIALIAS_OFF){
                textRendering = VALUE_OPTIMIZE_LEGIBILITY;
                shapeRendering = VALUE_CRISP_EDGES;
            }
            else if(antialiasing == hints.VALUE_ANTIALIAS_DEFAULT){
                textRendering = VALUE_AUTO;
                shapeRendering = VALUE_AUTO;
            }

            //
            // Text Antialiasing
            //
            Object textAntialiasing = hints.get(hints.KEY_TEXT_ANTIALIASING);
            if(textAntialiasing == hints.VALUE_TEXT_ANTIALIAS_ON)
                textRendering = VALUE_GEOMETRIC_PRECISION;
            else if(textAntialiasing == hints.VALUE_TEXT_ANTIALIAS_OFF)
                textRendering = VALUE_OPTIMIZE_LEGIBILITY;
            else if(textAntialiasing == hints.VALUE_TEXT_ANTIALIAS_DEFAULT)
                textRendering = VALUE_AUTO;

            //
            // Color Rendering
            //
            Object colorRenderingHint = hints.get(hints.KEY_COLOR_RENDERING);
            if(colorRenderingHint == hints.VALUE_COLOR_RENDER_DEFAULT)
                colorRendering = VALUE_AUTO;
            else if(colorRenderingHint == hints.VALUE_COLOR_RENDER_QUALITY)
                colorRendering = VALUE_OPTIMIZE_QUALITY;
            else if(colorRenderingHint == hints.VALUE_COLOR_RENDER_SPEED)
                colorRendering = VALUE_OPTIMIZE_SPEED;

            //
            // Interpolation
            //
            Object interpolation = hints.get(hints.KEY_INTERPOLATION);
            if(interpolation == hints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
                imageRendering = VALUE_OPTIMIZE_SPEED;
            else if(interpolation == hints.VALUE_INTERPOLATION_BICUBIC
                    ||
                    interpolation == hints.VALUE_INTERPOLATION_BILINEAR)
                imageRendering = VALUE_OPTIMIZE_QUALITY;
        } // if(hints != null)

        return new SVGHintsDescriptor(colorInterpolation,
                                      colorRendering,
                                      textRendering,
                                      shapeRendering,
                                      imageRendering);
    }
}
