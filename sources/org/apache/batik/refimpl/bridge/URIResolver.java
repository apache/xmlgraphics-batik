/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;

import org.xml.sax.SAXException;

/**
 * This class is used to resolve the URI that can be found in a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class URIResolver {
    /**
     * The reference document.
     */
    protected SVGOMDocument document;

    /**
     * The document URI.
     */
    protected String documentURI;

    /**
     * The document loader.
     */
    protected DocumentLoader documentLoader;

    /**
     * Creates a new URI resolver object.
     * @param doc The reference document.
     * @param dl The document loader.
     */
    public URIResolver(SVGDocument doc, DocumentLoader dl) {
        document = (SVGOMDocument)doc;
        documentURI = doc.getURL();
        documentLoader = dl;
    }

    /**
     * Imports the element referenced by the given URI.
     * @param uri The element URI.
     * @param clone Whether a local element should be cloned.
     */
    public Element importElement(String uri, boolean clone)
        throws MalformedURLException,
               SAXException, InterruptedException {
        Node n = getNode(uri);
        if (n.getNodeType() == n.DOCUMENT_NODE) {
            throw new Error("Documents not allowed");
        }
        return importElement((Element)n, clone);
    }

    /**
     * Returns the node referenced by the given URI.
     * @return The document or the element
     */
    public Node getNode(String uri) throws MalformedURLException,
                              SAXException, InterruptedException {
        if (documentURI.equals(uri)) {
            return document;
        }
        if (uri.startsWith(documentURI) &&
            uri.length() > documentURI.length() + 1 &&
            uri.charAt(documentURI.length()) == '#') {
            uri = uri.substring(documentURI.length());
        }
        if (uri.startsWith("#")) {
            return document.getElementById(uri.substring(1));
        }

        URL url = new URL(((SVGOMDocument)document).getURLObject(), uri);
        Document doc = documentLoader.loadDocument(url.toString());

        String ref = url.getRef();
        if (url.getRef() == null) {
            return doc;
        } else {
            return doc.getElementById(ref);
        }
    }

    /**
     * Imports the given element.
     * @param e The element to import.
     * @param clone Whether a local element must be cloned.
     */
    public Element importElement(Element e, boolean clone)
        throws MalformedURLException {
        SVGOMDocument doc = (SVGOMDocument)e.getOwnerDocument();
        if (doc == document) {
            if (clone) {
                return (Element)e.cloneNode(true);
            } else {
                return e;
            }
        } else {
            Element result = (Element)document.importNode(e, true);
            computeStyleAndURIs(e, (ViewCSS)doc.getDefaultView(),
                                result, (ViewCSS)document.getDefaultView(),
                                doc.getURLObject());
            return result;
        }
    }

    /**
     * Partially computes the style in the use tree and set it in
     * the target tree.
     */
    protected void computeStyleAndURIs(Element use, ViewCSS uv,
                                       Element def, ViewCSS dv, URL url)
        throws MalformedURLException {
        String href = XLinkSupport.getXLinkHref(def);

        if (!href.equals("")) {
            XLinkSupport.setXLinkHref(def, new URL(url, href).toString());
        }

        CSSOMReadOnlyStyleDeclaration usd;
        AbstractViewCSS uview = (AbstractViewCSS)uv;

        usd = (CSSOMReadOnlyStyleDeclaration)uview.computeStyle(use, null);
        updateURIs(usd, url);
        ((AbstractViewCSS)dv).setComputedStyle(def, null, usd);

        for (Node un = use.getFirstChild(), dn = def.getFirstChild();
             un != null;
             un = un.getNextSibling(), dn = dn.getNextSibling()) {
            if (un.getNodeType() == Node.ELEMENT_NODE) {
                computeStyleAndURIs((Element)un, uv, (Element)dn, dv, url);
            }
        }
    }

    /**
     * Updates the URIs in the given style declaration.
     */
    protected void updateURIs(CSSOMReadOnlyStyleDeclaration sd, URL url)
        throws MalformedURLException {
        int len = sd.getLength();
        for (int i = 0; i < len; i++) {
            String name = sd.item(i);
            CSSValue val = sd.getLocalPropertyCSSValue(name);
            if (val != null &&
                val.getCssValueType() ==
                CSSPrimitiveValue.CSS_PRIMITIVE_VALUE) {
                CSSPrimitiveValue pv = (CSSPrimitiveValue)val;
                if (pv.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                    CSSOMReadOnlyValue v =
                        new CSSOMReadOnlyValue
                        (new ImmutableString(CSSPrimitiveValue.CSS_URI,
                               new URL(url, pv.getStringValue()).toString()));
                    sd.setPropertyCSSValue(name, v,
                                           sd.getLocalPropertyPriority(name),
                                           sd.getLocalPropertyOrigin(name));
                }
            }
        }
    }
}
