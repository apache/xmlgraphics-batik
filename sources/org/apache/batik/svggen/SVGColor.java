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

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Color object into a set of
 * corresponding SVG attributes.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                 org.apache.batik.svggen.DOMTreeManager
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
    private static Map colorMap = new HashMap();

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
     * @param generatorContext used by converter to handle precision
     *        or to create elements.
     */
    public SVGColor(SVGGeneratorContext generatorContext) {
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
        Paint paint = gc.getPaint();
        return toSVG((Color)paint, generatorContext);
    }

    /**
     * Converts a Color object to a set of two corresponding
     * values: a CSS color string and an opacity value.
     */
    public static SVGPaintDescriptor toSVG(Color color, SVGGeneratorContext gc) {
        //
        // First, convert the color value
        //
        String cssColor = (String)colorMap.get(color);
        if (cssColor==null) {
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
        float alpha = color.getAlpha()/255f;

        String alphaString = gc.doubleString(alpha);

        return new SVGPaintDescriptor(cssColor, alphaString);
    }
}
