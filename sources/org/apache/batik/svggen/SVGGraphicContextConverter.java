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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformStackElement;

/**
 * This class performs the task of converting the state of the
 * Java 2D API graphic context into a set of graphic attributes.
 * It also manages a set of SVG definitions referenced by the
 * SVG attributes.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGraphicContextConverter {
    private static final int GRAPHIC_CONTEXT_CONVERTER_COUNT = 6;

    private SVGTransform transformConverter;
    private SVGPaint paintConverter;
    private SVGBasicStroke strokeConverter;
    private SVGComposite compositeConverter;
    private SVGClip clipConverter;
    private SVGRenderingHints hintsConverter;
    private SVGFont fontConverter;
    private SVGConverter converters[] =
        new SVGConverter[GRAPHIC_CONTEXT_CONVERTER_COUNT];

    public SVGTransform getTransformConverter() { return transformConverter; }
    public SVGPaint getPaintConverter(){ return paintConverter; }
    public SVGBasicStroke getStrokeConverter(){ return strokeConverter; }
    public SVGComposite getCompositeConverter(){ return compositeConverter; }
    public SVGClip getClipConverter(){ return clipConverter; }
    public SVGRenderingHints getHintsConverter(){ return hintsConverter; }
    public SVGFont getFontConverter(){ return fontConverter; }

    /**
     * @param generatorContext the context that will be used to create
     * elements, handle extension and images.
     */
    public SVGGraphicContextConverter(SVGGeneratorContext generatorContext) {
        if (generatorContext == null)
            throw new SVGGraphics2DRuntimeException(ErrorConstants.ERR_CONTEXT_NULL);

        transformConverter = new SVGTransform(generatorContext);
        paintConverter = new SVGPaint(generatorContext);
        strokeConverter = new SVGBasicStroke(generatorContext);
        compositeConverter = new SVGComposite(generatorContext);
        clipConverter = new SVGClip(generatorContext);
        hintsConverter = new SVGRenderingHints(generatorContext);
        fontConverter = new SVGFont(generatorContext);

        int i=0;
        converters[i++] = paintConverter;
        converters[i++] = strokeConverter;
        converters[i++] = compositeConverter;
        converters[i++] = clipConverter;
        converters[i++] = hintsConverter;
        converters[i++] = fontConverter;
    }

    /**
     * @return a String containing the transform attribute value
     *         equivalent of the input transform stack.
     */
    public String toSVG(TransformStackElement transformStack[]) {
        return transformConverter.toSVGTransform(transformStack);
    }

    /**
     * @return an object that describes the set of SVG attributes that
     *         represent the equivalent of the input GraphicContext state.
     */
    public SVGGraphicContext toSVG(GraphicContext gc) {
        // no need for synchronized map => use HashMap
        Map groupAttrMap = new HashMap();

        for (int i=0; i<converters.length; i++) {
            SVGDescriptor desc = converters[i].toSVG(gc);
            if (desc != null)
                desc.getAttributeMap(groupAttrMap);
        }

        // the ctor will to the splitting (group/element) job
        return new SVGGraphicContext(groupAttrMap,
                                     gc.getTransformStack());
    }

    /**
     * @return a set of element containing definitions for the attribute
     *         values generated by this converter since its creation.
     */
    public List getDefinitionSet() {
        List defSet = new LinkedList();
        for(int i=0; i<converters.length; i++)
            defSet.addAll(converters[i].getDefinitionSet());

        return defSet;
    }
}
