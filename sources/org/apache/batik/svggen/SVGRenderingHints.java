/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.awt.RenderingHints;

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
     * @param generatorContext used by converter to handle precision
     *        or to create elements.
     */
    public SVGRenderingHints(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

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
        // no hints should mean default
        String colorInterpolation = SVG_AUTO_VALUE;
        String colorRendering = SVG_AUTO_VALUE;
        String textRendering = SVG_AUTO_VALUE;
        String shapeRendering = SVG_AUTO_VALUE;
        String imageRendering = SVG_AUTO_VALUE;

        //
        // RENDERING
        //
        if(hints != null){
            Object rendering = hints.get(RenderingHints.KEY_RENDERING);
            if(rendering == RenderingHints.VALUE_RENDER_DEFAULT){
                colorInterpolation = SVG_AUTO_VALUE;
                colorRendering = SVG_AUTO_VALUE;
                textRendering = SVG_AUTO_VALUE;
                shapeRendering = SVG_AUTO_VALUE;
                imageRendering = SVG_AUTO_VALUE;
            }
            else if(rendering == RenderingHints.VALUE_RENDER_SPEED){
                colorInterpolation = SVG_SRGB_VALUE;
                colorRendering = SVG_OPTIMIZE_SPEED_VALUE;
                textRendering = SVG_OPTIMIZE_SPEED_VALUE;
                shapeRendering = SVG_GEOMETRIC_PRECISION_VALUE;
                imageRendering = SVG_OPTIMIZE_SPEED_VALUE;
            }
            else if(rendering == RenderingHints.VALUE_RENDER_QUALITY){
                colorInterpolation = SVG_LINEAR_RGB_VALUE;
                colorRendering = SVG_OPTIMIZE_QUALITY_VALUE;
                textRendering = SVG_OPTIMIZE_QUALITY_VALUE;
                shapeRendering = SVG_GEOMETRIC_PRECISION_VALUE;
                imageRendering = SVG_OPTIMIZE_QUALITY_VALUE;
            }

            //
            // Fractional Metrics
            //
            Object fractionalMetrics = hints.get(RenderingHints.KEY_FRACTIONALMETRICS);
            if(fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_ON){
                textRendering = SVG_OPTIMIZE_QUALITY_VALUE;
                shapeRendering = SVG_GEOMETRIC_PRECISION_VALUE;
            }
            else if(fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_OFF){
                textRendering = SVG_OPTIMIZE_SPEED_VALUE;
                shapeRendering = SVG_OPTIMIZE_SPEED_VALUE;
            }
            else if(fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT){
                textRendering = SVG_AUTO_VALUE;
                shapeRendering = SVG_AUTO_VALUE;
            }

            //
            // Antialiasing
            //
            Object antialiasing = hints.get(RenderingHints.KEY_ANTIALIASING);
            if(antialiasing == RenderingHints.VALUE_ANTIALIAS_ON){
                textRendering = SVG_OPTIMIZE_LEGIBILITY_VALUE;
                shapeRendering = SVG_AUTO_VALUE;
            }
            else if(antialiasing == RenderingHints.VALUE_ANTIALIAS_OFF){
                textRendering = SVG_GEOMETRIC_PRECISION_VALUE;
                shapeRendering = SVG_CRISP_EDGES_VALUE;
            }
            else if(antialiasing == RenderingHints.VALUE_ANTIALIAS_DEFAULT){
                textRendering = SVG_AUTO_VALUE;
                shapeRendering = SVG_AUTO_VALUE;
            }

            //
            // Text Antialiasing
            //
            Object textAntialiasing = hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            if(textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                textRendering = SVG_GEOMETRIC_PRECISION_VALUE;
            else if(textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
                textRendering = SVG_OPTIMIZE_SPEED_VALUE;
            else if(textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
                textRendering = SVG_AUTO_VALUE;

            //
            // Color Rendering
            //
            Object colorRenderingHint = hints.get(RenderingHints.KEY_COLOR_RENDERING);
            if(colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_DEFAULT)
                colorRendering = SVG_AUTO_VALUE;
            else if(colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_QUALITY)
                colorRendering = SVG_OPTIMIZE_QUALITY_VALUE;
            else if(colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_SPEED)
                colorRendering = SVG_OPTIMIZE_SPEED_VALUE;

            //
            // Interpolation
            //
            Object interpolation = hints.get(RenderingHints.KEY_INTERPOLATION);
            if(interpolation == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
                imageRendering = SVG_OPTIMIZE_SPEED_VALUE;
            else if(interpolation == RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    ||
                    interpolation == RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                imageRendering = SVG_OPTIMIZE_QUALITY_VALUE;
        } // if(hints != null)

        return new SVGHintsDescriptor(colorInterpolation,
                                      colorRendering,
                                      textRendering,
                                      shapeRendering,
                                      imageRendering);
    }
}
