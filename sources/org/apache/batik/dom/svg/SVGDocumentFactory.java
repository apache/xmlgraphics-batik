/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import org.w3c.dom.svg.SVGDocument;

import org.apache.batik.dom.util.DocumentFactory;

/**
 * This interface represents an object which can build a SVGDocument.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGDocumentFactory extends DocumentFactory {

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    SVGDocument createSVGDocument(String uri) throws IOException;

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    SVGDocument createSVGDocument(String uri, InputStream is) 
        throws IOException;

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    SVGDocument createSVGDocument(String uri, Reader r) throws IOException;

}
