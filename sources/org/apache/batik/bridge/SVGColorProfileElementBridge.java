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
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.color.NamedProfileCache;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class bridges an SVG <tt>color-profile</tt> element with
 * an <tt>ICC_ColorSpace</tt> object.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGColorProfileElementBridge implements Bridge, SVGConstants {
    /**
     * Profile cache
     */
    public NamedProfileCache cache = new NamedProfileCache();

    /**
     * Builds an ICC_ColorSpace for the given input color profile 
     * name, if the name can be resolved and successfully accessed
     *
     * @param iccProfileName name of the profile that should be loaded
     *        that could be a color-profile element or an @color-profile
     *        CSS rule
     *
     * @param ctx BridgeContext 
     *
     * @param paintedElement element on which the color is painted
     */
    public ICCColorSpaceExt build(String iccProfileName,
                                  BridgeContext ctx,
                                  Element paintedElement){
        /*
         * Check if there is one if the cache.
         */
        ICCColorSpaceExt cs = cache.request(iccProfileName.toLowerCase());
        if(cs != null){
            return cs;
        }

        /**
         * There was no cached copies for the profile. Load it
         * now.
         * Search for a color-profile element with specific name
         */
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

        /**
         * Now that we have a profile element, try to load the 
         * corresponding ICC profile xlink:href
         */
        String href = XLinkSupport.getXLinkHref(profile);
        ICC_Profile p = null;
        if(href != null){
            try{
                URL baseURL = ((SVGOMDocument)doc).getURLObject();
                URL url = new URL(baseURL, href);
                p = ICC_Profile.getInstance(url.openStream());
            }catch(MalformedURLException e){
                e.printStackTrace(System.err);
                throw new IllegalAttributeValueException
                    (Messages.formatMessage("color-profile.xlinkHref.invalid", null));
            }catch(IOException e){
                // ??? IS THAT AN ERROR FOR THE SVG SPEC ???
                e.printStackTrace(System.err);
            }
        }

        if(p == null){
            return null;
        }

        /**
         * Extract the rendering intent from profile element
         */
        String intentStr = profile.getAttributeNS(null, SVG_RENDERING_INTENT_ATTRIBUTE);
        int intent = convertIntent(intentStr);

        cs = new ICCColorSpaceExt(p, intent);

        /**
         * Add profile to cache
         */
        cache.put(iccProfileName.toLowerCase(), cs);

        return cs;
    }

    private int convertIntent(String intentStr){
        if("".equals(intentStr)){
            return ICCColorSpaceExt.AUTO;
        }
        if(VALUE_RENDERING_INTENT_PERCEPTUAL_VALUE.equals(intentStr)){
            return ICCColorSpaceExt.PERCEPTUAL;
        }
        if(VALUE_RENDERING_INTENT_AUTO_VALUE.equals(intentStr)){
            return ICCColorSpaceExt.AUTO;
        }
        if(VALUE_RENDERING_INTENT_RELATIVE_COLORIMETRIC_VALUE.equals(intentStr)){
            return ICCColorSpaceExt.RELATIVE_COLORIMETRIC;
        }
        if(VALUE_RENDERING_INTENT_ABSOLUTE_COLORIMETRIC_VALUE.equals(intentStr)){
            return ICCColorSpaceExt.ABSOLUTE_COLORIMETRIC;
        }

        if(VALUE_RENDERING_INTENT_SATURATION_VALUE.equals(intentStr)){
            return ICCColorSpaceExt.SATURATION;
        }

        throw new IllegalAttributeValueException
            (Messages.formatMessage("color-profile.intent.invalid", new Object[]{intentStr}));
    }
}
