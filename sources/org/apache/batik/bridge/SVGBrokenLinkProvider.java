/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.net.URL;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.i18n.LocalizableSupport;

import org.apache.batik.dom.util.DOMUtilities;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import java.util.Map;
import java.util.HashMap;
/**
 * This interface is to be used to provide alternate ways of 
 * generating a placeholder image when the ImageTagRegistry
 * fails to handle a given reference.
 */
public class SVGBrokenLinkProvider extends DefaultBrokenLinkProvider {

    final static String SVG_BROKEN_LINK_DOCUMENT_PROPERTY = 
        "org.apache.batik.bridge.BrokenLinkDocument";

    UserAgent      userAgent;
    DocumentLoader loader;
    GraphicsNodeRenderContext  rc;
    BridgeContext  ctx;
    GraphicsNode   gvtRoot = null;
    SVGDocument       svgDoc;
    
    public SVGBrokenLinkProvider() {
        StaticRenderer renderer = new StaticRenderer();
        rc        = renderer.getRenderContext();
        userAgent = new UserAgentAdapter();
        loader    = new DocumentLoader(userAgent);
        ctx       = new BridgeContext(userAgent, rc, loader);

        Class cls = SVGBrokenLinkProvider.class;
        URL blURL = cls.getResource("BrokenLink.svg");
        if (blURL == null) return;

        GVTBuilder builder = new GVTBuilder();
        try {
            svgDoc  = (SVGDocument)loader.loadDocument(blURL.toString());
            gvtRoot = builder.build(ctx, svgDoc);
        } catch (Throwable t) {
            // t.printStackTrace();
        }
    }

    /**
     * This method is responsible for constructing an image that will
     * represent the missing image in the document.  This method
     * recives information about the reason a broken link image is
     * being requested in the <tt>code</tt> and <tt>params</tt>
     * parameters. These parameters may be used to generate nicely
     * localized messages for insertion into the broken link image, or
     * for selecting the broken link image returned.
     *
     * @param code This is the reason the image is unavailable should
     *             be taken from ErrorConstants.
     * @param params This is more detailed information about
     *        the circumstances of the failure.  */
    public Filter getBrokenLinkImage(Object base, String code, 
                                     Object[] params) {
        if (gvtRoot != null) {
            String message = formatMessage(base, code, params);
            Document doc = DOMUtilities.deepCloneDocument(svgDoc,
                                                          svgDoc.getImplementation());
            Element infoE = doc.getElementById("More_About");
            Element desc = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                                               SVGConstants.SVG_DESC_TAG);
            desc.appendChild(doc.createTextNode(message));
            infoE.appendChild(desc);

            Map props = new HashMap();
            props.put(BROKEN_LINK_PROPERTY, message);
            props.put(SVG_BROKEN_LINK_DOCUMENT_PROPERTY, doc);

            // We should format the code and params and replace a node
            // in the gvtRoot with the result.
            return new GraphicsNodeRable8Bit(gvtRoot, rc, props);
        }
        return null;
    }
}
