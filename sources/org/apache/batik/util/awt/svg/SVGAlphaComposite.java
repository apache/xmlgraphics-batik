/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts an AlphaComposite object into
 * a set of SVG properties and definitions. Here is
 * how AlphaComposites are mapped to SVG:
 * + AlphaComposite.SRC_OVER with extra alpha is mapped
 *   to the opacity attribute
 * + AlphaComposite's other rules are translated into
 *   predefined filter effects.
 * One of the big differences between AlphaComposite and
 * the SVG feComposite filter is that feComposite does not
 * have the notion of extra alpha applied to the source.
 * The extra alpha equivalent is obtained by setting the
 * opacity property on the nodes to be composited.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.SVGAlphaComposite
 */
public class SVGAlphaComposite extends AbstractSVGConverter{
    /**
     * Map of all possible AlphaComposite filter equivalents
     */
    private Map compositeDefsMap = new Hashtable();

    /**
     * By default, access to the background is not required.
     */
    private boolean backgroundAccessRequired = false;

    /**
     * @param domFactory  for use by SVGAlphaComposite to build Elements
     */
    public SVGAlphaComposite(Document domFactory){
        super(domFactory);

        //
        // Initialize map of AlphaComposite filter definitions
        //
        compositeDefsMap.put(AlphaComposite.Src, compositeToSVG(AlphaComposite.Src));
        compositeDefsMap.put(AlphaComposite.SrcIn, compositeToSVG(AlphaComposite.SrcIn));
        compositeDefsMap.put(AlphaComposite.SrcOut, compositeToSVG(AlphaComposite.SrcOut));
        compositeDefsMap.put(AlphaComposite.DstIn, compositeToSVG(AlphaComposite.DstIn));
        compositeDefsMap.put(AlphaComposite.DstOut, compositeToSVG(AlphaComposite.DstOut));
        compositeDefsMap.put(AlphaComposite.DstOver, compositeToSVG(AlphaComposite.DstOver));
        compositeDefsMap.put(AlphaComposite.Clear, compositeToSVG(AlphaComposite.Clear));
    }

    /**
     * @return set of all AlphaComposite filter definitions
     */
    public Set getAlphaCompositeFilterSet(){
        return new HashSet(compositeDefsMap.values());
    }

    /**
     * @return true if background access is required for any
     *         of the converted AlphaComposite rules
     */
    public boolean requiresBackgroundAccess(){
        return backgroundAccessRequired;
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
        return toSVG((AlphaComposite)gc.getComposite());
    }

    /**
     * @param composite the AlphaComposite object to convert to SVG
     * @return an SVGCompositeDescriptor that defines how to map the
     *         input composite in SVG
     */
    public SVGCompositeDescriptor toSVG(AlphaComposite composite){
        SVGCompositeDescriptor compositeDesc = (SVGCompositeDescriptor)descMap.get(composite);

        if(compositeDesc == null){
            // Process the composite opacity
            String opacityValue = doubleString(composite.getAlpha());

            // For all rules different than SRC_OVER, a filter is
            // needed to represent the composition rule.
            String filterValue = null;
            Element filterDef = null;
            if(composite.getRule() != AlphaComposite.SRC_OVER){
                // Note that the extra alpha is ignored by using the
                // majorComposite. The extra alpha is already represented
                // by the ATTR_OPACITY value.
                AlphaComposite majorComposite = AlphaComposite.getInstance(composite.getRule());
                filterDef = (Element)compositeDefsMap.get(majorComposite);
                defSet.add(filterDef);

                // Process the filter value
                StringBuffer filterAttrBuf = new StringBuffer(URL_PREFIX);
                filterAttrBuf.append(SIGN_POUND);
                filterAttrBuf.append(filterDef.getAttribute(ATTR_ID));
                filterAttrBuf.append(URL_SUFFIX);

                filterValue = filterAttrBuf.toString();
            }
            else
                filterValue = VALUE_NONE;

            compositeDesc = new SVGCompositeDescriptor(opacityValue, filterValue, filterDef);

            descMap.put(composite, compositeDesc);
        }

        if(composite.getRule() != AlphaComposite.SRC_OVER)
            backgroundAccessRequired = true;

        return compositeDesc;
    }

