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

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class that converts a BufferedImageOp object into
 * an SVG filter.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGCustomBufferedImageOp
 * @see                org.apache.batik.svggen.SVGLookupOp
 * @see                org.apache.batik.svggen.SVGRescaleOp
 * @see                org.apache.batik.svggen.SVGConvolveOp
 */
public class SVGBufferedImageOp extends AbstractSVGFilterConverter {
    /**
     * All LookupOp convertion is handed to svgLookupOp
     */
    private SVGLookupOp svgLookupOp;

    /**
     * All RescaleOp convertion is handed to svgRescaleOp
     */
    private SVGRescaleOp svgRescaleOp;

    /**
     * All ConvolveOp convertion is handed to svgConvolveOp
     */
    private SVGConvolveOp svgConvolveOp;

    /**
     * All custom BufferedImageOp convertion is handed to '
     * svgCustomBufferedImageOp.
     */
    private SVGCustomBufferedImageOp svgCustomBufferedImageOp;

    /**
     * @param generatorContext used by the converter to create Element and other
     *        needed DOM objects and to handle unknown BufferedImageOp
     *        implementations.
     */
    public SVGBufferedImageOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.svgLookupOp = new SVGLookupOp(generatorContext);
        this.svgRescaleOp = new SVGRescaleOp(generatorContext);
        this.svgConvolveOp = new SVGConvolveOp(generatorContext);
        this.svgCustomBufferedImageOp =
            new SVGCustomBufferedImageOp(generatorContext);
    }

    /**
     * @return Set of filter Elements defining the BufferedImageOp this
     *         Converter has processed since it was created.
     */
    public List getDefinitionSet(){
        List filterSet = new LinkedList(svgLookupOp.getDefinitionSet());
        filterSet.addAll(svgRescaleOp.getDefinitionSet());
        filterSet.addAll(svgConvolveOp.getDefinitionSet());
        filterSet.addAll(svgCustomBufferedImageOp.getDefinitionSet());
        return filterSet;
    }

    public SVGLookupOp getLookupOpConverter(){
        return svgLookupOp;
    }

    public SVGRescaleOp getRescaleOpConverter(){
        return svgRescaleOp;
    }

    public SVGConvolveOp getConvolveOpConverter(){
        return svgConvolveOp;
    }

    public SVGCustomBufferedImageOp getCustomBufferedImageOpConverter(){
        return svgCustomBufferedImageOp;
    }

    /**
     * @param op BufferedImageOp to be converted to SVG
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return an SVGFilterDescriptor representing the SVG filter
     *         equivalent of the input BufferedImageOp
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp op,
                                     Rectangle filterRect){
        SVGFilterDescriptor filterDesc =
            svgCustomBufferedImageOp.toSVG(op, filterRect);

        if(filterDesc == null){
            if(op instanceof LookupOp)
                filterDesc = svgLookupOp.toSVG((LookupOp)op, filterRect);
            else if(op instanceof RescaleOp)
                filterDesc = svgRescaleOp.toSVG((RescaleOp)op, filterRect);
            else if(op instanceof ConvolveOp)
                filterDesc = svgConvolveOp.toSVG((ConvolveOp)op, filterRect);
        }

        return filterDesc;
    }
}
