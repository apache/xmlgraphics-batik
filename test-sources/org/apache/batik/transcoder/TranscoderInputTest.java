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


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This test validates that the various configurations of TranscoderInput 
 * are supported by the XMLAbstractTranscoder class.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TranscoderInputTest extends AbstractTest {
    public TestReport runImpl() throws Exception {
        String TEST_URI = (new File("samples/anne.svg")).toURL().toString();

        TestTranscoder t = new TestTranscoder();

        TranscoderOutput out = new TranscoderOutput(new StringWriter());

        // XMLReader
        {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            TranscoderInput ti = new TranscoderInput(xmlReader);
            ti.setURI(TEST_URI);
            t.transcode(ti, out);
            assertTrue(t.passed);
        }
        
        // Input Stream
        {
            URL uri = new URL(TEST_URI);
            InputStream is = uri.openStream();
            TranscoderInput ti = new TranscoderInput(is);
            ti.setURI(TEST_URI);
            t = new TestTranscoder();
            t.transcode(ti, out);
            assertTrue(t.passed);
        }

        // Reader
        {
            URL uri = new URL(TEST_URI);
            InputStream is = uri.openStream();
            Reader r = new InputStreamReader(is);
            TranscoderInput ti = new TranscoderInput(r);
            ti.setURI(TEST_URI);
            t = new TestTranscoder();
            t.transcode(ti, out);
            assertTrue(t.passed);
        }
        // Document
        {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            Document doc = f.createDocument(TEST_URI);        
            TranscoderInput ti = new TranscoderInput(doc);
            ti.setURI(TEST_URI);
            t = new TestTranscoder();
            t.transcode(ti, out);
            assertTrue(t.passed);
        }

        // Generic Document
        {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            DOMImplementation impl = 
                GenericDOMImplementation.getDOMImplementation();
            SAXDocumentFactory f = new SAXDocumentFactory(impl, parser);
            Document doc = f.createDocument(TEST_URI);
            TranscoderInput ti = new TranscoderInput(doc);
            ti.setURI(TEST_URI);
            t = new TestTranscoder();
            t.transcode(ti, out);
            assertTrue(t.passed);
        }

        // URI only
        {
            TranscoderInput ti = new TranscoderInput(TEST_URI);
            t = new TestTranscoder();
            t.transcode(ti, out);
            assertTrue(t.passed);
        }
        
        return reportSuccess();
    }

    static class TestTranscoder extends XMLAbstractTranscoder {
        boolean passed = false;

        public TestTranscoder() {
            addTranscodingHint(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                               SVGConstants.SVG_NAMESPACE_URI);
            addTranscodingHint(KEY_DOCUMENT_ELEMENT,
                               SVGConstants.SVG_SVG_TAG);
            addTranscodingHint(KEY_DOM_IMPLEMENTATION,
                               SVGDOMImplementation.getDOMImplementation());
        }

        protected void transcode(Document document,
                                 String uri,
                                 TranscoderOutput output) {
            passed = (document != null);
        }
    }
}
