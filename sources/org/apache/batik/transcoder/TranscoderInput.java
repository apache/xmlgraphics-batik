/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;

/**
 * This class represents a generic input of a <tt>Transcoder</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscoderInput {

    /**
     * The optional XML reader to receive SAX events.
     */
    protected XMLReader xmlReader;

    /**
     * The optional input has a byte stream.
     */
    protected InputStream istream;

    /**
     * The optional input as a character stream.
     */
    protected Reader reader;

    /**
     * The optional input as XML Document.
     */
    protected Document document;

    /**
     * The optional input as a URI.
     */
    protected String uri;

    /**
     * Constructs a new empty <tt>TranscoderInput</tt>.
     */
    public TranscoderInput() {
    }

    /**
     * Constructs a new <tt>TranscoderInput</tt> with the specified
     * XML reader.
     * @param xmlReader the XML reader of this transcoder input
     */
    public TranscoderInput(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    /**
     * Constructs a new <tt>TranscoderInput</tt> with the specified
     * byte stream input.
     * @param istream the byte stream of this transcoder input
     */
    public TranscoderInput(InputStream istream) {
        this.istream = istream;
    }

    /**
     * Constructs a new <tt>TranscoderInput</tt> with the specified
     * character stream.
     * @param reader the character stream of this transcoder input
     */
    public TranscoderInput(Reader reader) {
        this.reader = reader;
    }

    /**
     * Constructs a new <tt>TranscoderInput</tt> with the specified Document.
     * @param document the Document of this transcoder input
     */
    public TranscoderInput(Document document) {
        this.document = document;
    }

    /**
     * Constructs a new <tt>TranscoderInput</tt> with the specified uri.
     * @param uri the URI of this transcoder input
     */
    public TranscoderInput(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the input of this transcoder input with the specified
     * XML reader.
     * @param xmlReader the XML reader of this transcoder input
     */
    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    /**
     * Returns the XML reader of this transcoder or null if none was
     * supplied.
     */
    public XMLReader getXMLReader() {
        return xmlReader;
    }

    /**
     * Sets the input of this transcoder input with the specified
     * byte stream.
     * @param istream the byte stream of this transcoder input
     */
    public void setInputStream(InputStream istream) {
        this.istream = istream;
    }

    /**
     * Returns the input of this transcoder as a byte stream or null
     * if none was supplied.
     */
    public InputStream getInputStream() {
        return istream;
    }

    /**
     * Sets the input of this transcoder input with the specified
     * character stream.
     * @param reader the character stream of this transcoder input
     */
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * Returns the input of this transcoder as a character stream or null
     * if none was supplied.
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * Sets the input of this transcoder input with the specified
     * document.
     * @param document the document of this transcoder input
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Returns the input of this transcoder as a document or null if
     * none was supplied.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the input of this transcoder input with the specified URI.
     * @param uri the URI of this transcoder input
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the input of this transcoder as a URI or null if none
     * was supplied.
     */
    public String getURI() {
        return uri;
    }
}
