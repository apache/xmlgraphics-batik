/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import org.w3c.dom.Document;

/**
 * This interface represents an object which can build a Document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface DocumentFactory {

    /**
     * Sets whether or not the XML stream has to be validate, depending on the
     * specified parameter.
     *
     * @param isValidating true implies the XML stream will be validated
     */
    void setValidating(boolean isValidating);

    /**
     * Returns true if the XML stream has to be validated, false otherwise.
     */
    boolean isValidating();

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri) throws IOException;

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri, InputStream is)
        throws IOException;

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri, Reader r)
        throws IOException;

    /**
     * Returns the document descriptor associated with the latest created
     * document.
     * @return null if no document or descriptor was previously generated.
     */
    DocumentDescriptor getDocumentDescriptor();
}
