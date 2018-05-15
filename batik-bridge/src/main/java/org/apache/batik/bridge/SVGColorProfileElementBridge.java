/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.awt.color.ICC_Profile;
import java.io.IOException;

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.color.NamedProfileCache;
import org.apache.batik.util.ParsedURL;

import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class bridges an SVG <code>color-profile</code> element with an
 * <code>ICC_ColorSpace</code> object.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$ */
public class SVGColorProfileElementBridge extends AbstractSVGBridge
    implements ErrorConstants {

    /**
     * Profile cache
     */
    public NamedProfileCache cache = new NamedProfileCache();

    /**
     * Returns 'colorProfile'.
     */
    public String getLocalName() {
        return SVG_COLOR_PROFILE_TAG;
    }

    /**
     * Creates an ICC_ColorSpace according to the specified parameters.
     * <p>
     * If a color profile cannot be located an error message will be sent to the user agent
     * and the method returns null. In this case, Batik will later fall back to sRGB.
     *
     * @param ctx the bridge context to use
     * @param paintedElement element on which the color is painted
     * @param iccProfileName name of the profile that should be loaded
     *        that could be a color-profile element or an @color-profile
     *        CSS rule
     * @return the ICC color profile (or null if the color profile could not be located)
     */
    public ICCColorSpaceWithIntent createICCColorSpaceWithIntent(BridgeContext ctx,
                                                   Element paintedElement,
                                                   String iccProfileName) {
        // Check if there is one if the cache.
        ICCColorSpaceWithIntent cs = cache.request(iccProfileName.toLowerCase()); // todo locale??
        if (cs != null){
            return cs;
        }

        // There was no cached copies for the profile. Load it now.
        // Search for a color-profile element with specific name
        Document doc = paintedElement.getOwnerDocument();
        NodeList list = doc.getElementsByTagNameNS(SVG_NAMESPACE_URI,
                                                   SVG_COLOR_PROFILE_TAG);

        int n = list.getLength();
        Element profile = null;
        for(int i = 0; i < n; i++){
            Node node = list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element profileNode = (Element)node;
                String nameAttr
                    = profileNode.getAttributeNS(null, SVG_NAME_ATTRIBUTE);

                if(iccProfileName.equalsIgnoreCase(nameAttr)){
                    profile = profileNode;
                }
            }
        }

        if (profile == null) {
            return null;
        }

        // Now that we have a profile element,
        // try to load the corresponding ICC profile xlink:href
        String href = XLinkSupport.getXLinkHref(profile);
        ICC_Profile p = null;
        if (href != null) {
            String baseURI = profile.getBaseURI();
            ParsedURL pDocURL = null;
            if (baseURI != null) {
                pDocURL = new ParsedURL(baseURI);
            }

            ParsedURL purl = new ParsedURL(pDocURL, href);
            if (!purl.complete()) {
                BridgeException be = new BridgeException(ctx, paintedElement, ERR_URI_MALFORMED,
                        new Object[] {href});
                ctx.getUserAgent().displayError(be);
                return null; //Don't throw. Continue without the profile
            }
            try {
                ctx.getUserAgent().checkLoadExternalResource(purl, pDocURL);
                p = ICC_Profile.getInstance(purl.openStream());
            } catch (IOException ioEx) {
                BridgeException be = new BridgeException(ctx, paintedElement, ioEx, ERR_URI_IO,
                                          new Object[] {href});
                ctx.getUserAgent().displayError(be);
                return null; //Don't throw. Continue without the profile
                // ??? IS THAT AN ERROR FOR THE SVG SPEC ???
            } catch (SecurityException secEx) {
                BridgeException be = new BridgeException(ctx, paintedElement, secEx,
                        ERR_URI_UNSECURE,
                        new Object[] {href});
                ctx.getUserAgent().displayError(be);
                return null; //Don't throw. Continue without the profile
            }
        }
        if (p == null) {
            return null;
        }

        // Extract the rendering intent from profile element
        RenderingIntent intent = convertIntent(profile, ctx);
        cs = new ICCColorSpaceWithIntent(p, intent, href, iccProfileName);

        // Add profile to cache
        cache.put(iccProfileName.toLowerCase(), cs);
        return cs;
    }

    private static RenderingIntent convertIntent(Element profile, BridgeContext ctx) {

        String intent
            = profile.getAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE);

        if (intent.length() == 0) {
            return RenderingIntent.AUTO;
        }
        if (SVG_PERCEPTUAL_VALUE.equals(intent)) {
            return RenderingIntent.PERCEPTUAL;
        }
        if (SVG_AUTO_VALUE.equals(intent)) {
            return RenderingIntent.AUTO;
        }
        if (SVG_RELATIVE_COLORIMETRIC_VALUE.equals(intent)) {
            return RenderingIntent.RELATIVE_COLORIMETRIC;
        }
        if (SVG_ABSOLUTE_COLORIMETRIC_VALUE.equals(intent)) {
            return RenderingIntent.ABSOLUTE_COLORIMETRIC;
        }
        if (SVG_SATURATION_VALUE.equals(intent)) {
            return RenderingIntent.SATURATION;
        }
        throw new BridgeException
            (ctx, profile, ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] {SVG_RENDERING_INTENT_ATTRIBUTE, intent});
    }
}
