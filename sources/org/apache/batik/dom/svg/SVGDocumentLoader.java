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

import org.apache.batik.bridge.DocumentLoader;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is responsible on loading an SVG document.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGDocumentLoader implements DocumentLoader {

    /**
     * The document factory used to create the document according a
     * DOM implementation.
     */
    protected SVGDocumentFactory documentFactory;

    /**
     * Constructs a new <tt>DocumentLoader</tt> with the specified XML parser.
     * @param parser The SAX2 parser classname.
     */
    public SVGDocumentLoader(String parser) {
        this.documentFactory = new SVGDocumentFactory(parser);
    }

    public Document loadDocument(String uri) throws DOMException, SAXException, InterruptedException {
        return documentFactory.createDocument(uri, new InputSource(uri));
    }

    public void dispose() {
        // Nothing to do
    }
}
