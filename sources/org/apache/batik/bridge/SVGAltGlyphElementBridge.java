/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.font.Glyph;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Bridge class for the &lt;altGlyph> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGAltGlyphElementBridge extends AbstractSVGBridge
                                      implements ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;altGlyph> element.
     */
    public SVGAltGlyphElementBridge() {
    }

    /**
     * Returns 'altGlyph'.
     */
    public String getLocalName() {
        return SVG_ALT_GLYPH_TAG;
    }

    /**
     * Constructs an array of Glyphs that represents the specified
     * &lt;altGlyph> element at the requested size.
     *
     * @param ctx The current bridge context.
     * @param altGlyphElement The altGlyph element to base the SVGGVTGlyphVector
     * construction on.
     * @param fontSize The font size of the Glyphs to create.
     *
     * @return The new SVGGVTGlyphVector or null if any of the glyphs are
     * unavailable.
     */
    public Glyph[] createAltGlyphArray(BridgeContext ctx,
                                       Element altGlyphElement,
                                       float fontSize) {

        // get the referenced element
        String uri = XLinkSupport.getXLinkHref(altGlyphElement);
        Element refElement = ctx.getReferencedElement(altGlyphElement, uri);

        if (refElement == null) {
            // couldn't find the referenced element
            return null;
        }

        // if the referenced element is a glyph
        if (refElement.getTagName().equals(SVG_GLYPH_TAG)) {

            Glyph glyph = getGlyph(ctx, uri, altGlyphElement, fontSize);

            if (glyph == null) {
                // failed to create a glyph for the specified glyph uri
                return null;
            }

            Glyph[] glyphArray = new Glyph[1];
            glyphArray[0] = glyph;
            return glyphArray;
        }

        // else should be an altGlyphDef element
        if (refElement.getTagName().equals(SVG_ALT_GLYPH_DEF_TAG)) {

            // if not local import the referenced altGlyphDef into the current document
            SVGOMDocument document
                = (SVGOMDocument)altGlyphElement.getOwnerDocument();
            SVGOMDocument refDocument
                = (SVGOMDocument)refElement.getOwnerDocument();
            boolean isLocal = (refDocument == document);

            Element localRefElement = (isLocal) ? refElement
                                 : (Element)document.importNode(refElement, true);
            if (!isLocal) {
                // need to attach the imported element to the document and
                // then compute the styles and uris
                Element g = document.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
                g.appendChild(localRefElement);
                CSSUtilities.computeStyleAndURIs(refElement, localRefElement);
            }

            // look for glyphRef children
            NodeList altGlyphDefChildren = localRefElement.getChildNodes();
            boolean containsGlyphRefNodes = false;
            int numAltGlyphDefChildren = altGlyphDefChildren.getLength();
            for (int i = 0; i < numAltGlyphDefChildren; i++) {
                Node altGlyphChild = altGlyphDefChildren.item(i);
                if (altGlyphChild.getNodeType() == Node.ELEMENT_NODE) {
                    if (((Element)altGlyphChild).getTagName().equals(SVG_GLYPH_REF_TAG)) {
                        containsGlyphRefNodes = true;
                        break;
                    }
                }
            }
            if (containsGlyphRefNodes) { // process the glyphRef children

                NodeList glyphRefNodes = localRefElement.getElementsByTagName(SVG_GLYPH_REF_TAG);
                int numGlyphRefNodes = glyphRefNodes.getLength();
                Glyph[] glyphArray = new Glyph[numGlyphRefNodes];
                for (int i = 0; i < numGlyphRefNodes; i++) {
                    // get the referenced glyph element
                    Element glyphRefElement = (Element)glyphRefNodes.item(i);
                    String glyphUri = XLinkSupport.getXLinkHref(glyphRefElement);

                    Glyph glyph = getGlyph(ctx, glyphUri, altGlyphElement, fontSize);
                    if (glyph == null) {
                        // failed to create a glyph for the specified glyph uri
                        return null;
                    }
                    glyphArray[i] = glyph;
                }
                return glyphArray;

            } else { // try looking for altGlyphItem children

                NodeList altGlyphItemNodes = localRefElement.getElementsByTagName(SVG_ALT_GLYPH_ITEM_TAG);
                int numAltGlyphItemNodes = altGlyphItemNodes.getLength();
                if (numAltGlyphItemNodes > 0) {
                    Glyph[] glyphArray = new Glyph[numAltGlyphItemNodes];
                    for (int i = 0; i < numAltGlyphItemNodes; i++) {
                        // try to find a resolvable glyphRef
                        Element altGlyphItemElement = (Element)altGlyphItemNodes.item(i);
                        NodeList altGlyphRefNodes = altGlyphItemElement.getElementsByTagName(SVG_GLYPH_REF_TAG);
                        int numAltGlyphRefNodes = altGlyphRefNodes.getLength();
                        boolean foundMatchingGlyph = false;
                        for (int j = 0; j < numAltGlyphRefNodes; j++) {
                            // get the referenced glyph element
                            Element glyphRefElement = (Element)altGlyphRefNodes.item(j);
                            String glyphUri = XLinkSupport.getXLinkHref(glyphRefElement);

                            Glyph glyph = getGlyph(ctx, glyphUri, altGlyphElement, fontSize);
                            if (glyph != null) {
                                // found a matching glyph for this altGlyphItem
                                glyphArray[i] = glyph;
                                foundMatchingGlyph = true;
                                break;
                            }
                        }
                        if (!foundMatchingGlyph) {
                            // couldn't find a matching glyph for this alGlyphItem
                            // so stop and return null
                            return null;
                        }
                    }
                    return glyphArray;
                }
            }
        }

        // reference is not to a valid element type, throw an exception
        throw new BridgeException(altGlyphElement, ERR_URI_BAD_TARGET,
                                  new Object[] {uri});
    }


    /**
     * Returns a Glyph object that represents the glyph at the specified URI
     * scaled to the required font size.
     *
     * @param ctx The bridge context.
     * @param glyphUri The URI of the glyph to retreive.
     * @param altGlyphElement The element that references the glyph.
     * @param fontSize Indicates the required size of the glyph.
     * @return The Glyph or null if the glyph URI is not available.
     */
    private Glyph getGlyph(BridgeContext ctx,
                           String glyphUri,
                           Element altGlyphElement,
                           float fontSize) {

        Element refGlyphElement = null;
        try {
            refGlyphElement = ctx.getReferencedElement(altGlyphElement, glyphUri);
        } catch (BridgeException e) {
            // this is ok, it is possible that the glyph at the given uri is not available
        }

        if (refGlyphElement == null || !refGlyphElement.getTagName().equals(SVG_GLYPH_TAG)) {
            // couldn't find the referenced glyph element,
            // or referenced element not a glyph
            return null;
        }

        // see if the referenced glyph element is local
        SVGOMDocument document
            = (SVGOMDocument)altGlyphElement.getOwnerDocument();
        SVGOMDocument refDocument
            = (SVGOMDocument)refGlyphElement.getOwnerDocument();
        boolean isLocal = (refDocument == document);

        // if not local, import both the glyph and its font-face element
        Element localGlyphElement = null;
        Element localFontFaceElement = null;

        if (isLocal) {
            localGlyphElement = refGlyphElement;
            Element fontElement = (Element)localGlyphElement.getParentNode();
            NodeList fontFaceElements = fontElement.getElementsByTagName(SVG_FONT_FACE_TAG);
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }

        } else {

            // import the whole font
            Element localFontElement = (Element)document.importNode(refGlyphElement.getParentNode(), true);
            Element g = document.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
            g.appendChild(localFontElement);
            CSSUtilities.computeStyleAndURIs((Element)refGlyphElement.getParentNode(), localFontElement);

            // get the local glyph element
            String glyphId = refGlyphElement.getAttributeNS(null, SVG_ID_ATTRIBUTE);
            NodeList glyphElements = localFontElement.getElementsByTagName(SVG_GLYPH_TAG);
            for (int i = 0; i < glyphElements.getLength(); i++) {
                Element glyphElem = (Element)glyphElements.item(i);
                if (glyphElem.getAttributeNS(null, SVG_ID_ATTRIBUTE).equals(glyphId)) {
                    localGlyphElement = glyphElem;
                    break;
                }
            }
            // get the local font-face element
            NodeList fontFaceElements = localFontElement.getElementsByTagName(SVG_FONT_FACE_TAG);
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }
        }

        // if couldn't find the glyph or its font-face return null
        if (localGlyphElement == null || localFontFaceElement == null) {
            return null;
        }

        SVGFontFaceElementBridge fontFaceBridge
            = (SVGFontFaceElementBridge)ctx.getBridge(localFontFaceElement);
        SVGFontFace fontFace = fontFaceBridge.createFontFace(ctx, localFontFaceElement);

        SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(localGlyphElement);
        return glyphBridge.createGlyph(ctx, localGlyphElement, altGlyphElement, -1, fontSize, fontFace);

    }
}
