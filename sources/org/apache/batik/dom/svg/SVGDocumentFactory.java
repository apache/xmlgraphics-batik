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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.batik.dom.util.DocumentFactory;
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
public class SVGDocumentFactory extends DocumentFactory {
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
    public SVGDocumentFactory(String parser) {
	super(SVGDOMImplementation.getDOMImplementation(), parser);
    }

    /**
     * Creates a SVGOMDocument instance.
     * @param uri The document URI.
     * @param is  The document input source.
     * @exception DOMException if an error occured when building the document.
     * @exception SAXException if an error occured when reading the document.
     */
    public SVGOMDocument createDocument(String uri, InputSource is)
	throws DOMException, SAXException {
	SVGOMDocument doc;
	doc = (SVGOMDocument)createDocument
            (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, is);
	try {
	    doc.setURLObject(new URL(uri));
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e.getMessage());
	}
	return doc;
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
