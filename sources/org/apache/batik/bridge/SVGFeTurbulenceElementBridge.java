/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable8Bit;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feTurbulence</tt> filter element
 * with <tt>TurbulenceRable8Bit</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeTurbulenceElementBridge implements FilterPrimitiveBridge,
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

         GraphicsNodeRenderContext rc =
                         bridgeContext.getGraphicsNodeRenderContext();

        // parse the baseFrequency attribute
        String baseFrequencyAttr =
            filterElement.getAttributeNS(null, SVG_BASE_FREQUENCY_ATTRIBUTE);

        Float baseFrequencies[] =
            SVGUtilities.buildFloatPair(baseFrequencyAttr);

        float baseFrequencyX = 0.001f; // default is 0
        if (baseFrequencies[0] != null) {
            baseFrequencyX = baseFrequencies[0].floatValue();
        }

        float baseFrequencyY = baseFrequencyX; // default is baseFrequencyX
        if (baseFrequencies[1] != null) {
            baseFrequencyY = baseFrequencies[1].floatValue();
        }

        // parse the numOctaves attribute
        String numOctavesAttr =
            filterElement.getAttributeNS(null, SVG_NUM_OCTAVES_ATTRIBUTE);
        int numOctaves = 1; // default is 1
        if (numOctavesAttr.length() != 0) {
            numOctaves = SVGUtilities.convertSVGInteger(SVG_NUM_OCTAVES_ATTRIBUTE,
                                                        numOctavesAttr);
        }

        // parse the seed attribute
        String seedAttr = filterElement.getAttributeNS(null, SVG_SEED_ATTRIBUTE);
        int seed = 0;
        if (seedAttr.length() != 0) {
            seed = (int) SVGUtilities.convertSVGNumber(SVG_SEED_ATTRIBUTE, seedAttr);
        }

        // parse the stitchTiles attribute
        String stitchTilesAttr =
            filterElement.getAttributeNS(null, SVG_STITCH_TILES_ATTRIBUTE);
        boolean stitchTiles;
        if (stitchTilesAttr.length() == 0) {
            stitchTiles = false; // default is noStitch
        } else if (SVG_STITCH_VALUE.equals(stitchTilesAttr)) {
            stitchTiles = true;
        } else if (SVG_NO_STITCH_VALUE.equals(stitchTilesAttr)) {
            stitchTiles = false;
        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feTurbulence.stitchTiles.invalid",
                                       new Object[] { stitchTilesAttr }));
        }

        // parse the type attribute
        String feTurbulenceTypeAttr =
            filterElement.getAttributeNS(null, SVG_TYPE_ATTRIBUTE);

        boolean feTurbulenceType;
        if (feTurbulenceTypeAttr.length() == 0) {
            feTurbulenceType = false; // default is turbulence
        } else if(SVG_FRACTAL_NOISE_VALUE.equals(feTurbulenceTypeAttr)){
            feTurbulenceType = true;
        } else if(SVG_TURBULENCE_VALUE.equals(feTurbulenceTypeAttr)){
            feTurbulenceType = false;
        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feTurbulence.type.invalid",
                                       new Object[] { feTurbulenceTypeAttr }));
        }

        //
        // Now, build a TurbulenceRable8Bit from the parameters
        //

        // Default region is the filter chain region
        Rectangle2D defaultRegion = filterRegion;

        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(filterElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D turbulenceRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx);

        TurbulenceRable turbulenceRable
            = new TurbulenceRable8Bit(turbulenceRegion);

        turbulenceRable.setBaseFrequencyX(baseFrequencyX);
        turbulenceRable.setBaseFrequencyY(baseFrequencyY);
        turbulenceRable.setNumOctaves(numOctaves);
        turbulenceRable.setSeed(seed);
        turbulenceRable.setStitched(stitchTiles);
        turbulenceRable.setFractalNoise(feTurbulenceType);

        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            filterMap.put(result, turbulenceRable);
        }

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

