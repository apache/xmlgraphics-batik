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

package org.apache.batik.dom.svg;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
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
     * Key used for public identifiers
     */
    public static final String KEY_PUBLIC_IDS = "publicIds";

    /**
     * Key used for public identifiers
     */
    public static final String KEY_SKIPPABLE_PUBLIC_IDS = "skippablePublicIds";

    /**
     * Key used for the skippable DTD substitution
     */
    public static final String KEY_SKIP_DTD = "skipDTD";

    /**
     * Key used for system identifiers
     */
    public static final String KEY_SYSTEM_ID = "systemId.";

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
     * The DTD public IDs we know we can skip.
     */
    protected static String skippable_dtdids;

    /**
     * The DTD content to use when skipping
     */
    protected static String skip_dtd;

    /**
     * The ResourceBunder for the public and system ids
     */
    protected static ResourceBundle rb;

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

    public SVGDocument createSVGDocument(String uri) throws IOException {
        return (SVGDocument)createDocument(uri);
    }

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param inp The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGDocument createSVGDocument(String uri, InputStream inp) 
        throws IOException {
        return (SVGDocument)createDocument(uri, inp);
    }

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public SVGDocument createSVGDocument(String uri, Reader r)
        throws IOException {
        return (SVGDocument)createDocument(uri, r);
    }

    /**
     * Creates a SVG Document instance.<br>
     * This method supports gzipped sources.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri) throws IOException {
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

        Document doc = super.createDocument
            (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, isrc);
        try {
            ((SVGOMDocument)doc).setURLObject(new URL(purl.toString()));
        } catch (MalformedURLException mue) {
            // Not very likely to happen given we already opened the stream.
            throw new IOException("Malformed URL: " + uri);
        }

        return doc;
    }

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param inp The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri, InputStream inp)
        throws IOException {
        Document doc;
        InputSource is = new InputSource(inp);
        is.setSystemId(uri);

        try {
            doc = super.createDocument
                (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)doc).setURLObject(new URL(uri));
            }
        } catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
        return doc;
    }

    /**
     * Creates a SVG Document instance.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri, Reader r)
        throws IOException {
        Document doc;
        InputSource is = new InputSource(r);
        is.setSystemId(uri);

        try {
            doc = super.createDocument
                (SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", uri, is);
            if (uri != null) {
                ((SVGOMDocument)doc).setURLObject(new URL(uri));
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
	namespaces.put("xlink", XLinkSupport.XLINK_NAMESPACE_URI);
    }

    /**
     * <b>SAX2</b>: Implements {@link
     * org.xml.sax.EntityResolver#resolveEntity(String,String)}.
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
        try {
            if (rb == null)
                rb = ResourceBundle.getBundle(DTDIDS, Locale.getDefault());

            if (dtdids == null)
                dtdids = rb.getString(KEY_PUBLIC_IDS);

            if (skippable_dtdids == null)
                skippable_dtdids = rb.getString(KEY_SKIPPABLE_PUBLIC_IDS);
            if (skip_dtd == null)
                skip_dtd = rb.getString(KEY_SKIP_DTD);

            if (publicId != null){
                if (!isValidating && 
                    (skippable_dtdids.indexOf(publicId) != -1)) {
                    // We are not validating and this is a DTD we can
                    // safely skip so do it...  Here we provide just enough
                    // of the DTD to keep stuff running (set svg and
                    // xlink namespaces).
                    return new InputSource(new StringReader(skip_dtd));
                }

                if (dtdids.indexOf(publicId) != -1) {
                    String localSystemId = 
                        rb.getString(KEY_SYSTEM_ID + 
                                     publicId.replace(' ', '_'));

                    if (localSystemId != null && !"".equals(localSystemId)){
                        return new InputSource
                            (getClass().getResource(localSystemId).toString());
                    }
                }
            }
        } catch (MissingResourceException e) {
            throw new SAXException(e);
        }
        // Let the SAX parser find the entity.
        return null;
    }
}
