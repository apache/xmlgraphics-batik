/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;


import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.ExtensibleSVGDOMImplementation;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;

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
            addTranscodingHint(KEY_DOM_IMPLEMENTATION,
                               ExtensibleSVGDOMImplementation.getDOMImplementation());
            addTranscodingHint(KEY_DOCUMENT_ELEMENT,
                               SVGConstants.SVG_SVG_TAG);
            addTranscodingHint(KEY_DOM_IMPLEMENTATION,
                               ExtensibleSVGDOMImplementation.getDOMImplementation());
        }

        protected void transcode(Document document,
                                 String uri,
                                 TranscoderOutput output) {
            passed = (document != null);
        }
    }
}
