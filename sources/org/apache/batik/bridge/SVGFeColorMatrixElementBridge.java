/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.ColorMatrixRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.gvt.filter.ConcreteColorMatrixRable;
import org.apache.batik.gvt.filter.ConcretePadRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feColorMatrix</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeColorMatrixElementBridge implements FilterPrimitiveBridge,
                                                      SVGConstants {

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
        // Extract the matrix type. Interpret the values accordingly.
        //
        String typeStr = filterElement.getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
        int type = convertType(typeStr);
        String valuesStr = filterElement.getAttributeNS(null, SVG_VALUES_ATTRIBUTE);
        ColorMatrixRable colorMatrix;
        switch(type){
        case ColorMatrixRable.TYPE_MATRIX:
            float matrix[][] = convertValuesToMatrix(valuesStr);
            colorMatrix = ConcreteColorMatrixRable.buildMatrix(matrix);
            break;
        case ColorMatrixRable.TYPE_SATURATE:
            float s = 1;
            if (valuesStr.length() > 0) {
                s = CSSUtilities.convertRatio(valuesStr);
            }
            colorMatrix = ConcreteColorMatrixRable.buildSaturate(s);
            break;
        case ColorMatrixRable.TYPE_HUE_ROTATE:
            float a = 0; // default is 0
            if (valuesStr.length() > 0) {
                a = (float)(SVGUtilities.convertSVGNumber
                            (SVG_VALUES_ATTRIBUTE, valuesStr) * Math.PI/180);
            }
            colorMatrix = ConcreteColorMatrixRable.buildHueRotate(a);
            break;
        case ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA:
            colorMatrix = ConcreteColorMatrixRable.buildLuminanceToAlpha();
            break;
        default:
            /* Never happen: Bad type is catched previously */
            throw new Error();
        }

        colorMatrix.setSource(in);

        filter = new ConcretePadRable(colorMatrix,
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
     * Converts the set of values to a matrix
     */
    private static float[][] convertValuesToMatrix(String value){
        StringTokenizer st = new StringTokenizer(value, " ,");
        float matrix[][] = new float[4][5];
        if(st.countTokens() != 20){
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feColorMatrix.values.invalid",
                                       new Object[] { value }));
        }
        int i = 0;
        String v = "";
        try {
            while(st.hasMoreTokens()){
                v = st.nextToken();
                matrix[i/5][i%5] = Float.parseFloat(v);
                i++;
            }
        } catch (NumberFormatException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feColorMatrix.value.invalid",
                                       new Object[] { v }));

        }

        for(i=0; i<4; i++){
            matrix[i][4] *= 255;
        }
        return matrix;
    }

    /**
     * Converts an feFuncXX type attribute into a
     * ComponentTransferFunction type constant
     */
    private static int convertType(String typeStr){
        int type;
        if (typeStr.length() == 0) {
            type = ColorMatrixRable.TYPE_MATRIX; // default value

        } else if (SVG_SATURATE_VALUE.equals(typeStr)) {
            type = ColorMatrixRable.TYPE_SATURATE;

        } else if (SVG_HUE_ROTATE_VALUE.equals(typeStr)) {
            type = ColorMatrixRable.TYPE_HUE_ROTATE;

        } else if (SVG_LUMINANCE_TO_ALPHA_VALUE.equals(typeStr)) {
            type = ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA;

        } else if (SVG_MATRIX_VALUE.equals(typeStr)) {
            type = ColorMatrixRable.TYPE_MATRIX;

        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feColorMatrix.type.invalid",
                                       new Object[] { typeStr }));
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
