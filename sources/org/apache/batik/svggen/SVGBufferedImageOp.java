/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
