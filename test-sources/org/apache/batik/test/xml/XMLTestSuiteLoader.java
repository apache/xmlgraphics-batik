/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;

import java.net.URL;
import java.net.MalformedURLException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Vector;

import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.TestSuite;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestException;
import org.apache.batik.test.TestReportProcessor;

import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.xml.sax.InputSource;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class loads an XML document describing a test suite
 * into a <tt>TestSuite</tt> object.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class XMLTestSuiteLoader implements XTSConstants {
    /**
     * Configuration parameter. Use same parser as XMLTestSuiteRunner
     */
    public static final String XML_PARSER = 
        "XMLTestSuiteRunner.config.xml.parser";

    /**
     * An error happened while loading a test suite document.
     * {0} : the &lt;testSuite&gt; href value.
     * {1} : the exception's class name
     * {2} : exception's message
     * {3} : exception's stack trace
     */
    public static final String TEST_SUITE_LOADING_EXCEPTION
        = "xml.XMLTestSuiteLoader.error.test.suite.loading.exception";

    /**
     * An error happened while processing a <tt>Test</tt>
     * description.
     * {0} : the <test> "className" attribute value
     * {1} : exception's class name
     * {2} : exception's message
     * {3} : exception's stack trace
     */
    public static final String CANNOT_CREATE_TEST
        = "xml.XMLTestSuiteLoader.error.cannot.create.test";

    /**
     * Load the test suite defined by the input URI
     */
    public static TestSuite loadTestSuite(String testSuiteURI, 
                                          TestSuite parent) 
        throws TestException{
        // System.out.println("loading test suite: " + testSuiteURI);
        Document testSuiteDocument = loadTestSuiteDocument(testSuiteURI);
        return buildTestSuite(testSuiteDocument.getDocumentElement(), parent);
    }

    /**
     * Loads the URI as a <tt>Document</tt>
     */
    protected static Document loadTestSuiteDocument(String testSuiteURI)
        throws TestException{
        DocumentFactory df 
            = new SAXDocumentFactory(SVGDOMImplementation.getDOMImplementation(), 
                                     Messages.formatMessage(XML_PARSER, null));

        Document doc = null;

        try{
            URL url = new URL(testSuiteURI);
            doc = df.createDocument(null,
                                    XTS_TEST_SUITE_TAG,
                                    url.toString(),
                                    url.openStream());
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(TEST_SUITE_LOADING_EXCEPTION,
                                    new Object[] { testSuiteURI,
                                                   e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);            

        }

        return doc;
    }

    /**
     * Builds a <tt>TestSuite</tt> from an input element.
     * This method assumes that element is a &lt;testSuite&gt;
     * instance, as the input document should have been
     * validated when loaded.
     */
    protected static TestSuite buildTestSuite(Element element,
                                              TestSuite parent) 
        throws TestException {
        DefaultTestSuite testSuite 
            = new DefaultTestSuite();

        String suiteName 
            = element.getAttributeNS(null,
                                     XTS_NAME_ATTRIBUTE);

        String suiteId 
            = element.getAttributeNS(null,
                                     XTS_ID_ATTRIBUTE);

        testSuite.setId(suiteId);

        NodeList children = element.getChildNodes();
        if(children != null && children.getLength() > 0){
            int n = children.getLength();
            for(int i=0; i<n; i++){
                Node child = children.item(i);
                if(child.getNodeType() == Node.ELEMENT_NODE){
                    Element childElement = (Element)child;
                    String tagName = childElement.getTagName().intern();
                    // System.out.println("Processing child : " + tagName);
                    if(tagName == XTS_TEST_TAG){
                        Test t = buildTest(childElement);
                        testSuite.addTest(t);
                    }
                    else if(tagName == XTS_TEST_GROUP_TAG){
                        Test t = buildTestSuite(childElement, testSuite);
                        testSuite.addTest(t);
                    }
                }
            }
        }

        return testSuite;
    }

    protected static Test buildTest(Element element) throws TestException {
        try{
            Test t = (Test)XMLReflect.buildObject(element);

            String id 
                = element.getAttributeNS(null,
                                         XTS_ID_ATTRIBUTE);
            t.setId(id);
            return t;
        }catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(CANNOT_CREATE_TEST,
                                    new Object[] { element.getAttributeNS(null, XR_CLASS_ATTRIBUTE),
                                                   e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);
        }
    }


}
