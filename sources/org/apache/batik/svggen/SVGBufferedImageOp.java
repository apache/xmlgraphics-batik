/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

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
public class SVGBufferedImageOp extends AbstractSVGFilterConverter{
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
     * All custom BufferedImageOp convertion is handed to svgCustomBufferedImageOp
     */
    private SVGCustomBufferedImageOp svgCustomBufferedImageOp;

    /**
     * @param domFactory used by the converter to create Element and other
     *        needed DOM objects
     * @param extensionHandler can be invoked to handle unknown BufferedImageOp
     *        implementations.
     */
    public SVGBufferedImageOp(Document domFactory, ExtensionHandler extensionHandler){
        super(domFactory);
        this.svgLookupOp = new SVGLookupOp(domFactory);
        this.svgRescaleOp = new SVGRescaleOp(domFactory);
        this.svgConvolveOp = new SVGConvolveOp(domFactory);
        this.svgCustomBufferedImageOp = new SVGCustomBufferedImageOp(domFactory, extensionHandler);
    }

    /**
     * @param new extension handler this object should use
     */
    void setExtensionHandler(ExtensionHandler extensionHandler){
        this.svgCustomBufferedImageOp = new SVGCustomBufferedImageOp(domFactory, extensionHandler);
    }


    /**
     * @return Set of filter Elements defining the BufferedImageOp this
     *         Converter has processed since it was created.
     */
    public Set getDefinitionSet(){
        Set filterSet = new HashSet(svgLookupOp.getDefinitionSet());
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
        SVGFilterDescriptor filterDesc = svgCustomBufferedImageOp.toSVG(op, filterRect);

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

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        byte bi[] = new byte[256];
        for(int i=0; i<=255; i++)
            bi[i] = (byte)(0xff & (255-i));

        float kernelData[] = { 1, 1, 1,
                               2, 2, 2,
                               3, 3, 3 };
        Kernel kernel = new Kernel(3, 3, kernelData);

        BufferedImageOp ops[] = { new LookupOp(new ByteLookupTable(0, bi), null),
                                  new RescaleOp(4, 0, null),
                                  new ConvolveOp(kernel),
                                  new NullOp(),
        };

        Document domFactory = TestUtil.getDocumentPrototype();
        SVGBufferedImageOp converter = new SVGBufferedImageOp(domFactory,
                                                              new DefaultExtensionHandler());

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);

        for(int i=0; i<ops.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(ops[i], null);
            if(filterDesc != null){
                Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_RECT);
                rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
                rectGroupOne.appendChild(rect);
            }
        }

        for(int i=0; i<ops.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(ops[i], null);
            if(filterDesc != null){
                Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_RECT);
                rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
                rectGroupTwo.appendChild(rect);
            }
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element filterDef = (Element)iter.next();
            defs.appendChild(filterDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        TestUtil.trace(group, System.out);
    }
}
