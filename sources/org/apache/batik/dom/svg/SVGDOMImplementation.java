/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.net.URL;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.css.CSSOMStyleDeclaration;
import org.apache.batik.css.CSSOMStyleSheet;
import org.apache.batik.css.DOMMediaList;
import org.apache.batik.css.svg.SVGValueFactoryMap;
import org.apache.batik.css.value.ValueFactoryMap;
import org.apache.batik.dom.AbstractDOMImplementation;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.util.CSSStyleDeclarationFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.svg.SVGDocument;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGDOMImplementation
    extends    AbstractDOMImplementation
    implements DOMImplementationCSS,
               CSSStyleDeclarationFactory,
               StyleSheetFactory {
    /**
     * The SVG namespace uri.
     */
    public final static String SVG_NAMESPACE_URI =
        "http://www.w3.org/2000/svg";

    /**
     * The default instance of this class.
     */
    protected final static DOMImplementation DOM_IMPLEMENTATION =
        new SVGDOMImplementation();

    static {
        EventSupport.registerEventFactory("SVGEvents",
            new EventSupport.EventFactory() {
                    public Event createEvent() {
                        return new SVGOMEvent();
                    }
                });
    }

    {
        features.put("CSS",            "2.0");
        features.put("StyleSheets",    "2.0");
        features.put("SVG",            "1.0");
        features.put("SVGEvents",      "1.0");
    }

    /**
     * Returns the default instance of this class.
     */
    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    /**
     * The CSS value factory map for SVG.
     */
    protected ValueFactoryMap valueFactoryMap =
        new SVGValueFactoryMap(CSSDocumentHandler.createParser());

    /**
     * <b>DOM</b>: Implements {@link
     * DOMImplementation#createDocumentType(String,String,String)}.
     */
    public DocumentType createDocumentType(String qualifiedName,
                                           String publicId,
                                           String systemId) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR,
                               "Doctype not supported");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * DOMImplementation#createDocument(String,String,DocumentType)}.
     */
    public Document createDocument(String namespaceURI,
                                   String qualifiedName,
                                   DocumentType doctype)
        throws DOMException {
        Document result = new SVGOMDocument(doctype, this);
        result.appendChild(result.createElementNS(namespaceURI,
                                                  qualifiedName));
        return result;
    }

    // CSSStyleDeclarationFactory ///////////////////////////////////////////

    /**
     * Creates a style declaration.
     * @return a CSSOMStyleDeclaration instance.
     */
    public CSSStyleDeclaration createCSSStyleDeclaration() {
        try {
            CSSOMStyleDeclaration result = new CSSOMStyleDeclaration();
            result.setValueFactoryMap(valueFactoryMap);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // DOMImplementationCSS /////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * DOMImplementationCSS#createCSSStyleSheet(String,String)}.
     */
    public CSSStyleSheet createCSSStyleSheet(String title, String media) {
        try {
            return new CSSOMStyleSheet(null,
                                       null,
                                       null,
                                       title,
                                       new DOMMediaList(media),
                                       null,
                                       valueFactoryMap,
                                       CSSDocumentHandler.createParser());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // StyleSheetFactory /////////////////////////////////////////////

    /**
     * Creates a stylesheet from the data of an xml-stylesheet
     * processing instruction or throws a DOMException when it is not possible
     * to create the given stylesheet.
     */
    public StyleSheet createStyleSheet(Node n, String data) {
        HashTable attrs = new HashTable();
        attrs.put("alternate", "no");
        attrs.put("media", "all");
        DOMUtilities.parseStyleSheetPIData(data, attrs);

        String type = (String)attrs.get("type");

        if ("text/css".equals(type)) {
            try {
                String title = (String)attrs.get("title");
                String media = (String)attrs.get("media");
                String href  = (String)attrs.get("href");

                SVGOMDocument doc = (SVGOMDocument)n.getOwnerDocument();
                URL url = new URL(doc.getURLObject(), href);

                CSSOMStyleSheet ss = new CSSOMStyleSheet
                    (n,
                     null,
                     url.toString(),
                     title,
                     new DOMMediaList(media),
                     null,
                     valueFactoryMap,
                     CSSDocumentHandler.createParser());

                CSSDocumentHandler.parseStyleSheet(ss, url.toString());
                return ss;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        //throw new RuntimeException("'" + type + "' not supported");
        return null;
    }
}
