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
import org.apache.batik.gvt.filter.ColorMatrixRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteColorMatrixRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
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
 * @version $Id$
 */
public class SVGFeColorMatrixElementBridge implements FilterBridge,
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
                         FilterRegion filterRegion,
                         Map filterMap){
        ColorMatrixRable filter = null;

        //
        // First, extract source
        //
        String inAttr = filterElement.getAttributeNS(null, ATTR_IN);
        int inValue = SVGUtilities.parseInAttribute(inAttr);
        switch(inValue){
        case SVGUtilities.EMPTY:
            // Do not change in's value. It is correctly
            // set to the current chain result.
            break;
        case SVGUtilities.BACKGROUND_ALPHA:
            throw new Error("BackgroundAlpha not implemented yet");
        case SVGUtilities.BACKGROUND_IMAGE:
            throw new Error("BackgroundImage not implemented yet");
        case SVGUtilities.FILL_PAINT:
            throw new Error("Not implemented yet");
        case SVGUtilities.SOURCE_ALPHA:
            throw new Error("SourceAlpha not implemented yet");
        case SVGUtilities.SOURCE_GRAPHIC:
            in = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);
            break;
        case SVGUtilities.STROKE_PAINT:
            throw new Error("Not implemented yet");
        case SVGUtilities.IDENTIFIER:
            in = (Filter)filterMap.get(inAttr);
            break;
        default:
            // Should never, ever, ever happen
            throw new Error();
        }

        if(in != null){
            FilterRegion defaultRegion = new FilterSourceRegion(in);

            // Get unit. Comes from parent node.
            Node parentNode = filterElement.getParentNode();
            String units = VALUE_USER_SPACE_ON_USE;
            if((parentNode != null)
               &&
               (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
                units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
            }

            //
            // Now, extraact filter region
            //
            CSSStyleDeclaration cssDecl
                = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                              null);

            UnitProcessor.Context uctx
                = new DefaultUnitProcessorContext(bridgeContext,
                                                  cssDecl);

            final FilterRegion blurArea
                = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                            filteredElement,
                                                            defaultRegion,
                                                            units,
                                                            filteredNode,
                                                            uctx);

            //
            // Extract the matrix type. Interpret the values
            // accordingly.
            //
            String typeStr = filterElement.getAttributeNS(null, ATTR_TYPE);
            int type = convertType(typeStr);
            String valuesStr = filterElement.getAttributeNS(null, ATTR_VALUES);

            switch(type){
            case ColorMatrixRable.TYPE_MATRIX:
                float matrix[][] = convertValuesToMatrix(valuesStr);
                filter = ConcreteColorMatrixRable.buildMatrix(matrix);
                break;
            case ColorMatrixRable.TYPE_SATURATE:
                float s = convertRatio(valuesStr);
                filter = ConcreteColorMatrixRable.buildSaturate(s);
                break;
            case ColorMatrixRable.TYPE_HUE_ROTATE:
                float a = convertFloatValue(valuesStr);
                filter = ConcreteColorMatrixRable.buildHueRotate((float)(a*Math.PI/180));
                break;
            case ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA:
                filter = ConcreteColorMatrixRable.buildLuminanceToAlpha();
                break;
            }

            filter.setSource(in);

            Filter mapFilter = filter;
            mapFilter = new ConcretePadRable(filter,
                                          blurArea.getRegion(),
                                          PadMode.ZERO_PAD){
                    public Rectangle2D getBounds2D(){
                        setPadRect(blurArea.getRegion());
                        return super.getBounds2D();
                    }

                    public java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext rc){
                        setPadRect(blurArea.getRegion());
                        return super.createRendering(rc);
                    }
                };


            // Get result attribute and update map
            String result
                = filterElement.getAttributeNS(null,
                                               ATTR_RESULT);
            if((result != null) && (result.trim().length() > 0)){
                // The filter will be added to the filter map. Before
                // we do that, append the filter region crop
                filterMap.put(result, mapFilter);
            }
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
            throw new IllegalArgumentException();
        }
        int i = 0;
        while(st.hasMoreTokens()){
            String v = st.nextToken();
            matrix[i/5][i%5] = Float.parseFloat(v);
            i++;
        }

        for(i=0; i<4; i++){
            matrix[i][4] *= 255;
        }
        return matrix;
    }

    /**
     * Converts the input value to a ratio between 0 and 1
     */
    private static float convertRatio(String value){
        value = value.trim();
        float r = 0;
        float d = 1;
        if(value.length() > 0 && value.endsWith("%")){
            value = value.substring(0, value.length() - 1);
            d = 100;
        }

        r = Float.parseFloat(value);
        r /= d;

        if(r < 0){
            r = 0;
        }
        else if(r > 1){
            r = 1;
        }
        return r;
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
        int type = ColorMatrixRable.TYPE_MATRIX;
        if(value.length() > 0){
            switch(value.charAt(0)){
            case 's': // saturate
                if(value.length() == "saturate".length()){
                    if(value.charAt(1) == 'a' &&
                       value.charAt(2) == 't' &&
                       value.charAt(3) == 'u' &&
                       value.charAt(4) == 'r' &&
                       value.charAt(5) == 'a' &&
                       value.charAt(6) == 't' &&
                       value.charAt(7) == 'e' ){
                        type = ColorMatrixRable.TYPE_SATURATE;
                    }
                }
                break;
            case 'h': // hueRotate
                if(value.length() == "hueRotate".length()){
                    if(value.charAt(1) == 'u' &&
                       value.charAt(2) == 'e' &&
                       value.charAt(3) == 'R' &&
                       value.charAt(4) == 'o' &&
                       value.charAt(5) == 't' &&
                       value.charAt(6) == 'a' &&
                       value.charAt(7) == 't' &&
                       value.charAt(8) == 'e'){
                        type = ColorMatrixRable.TYPE_HUE_ROTATE;
                    }
                }
                break;
            case 'l': // luminanceToAlpha
                if(value.length() == "luminanceToAlpha".length()){
                    if(value.charAt(1) == 'u' &&
                       value.charAt(2) == 'm' &&
                       value.charAt(3) == 'i' &&
                       value.charAt(4) == 'n' &&
                       value.charAt(5) == 'a' &&
                       value.charAt(6) == 'n' &&
                       value.charAt(7) == 'c' &&
                       value.charAt(8) == 'e' &&
                       value.charAt(9) == 'T' &&
                       value.charAt(10) == 'o' &&
                       value.charAt(11) == 'A' &&
                       value.charAt(12) == 'l' &&
                       value.charAt(13) == 'p' &&
                       value.charAt(14) == 'h' &&
                       value.charAt(15) == 'a'){
                        type = ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA;
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
