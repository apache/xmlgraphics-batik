/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.StyleSheet;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.StyleSheetProcessingInstruction;

import org.apache.batik.dom.util.HashTable;

import org.w3c.dom.Node;

/**
 * This class provides an implementation of the 'xml-stylesheet' processing
 * instructions.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGStyleSheetProcessingInstruction
    extends StyleSheetProcessingInstruction
    implements CSSStyleSheetNode {
    
    /**
     * The style-sheet.
     */
    protected StyleSheet styleSheet;

    /**
     * Creates a new ProcessingInstruction object.
     */
    protected SVGStyleSheetProcessingInstruction() {
    }

    /**
     * Creates a new ProcessingInstruction object.
     */
    public SVGStyleSheetProcessingInstruction(String            data,
                                              AbstractDocument  owner,
                                              StyleSheetFactory f) {
        super(data, owner, f);
    }

    /**
     * Returns the URI of the referenced stylesheet.
     */
    public String getStyleSheetURI() {
        SVGOMDocument svgDoc;
        svgDoc = (SVGOMDocument)getOwnerDocument();
        URL url = svgDoc.getURLObject();
        String href = (String)getPseudoAttributes().get("href");
        if (url != null) {
            try {
                return new URL(url, href).toString();
            } catch (MalformedURLException e) {
            }
        }
        return href;
    }

    /**
     * Returns the associated style-sheet.
     */
    public StyleSheet getCSSStyleSheet() {
        if (styleSheet == null) {
            HashTable attrs = getPseudoAttributes();
            String type = (String)attrs.get("type");

            if ("text/css".equals(type)) {
                try {
                    String title     = (String)attrs.get("title");
                    String media     = (String)attrs.get("media");
                    String href      = (String)attrs.get("href");
                    String alternate = (String)attrs.get("alternate");
                    SVGOMDocument doc = (SVGOMDocument)getOwnerDocument();
                    URL url = doc.getURLObject();
                    CSSEngine e = doc.getCSSEngine();
                    styleSheet = e.parseStyleSheet(new URL(url, href), media);
                    styleSheet.setAlternate("yes".equals(alternate));
                    styleSheet.setTitle(title);
                } catch (MalformedURLException e) {
                    // !!! TODO exception handling.
                    e.printStackTrace();
                }
            }
        }
        return styleSheet;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGStyleSheetProcessingInstruction();
    }
}
