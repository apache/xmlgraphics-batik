/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Paint;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.w3c.dom.Element;

/**
 * Utility class that converts an custom Paint object into
 * a set of SVG properties and definitions.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGPaint
 */
public class SVGCustomPaint extends AbstractSVGConverter {
    /**
     * @param generatorContext the context.
     */
    public SVGCustomPaint(SVGGeneratorContext generatorContext) {
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
    public SVGDescriptor toSVG(GraphicContext gc) {
        return toSVG(gc.getPaint());
    }

    /**
     * @param paint the Paint object to convert to SVG
     * @return a description of the SVG paint and opacity corresponding
     *         to the Paint. The definiton of the paint is put in the
     *         linearGradientDefsMap
     */
    public SVGPaintDescriptor toSVG(Paint paint) {
        SVGPaintDescriptor paintDesc = (SVGPaintDescriptor)descMap.get(paint);

        if (paintDesc == null) {
            // First time this paint is used. Request handler
            // to do the convertion
            paintDesc =
                generatorContext.extensionHandler.
                handlePaint(paint,
                            generatorContext);

            if (paintDesc != null) {
                Element def = paintDesc.getDef();
                if(def != null) defSet.add(def);
                descMap.put(paint, paintDesc);
            }
        }

        return paintDesc;
    }
}
