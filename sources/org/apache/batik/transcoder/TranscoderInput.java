/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
