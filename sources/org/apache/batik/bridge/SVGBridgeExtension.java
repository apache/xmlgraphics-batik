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

import java.util.Collections;
import java.util.Iterator;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * This is a Service interface for classes that want to extend the
 * functionality of the Bridge, to support new tags in the rendering tree.
 */
public class SVGBridgeExtension implements BridgeExtension {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.
     */
    public float getPriority() { return 0f; }

    /**
     * This should return the list of extensions implemented
     * by this BridgeExtension.
     * @return An iterator containing strings one for each implemented
     *         extension.
     */
    public Iterator getImplementedExtensions() {
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor() {
        return "The Apache Batik Team.";
    }

    /**
     * This should contain a contact address (usually an e-mail address).
     */
    public String getContactAddress() {
        return "batik-dev@xml.apache.org";
    }

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL() {
        return "http://xml.apache.org/batik";
    }

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription() {
        return "The required SVG 1.0 tags";
    }

    /**
     * This method should update the BridgeContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The BridgeContext instance to be updated
     */
    public void registerTags(BridgeContext ctx) {
        // bridges to handle elements in the SVG namespace

        ctx.putBridge(new SVGAElementBridge());
        ctx.putBridge(new SVGAltGlyphElementBridge());
        ctx.putBridge(new SVGCircleElementBridge());
        ctx.putBridge(new SVGClipPathElementBridge());
        ctx.putBridge(new SVGColorProfileElementBridge());
        ctx.putBridge(new SVGDescElementBridge());
        ctx.putBridge(new SVGEllipseElementBridge());
        ctx.putBridge(new SVGFeBlendElementBridge());
        ctx.putBridge(new SVGFeColorMatrixElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge());
        ctx.putBridge(new SVGFeCompositeElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncAElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncRElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncGElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncBElementBridge());
        ctx.putBridge(new SVGFeConvolveMatrixElementBridge());
        ctx.putBridge(new SVGFeDiffuseLightingElementBridge());
        ctx.putBridge(new SVGFeDisplacementMapElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeDistantLightElementBridge());
        ctx.putBridge(new SVGFeFloodElementBridge());
        ctx.putBridge(new SVGFeGaussianBlurElementBridge());
        ctx.putBridge(new SVGFeImageElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge.SVGFeMergeNodeElementBridge());
        ctx.putBridge(new SVGFeMorphologyElementBridge());
        ctx.putBridge(new SVGFeOffsetElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFePointLightElementBridge());
        ctx.putBridge(new SVGFeSpecularLightingElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeSpotLightElementBridge());
        ctx.putBridge(new SVGFeTileElementBridge());
        ctx.putBridge(new SVGFeTurbulenceElementBridge());
        ctx.putBridge(new SVGFontElementBridge());
        ctx.putBridge(new SVGFontFaceElementBridge());
        ctx.putBridge(new SVGFilterElementBridge());
        ctx.putBridge(new SVGGElementBridge());
        ctx.putBridge(new SVGGlyphElementBridge());
        ctx.putBridge(new SVGHKernElementBridge());
        ctx.putBridge(new SVGImageElementBridge());
        ctx.putBridge(new SVGLineElementBridge());
        ctx.putBridge(new SVGLinearGradientElementBridge());
        ctx.putBridge(new SVGMarkerElementBridge());
        ctx.putBridge(new SVGMaskElementBridge());
        ctx.putBridge(new SVGMissingGlyphElementBridge());
        ctx.putBridge(new SVGPathElementBridge());
        ctx.putBridge(new SVGPatternElementBridge());
        ctx.putBridge(new SVGPolylineElementBridge());
        ctx.putBridge(new SVGPolygonElementBridge());
        ctx.putBridge(new SVGRadialGradientElementBridge());

        //
        // <!> FIX ME
        // Once we have more DynamicBridges, we will have a 
        // single if() and register all the right bridges
        // for static or dynamic together.
        //
        // if (false) { // ctx.isDynamic()){
            // System.out.println("Using Dynamic rect Bridge");
            //ctx.putBridge(new SVGRectElementBridge.Dynamic());
        //} else {
            ctx.putBridge(new SVGRectElementBridge());
            //}

        ctx.putBridge(new AbstractSVGGradientElementBridge.SVGStopElementBridge());
        ctx.putBridge(new SVGSVGElementBridge());
        ctx.putBridge(new SVGSwitchElementBridge());
        ctx.putBridge(new SVGTextElementBridge());
        ctx.putBridge(new SVGTextPathElementBridge());
        ctx.putBridge(new SVGTitleElementBridge());
        ctx.putBridge(new SVGUseElementBridge());
        ctx.putBridge(new SVGVKernElementBridge());

    }

    /**
     * Whether the presence of the specified element should cause
     * the document to be dynamic.  If this element isn't handled
     * by this BridgeExtension, just return false.
     *
     * @param e The element to check.
     */
    public boolean isDynamicElement(Element e) {
        String ns = e.getNamespaceURI();
        if (!SVGConstants.SVG_NAMESPACE_URI.equals(ns)) {
            return false;
        }
        String ln = e.getLocalName();
        if (ln.equals(SVGConstants.SVG_SCRIPT_TAG)
                || ln.startsWith("animate")
                || ln.equals("set")) {
            return true;
        }
        return false;
    }
}
