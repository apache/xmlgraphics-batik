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
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MissingAttributeException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.ComponentTransferFunction;
import org.apache.batik.gvt.filter.ComponentTransferRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;

import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.refimpl.gvt.filter.ConcreteComponentTransferFunction;
import org.apache.batik.refimpl.gvt.filter.ConcreteComponentTransferRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;

import org.apache.batik.util.SVGConstants;
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
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeComponentTransferElementBridge
        implements FilterPrimitiveBridge, SVGConstants {

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

        GraphicsNodeRenderContext rc = 
                         bridgeContext.getGraphicsNodeRenderContext();

        // First, extract source
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        in = CSSUtilities.getFilterSource(filteredNode,
                                          inAttr,
                                          bridgeContext,
                                          filteredElement,
                                          in,
                                          filterMap);

        // Exit if no 'in' found
        if (in == null) {
            return null;
        }

        //
        // The default region is the input source's region unless the
        // source is SourceGraphics, in which case the default region
        // is the filter chain's region
        //
        Filter sourceGraphics = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        Rectangle2D defaultRegion = in.getBounds2D();

        if (in == sourceGraphics){
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
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
                                                  SVG_FE_FUNC_A_TAG);
            if(alphaFunction != null){
                curChild--;
            }
        }

        if(curChild >= 0){
            blueFunction
                = createComponentTransferFunction(filterElement,
                                                  children.item(curChild),
                                                  SVG_FE_FUNC_B_TAG);
            if(blueFunction != null){
                curChild--;
            }
        }

        if(curChild >= 0){
            greenFunction
                = createComponentTransferFunction(filterElement,
                                                  children.item(curChild),
                                                  SVG_FE_FUNC_G_TAG);
            if(greenFunction != null){
                curChild--;
            }
        }

        if(curChild >= 0){
            redFunction
                = createComponentTransferFunction(filterElement,
                                                  children.item(curChild),
                                                  SVG_FE_FUNC_R_TAG);
        }


        filter = new ConcreteComponentTransferRable(in,
                                                    alphaFunction,
                                                    redFunction,
                                                    greenFunction,
                                                    blueFunction);

        filter = new ConcretePadRable(filter,
                                      primitiveRegion,
                                      PadMode.ZERO_PAD);


        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if ((result != null) && (result.trim().length() > 0)) {
            filterMap.put(result, filter);
        }

        return filter;
    }

    /**
     * Creates a ComponentTransferFunction corresponding to the
     * input tag (i.e., feFuncXX, where XX can be A, R, G or B).
     */
    private static
        ComponentTransferFunction createComponentTransferFunction(
                                                          Element filterElement,
                                                          Node node,
                                                          String tag) {
        ComponentTransferFunction txfFunc = null;

        if(node.getNodeType() == Node.ELEMENT_NODE){
            Element elt = (Element)node;
            if(elt.getNodeName().equals(tag)){
                //
                // Get type. Depending on the type, some of the
                // attributes are ignored.
                //
                String typeStr = elt.getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
                int type = convertType(elt, typeStr);

                switch(type){
                case ComponentTransferFunction.IDENTITY:
                    txfFunc =
                        ConcreteComponentTransferFunction.getIdentityTransfer();
                    break;
                case ComponentTransferFunction.TABLE:
                    {
                        String tableValuesStr =
                            elt.getAttributeNS(null, SVG_TABLE_VALUES_ATTRIBUTE);
                        if (tableValuesStr.length() == 0) { // default is IDENTITY
                            txfFunc =
                                ConcreteComponentTransferFunction.getIdentityTransfer();
                        } else {
                            float tableValues[] =
                                convertTableValues(tableValuesStr);

                            txfFunc = ConcreteComponentTransferFunction.getTableTransfer(tableValues);
                        }
                    }
                    break;
                case ComponentTransferFunction.DISCRETE:
                    {
                        String tableValuesStr =
                            elt.getAttributeNS(null, SVG_TABLE_VALUES_ATTRIBUTE);
                        if (tableValuesStr.length() == 0) { // default is IDENTITY
                            txfFunc =
                                ConcreteComponentTransferFunction.getIdentityTransfer();
                        } else {
                            float tableValues[] =
                                convertTableValues(tableValuesStr);

                            txfFunc = ConcreteComponentTransferFunction.getDiscreteTransfer(tableValues);
                        }
                    }
                    break;
                case ComponentTransferFunction.LINEAR:
                    {
                        String slopeStr = elt.getAttributeNS(null, SVG_SLOPE_ATTRIBUTE);
                        float slope = 1; // default is 1
                        if(slopeStr.length() > 0){
                            slope = SVGUtilities.convertSVGNumber(SVG_SLOPE_ATTRIBUTE, slopeStr);
                        }
                        String interceptStr = elt.getAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE);
                        float intercept = 0; // default is 0
                        if (interceptStr.length() > 0) {
                            intercept = SVGUtilities.convertSVGNumber(SVG_INTERCEPT_ATTRIBUTE, interceptStr);
                        }
                        txfFunc = ConcreteComponentTransferFunction.getLinearTransfer(slope, intercept);
                    }
                    break;
                case ComponentTransferFunction.GAMMA:
                    {
                        String amplitudeStr = elt.getAttributeNS(null, SVG_AMPLITUDE_ATTRIBUTE);
                        float amplitude = 1; // default is 1
                        if (amplitudeStr.length() > 0) {
                            amplitude = SVGUtilities.convertSVGNumber(SVG_AMPLITUDE_ATTRIBUTE, amplitudeStr);
                        }

                        String exponentStr = elt.getAttributeNS(null, SVG_EXPONENT_ATTRIBUTE);
                        float exponent = 1; // default is 1
                        if (exponentStr.length() > 0) {
                            exponent = SVGUtilities.convertSVGNumber(SVG_EXPONENT_ATTRIBUTE, exponentStr);
                        }

                        String offsetStr = elt.getAttributeNS(null, SVG_OFFSET_ATTRIBUTE);
                        float offset = 0; // default is 0
                        if (offsetStr.length() > 0) {
                            offset = SVGUtilities.convertSVGNumber(SVG_OFFSET_ATTRIBUTE, offsetStr);
                        }

                        txfFunc = ConcreteComponentTransferFunction.getGammaTransfer(amplitude,
                                                                                     exponent,
                                                                                     offset);
                    }
                    break;
                default:
                    /* Never happen: Bad type is catched previously */
                    throw new Error();
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
        String v = "";
        try {
            while(st.hasMoreTokens()){
                v = st.nextToken();
                tableValues[i++] = Float.parseFloat(v);
            }
        } catch(NumberFormatException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feComponentTransfer.value.invalid",
                                       new Object[] { v }));
        }
        return tableValues;
    }

    /**
     * Converts an feFuncXX type attribute into a
     * ComponentTransferFunction type constant
     */
    private static int convertType(Element elt, String value){
        if (value.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("feComponentTransfer.type.required",
                                       new Object[] {elt.getLocalName()}));

        }

        int type;
        if (SVG_TABLE_VALUE.equals(value)) {
            type = ComponentTransferFunction.TABLE;
        } else if (SVG_DISCRETE_VALUE.equals(value)) {
            type = ComponentTransferFunction.DISCRETE;
        } else if (SVG_LINEAR_VALUE.equals(value)) {
            type = ComponentTransferFunction.LINEAR;
        } else if (SVG_GAMMA_VALUE.equals(value)) {
            type = ComponentTransferFunction.GAMMA;
        } else if (SVG_IDENTITY_VALUE.equals(value)) {
            type = ComponentTransferFunction.IDENTITY;
        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feComponentTransfer.type.invalid",
                                       new Object[] { value }));
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
