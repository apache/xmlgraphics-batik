/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.color.NamedProfileCache;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.ParsedURL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class bridges an SVG <tt>color-profile</tt> element with an
 * <tt>ICC_ColorSpace</tt> object.
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
     *
     * @param ctx the bridge context to use
     * @param paintedElement element on which the color is painted
     * @param iccProfileName name of the profile that should be loaded
     *        that could be a color-profile element or an @color-profile
     *        CSS rule
     */
    public ICCColorSpaceExt createICCColorSpaceExt(BridgeContext ctx,
                                                   Element paintedElement,
                                                   String iccProfileName) {
        // Check if there is one if the cache.
        ICCColorSpaceExt cs = cache.request(iccProfileName.toLowerCase());
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
        for(int i=0; i<n; i++){
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

        if(profile == null)
            return null;

        // Now that we have a profile element,
        // try to load the corresponding ICC profile xlink:href
        String href = XLinkSupport.getXLinkHref(profile);
        ICC_Profile p = null;
        if (href != null) {
            String baseURI= ((SVGOMDocument)doc).getURL();
            ParsedURL purl = new ParsedURL(baseURI, href);
            if (!purl.complete()) 
                throw new BridgeException(paintedElement, ERR_URI_MALFORMED,
                                          new Object[] {href});
            try{
                p = ICC_Profile.getInstance(purl.openStream());
            } catch(IOException e) {
                throw new BridgeException(paintedElement, ERR_URI_IO,
                                          new Object[] {href});
                // ??? IS THAT AN ERROR FOR THE SVG SPEC ???
            }
        }
        if (p == null) {
            return null;
        }

        // Extract the rendering intent from profile element
        int intent = convertIntent(profile);
        cs = new ICCColorSpaceExt(p, intent);

        // Add profile to cache
        cache.put(iccProfileName.toLowerCase(), cs);
        return cs;
    }

    private static int convertIntent(Element profile) {

        String intent
            = profile.getAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE);

        if (intent.length() == 0) {
            return ICCColorSpaceExt.AUTO;
        }
        if (SVG_PERCEPTUAL_VALUE.equals(intent)) {
            return ICCColorSpaceExt.PERCEPTUAL;
        }
        if (SVG_AUTO_VALUE.equals(intent)) {
            return ICCColorSpaceExt.AUTO;
        }
        if (SVG_RELATIVE_COLORIMETRIC_VALUE.equals(intent)) {
            return ICCColorSpaceExt.RELATIVE_COLORIMETRIC;
        }
        if (SVG_ABSOLUTE_COLORIMETRIC_VALUE.equals(intent)) {
            return ICCColorSpaceExt.ABSOLUTE_COLORIMETRIC;
        }
        if (SVG_SATURATION_VALUE.equals(intent)) {
            return ICCColorSpaceExt.SATURATION;
        }
        throw new BridgeException
            (profile, ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] {SVG_RENDERING_INTENT_ATTRIBUTE, intent});
    }
}
