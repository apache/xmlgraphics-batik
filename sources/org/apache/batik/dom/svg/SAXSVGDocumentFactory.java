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

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.zip.GZIPInputStream;

import org.apache.batik.dom.util.SAXDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class contains methods for creating SVGDocument instances
 * from an URI using SAX2.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SAXSVGDocumentFactory
    extends    SAXDocumentFactory
    implements SVGDocumentFactory {

    /**
     * The dtd URIs resource bundle class name.
     */
    protected final static String DTDS =
        "org.apache.batik.dom.svg.resources.dtduris";

    /**
     * The accepted DTD URIs.
     */
    protected static String uris;

    /**
     * Creates a new SVGDocumentFactory object.
     * @param parser The SAX2 parser classname.
     */
    public SAXSVGDocumentFactory(String parser) {
        super(SVGDOMImplementation.getDOMImplementation(), parser);
    }

    /**
     * Creates a SVGOMDocument instance.<br>
     * This method supports gzipped sources.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGOMDocument createDocument(String uri) throws IOException {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IOException("Malformed URL: " + uri);
        }

        InputStream is = url.openStream();
        try {
            is = new GZIPInputStream(is);
        } catch (IOException e) {
            is.close();
            is = url.openStream();
        }

        InputSource isrc = new InputSource(is);

        SVGOMDocument doc = (SVGOMDocument)super.createDocument
            (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, isrc);
        doc.setURLObject(url);

        return doc;
    }

    /**
     * Creates a SVGOMDocument instance.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGOMDocument createDocument(String uri, InputStream inp) throws IOException {
        SVGOMDocument doc;
        InputSource is = new InputSource(inp);
        is.setSystemId(uri);

        try {
            doc = (SVGOMDocument)super.createDocument
                (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, is);
            if (uri != null) {
                doc.setURLObject(new URL(uri));
            }
        } catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }

    /**
     * Creates a SVGOMDocument instance.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGOMDocument createDocument(String uri, Reader r) throws IOException {
        SVGOMDocument doc;
        InputSource is = new InputSource(r);
        is.setSystemId(uri);

        try {
            doc = (SVGOMDocument)super.createDocument
                (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, is);
            if (uri != null) {
                doc.setURLObject(new URL(uri));
            }
        } catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri)
        throws IOException {
        if (!SVGDOMImplementation.SVG_NAMESPACE_URI.equals(ns) ||
            !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return createDocument(uri);
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri, InputStream is)
        throws IOException {
        if (!SVGDOMImplementation.SVG_NAMESPACE_URI.equals(ns) ||
            !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return createDocument(uri, is);
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri, Reader r)
        throws IOException {
        if (!SVGDOMImplementation.SVG_NAMESPACE_URI.equals(ns) ||
            !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return createDocument(uri, r);
    }

    /**
     * <b>SAX2</b>: Implements {@link
     * org.xml.sax.EntityResolver#resolveEntity(String,String)}.
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
        try {
            if (uris == null) {
                ResourceBundle rb;
                rb = ResourceBundle.getBundle(DTDS,
                                              Locale.getDefault());
                uris = rb.getString("uris");
            }
            if (uris.indexOf(systemId) != -1) {
                return new InputSource
                    (getClass().getResource
                     ("resources/svg-20001102.dtd").toString());
            }
        } catch (MissingResourceException e) {
            throw new SAXException(e);
        }
        // Let the SAX parser find the entity.
        return null;
    }
}
