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

import java.awt.BasicStroke;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Java BasicStroke object into
 * a set of SVG style attributes
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGBasicStroke extends AbstractSVGConverter{
    /**
     * @param generatorContext used by converter to handle precision
     *        or to create elements.
     */
    public SVGBasicStroke(SVGGeneratorContext generatorContext) {
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
        if(gc.getStroke() instanceof BasicStroke)
            return toSVG((BasicStroke)gc.getStroke());
        else
            return null;
    }

    /**
     * @param stroke BasicStroke to convert to a set of
     *        SVG attributes
     * @return map of attributes describing the stroke
     */
    public final SVGStrokeDescriptor toSVG(BasicStroke stroke)
    {
        // Stroke width
        String strokeWidth = doubleString(stroke.getLineWidth());

        // Cap style
        String capStyle = endCapToSVG(stroke.getEndCap());

        // Join style
        String joinStyle = joinToSVG(stroke.getLineJoin());

        // Miter limit
        String miterLimit = doubleString(stroke.getMiterLimit());

        // Dash array
        float[] array = stroke.getDashArray();
        String dashArray = null;
        if(array != null)
            dashArray = dashArrayToSVG(array);
        else
            dashArray = SVG_NONE_VALUE;

        // Dash offset
        String dashOffset = doubleString(stroke.getDashPhase());

        return new SVGStrokeDescriptor(strokeWidth, capStyle,
                                       joinStyle, miterLimit,
                                       dashArray, dashOffset);
    }

    /**
     * @param dashArray float array to convert to a string
     */
    private final String dashArrayToSVG(float dashArray[]){
        StringBuffer dashArrayBuf = new StringBuffer();
        if(dashArray.length > 0)
            dashArrayBuf.append(doubleString(dashArray[0]));

        for(int i=1; i<dashArray.length; i++){
            dashArrayBuf.append(COMMA);
            dashArrayBuf.append(doubleString(dashArray[i]));
        }

        return dashArrayBuf.toString();
    }

    /**
     * @param lineJoin join style
     */
    private static String joinToSVG(int lineJoin){
        switch(lineJoin){
        case BasicStroke.JOIN_BEVEL:
            return SVG_BEVEL_VALUE;
        case BasicStroke.JOIN_ROUND:
            return SVG_ROUND_VALUE;
        case BasicStroke.JOIN_MITER:
        default:
            return SVG_MITER_VALUE;
        }
    }

    /**
     * @param endCap cap style
     */
    private static String endCapToSVG(int endCap){
        switch(endCap){
        case BasicStroke.CAP_BUTT:
            return SVG_BUTT_VALUE;
        case BasicStroke.CAP_ROUND:
            return SVG_ROUND_VALUE;
        default:
        case BasicStroke.CAP_SQUARE:
            return SVG_SQUARE_VALUE;
        }
    }
}
