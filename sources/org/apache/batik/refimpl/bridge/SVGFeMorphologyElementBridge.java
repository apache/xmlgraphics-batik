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
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.MorphologyRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteMorphologyRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com">Sheng Pei</a>
 * @version $Id$
 */
public class SVGFeMorphologyElementBridge implements FilterBridge,
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
                         FilterRegion filterRegion,
                         Map filterMap){
        //
        // Extract standard deviation
        //
        String radius
            = filterElement.getAttributeNS(null,
                                           ATTR_RADIUS);

        Float radiusPair[]
            = SVGUtilities.buildFloatPair(radius);

        String operator
            = filterElement.getAttributeNS(null,
                                           ATTR_OPERATOR);

        boolean doDilation = VALUE_DILATE.equals(operator);

        //
        // Build filter only if radius is not negative
        //
        Filter filter = null;
        if((radiusPair[0] != null)
           || (radiusPair[0].floatValue() > 0)){
            float radiusX = radiusPair[0].floatValue();
            float radiusY = radiusX;
            if(radiusPair[1] != null){
                radiusY = radiusPair[1].floatValue();
            }

            if(radiusY > 0){
                // Get source
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

                // feMorphology is a point operation. Therefore, to take the
                // filter primitive region into account, only a pad operation
                // on the input is required.

                // The primitive region defaults to the source's region.
                FilterRegion defaultRegion = new FilterSourceRegion(in);

                // Get unit. Comes from parent node.
                Node parentNode = filterElement.getParentNode();
                String units = VALUE_USER_SPACE_ON_USE;
                if((parentNode != null)
                   &&
                   (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
                    units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
                }

                // Compute primitive region
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

                PadRable pad = new ConcretePadRable(in,
                                                    new Rectangle2D.Float(0, 0, 0, 0),
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

                // Build filter
                filter = new ConcreteMorphologyRable(pad, radiusX, radiusY, doDilation);

                // Get result attribute if any
                String result
                    = filterElement.getAttributeNS(null,
                                                   ATTR_RESULT);
                if((result != null) && (result.trim().length() > 0)){
                    // The filter will be added to the filter map. Before
                    // we do that, append the filter region crop
                    filterMap.put(result, filter);
                }
            }
        }
        else{
            System.out.println("radiusPair : " + radiusPair[0] + " / " + radiusPair[1]);
        }

        return filter;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
