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