    /**
     * @param composite AlphaComposite to convert to a filter effect
     * @exception Error if an AlphaComposite with SRC_OVER rule in passed to
     *            this method.
     */
    private Element compositeToSVG(AlphaComposite composite){
        // operator is equivalent to rule
        String operator = null;

        // input1 is equivalent to Src
        String input1 = null;

        // input2 is equivalent to Dst
        String input2 = null;

        // k2 is used only for arithmetic
        // to obtain the equivalent of SRC
        String k2 = "0";

        // ID used to identify the composite
        String id = null;

        switch(composite.getRule()){
        case AlphaComposite.CLEAR:
            operator = VALUE_ARITHMETIC;
            input1 = VALUE_SOURCE_GRAPHIC;
            input2 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_CLEAR;
            break;
        case AlphaComposite.SRC:
            operator = VALUE_ARITHMETIC;
            input1 = VALUE_SOURCE_GRAPHIC;
            input2 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_SRC;
            k2 = VALUE_DIGIT_ONE;
            break;
        case AlphaComposite.SRC_IN:
            operator = VALUE_IN;
            input1 = VALUE_SOURCE_GRAPHIC;
            input2 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_SRC_IN;
            break;
        case AlphaComposite.SRC_OUT:
            operator = VALUE_OUT;
            input1 = VALUE_SOURCE_GRAPHIC;
            input2 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_SRC_OUT;
            break;
        case AlphaComposite.DST_IN:
            operator = VALUE_IN;
            input2 = VALUE_SOURCE_GRAPHIC;
            input1 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_DST_IN;
            break;
        case AlphaComposite.DST_OUT:
            operator = VALUE_OUT;
            input2 = VALUE_SOURCE_GRAPHIC;
            input1 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_DST_OUT;
            break;
        case AlphaComposite.DST_OVER:
            operator = VALUE_OVER;
            input2 = VALUE_SOURCE_GRAPHIC;
            input1 = VALUE_BACKGROUND_IMAGE;
            id = ID_PREFIX_ALPHA_COMPOSITE_DST_OVER;
            break;
        default:
            throw new Error();
        }

        Element compositeFilter = domFactory.createElement(TAG_FILTER);
        compositeFilter.setAttribute(ATTR_ID, id);
        compositeFilter.setAttribute(ATTR_FILTER_UNITS, VALUE_OBJECT_BOUNDING_BOX);
        compositeFilter.setAttribute(ATTR_X, VALUE_ZERO_PERCENT);
        compositeFilter.setAttribute(ATTR_Y, VALUE_ZERO_PERCENT);
        compositeFilter.setAttribute(ATTR_WIDTH, VALUE_HUNDRED_PERCENT);
        compositeFilter.setAttribute(ATTR_HEIGHT, VALUE_HUNDRED_PERCENT);

        Element feComposite = domFactory.createElement(TAG_FE_COMPOSITE);
        feComposite.setAttribute(ATTR_OPERATOR, operator);
        feComposite.setAttribute(ATTR_IN, input1);
        feComposite.setAttribute(ATTR_IN2, input2);
        feComposite.setAttribute(ATTR_K2, k2);
        feComposite.setAttribute(ATTR_RESULT, VALUE_COMPOSITE);

        Element feFlood = domFactory.createElement(TAG_FE_FLOOD);
        feFlood.setAttribute(ATTR_FLOOD_COLOR, "white");
        feFlood.setAttribute(ATTR_FLOOD_OPACITY, "1");
        feFlood.setAttribute(ATTR_RESULT, VALUE_FLOOD);


        Element feMerge = domFactory.createElement(TAG_FE_MERGE);
        Element feMergeNodeFlood = domFactory.createElement(TAG_FE_MERGE_NODE);
        feMergeNodeFlood.setAttribute(ATTR_IN, VALUE_FLOOD);
        Element feMergeNodeComposite = domFactory.createElement(TAG_FE_MERGE_NODE);
        feMergeNodeComposite.setAttribute(ATTR_IN, VALUE_COMPOSITE);

        feMerge.appendChild(feMergeNodeFlood);
        feMerge.appendChild(feMergeNodeComposite);

        compositeFilter.appendChild(feFlood);
        compositeFilter.appendChild(feComposite);
        compositeFilter.appendChild(feMerge);

        return compositeFilter;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        AlphaComposite ac = AlphaComposite.Src;
        AlphaComposite composites[] = { ac.SrcOver,
                                        ac.Src,
                                        ac.SrcIn,
                                        ac.SrcOut,
                                        ac.DstIn,
                                        ac.DstOut,
                                        ac.Clear,
                                        ac.getInstance(ac.SRC_OVER, .5f),
                                        ac.getInstance(ac.SRC, .5f),
                                        ac.getInstance(ac.SRC_IN, .5f),
                                        ac.getInstance(ac.SRC_OUT, .5f),
                                        ac.getInstance(ac.DST_IN, .5f),
                                        ac.getInstance(ac.DST_OUT, .5f),
                                        ac.getInstance(ac.CLEAR, .5f) };

        Document domFactory = TestUtil.getDocumentPrototype();
        SVGAlphaComposite converter = new SVGAlphaComposite(domFactory);

        Element groupOne = domFactory.createElement(TAG_G);
        groupOne.setAttribute(ATTR_ID, "groupOne");
        buildTestGroup(groupOne, composites, converter);

        Element groupTwo = domFactory.createElement(TAG_G);
        groupTwo.setAttribute(ATTR_ID, "group2");
        buildTestGroup(groupTwo, composites, converter);

        Element defs = domFactory.createElement(TAG_DEFS);
        Iterator iter = converter.getAlphaCompositeFilterSet().iterator();
        while(iter.hasNext()){
            Element filter = (Element)iter.next();
            defs.appendChild(filter);
        }

        Element groupThree = domFactory.createElement(TAG_G);
        groupThree.setAttribute(ATTR_ID, "groupThree");
        SVGAlphaComposite newConverter = new SVGAlphaComposite(domFactory);
        buildTestGroup(groupThree, new AlphaComposite[]{ ac.SrcIn, ac.DstOut },
        newConverter);
        Element newDefs = domFactory.createElement(TAG_DEFS);
        newDefs.setAttribute(ATTR_ID, "alphaCompositeSubset");
        Iterator newIter = newConverter.getDefinitionSet().iterator();
        while(newIter.hasNext()){
            Element filter = (Element)newIter.next();
            newDefs.appendChild(filter);
        }

        groupThree.insertBefore(newDefs, groupThree.getFirstChild());

        Element group = domFactory.createElement(TAG_G);
        group.appendChild(defs);
        group.appendChild(groupOne);
        group.appendChild(groupTwo);
        group.appendChild(groupThree);

        TestUtil.trace(group, System.out);

    }

    /**
     * For test purpose only
     */
    private static void buildTestGroup(Element group, AlphaComposite composites[], SVGAlphaComposite converter){
        Document domFactory = group.getOwnerDocument();

        for(int i=0; i<composites.length; i++){
            SVGCompositeDescriptor compositeDesc = converter.toSVG(composites[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_OPACITY, compositeDesc.getOpacityValue());
            if(compositeDesc.getDef() != null)
                rect.setAttribute(ATTR_FILTER, compositeDesc.getFilterValue());
            group.appendChild(rect);
        }
    }
}
