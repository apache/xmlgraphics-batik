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
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Paint object into an
 * SVG element.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see              org.apache.batik.svggen.SVGLinearGradient
 * @see              org.apache.batik.svggen.SVGTexturePaint
 */
public class SVGPaint implements SVGConverter {
    /**
     * All GradientPaint convertions are handed to svgLinearGradient
     */
    private SVGLinearGradient svgLinearGradient;

    /**
     * All TexturePaint convertions are handed to svgTextureGradient
     */
    private SVGTexturePaint svgTexturePaint;

    /**
     * All Color convertions are handed to svgColor
     */
    private SVGColor svgColor;

    /**
     * All custom Paint convetions are handed to svgCustomPaint
     */
    private SVGCustomPaint svgCustomPaint;

    /**
     * Used to generate DOM elements
     */
    private SVGGeneratorContext generatorContext;

    /**
     * @param generatorContext the context.
     */
    public SVGPaint(SVGGeneratorContext generatorContext) {
        this.svgLinearGradient = new SVGLinearGradient(generatorContext);
        this.svgTexturePaint = new SVGTexturePaint(generatorContext);
        this.svgCustomPaint = new SVGCustomPaint(generatorContext);
        this.svgColor = new SVGColor(generatorContext);
        this.generatorContext = generatorContext;
    }

    /**
     * @return Set of Elements defining the Paints this
     *         converter has processed since it was created
     */
    public List getDefinitionSet(){
        List paintDefs = new LinkedList(svgLinearGradient.getDefinitionSet());
        paintDefs.addAll(svgTexturePaint.getDefinitionSet());
        paintDefs.addAll(svgCustomPaint.getDefinitionSet());
        paintDefs.addAll(svgColor.getDefinitionSet());
        return paintDefs;
    }

    public SVGTexturePaint getTexturePaintConverter(){
        return svgTexturePaint;
    }

    public SVGLinearGradient getGradientPaintConverter(){
        return svgLinearGradient;
    }

    public SVGCustomPaint getCustomPaintConverter(){
        return svgCustomPaint;
    }

    public SVGColor getColorConverter(){
        return svgColor;
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
        return toSVG(gc.getPaint());
    }

    /**
     * @param paint Paint to be converted to SVG
     * @return a descriptor of the corresponding SVG paint
     */
    public SVGPaintDescriptor toSVG(Paint paint){
        // we first try the extension handler because we may
        // want to override the way a Paint is managed!
        SVGPaintDescriptor paintDesc = svgCustomPaint.toSVG(paint);

        if (paintDesc == null) {
            if (paint instanceof Color)
                paintDesc = SVGColor.toSVG((Color)paint, generatorContext);
            else if (paint instanceof GradientPaint)
                paintDesc = svgLinearGradient.toSVG((GradientPaint)paint);
            else if (paint instanceof TexturePaint)
                paintDesc = svgTexturePaint.toSVG((TexturePaint)paint);
        }

        return paintDesc;
    }
}
