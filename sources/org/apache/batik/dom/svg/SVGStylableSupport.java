/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides support for implementing a stylable element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGStylableSupport implements SVGConstants {

    /**
     * Creates a new SVGStylableSupport object.
     */
    public SVGStylableSupport() {
    }

    /**
     * To implement {@link
     * org.apache.batik.css.ExtendedElementCSSInlineStyle#hasStyle()}.
     */
    public static boolean hasStyle(Element elt) {
        return elt.hasAttribute(SVG_STYLE_ATTRIBUTE);
    }

    /**
     * To implement {@link org.w3c.dom.svg.SVGStylable#getStyle()}.
     */
    public static CSSStyleDeclaration getStyle(Element elt) {
	// !!! TODO: getStyle()
        CSSStyleDeclaration style;
        SVGDOMImplementation impl;
        impl = (SVGDOMImplementation)elt.getOwnerDocument().getImplementation();
        style = impl.createCSSStyleDeclaration();
        style.setCssText(elt.getAttribute(SVG_STYLE_ATTRIBUTE));
        return style;
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGStylable#getPresentationAttribute(String)}.
     */
    public static CSSValue getPresentationAttribute(String name, Element elt) {
	throw new RuntimeException(" !!! TODO: getPresentationAttribute()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGStylable#getClassName()}.
     */
    public static SVGAnimatedString getClassName(Element elt) {
	throw new RuntimeException(" !!! TODO: getClassName()");
    }

    /**
     * To implements {@link
     * OverrideStyleElement#hasOverrideStyle(String)}.
     */
    public static boolean hasOverrideStyle(String pseudoElt) {
        // !!! TODO: hasOverrideStyle()
        return false;
    }

    /**
     * To implements {@link
     * OverrideStyleElement#getOverrideStyle(String)}.
     */
    public static CSSStyleDeclaration getOverrideStyle(String pseudoElt, Element elt) {
	// !!! TODO: getOverrideStyle()
        Document doc = elt.getOwnerDocument();
        SVGDOMImplementation impl = (SVGDOMImplementation)doc.getImplementation();
        return impl.createCSSStyleDeclaration();
    }
}
