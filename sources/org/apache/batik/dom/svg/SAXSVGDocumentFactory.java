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
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.MimeTypeConstants;

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
     * The dtd public IDs resource bundle class name.
     */
    protected final static String DTDIDS =
        "org.apache.batik.dom.svg.resources.dtdids";

    /**
     * Constant for HTTP content type header charset field.
     */
    protected final static String HTTP_CHARSET = "charset";

    /**
     * The accepted DTD public IDs.
     */
    protected static String dtdids;

    /**
     * Creates a new SVGDocumentFactory object.
     * @param parser The SAX2 parser classname.
     */
    public SAXSVGDocumentFactory(String parser) {
        super(ExtensibleSVGDOMImplementation.getDOMImplementation(), parser);
    }

    /**
     * Creates a new SVGDocumentFactory object.
     * @param parser The SAX2 parser classname.
     * @param dd Whether a document descriptor must be generated.
     */
    public SAXSVGDocumentFactory(String parser, boolean dd) {
        super(ExtensibleSVGDOMImplementation.getDOMImplementation(),
              parser, dd);
    }

    /**
     * Creates a SVGOMDocument instance.<br>
     * This method supports gzipped sources.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGOMDocument createDocument(String uri) throws IOException {
        ParsedURL purl = new ParsedURL(uri);

        InputStream is = purl.openStream(MimeTypeConstants.MIME_TYPES_SVG);

        InputSource isrc = new InputSource(is);
        
        // now looking for a charset encoding in the content type such
        // as "image/svg+xml; charset=iso8859-1" this is not official
        // for image/svg+xml yet! only for text/xml and maybe
        // for application/xml
        String contentType = purl.getContentType();
        int cindex = -1;
        if (contentType != null) {
            contentType = contentType.toLowerCase();
            cindex = contentType.indexOf(HTTP_CHARSET);
        }
 
        if (cindex != -1) {
            int i                 = cindex + HTTP_CHARSET.length();
            int eqIdx = contentType.indexOf('=', i);
            if (eqIdx != -1) {
                eqIdx++; // no one is interested in the equals sign...

                String charset;
                // The patch had ',' as the terminator but I suspect
                // that is the delimiter between possible charsets,
                // but if another 'attribute' were in the accept header
                // charset would be terminated by a ';'.  So I look
                // for both and take to closer of the two.
                int idx     = contentType.indexOf(',', eqIdx);
                int semiIdx = contentType.indexOf(';', eqIdx);
                if ((semiIdx != -1) && ((semiIdx < idx) || (idx == -1)))
                    idx = semiIdx;
                if (idx != -1)
                    charset = contentType.substring(eqIdx, idx);
                else 
                    charset = contentType.substring(eqIdx);
                isrc.setEncoding(charset.trim());
            }
        }

        isrc.setSystemId(uri);

        SVGOMDocument doc = (SVGOMDocument)super.createDocument
            (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, isrc);
        try {
            doc.setURLObject(new URL(purl.toString()));
        } catch (MalformedURLException mue) {
            // Not very likely to happen given we already opened the stream.
            throw new IOException("Malformed URL: " + uri);
        }

        return doc;
    }

    /**
     * Creates a SVGOMDocument instance.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGOMDocument createDocument(String uri, InputStream inp)
        throws IOException {
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
    public SVGOMDocument createDocument(String uri, Reader r)
        throws IOException {
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
    public Document createDocument(String ns, String root, String uri,
                                   InputStream is) throws IOException {
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
    public Document createDocument(String ns, String root, String uri,
                                   Reader r) throws IOException {
        if (!SVGDOMImplementation.SVG_NAMESPACE_URI.equals(ns) ||
            !"svg".equals(root)) {
            throw new RuntimeException("Bad root element");
        }
        return createDocument(uri, r);
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#startDocument()}.
     */
    public void startDocument() throws SAXException {
        super.startDocument();
	namespaces.put("", SVGDOMImplementation.SVG_NAMESPACE_URI);
	namespaces.put("xmlns:xlink", XLinkSupport.XLINK_NAMESPACE_URI);
    }

    /**
     * <b>SAX2</b>: Implements {@link
     * org.xml.sax.EntityResolver#resolveEntity(String,String)}.
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
        try {
            if (dtdids == null) {
                ResourceBundle rb;
                rb = ResourceBundle.getBundle(DTDIDS,
                                              Locale.getDefault());
                dtdids = rb.getString("publicIds");
            }
            if (publicId != null && dtdids.indexOf(publicId) != -1) {
                return new InputSource
                    (getClass().getResource
                     ("resources/svg10.dtd").toString());
            }
        } catch (MissingResourceException e) {
            throw new SAXException(e);
        }
        // Let the SAX parser find the entity.
        return null;
    }
}
