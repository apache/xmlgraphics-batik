/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import org.apache.batik.dom.util.OverrideStyleElement;
import org.apache.batik.dom.util.OverrideStyleElementSupport;
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
public class SVGStylableSupport {
    /**
     * The name of the style attribute.
     */
    public final static String STYLE = "style";

    /**
     * The name of the class attribute.
     */
    public final static String CLASS = "class";

    /**
     * The reference to the inline style.
     */
    protected WeakReference inlineStyle;

    /**
     * The override style support.
     */
    protected OverrideStyleElementSupport overrideStyleSupport;

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
        return elt.hasAttribute(STYLE);
    }

    /**
     * To implement {@link org.w3c.dom.svg.SVGStylable#getStyle()}.
     */
    public CSSStyleDeclaration getStyle(Element elt) {
        CSSStyleDeclaration style;
        if (inlineStyle == null ||
            (style = (CSSStyleDeclaration)inlineStyle.get()) == null) {
	    SVGDOMImplementation impl;
            impl = (SVGDOMImplementation)elt.getOwnerDocument().
                getImplementation();
	    style = impl.createCSSStyleDeclaration();
	    style.setCssText(elt.getAttribute(STYLE));
            inlineStyle = new WeakReference(style);
        }
        return style;
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGStylable#getPresentationAttribute(String)}.
     */
    public CSSValue getPresentationAttribute(String name, Element elt) {
	throw new RuntimeException
	    (" !!! TODO: SVGStylableSupport.getPresentationAttribute()");
    }

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGStylable#getClassName()}.
     */
    public SVGAnimatedString getClassName(Element elt) {
	throw new RuntimeException(" !!! TODO: SVGStylableSupport.getClassName()");
    }

    /**
     * To implements {@link
     * OverrideStyleElement#hasOverrideStyle(String)}.
     */
    public boolean hasOverrideStyle(String pseudoElt) {
        return (overrideStyleSupport == null) ||
            overrideStyleSupport.hasOverrideStyle(pseudoElt);
    }    

    /**
     * To implements {@link
     * OverrideStyleElement#getOverrideStyle(String)}.
     */
    public CSSStyleDeclaration getOverrideStyle(String pseudoElt,
                                                Element elt) {
        if (overrideStyleSupport == null) {
            Document doc = elt.getOwnerDocument();
            SVGDOMImplementation impl;
            impl = (SVGDOMImplementation)doc.getImplementation();
            overrideStyleSupport = new OverrideStyleElementSupport(impl);
        }
        return overrideStyleSupport.getOverrideStyle(pseudoElt);
    }
}
