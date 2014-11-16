/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.batik.transcoder;


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;

import org.xml.sax.XMLReader;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test validates that the various configurations of TranscoderInput 
 * are supported by the XMLAbstractTranscoder class.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
@Ignore
public class TranscoderInputTestCase extends AbstractTest {
    public TestReport runImpl() throws Exception {
        String TEST_URI = (new File("samples/anne.svg")).toURL().toString();

        TestTranscoder t = new TestTranscoder();

        TranscoderOutput out = new TranscoderOutput(new StringWriter());

        // XMLReader
        {
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            saxFactory.setValidating(false);
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            //XMLReader xmlReader = XMLReaderFactory.createXMLReader();
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
