/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.ComponentTransferFunction;
import org.apache.batik.gvt.filter.ComponentTransferRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteComponentTransferFunction;
import org.apache.batik.refimpl.gvt.filter.ConcreteComponentTransferRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feCompoentTransfer</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFeComponentTransferElementBridge implements FilterBridge,
                                                            SVGConstants{

    /**
     * Returns the <tt>Filter</tt> that implements the filter
     * operation modeled by the input DOM element
     *
     * @param filteredNode the node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chained
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter create(GraphicsNode filteredNode,
                         BridgeContext bridgeContext,
                         Element filterElement,
                         Element filteredElement,
                         Filter in,
                         Rectangle2D filterRegion,
                         Map filterMap){
        Filter filter = null;

        //
        // First, extract source
        //
        String inAttr = filterElement.getAttributeNS(null, ATTR_IN);
        in = CSSUtilities.getFilterSource(filteredNode, inAttr, 
                                           bridgeContext, filteredElement,
                                          in, filterMap);

        if(in != null){
            //
            // The default region is the input source's region
            // unless the source is SourceGraphics, in which
            // case the default region is the filter chain's 
            // region
            //
            Filter sourceGraphics 
                = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

            Rectangle2D defaultRegion 
                = in.getBounds2D();

            if(in == sourceGraphics){
                defaultRegion = filterRegion;
            }

            CSSStyleDeclaration cssDecl
                = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                              null);

            UnitProcessor.Context uctx
                = new DefaultUnitProcessorContext(bridgeContext,
                                                  cssDecl);

            Rectangle2D primitiveRegion 
                = SVGUtilities.convertFilterPrimitiveRegion2
                (filterElement,
                 filteredElement,
                 defaultRegion,
                 filteredNode,
                 uctx);

            //
            // Now, extract the various transfer functions. They
            // are defined in the filterElement's children. Functions
            // are ordered as follow: r, g, b, a. We start by the
            // last child.
            //
            NodeList children = filterElement.getChildNodes();
            int nChildren = children.getLength();
            int curChild = nChildren - 1;

            ComponentTransferFunction alphaFunction = null;
            ComponentTransferFunction redFunction = null;
            ComponentTransferFunction greenFunction = null;
            ComponentTransferFunction blueFunction = null;

            if(curChild >= 0){
                alphaFunction
                    = createComponentTransferFunction(filterElement,
                                                      children.item(curChild),
                                                      TAG_FE_FUNC_A);
                if(alphaFunction != null){
                    curChild--;
                }
            }

            if(curChild >= 0){
                blueFunction
                    = createComponentTransferFunction(filterElement,
                                                      children.item(curChild),
                                                      TAG_FE_FUNC_B);
                if(blueFunction != null){
                    curChild--;
                }
            }

            if(curChild >= 0){
                greenFunction
                    = createComponentTransferFunction(filterElement,
                                                      children.item(curChild),
                                                      TAG_FE_FUNC_G);
                if(greenFunction != null){
                    curChild--;
                }
            }

            if(curChild >= 0){
                redFunction
                    = createComponentTransferFunction(filterElement,
                                                      children.item(curChild),
                                                      TAG_FE_FUNC_R);
            }


            filter = new ConcreteComponentTransferRable
                (in,
                 alphaFunction,
                 redFunction,
                 greenFunction,
                 blueFunction);

            filter = new ConcretePadRable(filter,
                                          primitiveRegion,
                                          PadMode.ZERO_PAD);


            // Get result attribute and update map
            String result
                = filterElement.getAttributeNS(null,
                                               ATTR_RESULT);
            if((result != null) && (result.trim().length() > 0)){
                filterMap.put(result, filter);
            }
        }

        return filter;

    }

    /**
     * Creates a ComponentTransferFunction corresponding to the
     * input tag (i.e., feFuncXX, where XX can be A, R, G or B).
     */
    private static ComponentTransferFunction
        createComponentTransferFunction(Element filterElement,
                                        Node node,
                                        String tag){
        ComponentTransferFunction txfFunc = null;

        if(node.getNodeType() == Node.ELEMENT_NODE){
            Element elt = (Element)node;
            if(elt.getNodeName().equals(tag)){
                //
                // Get type. Depending on the type, some of the
                // attributes are ignored.
                //
                String typeStr = elt.getAttributeNS(null, ATTR_TYPE);
                int type = convertType(typeStr);

                switch(type){
                case ComponentTransferFunction.IDENTITY:
                    txfFunc = ConcreteComponentTransferFunction.getIdentityTransfer();
                    break;
                case ComponentTransferFunction.TABLE:
                    {
                        String tableValuesStr = elt.getAttributeNS(null, ATTR_TABLE_VALUES);
                        float tableValues[] = convertTableValues(tableValuesStr);
                        txfFunc = ConcreteComponentTransferFunction.getTableTransfer(tableValues);
                    }
                    break;
                case ComponentTransferFunction.DISCRETE:
                    {
                        String tableValuesStr = elt.getAttributeNS(null, ATTR_TABLE_VALUES);
                        float tableValues[] = convertTableValues(tableValuesStr);
                        txfFunc = ConcreteComponentTransferFunction.getDiscreteTransfer(tableValues);
                    }
                    break;
                case ComponentTransferFunction.LINEAR:
                    {
                        String slopeStr = elt.getAttributeNS(null, ATTR_SLOPE);
                        float slope = 1;
                        if(slopeStr.length() > 0){
                            slope = convertFloatValue(slopeStr);
                        }
                        String interceptStr = elt.getAttributeNS(null, ATTR_INTERCEPT);
                        float intercept = convertFloatValue(interceptStr);
                        txfFunc = ConcreteComponentTransferFunction.getLinearTransfer(slope, intercept);
                    }
                    break;
                case ComponentTransferFunction.GAMMA:
                    {
                        String amplitudeStr = elt.getAttributeNS(null, ATTR_AMPLITUDE);
                        float amplitude = convertFloatValue(amplitudeStr);
                        String exponentStr = elt.getAttributeNS(null, ATTR_EXPONENT);
                        float exponent = convertFloatValue(exponentStr);
                        String offsetStr = elt.getAttributeNS(null, ATTR_OFFSET);
                        float offset = convertFloatValue(offsetStr);
                        txfFunc = ConcreteComponentTransferFunction.getGammaTransfer(amplitude,
                                                                                     exponent,
                                                                                     offset);
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
                }

            }
        }
        return txfFunc;
    }

    /**
     * Converts a table value
     */
    private static float[] convertTableValues(String value){
        StringTokenizer st = new StringTokenizer(value, " ,");
        float tableValues[] = new float[st.countTokens()];
        int i = 0;
        while(st.hasMoreTokens()){
            String v = st.nextToken();
            tableValues[i++] = Float.parseFloat(v);
        }

        return tableValues;
    }

    /**
     * Converts a float value
     */
    private static float convertFloatValue(String value){
        return Float.parseFloat(value);
    }

    /**
     * Converts an feFuncXX type attribute into a
     * ComponentTransferFunction type constant
     */
    private static int convertType(String value){
        System.out.println("Converting : " + value);
        int type = ComponentTransferFunction.IDENTITY;
        if(value.length() > 0){
            switch(value.charAt(0)){
            case 't': // table
                if(value.length() == "table".length()){
                    if(value.charAt(1) == 'a' &&
                       value.charAt(2) == 'b' &&
                       value.charAt(3) == 'l' &&
                       value.charAt(4) == 'e'){
                        type = ComponentTransferFunction.TABLE;
                    }
                }
                break;
            case 'd': // discrete
                if(value.length() == "discrete".length()){
                    if(value.charAt(1) == 'i' &&
                       value.charAt(2) == 's' &&
                       value.charAt(3) == 'c' &&
                       value.charAt(4) == 'r' &&
                       value.charAt(5) == 'e' &&
                       value.charAt(6) == 't' &&
                       value.charAt(7) == 'e'){
                        type = ComponentTransferFunction.DISCRETE;
                    }
                }
                break;
            case 'l': // linear
                if(value.length() == "linear".length()){
                    if(value.charAt(1) == 'i' &&
                       value.charAt(2) == 'n' &&
                       value.charAt(3) == 'e' &&
                       value.charAt(4) == 'a' &&
                       value.charAt(5) == 'r' ){
                        type = ComponentTransferFunction.LINEAR;
                    }
                }
                break;
            case 'g': // gamma
                if(value.length() == "gamma".length()){
                    if(value.charAt(1) == 'a' &&
                       value.charAt(2) == 'm' &&
                       value.charAt(3) == 'm' &&
                       value.charAt(4) == 'a'){
                        type = ComponentTransferFunction.GAMMA;
                    }
                }
                break;
            }
        }

        return type;
    }


    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

}
