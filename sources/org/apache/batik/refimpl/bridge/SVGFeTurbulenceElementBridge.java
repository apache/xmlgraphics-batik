/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.gvt.filter.TurbulenceRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteTurbulenceRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feTurbulence</tt> filter element
 * with <tt>ConcreteTurbulenceRable</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFeTurbulenceElementBridge implements FilterBridge,
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
        // System.out.println("In SVGFeTurbulenceElementBridge" );

        //
        // Get baseFrequency along each of the user space axis
        //
        String baseFrequencyAttr =
            filterElement.getAttributeNS(null, ATTR_BASE_FREQUENCY);
        Float baseFrequencies[] =
            SVGUtilities.buildFloatPair(baseFrequencyAttr);
        float baseFrequencyX = DEFAULT_VALUE_BASE_FREQUENCY;
        if(baseFrequencies[0] != null){
            baseFrequencyX = baseFrequencies[0].floatValue();
        }

        float baseFrequencyY = baseFrequencyX;
        if(baseFrequencies[1] != null){
            baseFrequencyY = baseFrequencies[1].floatValue();
        }

        //
        // Get number of octaves
        //
        String numOctavesAttr =
            filterElement.getAttributeNS(null, ATTR_NUM_OCTAVES);
        int numOctaves = DEFAULT_VALUE_NUM_OCTAVES;
        try {
            numOctaves = SVGUtilities.convertSVGInteger(numOctavesAttr);
        } catch(NumberFormatException e) {
        }

        //
        // Get seed
        //
        String seedAttr = filterElement.getAttributeNS(null, ATTR_SEED);
        int seed = DEFAULT_VALUE_SEED;
        try {
            seed = SVGUtilities.convertSVGInteger(seedAttr);
        } catch(NumberFormatException e) {
        }

        //
        // Get Stitch procedure
        //
        String stitchTilesAttr =
            filterElement.getAttributeNS(null, ATTR_STITCH_TILES);
        boolean stitchTiles = DEFAULT_VALUE_STITCH_TILES;
        if(VALUE_STITCH.equals(stitchTilesAttr)){
            stitchTiles = true;
        }
        else if(VALUE_NO_STITCH.equals(stitchTilesAttr)){
            // This second if is required to make sure value is
            // properly set to noStitch value. Otherwise, default
            // has to be used.
            stitchTiles = false;
        }

        //
        // Get Type
        //
        String feTurbulenceTypeAttr = filterElement.getAttributeNS(null,
                                                                   ATTR_TYPE);
        boolean feTurbulenceType = DEFAULT_VALUE_FE_TURBULENCE_TYPE;
        if(VALUE_FRACTAL_NOISE.equals(feTurbulenceTypeAttr)){
            feTurbulenceType = true;
        }
        else if(VALUE_TURBULENCE.equals(feTurbulenceTypeAttr)){
            // This second if is required to make sure the value
            // is properly set to turbulence. Otherwise, default
            // value has to be used.
            feTurbulenceType = false;
        }

        //
        // Now, build a ConcreteTurbulenceRable from the parameters
        //

        // Turbulence region is defined by the filter region
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           &&
           (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
            units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
        }

        final FilterRegion turbulenceRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filterRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);

        TurbulenceRable turbulenceRable
            = new ConcreteTurbulenceRable(turbulenceRegion);

        turbulenceRable.setBaseFrequencyX(baseFrequencyX);
        turbulenceRable.setBaseFrequencyY(baseFrequencyY);
        turbulenceRable.setNumOctaves(numOctaves);
        turbulenceRable.setSeed(seed);
        turbulenceRable.setStitched(stitchTiles);
        turbulenceRable.setFractalNoise(feTurbulenceType);

        /*Clip clip 
            = new ConcreteClipRable(turbulenceRable,
                                    new Rectangle(0, 0, 0, 0)){
                    public RenderedImage createRendering(RenderContext rc){
                        setClipPath(turbulenceRegion.getRegion());
                        return super.createRendering(rc);
                    }
                };

                return clip;*/
        return turbulenceRable;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}

