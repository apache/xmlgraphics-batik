/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.HashSet;
import java.util.Set;
import org.apache.batik.dom.util.CSSStyleDeclarationFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class provides support for non-CSS presentational hints
 * processing in SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ElementNonCSSPresentationalHintsSupport {
    /**
     * The presentation attributes.
     */
    public final static Set PRESENTATION_ATTRIBUTES = new HashSet();
    static {
        PRESENTATION_ATTRIBUTES.add("alignment-baseline");
        PRESENTATION_ATTRIBUTES.add("baseline-shift");
        PRESENTATION_ATTRIBUTES.add("clip");
        PRESENTATION_ATTRIBUTES.add("clip-path");
        PRESENTATION_ATTRIBUTES.add("clip-rule");
        PRESENTATION_ATTRIBUTES.add("color");
        PRESENTATION_ATTRIBUTES.add("color-interpolation");
        PRESENTATION_ATTRIBUTES.add("color-rendering");
        PRESENTATION_ATTRIBUTES.add("cursor");
        PRESENTATION_ATTRIBUTES.add("direction");
        PRESENTATION_ATTRIBUTES.add("display");
        PRESENTATION_ATTRIBUTES.add("dominant-baseline");
	PRESENTATION_ATTRIBUTES.add("enable-background");
	PRESENTATION_ATTRIBUTES.add("fill");
        PRESENTATION_ATTRIBUTES.add("fill-opacity");
        PRESENTATION_ATTRIBUTES.add("fill-rule");
        PRESENTATION_ATTRIBUTES.add("filter");
        PRESENTATION_ATTRIBUTES.add("flood-color");
        PRESENTATION_ATTRIBUTES.add("flood-opacity");
        PRESENTATION_ATTRIBUTES.add("font-family");
        PRESENTATION_ATTRIBUTES.add("font-size");
        PRESENTATION_ATTRIBUTES.add("font-size-adjust");
        PRESENTATION_ATTRIBUTES.add("font-stretch");
        PRESENTATION_ATTRIBUTES.add("font-style");
        PRESENTATION_ATTRIBUTES.add("font-variant");
        PRESENTATION_ATTRIBUTES.add("font-weight");
        PRESENTATION_ATTRIBUTES.add("glyph-orientation-horizontal");
        PRESENTATION_ATTRIBUTES.add("glyph-orientation-vertical");
        PRESENTATION_ATTRIBUTES.add("image-rendering");
        PRESENTATION_ATTRIBUTES.add("letter-spacing");
        PRESENTATION_ATTRIBUTES.add("lighting-color");
        PRESENTATION_ATTRIBUTES.add("marker-end");
        PRESENTATION_ATTRIBUTES.add("marker-mid");
        PRESENTATION_ATTRIBUTES.add("marker-start");
        PRESENTATION_ATTRIBUTES.add("mask");
        PRESENTATION_ATTRIBUTES.add("opacity");
        PRESENTATION_ATTRIBUTES.add("overflow");
        PRESENTATION_ATTRIBUTES.add("pointer-events");
        PRESENTATION_ATTRIBUTES.add("shape-rendering");
        PRESENTATION_ATTRIBUTES.add("stop-color");
        PRESENTATION_ATTRIBUTES.add("stop-opacity");
	PRESENTATION_ATTRIBUTES.add("stroke");
	PRESENTATION_ATTRIBUTES.add("stroke-dasharray");
	PRESENTATION_ATTRIBUTES.add("stroke-dashoffset");
	PRESENTATION_ATTRIBUTES.add("stroke-linecap");
	PRESENTATION_ATTRIBUTES.add("stroke-linejoin");
	PRESENTATION_ATTRIBUTES.add("stroke-miterlimit");
        PRESENTATION_ATTRIBUTES.add("stroke-opacity");
	PRESENTATION_ATTRIBUTES.add("stroke-width");
        PRESENTATION_ATTRIBUTES.add("text-anchor");
        PRESENTATION_ATTRIBUTES.add("text-decoration");
        PRESENTATION_ATTRIBUTES.add("text-rendering");
        PRESENTATION_ATTRIBUTES.add("unicode-bidi");
        PRESENTATION_ATTRIBUTES.add("visibility");
        PRESENTATION_ATTRIBUTES.add("word-spacing");
        PRESENTATION_ATTRIBUTES.add("writing-mode");
    }

    /**
     * Returns the translation of the non-CSS hints to the corresponding
     * CSS rules. The result can be null.
     */
    public static CSSStyleDeclaration getNonCSSPresentationalHints
        (Element elt) {
	CSSStyleDeclaration result = null;

	NamedNodeMap nnm = elt.getAttributes();
	int len = nnm.getLength();
	for (int i = 0; i < len; i++) {
	    Node attr = nnm.item(i);
	    String an = attr.getNodeName();
	    if (PRESENTATION_ATTRIBUTES.contains(an)) {
		if (result == null) {
		    DOMImplementation impl;
                    impl = elt.getOwnerDocument().getImplementation();
		    CSSStyleDeclarationFactory f;
                    f = (CSSStyleDeclarationFactory)impl;
		    result = f.createCSSStyleDeclaration();
		}
		result.setProperty(an, attr.getNodeValue(), "");
	    }
	}
	return result;
    }
}
