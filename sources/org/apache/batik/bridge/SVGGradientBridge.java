/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.xml.sax.SAXException;

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
        Element e = paintElement;
        List refs = new LinkedList();
        Vector stops = new Vector();
        DocumentLoader loader = ctx.getDocumentLoader();
        for (;;) {
            for(Node stop = e.getFirstChild();
                stop != null;
                stop = stop.getNextSibling()){
                if(stop.getNodeType() == stop.ELEMENT_NODE &&
                   stop.getNodeName().equals(SVG_STOP_TAG)){
                    GradientStop gs = convertGradientStop((Element)stop, ctx);
                    if(gs != null){
                        stops.addElement(gs);
                    }
                }
            }
            if (stops.size() > 0) {
                return stops; // exit if stop defined
            }
            String uriStr = XLinkSupport.getXLinkHref(e);
            if (uriStr.length() == 0) {
                return stops; // exit if no more xlink:href
            }
            SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            try {
                URL url = new URL(baseURL, uriStr);
                Iterator iter = refs.iterator();
                while (iter.hasNext()) {
                    URL urlTmp = (URL)iter.next();
                    if (urlTmp.sameFile(url) &&
                            urlTmp.getRef().equals(url.getRef())) {
                        throw new IllegalAttributeValueException(
                            "circular reference on "+e);
                    }
                }
                URIResolver resolver = new URIResolver(svgDoc, loader);
                e = resolver.getElement(url.toString());
                refs.add(url);
            } catch(MalformedURLException ex) {
                throw new IllegalAttributeValueException("bad url on "+uriStr);
            } catch(SAXException ex) {
                throw new IllegalAttributeValueException("bad document on "+uriStr);
            } catch(IOException ex) {
                throw new IllegalAttributeValueException("I/O error on "+uriStr);
            }
        }
    }

    public static GradientStop convertGradientStop(Element stop,
                                                   BridgeContext ctx) {
        // parse the offset attribute, (required and must between [0-1])
        String offsetStr = stop.getAttributeNS(null, SVG_OFFSET_ATTRIBUTE);
        if (offsetStr.length() == 0) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("stop.offset.required", null));
        }
        float ratio = CSSUtilities.convertRatio(offsetStr);
        // parse the stop-color CSS properties
        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(stop);
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

