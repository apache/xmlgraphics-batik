/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
/**
 * This interface is to be used to provide alternate ways of 
 * generating a placeholder image when the ImageTagRegistry
 * fails to handle a given reference.
 */
public class SVGBrokenLinkProvider 
    extends    DefaultBrokenLinkProvider 
    implements ErrorConstants {

    public final static String SVG_BROKEN_LINK_DOCUMENT_PROPERTY = 
        "org.apache.batik.bridge.BrokenLinkDocument";

    UserAgent      userAgent;
    DocumentLoader loader;
    BridgeContext  ctx;
    GraphicsNode   gvtRoot = null;
    SVGDocument       svgDoc;
    
    public SVGBrokenLinkProvider() {
        userAgent = new UserAgentAdapter();
        loader    = new DocumentLoader(userAgent);
        ctx       = new BridgeContext(userAgent, loader);

        Class cls = SVGBrokenLinkProvider.class;
        URL blURL = cls.getResource("BrokenLink.svg");
        if (blURL == null) return;

        GVTBuilder builder = new GVTBuilder();
        try {
            svgDoc  = (SVGDocument)loader.loadDocument(blURL.toString());
            gvtRoot = builder.build(ctx, svgDoc);
        } catch (Exception ex) {
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
        if (gvtRoot == null) 
            return null;

        String message = formatMessage(base, code, params);
        Document doc = getBrokenLinkDocument(message);
        Map props = new HashMap();
        props.put(BROKEN_LINK_PROPERTY, message);
        props.put(SVG_BROKEN_LINK_DOCUMENT_PROPERTY, doc);
        
        return new GraphicsNodeRable8Bit(gvtRoot, props);
    }

    public SVGDocument getBrokenLinkDocument(Object base, 
                                          String code, Object [] params) {
        String message = formatMessage(base, code, params);
        return getBrokenLinkDocument(message);
    }

    public SVGDocument getBrokenLinkDocument(String message) {
        SVGDocument doc = (SVGDocument)DOMUtilities.deepCloneDocument
            (svgDoc, svgDoc.getImplementation());
        Element infoE = doc.getElementById("__More_About");
        Element title = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                                            SVGConstants.SVG_TITLE_TAG);
        title.appendChild(doc.createTextNode
                          (Messages.formatMessage
                           (MSG_BROKEN_LINK_TITLE, null)));
        Element desc = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                                           SVGConstants.SVG_DESC_TAG);
        desc.appendChild(doc.createTextNode(message));
        infoE.insertBefore(desc, infoE.getFirstChild());
        infoE.insertBefore(title, desc);
        return doc;
    }
}
