/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.image.Kernel;

import java.util.StringTokenizer;

import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feConvolveMatrix</tt> element with a concrete
 * <tt>ConvolveMatrixRable</tt>
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGFeConvolveMatrixElementBridge implements FilterPrimitiveBridge,
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

        // Extract kernel Order (Size)
        String attrStr;
        attrStr = filterElement.getAttributeNS(null, SVG_ORDER_ATTRIBUTE);

        Float orderPair[] = SVGUtilities.buildFloatPair(attrStr);

        int orderX=0, orderY=0;
        if (orderPair[0] != null) {
            orderX = (int)orderPair[0].floatValue();
            if (orderX != (int)orderPair[0].floatValue())
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.order.notPosInt", null));
        }

        if (orderX <= 0)
            throw new IllegalAttributeValueException
                (Messages.formatMessage
                 ("feConvolveMatrix.order.notPosInt", null));

        if (orderPair[1] == null)
            orderY = orderX;
        else {
            orderY = (int)orderPair[1].floatValue();
            if (orderY != (int)orderPair[1].floatValue())
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.order.notPosInt", null));
        }
        if (orderY <= 0)
            throw new IllegalAttributeValueException
                (Messages.formatMessage
                 ("feConvolveMatrix.order.notPosInt", null));


        float [] kernel = new float[orderX*orderY];
        attrStr = filterElement.getAttributeNS
            (null, SVG_KERNEL_MATRIX_ATTRIBUTE);
        {
            StringTokenizer st = new StringTokenizer(attrStr);
            int i=0;
            while (st.hasMoreTokens()) {
                kernel[i++] = SVGUtilities.convertSVGNumber
                    (SVG_KERNEL_MATRIX_ATTRIBUTE, st.nextToken());
                if (i == orderX*orderY) break;
            }

            if (st.hasMoreTokens())
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.kernelMatrix.tooMany", null));

            if (i != orderX*orderY)
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.kernelMatrix.notEnough", null));
        }

        attrStr = filterElement.getAttributeNS(null, SVG_DIVISOR_ATTRIBUTE);
        float divisor = 0;
        if (attrStr.length() != 0) {
            divisor = SVGUtilities.convertSVGNumber
                (SVG_DIVISOR_ATTRIBUTE, attrStr);
        } else {
            // Default is sum of kernel values (if sum is zero then 1.0).
            for (int i=0; i<kernel.length; i++)
                divisor += kernel[i];
            if (divisor == 0.0f) divisor = 1.0f;
        }
        if (divisor == 0.0f) 
            throw new IllegalAttributeValueException
                (Messages.formatMessage
                     ("feConvolveMatrix.divisor.zero", null));

        attrStr = filterElement.getAttributeNS(null, SVG_BIAS_ATTRIBUTE);
        float bias = 0.0f;
        if (attrStr.length() != 0) {
            bias = SVGUtilities.convertSVGNumber(SVG_BIAS_ATTRIBUTE, attrStr);
        }


        attrStr = filterElement.getAttributeNS(null, SVG_TARGET_X_ATTRIBUTE);
        int targetX=orderX/2;
        if (attrStr.length() != 0) {
            targetX = SVGUtilities.convertSVGInteger
                (SVG_TARGET_X_ATTRIBUTE, attrStr);
        }
        if ((targetX < 0) || (targetX >= orderX))
            throw new IllegalAttributeValueException
                (Messages.formatMessage
                 ("feConvolveMatrix.targetX.invalid", null));


        attrStr = filterElement.getAttributeNS(null, SVG_TARGET_Y_ATTRIBUTE);
        int targetY=orderY/2;
        if (attrStr.length() != 0) {
            targetY = SVGUtilities.convertSVGInteger
                (SVG_TARGET_Y_ATTRIBUTE, attrStr);
        }
        if ((targetY < 0) || (targetY >= orderY))
            throw new IllegalAttributeValueException
                (Messages.formatMessage
                 ("feConvolveMatrix.targetY.invalid", null));


        attrStr = filterElement.getAttributeNS(null, SVG_EDGE_MODE_ATTRIBUTE);
        PadMode padMode = PadMode.REPLICATE;
        if (attrStr.length() != 0) {
            attrStr = attrStr.toLowerCase();

            if (attrStr.equals(SVG_DUPLICATE_VALUE))
                padMode = PadMode.REPLICATE;
            else if (attrStr.equals(SVG_WRAP_VALUE))
                padMode = PadMode.WRAP;
            else if (attrStr.equals(SVG_NONE_VALUE))
                padMode = PadMode.ZERO_PAD;
            else
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.edgeMode.invalid",
                                       new Object[] { attrStr }));
        }

        attrStr = filterElement.getAttributeNS
            (null, SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE);
        double [] kernelUnitLength = null;
        if (attrStr.length() != 0) {
            Float [] fp = SVGUtilities.buildFloatPair(attrStr);
            kernelUnitLength = new double[2];

            if (fp[0] != null)
                kernelUnitLength[0] = fp[0].floatValue();

            if (fp[1] != null)
                kernelUnitLength[1] = fp[1].floatValue();
            else
                kernelUnitLength[1] = kernelUnitLength[0];
        }

        if (kernelUnitLength != null) {
            if (kernelUnitLength[0] <= 0)
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.kernelUnitLength.notPositive", null));
            if (kernelUnitLength[1] <= 0)
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.kernelUnitLength.notPositive", null));
        }

        attrStr = filterElement.getAttributeNS
            (null, SVG_PRESERVE_ALPHA_ATTRIBUTE);
        boolean preserveAlpha = false;
        if (attrStr.length() != 0) {
            attrStr = attrStr.toLowerCase();
            if (attrStr.equals("false"))
                preserveAlpha = false;
            else if (attrStr.equals("true"))
                preserveAlpha = true;
            else
                throw new IllegalAttributeValueException
                    (Messages.formatMessage
                     ("feConvolveMatrix.preserveAlpha.invalid",
                                       new Object[] { attrStr }));
        }

        // Get source
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        in = CSSUtilities.getFilterSource(filteredNode,
                                          inAttr,
                                          bridgeContext,
                                          filteredElement,
                                          in,
                                          filterMap);

        if (in == null)
            return null;

        //
        // The default region is the input source's region unless the
        // source is SourceGraphics, in which case the default region
        // is the filter chain's region
        //
        Filter sourceGraphics = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        Rectangle2D defaultRegion = in.getBounds2D();
        if (in == sourceGraphics) {
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        GraphicsNodeRenderContext rc = 
                         bridgeContext.getGraphicsNodeRenderContext();
        Rectangle2D convolveArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx);

        PadRable pad;
        pad = new PadRable8Bit(in, convolveArea, PadMode.ZERO_PAD);

        // Build filter
        ConvolveMatrixRable convolve;
        convolve = new ConvolveMatrixRable8Bit(pad);

        /* SVG No longer does it's kernels funny!!!
         * And there was much rejoicing!
           // Rotate the kernel 180 deg.
           int len2 = kernel.length/2;
           int len1 = kernel.length-1;
           for (int i=0; i<len2; i++) {
               float tmp      = kernel[i];
               kernel[i]      = kernel[len1-i];
               kernel[len1-i] = tmp;
           }
        */

        for (int i=0; i<kernel.length; i++)
            kernel[i] /= divisor;

        // System.out.print("Kernel: ");
        // for (int i=0; i<kernel.length; i++)
        //     System.out.print(kernel[i] + ", ");
        // System.out.println("");
        
        convolve.setKernel(new Kernel(orderX, orderY, kernel));
        convolve.setTarget(new Point(targetX, targetY));
        convolve.setBias(bias);
        convolve.setEdgeMode(padMode);
        convolve.setKernelUnitLength(kernelUnitLength);
        convolve.setPreserveAlpha(preserveAlpha);

        // Get result attribute if any
        String result = filterElement.getAttributeNS(null,ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            filterMap.put(result, convolve);
        }

        return convolve;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
