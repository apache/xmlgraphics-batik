/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.font.*;

import org.w3c.dom.*;

/**
 * This class performs the task of converting the state of the
 * Java 2D API graphic context into a set of graphic attributes.
 * It also manages a set of SVG definitions referenced by the
 * SVG attributes.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGraphicContextConverter implements SVGSyntax{
    public static final String ERROR_NULL_INPUT = "domFactory, extensionHandler and imageHandler should not be null";
    private static final int GRAPHIC_CONTEXT_CONVERTER_COUNT = 6;

    private String leafOnlyAttributes[] = { ATTR_OPACITY,
                                            SVG_FILTER_ATTRIBUTE,
                                            SVG_CLIP_PATH_ATTRIBUTE
    };

    private SVGTransform transformConverter;
    private SVGPaint paintConverter;
    private SVGBasicStroke strokeConverter;
    private SVGComposite compositeConverter;
    private SVGClip clipConverter;
    private SVGRenderingHints hintsConverter;
    private SVGFont fontConverter;
    private SVGConverter converters[] = new SVGConverter[GRAPHIC_CONTEXT_CONVERTER_COUNT];

    public SVGTransform getTransformConverter() { return transformConverter; }
    public SVGPaint getPaintConverter(){ return paintConverter; }
    public SVGBasicStroke getStrokeConverter(){ return strokeConverter; }
    public SVGComposite getCompositeConverter(){ return compositeConverter; }
    public SVGClip getClipConverter(){ return clipConverter; }
    public SVGRenderingHints getHintsConverter(){ return hintsConverter; }
    public SVGFont getFontConverter(){ return fontConverter; }

    /**
     * @param domFactory used to create top level svg root node
     *                    and children group nodes.
     * @param extensionHandler used by SVGConverters to handle custom
     *                         implementations of interfaces such as Paint,
     *                         Composite and BufferedImageOp.
     * @param imageHandler used by SVGConverters that need to create
     *                     image elements (e.g., SVGTexturePaint)
     */
    public SVGGraphicContextConverter(Document domFactory,
                                      ExtensionHandler extensionHandler,
                                      ImageHandler imageHandler){
        if(domFactory==null ||
           extensionHandler==null ||
           imageHandler == null)
            throw new IllegalArgumentException(ERROR_NULL_INPUT);

        transformConverter = new SVGTransform();
        paintConverter = new SVGPaint(domFactory, imageHandler, extensionHandler);
        strokeConverter = new SVGBasicStroke();
        compositeConverter = new SVGComposite(domFactory, extensionHandler);
        clipConverter = new SVGClip(domFactory);
        hintsConverter = new SVGRenderingHints();
        fontConverter = new SVGFont();

        int i=0;
        converters[i++] = paintConverter;
        converters[i++] = strokeConverter;
        converters[i++] = compositeConverter;
        converters[i++] = clipConverter;
        converters[i++] = hintsConverter;
        converters[i++] = fontConverter;
    }

    /**
     * @param new extension handler this object should use
     */
    void setExtensionHandler(ExtensionHandler extensionHandler){
        paintConverter.setExtensionHandler(extensionHandler);
        compositeConverter.setExtensionHandler(extensionHandler);
    }

    /**
     * @return a String containing the transform attribute value
     *         equivalent of the input transform stack.
     */
    public String toSVG(TransformStackElement transformStack[]){
        return transformConverter.toSVGTransform(transformStack);
    }

    /**
     * @return an object that describes the set of SVG attributes that
     *         represent the equivalent of the input GraphicContext state.
     */
    public SVGGraphicContext toSVG(GraphicContext gc){
        Map groupAttrMap = new Hashtable();

        for(int i=0; i<converters.length; i++){
            SVGDescriptor desc = converters[i].toSVG(gc);
            if(desc != null)
                desc.getAttributeMap(groupAttrMap);
        }

        //
        // Now, move attributes that only apply to
        // leaf elements to a separate map.
        //
        Map graphicElementsMap = new Hashtable();
        for(int i=0; i<leafOnlyAttributes.length; i++){
            Object attrValue = groupAttrMap.get(leafOnlyAttributes[i]);
            if(attrValue != null){
                graphicElementsMap.put(leafOnlyAttributes[i], attrValue);
                groupAttrMap.remove(leafOnlyAttributes[i]);
            }
        }

        return new SVGGraphicContext(groupAttrMap, graphicElementsMap, gc.getTransformStack());
    }

    /**
     * @return a set of element containing definitions for the attribute
     *         values generated by this converter since its creation.
     */
    public Set getDefinitionSet(){
        Set defSet = new HashSet();
        for(int i=0; i<converters.length; i++)
            defSet.addAll(converters[i].getDefinitionSet());

        return defSet;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Document domFactory = TestUtil.getDocumentPrototype();

        //
        // Create a GraphicContext and do the following:
        // a. Dump list of default SVG attributes
        // b. Modify the value of each of the context attributes
        // c. Dump new list of SVG attributes
        // d. Dump list of defs
        //

        GraphicContext gc = new GraphicContext(new AffineTransform());
        SVGGraphicContextConverter converter = new SVGGraphicContextConverter(domFactory,
                                                                              new DefaultExtensionHandler(),
                                                                              new DefaultImageHandler());
        SVGGraphicContext defaultSVGGC = converter.toSVG(gc);
        traceSVGGC(defaultSVGGC, converter);

        // Transform
        gc.translate(40, 40);

        // Paint
        gc.setPaint(new GradientPaint(0, 0, Color.yellow, 200, 200, Color.red));

        // Stroke
        gc.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 40, new float[]{ 4, 5, 6, 7 }, 3));

        // Composite
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, .25f));

        // Clip
        gc.setClip(new Ellipse2D.Double(20, 30, 40, 50));

        // Hints
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Font
        gc.setFont(new Font("French Script MT", Font.BOLD, 45));

        SVGGraphicContext modifiedSVGGC = converter.toSVG(gc);

        traceSVGGC(modifiedSVGGC, converter);

        Set defSet = converter.getDefinitionSet();
        Iterator iter = defSet.iterator();
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        while(iter.hasNext()){
            Element def = (Element)iter.next();
            defs.appendChild(def);
        }

        TestUtil.trace(defs, System.out);
    }

    /**
     * For unit testing only
     */
    static void traceSVGGC(SVGGraphicContext svgGC, SVGGraphicContextConverter converter){
        System.out.println("=============================================");
        Map groupAttrMap = svgGC.getGroupContext();
        Iterator iter = groupAttrMap.keySet().iterator();
        while(iter.hasNext()){
            String attrName = (String)iter.next();
            String attrValue = (String)groupAttrMap.get(attrName);
            System.out.println(attrName + " = " + attrValue);
        }

        System.out.println("++++++++++++++++++");

        Map geAttrMap = svgGC.getGraphicElementContext();
        iter = geAttrMap.keySet().iterator();
        while(iter.hasNext()){
            String attrName = (String)iter.next();
            String attrValue = (String)geAttrMap.get(attrName);
            System.out.println(attrName + " = " + attrValue);
        }

        System.out.println("++++++++++++++++++");
        System.out.println("transform: " + converter.toSVG(svgGC.getTransformStack()));

        System.out.println("=============================================");

    }
}
