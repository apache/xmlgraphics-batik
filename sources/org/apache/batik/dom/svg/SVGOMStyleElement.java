/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.css.CSSOMStyleSheet;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.svg.SVGStyleElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGStyleElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMStyleElement
    extends    SVGOMElement
    implements SVGStyleElement,
	       LinkStyle {
    /**
     * The "type" attribute name.
     */
    public final static String TYPE = "type";

    /**
     * The "media" attribute name.
     */
    public final static String MEDIA = "media";

    /**
     * The "title" attribute name.
     */
    public final static String TITLE = "title";

    // The default attribute values registration.
    static {
	registerDefaultAttributeValue("style",
                                      XMLSupport.XML_NAMESPACE_URI,
				      "space",
				      "preserve");
    }

    /**
     * The style sheet.
     */
    protected StyleSheet sheet;

    /**
     * Creates a new SVGOMStyleElement object.
     */
    public SVGOMStyleElement() {
    }

    /**
     * Creates a new SVGOMStyleElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMStyleElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "style";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#getXMLspace()}.
     */
    public String getXMLspace() {
	return XMLSupport.getXMLSpace(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.LinkStyle#getSheet()}.
     */
    public StyleSheet getSheet() {
        if (sheet == null) {
            // Create the stylesheet
	    if (!getType().equals("text/css")) {
		throw createDOMException
		    (DOMException.NOT_SUPPORTED_ERR,
		     "stylesheet.type",
		     new Object[] { getType() });
	    }

            DOMImplementationCSS impl;
            impl = (DOMImplementationCSS)getOwnerDocument().
                getImplementation();
            CSSOMStyleSheet ss;
            ss = (CSSOMStyleSheet)impl.createCSSStyleSheet(getTitle(),
                                                           getMedia());

	    StringBuffer sb = new StringBuffer();
            for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
		sb.append(n.getNodeValue());
            }
	    CSSDocumentHandler.parseRules(ss, sb.toString());
            sheet = ss;
        }
        return sheet;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#setXMLspace(String)}.
     */
    public void setXMLspace(String xmlspace) throws DOMException {
	XMLSupport.setXMLSpace(this, xmlspace);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#getType()}.
     */
    public String getType() {
	return getAttribute(TYPE);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#setType(String)}.
     */
    public void setType(String type) throws DOMException {
	setAttribute(TYPE, type);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#getMedia()}.
     */
    public String getMedia() {
	return getAttribute(MEDIA);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#setMedia(String)}.
     */
    public void setMedia(String media) throws DOMException {
	setAttribute(MEDIA, media);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#getTitle()}.
     */
    public String getTitle() {
	return getAttribute(TITLE);
    }
 
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStyleElement#setTitle(String)}.
     */
    public void setTitle(String title) throws DOMException {
	setAttribute(TITLE, title);
    }
}
