/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.test.xml;

import java.io.StringWriter;
import java.io.PrintWriter;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.TestSuite;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestException;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class loads an XML document describing a test suite
 * into a <code>TestSuite</code> object.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class XMLTestSuiteLoader implements XTSConstants {

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
     * An error happened while processing a <code>Test</code>
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
     * Loads the URI as a <code>Document</code>
     */
    protected static Document loadTestSuiteDocument(String testSuiteURI)
        throws TestException{

        Document doc = null;

        try{
            DocumentBuilder docBuilder
                = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            doc = docBuilder.parse(testSuiteURI);
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
     * Builds a <code>TestSuite</code> from an input element.
     * This method assumes that element is a &lt;testSuite&gt;
     * instance, as the input document should have been
     * validated when loaded.
     */
    protected static TestSuite buildTestSuite(Element element,
                                              TestSuite parent) 
        throws TestException {
        DefaultTestSuite testSuite 
            = new DefaultTestSuite();

        /* FIXX: Not used -- should we call testSuite.setName(suiteName)??? */
        // String suiteName 
        //     = element.getAttribute(XTS_NAME_ATTRIBUTE);

        String suiteId 
            = element.getAttribute(XTS_ID_ATTRIBUTE);

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
                = element.getAttribute(XTS_ID_ATTRIBUTE);
            t.setId(id);
            return t;
        }catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(CANNOT_CREATE_TEST,
                                    new Object[] { element.getAttribute(XR_CLASS_ATTRIBUTE),
                                                   e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);
        }
    }


}
