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

import java.io.IOException;

import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.DOMImplementationKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This class may be the base class of all transcoders which take an
 * XML document as input and which need to build a DOM tree. In order
 * to take advantage of this class, you have to specify the following
 * transcoding hints:
 *
 * <ul>
 * <li><tt>KEY_DOM_IMPLEMENTATION</tt>: the DOM Implementation to use
 *
 * <li><tt>KEY_DOCUMENT_ELEMENT_NAMESPACE_URI</tt>: the namespace URI of the
 * document to create
 *
 * <li><tt>KEY_DOCUMENT_ELEMENT</tt>: the qualified name of the document type
 * to create
 * </ul>
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class XMLAbstractTranscoder extends AbstractTranscoder {

    /**
     * Constructs a new <tt>XMLAbstractTranscoder</tt>.
     */
    protected XMLAbstractTranscoder() {
	hints.put(KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
    }

    /**
     * Transcodes the specified XML input in the specified output. All
     * <tt>TranscoderException</tt> exceptions not catched previously
     * are tagged as fatal errors (ie. call the <tt>fatalError</tt>
     * method of the <tt>ErrorHandler</tt>).
     *
     * @param input the XML input to transcode
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    public void transcode(TranscoderInput input, TranscoderOutput output)
            throws TranscoderException {

        Document document = null;
        String uri = input.getURI();
        if (input.getDocument() != null) {
            document = input.getDocument();
        } else {
            String parserClassname =
                (String)hints.get(KEY_XML_PARSER_CLASSNAME);
            String namespaceURI =
                (String)hints.get(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI);
            String documentElement =
                (String)hints.get(KEY_DOCUMENT_ELEMENT);
            DOMImplementation domImpl =
                (DOMImplementation)hints.get(KEY_DOM_IMPLEMENTATION);
	    
            if (parserClassname == null) {
                parserClassname = XMLResourceDescriptor.getXMLParserClassName();
            }
            if (domImpl == null) {
                handler.fatalError(new TranscoderException(
                    "Unspecified transcoding hints: KEY_DOM_IMPLEMENTATION"));
                return;
            }
            if (namespaceURI == null) {
                handler.fatalError(new TranscoderException(
                "Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT_NAMESPACE_URI"));
                return;
            }
            if (documentElement == null) {
                handler.fatalError(new TranscoderException(
                    "Unspecified transcoding hints: KEY_DOCUMENT_ELEMENT"));
                return;
            }
            // parse the XML document
            DocumentFactory f = createDocumentFactory(domImpl, parserClassname);
	    boolean b =
		((Boolean)hints.get(KEY_XML_PARSER_VALIDATING)).booleanValue();
	    f.setValidating(b);
            try {
                if (input.getInputStream() != null) {
                    document = f.createDocument(namespaceURI,
                                                documentElement,
                                                input.getURI(),
                                                input.getInputStream());
                } else if (input.getReader() != null) {
                    document = f.createDocument(namespaceURI,
                                                documentElement,
                                                input.getURI(),
                                                input.getReader());
                } else if (input.getXMLReader() != null) {
                    document = f.createDocument(namespaceURI,
                                                documentElement,
                                                input.getURI(),
                                                input.getXMLReader());
                } else if (uri != null) {
                    document = f.createDocument(namespaceURI,
                                                documentElement,
                                                uri);
                } 
            } catch (DOMException ex) {
                handler.fatalError(new TranscoderException(ex));
            } catch (IOException ex) {
                ex.printStackTrace();
                handler.fatalError(new TranscoderException(ex));
            }
        }
        // call the dedicated transcode method
        if (document != null) {
            try {
                transcode(document, uri, output);
            } catch(TranscoderException ex) {
                // at this time, all TranscoderExceptions are fatal errors
                handler.fatalError(ex);
                return;
            }
        }
    }

    /**
     * Creates the <tt>DocumentFactory</tt> used to create the DOM
     * tree. Override this method if you have to use another
     * implementation of the <tt>DocumentFactory</tt> (ie. for SVG,
     * you have to use the <tt>SAXSVGDocumentFactory</tt>).
     *
     * @param domImpl the DOM Implementation to use
     * @param parserClassname the XML parser classname
     */
    protected DocumentFactory createDocumentFactory(DOMImplementation domImpl,
                                                    String parserClassname) {
	return new SAXDocumentFactory(domImpl, parserClassname);
    }

    /**
     * Transcodes the specified Document in the specified output.
     *
     * @param document the document to transcode
     * @param uri the uri of the document or null if any
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    protected abstract void transcode(Document document,
                                      String uri,
                                      TranscoderOutput output)
            throws TranscoderException;

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * XML parser classname key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_XML_PARSER_CLASSNAME</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">Yes</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the XML parser classname to use.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_XML_PARSER_CLASSNAME
        = new StringKey();

    /**
     * The validation mode of the XML parser.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_XML_PARSER_VALIDATING</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">false</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the validation mode of the XML parser.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_XML_PARSER_VALIDATING
        = new BooleanKey();

    /**
     * Document element key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_DOCUMENT_ELEMENT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">Yes</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the qualified name of the document
     * type to be created.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT
        = new StringKey();

    /**
     * Document element namespace URI key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_DOCUMENT_ELEMENT_NAMESPACE_URI</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">Yes</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     *
     * <TD VALIGN="TOP">Specify the namespace URI of the document
     * element.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_DOCUMENT_ELEMENT_NAMESPACE_URI
        = new StringKey();

    /**
     * DOM Implementation key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_DOM_IMPLEMENTATION</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">Yes</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     *
     * <TD VALIGN="TOP">Specify the DOM Implementation to use.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_DOM_IMPLEMENTATION
        = new DOMImplementationKey();
}


