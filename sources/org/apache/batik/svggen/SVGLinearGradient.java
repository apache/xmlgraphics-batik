/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Java GradientPaint into an
 * SVG linear gradient element
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLinearGradient extends AbstractSVGConverter{
    /**
     * @param domFactory used to build Elements
     */
    public SVGLinearGradient(Document domFactory){
        super(domFactory);
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
        Paint paint = gc.getPaint();
        return toSVG((GradientPaint)paint);
    }

    /**
     * @param gradient the GradientPaint to be converted
     * @return a description of the SVG paint and opacity corresponding
     to the gradient Paint. The definiton of the
     *         linearGradient is put in the linearGradientDefsMap
     */
    public SVGPaintDescriptor toSVG(GradientPaint gradient){
        // Reuse definition if gradient has already been converted
        SVGPaintDescriptor gradientDesc = (SVGPaintDescriptor)descMap.get(gradient);

        if(gradientDesc == null){
            Element gradientDef = domFactory.createElement(TAG_LINEAR_GRADIENT);
            gradientDef.setAttribute(ATTR_GRADIENT_UNITS,
                                     SVG_USER_SPACE_ON_USE_VALUE);

            //
            // Process gradient vector
            //
            Point2D p1 = gradient.getPoint1();
            Point2D p2 = gradient.getPoint2();
            gradientDef.setAttribute(ATTR_X1, "" + doubleString(p1.getX()));
            gradientDef.setAttribute(ATTR_Y1, "" + doubleString(p1.getY()));
            gradientDef.setAttribute(ATTR_X2, "" + doubleString(p2.getX()));
            gradientDef.setAttribute(ATTR_Y2, "" + doubleString(p2.getY()));

            //
            // Spread method
            //
            String spreadMethod = VALUE_PAD;
            if(gradient.isCyclic())
                spreadMethod = VALUE_REFLECT;
            gradientDef.setAttribute(ATTR_SPREAD_METHOD, spreadMethod);

            //
            // First gradient stop
            //
            Element gradientStop = domFactory.createElement(SVG_STOP_TAG);
            gradientStop.setAttribute(SVG_OFFSET_ATTRIBUTE,
                                      VALUE_ZERO_PERCENT);

            SVGPaintDescriptor colorDesc = SVGColor.toSVG(gradient.getColor1());
            gradientStop.setAttribute(ATTR_STOP_COLOR,
                                      colorDesc.getPaintValue());
            gradientStop.setAttribute(ATTR_STOP_OPACITY,
                                      colorDesc.getOpacityValue());

            gradientDef.appendChild(gradientStop);

            //
            // Second gradient stop
            //
            gradientStop = domFactory.createElement(SVG_STOP_TAG);
            gradientStop.setAttribute(SVG_OFFSET_ATTRIBUTE,
                                      VALUE_HUNDRED_PERCENT);

            colorDesc = SVGColor.toSVG(gradient.getColor2());
            gradientStop.setAttribute(ATTR_STOP_COLOR,
                                      colorDesc.getPaintValue());
            gradientStop.setAttribute(ATTR_STOP_OPACITY,
                                      colorDesc.getOpacityValue());

            gradientDef.appendChild(gradientStop);

            //
            // Gradient ID
            //
            gradientDef.setAttribute(ATTR_ID, SVGIDGenerator.generateID(ID_PREFIX_LINEAR_GRADIENT));

            //
            // Build Paint descriptor
            //
            StringBuffer paintAttrBuf = new StringBuffer(URL_PREFIX);
            paintAttrBuf.append(SIGN_POUND);
            paintAttrBuf.append(gradientDef.getAttribute(ATTR_ID));
            paintAttrBuf.append(URL_SUFFIX);

            gradientDesc = new SVGPaintDescriptor(paintAttrBuf.toString(),
                                                  VALUE_OPAQUE,
                                                  gradientDef);

            //
            // Update maps so that gradient can be reused if needed
            //
            descMap.put(gradient, gradientDesc);
            defSet.add(gradientDef);
        }

        return gradientDesc;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();

        GradientPaint gradient = new GradientPaint(20, 20,
                                                   Color.black,
                                                   300, 300,
                                                   new Color(220, 230, 240),
                                                   true);

        SVGLinearGradient converter = new SVGLinearGradient(domFactory);

        Element group = domFactory.createElement(SVG_G_TAG);
        Element defs = domFactory.createElement(SVG_DEFS_TAG);

        SVGPaintDescriptor gradientDesc = converter.toSVG(gradient);

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element linearGradientDef = (Element)iter.next();
            defs.appendChild(linearGradientDef);
        }

        Element rect = domFactory.createElement(TAG_RECT);
        rect.setAttribute(SVG_FILL_ATTRIBUTE, gradientDesc.getPaintValue());
        rect.setAttribute(SVG_FILL_OPACITY_ATTRIBUTE, gradientDesc.getOpacityValue());

        group.appendChild(defs);
        group.appendChild(rect);

        TestUtil.trace(group, System.out);
    }
}
