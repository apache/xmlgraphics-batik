/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Document;
import org.xml.sax.XMLFilter;

/**
 * This class represents a single output for a <tt>Transcoder</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscoderOutput {

    /**
     * The optional XML filter where to send SAX events.
     */
    protected XMLFilter xmlFilter;

    /**
     * The optional output has a byte stream.
     */
    protected OutputStream ostream;

    /**
     * The optional output as a character stream.
     */
    protected Writer writer;

    /**
     * The optional output as XML Document.
     */
    protected Document document;

    /**
     * The optional output as a URI.
     */
    protected String uri;

    /**
     * Constructs a new empty <tt>TranscoderOutput</tt>.
     */
    public TranscoderOutput() {
    }

    /**
     * Constructs a new <tt>TranscoderOutput</tt> with the specified
     * XML filter.
     * @param xmlFilter the XML filter of this transcoder output
     */
    public TranscoderOutput(XMLFilter xmlFilter) {
        this.xmlFilter = xmlFilter;
    }

    /**
     * Constructs a new <tt>TranscoderOutput</tt> with the specified
     * byte stream output.
     * @param ostream the byte stream of this transcoder output
     */
    public TranscoderOutput(OutputStream ostream) {
        this.ostream = ostream;
    }

    /**
     * Constructs a new <tt>TranscoderOutput</tt> with the specified
     * character stream.
     * @param writer the character stream of this transcoder output
     */
    public TranscoderOutput(Writer writer) {
        this.writer = writer;
    }

    /**
     * Constructs a new <tt>TranscoderOutput</tt> with the specified Document.
     * @param document the Document of this transcoder output
     */
    public TranscoderOutput(Document document) {
        this.document = document;
    }

    /**
     * Constructs a new <tt>TranscoderOutput</tt> with the specified uri.
     * @param uri the URI of this transcoder output
     */
    public TranscoderOutput(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the output of this transcoder output with the specified
     * XML filter.
     * @param xmlFilter the XML filter of this transcoder output
     */
    public void setXMLFilter(XMLFilter xmlFilter) {
        this.xmlFilter = xmlFilter;
    }

    /**
     * Returns the output of this transcoder as a XML filter or null
     * if none was supplied.
     */
    public XMLFilter getXMLFilter() {
        return xmlFilter;
    }


    /**
     * Sets the output of this transcoder output with the specified
     * byte stream.
     * @param ostream the byte stream of this transcoder output
     */
    public void setOutputStream(OutputStream ostream) {
        this.ostream = ostream;
    }

    /**
     * Returns the output of this transcoder as a byte stream or null
     * if none was supplied.
     */
    public OutputStream getOutputStream() {
        return ostream;
    }

    /**
     * Sets the output of this transcoder output with the specified
     * character stream.
     * @param writer the character stream of this transcoder output
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Returns the output of this transcoder as a character stream or null
     * if none was supplied.
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * Sets the output of this transcoder output with the specified
     * document.
     * @param document the document of this transcoder output
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Returns the output of this transcoder as a document or null if
     * none was supplied.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the output of this transcoder output with the specified URI.
     * @param uri the URI of this transcoder output
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the output of this transcoder as a URI or null if none
     * was supplied.
     */
    public String getURI() {
        return uri;
    }
}
