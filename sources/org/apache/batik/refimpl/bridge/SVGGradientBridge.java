/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.StringReader;

import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.util.awt.LinearGradientPaint;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * Base class for SVGLinearGradientBridge and SVGRadialGradientBridge
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class SVGGradientBridge implements SVGConstants {

    /**
     * Used to store a gradient stop's color and interval
     */
    public static class GradientStop {

        /** The color of this stop Element. */
        public Color stopColor;
        /** The offset of this stop Element. */
        public float offset;

        public GradientStop(Color stopColor,
                            float offset){
            this.stopColor = stopColor;
            this.offset = offset;
        }
    }

    protected static Vector extractGradientStops(Element paintElement,
                                                 BridgeContext ctx){
        Vector stops = new Vector();
        for(Node stop = paintElement.getFirstChild();
            stop != null;
            stop = stop.getNextSibling()){
            if(stop.getNodeType() == stop.ELEMENT_NODE &&
               stop.getNodeName().equals(TAG_STOP)){
                GradientStop gs = convertGradientStop((Element)stop, ctx);
                if(gs != null){
                    stops.addElement(gs);
                }
            }
        }
        return stops;
    }

    public static GradientStop convertGradientStop(Element stop,
                                                   BridgeContext ctx) {
        // parse the offset attribute, (required and must between [0-1])
        String offsetStr = stop.getAttributeNS(null, ATTR_OFFSET);
        if (offsetStr.length() == 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("stop.offset.required", null));
        }
        float ratio = CSSUtilities.convertRatio(offsetStr);
        // parse the stop-color CSS properties
        CSSStyleDeclaration decl =
            ctx.getViewCSS().getComputedStyle(stop, null);
        Color stopColor = CSSUtilities.convertStopColorToPaint(decl);

        return new GradientStop(stopColor, ratio);
    }

    protected static LinearGradientPaint.CycleMethodEnum
            convertSpreadMethod(String spreadMethod){

        if (VALUE_REFLECT.equals(spreadMethod)) {
            return LinearGradientPaint.REFLECT;
        } else if (VALUE_REPEAT.equals(spreadMethod)) {
            return LinearGradientPaint.REPEAT;
        } else if (VALUE_PAD.equals(spreadMethod)) {
            return LinearGradientPaint.NO_CYCLE;
        }
        throw new IllegalAttributeValueException(
            Messages.formatMessage("gradient.spreadMethod.invalid",
                                   new Object[] {spreadMethod}));
    }
}

