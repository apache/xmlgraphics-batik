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

import org.apache.batik.css.ExtendedLinkStyle;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.StyleSheetProcessingInstruction;

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
    implements ExtendedLinkStyle {
    
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
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGStyleSheetProcessingInstruction();
    }
}
