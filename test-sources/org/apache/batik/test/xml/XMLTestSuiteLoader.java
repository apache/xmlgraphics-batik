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
 * into a <tt>TestSuite</tt> object.
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
