/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

/**
 * This class implements the {@link org.w3c.dom.DOMImplementation}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericDOMImplementation extends AbstractDOMImplementation {
    /**
     * The default instance of this class.
     */
    protected final static DOMImplementation DOM_IMPLEMENTATION =
        new GenericDOMImplementation();

    /**
     * Creates a new GenericDOMImplementation object.
     */
    public GenericDOMImplementation() {
    }

    /**
     * Returns the default instance of this class.
     */
    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    // DOMImplementation //////////////////////////////////////////////////////

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
     * DOMImplementation#createDocument(String,String,DocumentType)}
.
     */
    public Document createDocument(String namespaceURI, 
                                   String qualifiedName, 
                                   DocumentType doctype) throws DOMException {
        Document result = new GenericDocument(doctype, this);
        result.appendChild(result.createElementNS(namespaceURI,
                                                  qualifiedName));
        return result;
    }
}
