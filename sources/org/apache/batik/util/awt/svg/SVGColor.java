/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.Color;
import java.awt.Paint;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.*;

/**
 * Utility class that converts a Color object into a set of
 * corresponding SVG attributes.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                 org.apache.batik.util.awt.svg.DOMTreeManager
 */
public class SVGColor extends AbstractSVGConverter{
    /**
     * Predefined CSS colors
     */
    public static final Color aqua = new Color(0x00, 0xff, 0xff);
    public static final Color black = Color.black;
    public static final Color blue = Color.blue;
    public static final Color fuchsia = new Color(0xff, 0x00, 0xff);
    public static final Color gray = new Color(0x80, 0x80, 0x80);
    public static final Color green = new Color(0x00, 0x80, 0x00);
    public static final Color lime = new Color(0x00, 0xff, 0x00);
    public static final Color maroon = new Color(0x80, 0x00, 0x00);
    public static final Color navy = new Color(0x00, 0x00, 0x80);
    public static final Color olive = new Color(0x80, 0x80, 00);
    public static final Color purple = new Color(0x80, 0x00, 0x80);
    public static final Color red = new Color(0xff, 0x00, 0x00);
    public static final Color silver = new Color(0xc0, 0xc0, 0xc0);
    public static final Color teal = new Color(0x00, 0x80, 0x80);
    public static final Color white = Color.white;
    public static final Color yellow = Color.yellow;

    /**
     * Color map maps Color values to HTML 4.0 color names
     */
    private static Hashtable colorMap = new Hashtable();

    static {
        colorMap.put(black, "black");
        colorMap.put(silver, "silver");
        colorMap.put(gray, "gray");
        colorMap.put(white, "white");
        colorMap.put(maroon, "maroon");
        colorMap.put(red, "red");
        colorMap.put(purple, "purple");
        colorMap.put(fuchsia, "fuchsia");
        colorMap.put(green, "green");
        colorMap.put(lime, "lime");
        colorMap.put(olive, "olive");
        colorMap.put(yellow, "yellow");
        colorMap.put(navy, "navy");
        colorMap.put(blue, "blue");
        colorMap.put(teal, "teal");
        colorMap.put(aqua, "aqua");
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.util.awt.svg.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        Paint paint = gc.getPaint();
        return toSVG((Color)paint);
    }

    /**
     * Converts a Color object to a set of two corresponding
     * values: a CSS color string and an opacity value.
     */
    public static SVGPaintDescriptor toSVG(Color color){
        //
        // First, convert the color value
        //
        String cssColor = (String)colorMap.get(color);
        if(cssColor==null){
            // color is not one of the predefined colors
            StringBuffer cssColorBuffer = new StringBuffer(RGB_PREFIX);
            cssColorBuffer.append(color.getRed());
            cssColorBuffer.append(COMMA);
            cssColorBuffer.append(color.getGreen());
            cssColorBuffer.append(COMMA);
            cssColorBuffer.append(color.getBlue());
            cssColorBuffer.append(RGB_SUFFIX);
            cssColor = cssColorBuffer.toString();
        }

        //
        // Now, convert the alpha value, if needed
        //
        int alphaInt = color.getAlpha();
        float alpha = color.getAlpha()/255f;

        String alphaString = doubleString(alpha);

        return new SVGPaintDescriptor(cssColor, alphaString);
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{

        Color  testColors[] = {
            new Color(0x00, 0xff, 0xff), // aqua
            new Color(0x00, 0x00, 0x00), // black
            new Color(0x00, 0x00, 0xff), // blue
            new Color(0xff, 0x00, 0xff), // fuchsia
            new Color(0x80, 0x80, 0x80), // gray
            new Color(0x00, 0x80, 0x00), // green
            new Color(0x00, 0xff, 0x00), // lime
            new Color(0x80, 0x00, 0x00), // maroon
            new Color(0x00, 0x00, 0x80), // navy
            new Color(0x80, 0x80, 00),   // olive
            new Color(0x80, 0x00, 0x80), // purple
            new Color(0xff, 0x00, 0x00), // red
            new Color(0xc0, 0xc0, 0xc0), // silver
            new Color(0x00, 0x80, 0x80), // teal
            new Color(0xff, 0xff, 0xff), // white
            new Color(0xff, 0xff, 0x00), // yellow
            new Color(30, 40, 50),       // arbitrary 1
            new Color(255, 30, 200),     // arbitraty 2
            new Color(0, 0, 0, 128),     // arbitrary with alpha
            new Color(255, 255, 255, 64),// arbitrary with alpha
        };

        Document domFactory = TestUtil.getDocumentPrototype();
        Element group = domFactory.createElement(SVG_G_TAG);
        for(int i=0; i<testColors.length; i++){
            SVGPaintDescriptor paintDesc = toSVG(testColors[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(SVG_FILL_ATTRIBUTE, paintDesc.getPaintValue());
            rect.setAttribute(SVG_FILL_OPACITY_ATTRIBUTE, paintDesc.getOpacityValue());
            group.appendChild(rect);
        }

        TestUtil.trace(group, System.out);
    }
}
