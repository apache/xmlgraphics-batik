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

package org.apache.batik.bridge;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;

import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.XMLResourceDescriptor;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.apache.batik.test.*;
import org.apache.batik.util.ApplicationSecurityEnforcer;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.test.svg.SelfContainedSVGOnLoadTest;

/**
 * This test validates that SecurityExceptions are generated when 
 * the user is trying the access external resources and the UserAgent
 * disallows that.
 *
 * In the following, 'unsecure' means an external resource coming from
 * a different location than the file referencing it.
 *
 * This test works with an SVG file containing an unsecure stylesheet
 * and a set of unsecure elements of all kinds, such as &lt;image&gt;
 * &lt;use&gt; or &lt;feImage&gt;. All these elements are defined
 * in a defs section. The test tries to load the document and validates
 * that a SecurityException is thrown (because of the unsecure 
 * stylesheet). Then, the test iterates over the various unsecure
 * elements, inserting them into the document outside the defs
 * section, which should result in a SecurityException in each case.
 * 
 * There is a property (secure) to have the test work the opposite
 * way and check that no SecurityException is thrown if access
 * to external resources is allowed.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

public class ExternalResourcesTest extends AbstractTest {
    /**
     * Error when the input file cannot be loaded into a
     * Document object
     * {0} = IOException message
     */
    public static final String ERROR_CANNOT_LOAD_SVG_DOCUMENT
        = "ExternalResourcesTest.error.cannot.load.svg.document";

    /**
     * Error while processing the document
     * {0} = BridgeException message
     */
    public static final String ERROR_WHILE_PROCESSING_SVG_DOCUMENT
        = "ExternalResourcesTest.error.while.processing.svg.document";

    /**
     * Error: an expected exception was not thrown
     * {0} = List of ids for which the exception was not thrown
     */
    public static final String ERROR_UNTHROWN_SECURITY_EXCEPTIONS
        = "ExternalResourcesTest.error.unthrown.security.exceptions";

    /**
     * Error: an unexpected exception was thrown
     * {0} = List of ids for which an exception was thrown
     */
    public static final String ERROR_THROWN_SECURITY_EXCEPTIONS
        = "ExternalResourcesTest.error.thrown.security.exceptions";

    /**
     * Error when the insertion point cannot be found in the 
     * test document
     * {0} = insertion point id
     */
    public static final String ERROR_NO_INSERTION_POINT_IN_DOCUMENT
        = "ExternalResourceTest.error.no.insertion.point.in.document";

    /**
     * Error when the test could not find a list of ids for testing
     */
    public static final String ERROR_NO_ID_LIST
        = "ExternalResourceTest.error.no.id.list";

    /**
     * Error when one of the target id cannot be found
     * {0} = id which was not found
     */
    public static final String ERROR_TARGET_ID_NOT_FOUND
        = "ExternalResourcesTest.error.target.id.not.found";

    /**
     * Entry describing the error
     */
    public static final String ENTRY_KEY_ERROR_DESCRIPTION 
        = "ExternalResourcesTest.entry.key.error.description";

    public static final String ENTRY_KEY_INSERTION_POINT_ID
        = "ExternalResourcesTest.entry.key.insertion.point.id";

    public static final String ENTRY_KEY_TARGET_ID
        = "ExternalResourcesTest.entry.target.id";

    public static final String ENTRY_KEY_EXPECTED_EXCEPTION_ON
        = "ExternalResourcesTest.entry.key.expected.exception.on";

    public static final String ENTRY_KEY_UNEXPECTED_EXCEPTION_ON
        = "ExternalResourcesTest.entry.key.unexpected.exception.on";

    /**
     * Pseudo id for the external stylesheet test
     */
    public static final String EXTERNAL_STYLESHEET_ID 
        = "external-stylesheet";

    /**
     * Test Namespace
     */
    public static final String testNS = "http://xml.apache.org/batik/test";

    /**
     * Id of the element where unsecure content is inserted
     */
    public static final String INSERTION_POINT_ID = "insertionPoint";

    /**
     * Controls whether the test works in secure mode or not
     */
    protected boolean secure = true;

    String svgURL;

    public void setId(String id){
        super.setId(id);
        svgURL = resolveURL("test-resources/org/apache/batik/bridge/" + id + ".svg");
    }

    public Boolean getSecure(){
        return new Boolean(secure);
    }

    public void setSecure(Boolean secure) {
        this.secure = secure.booleanValue();
    }

    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    protected String resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if(f.getParentFile().exists()){
            try{
                return f.toURL().toString();
            }catch(MalformedURLException e){
                throw new IllegalArgumentException();
            }
        }
        
        // url is not a file. It must be a regular URL...
        try{
            return (new URL(url)).toString();
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(url);
        }
    }

    /**
     * This test uses a list of ids found in the test document. These ids reference
     * elements found in a defs section. For each such element, the test will 
     * attempt to insert the target id at a given insertion point. That insertion
     * should cause a SecurityException. If so, the test passes. Otherwise, the test
     * will fail
     */
    public TestReport runImpl() throws Exception{
        DefaultTestReport report 
            = new DefaultTestReport(this);

        //
        // First step:
        //
        // Load the input SVG into a Document object
        //
        String parserClassName = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parserClassName);
        Document doc = null;

        try {
            doc = f.createDocument(svgURL);
        } catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_LOAD_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        } catch(Exception e){
            report.setErrorCode(ERROR_CANNOT_LOAD_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        }

        Vector failures = new Vector();

        //
        // Do an initial processing to validate that the external 
        // stylesheet causes a SecurityException
        //
        UserAgent userAgent = buildUserAgent();
        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(userAgent);
        ctx.setDynamic(true);

        // We expect either a SecurityException or a BridgeException 
        // with ERR_URI_UNSECURE.
        try {
            builder.build(ctx, doc);
            if (secure) {
                failures.addElement(EXTERNAL_STYLESHEET_ID);
            }
        } catch (BridgeException e){
            if (!secure 
                ||
                (secure && !ErrorConstants.ERR_URI_UNSECURE.equals(e.getCode()))) {
                report.setErrorCode(ERROR_WHILE_PROCESSING_SVG_DOCUMENT);
                report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                           e.getMessage());
                report.setPassed(false);
                return report;
            } 
            
        } catch (SecurityException e) {
            if (!secure) {
                failures.addElement(EXTERNAL_STYLESHEET_ID);
            }
        }
        
        //
        // Remove the stylesheet from the document
        //
        Node child = doc.getFirstChild();
        Node next = null;
        while (child != null) {
            next = child.getNextSibling();
            if (child.getNodeType() == child.PROCESSING_INSTRUCTION_NODE) {
                doc.removeChild(child);
            }
            child = next;
        }

        //
        // Now, get the list of ids to be checked
        //
        Element root = doc.getDocumentElement();
        String idList = root.getAttributeNS(testNS, "targetids");
        if (idList == null || "".equals(idList)) {
            report.setErrorCode(ERROR_NO_ID_LIST);
            report.setPassed(false);
            return report;
        }

        StringTokenizer st = new StringTokenizer(idList, ",");
        String[] ids = new String[st.countTokens()];
        for (int i=0; i<ids.length; i++) {
            ids[i] = st.nextToken().toString().trim();
        }

        for (int i=0; i<ids.length; i++) {
            String id = ids[i];
            userAgent = buildUserAgent();
            builder = new GVTBuilder();
            ctx = new BridgeContext(userAgent);
            ctx.setDynamic(true);

            Document cloneDoc = (Document)doc.cloneNode(true);
            Element insertionPoint = cloneDoc.getElementById(INSERTION_POINT_ID);
            
            if (insertionPoint == null) {
                report.setErrorCode(ERROR_NO_INSERTION_POINT_IN_DOCUMENT);
                report.addDescriptionEntry(ENTRY_KEY_INSERTION_POINT_ID, 
                                           INSERTION_POINT_ID);
                report.setPassed(false);
                return report;
            }

            Element target = cloneDoc.getElementById(id);

            if (target == null) {
                report.setErrorCode(ERROR_TARGET_ID_NOT_FOUND);
                report.addDescriptionEntry(ENTRY_KEY_TARGET_ID,
                                           id);
                report.setPassed(false);
                return report;
            }

            insertionPoint.appendChild(target);

            try {
                builder.build(ctx, cloneDoc);
                if (secure) {
                    // If we get here, it means that no SecurityException
                    // was thrown, which is wrong.
                    failures.addElement(id);
                }
            } catch (BridgeException e){
                if (!secure
                    ||
                    (secure && !ErrorConstants.ERR_URI_UNSECURE.equals(e.getCode()))) {
                    report.setErrorCode(ERROR_WHILE_PROCESSING_SVG_DOCUMENT);
                    report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                               e.getMessage());
                    report.setPassed(false);
                    return report;
                }
            } catch (SecurityException e) {
                if (!secure) {
                    failures.addElement(id);
                }
            }
        }

        if (failures.size() == 0) {
            return reportSuccess();
        }

        if (secure) {
            report.setErrorCode(ERROR_UNTHROWN_SECURITY_EXCEPTIONS);
            for (int i=0; i<failures.size(); i++) {
                report.addDescriptionEntry(ENTRY_KEY_EXPECTED_EXCEPTION_ON,
                                           failures.elementAt(i));
            }
        } else {
            report.setErrorCode(ERROR_THROWN_SECURITY_EXCEPTIONS);
            for (int i=0; i<failures.size(); i++) {
                report.addDescriptionEntry(ENTRY_KEY_UNEXPECTED_EXCEPTION_ON,
                                           failures.elementAt(i));
            }
        }

        report.setPassed(false);
        return report;
    }

    protected UserAgent buildUserAgent(){
        if (secure) {
            return new SecureUserAgent();
        } else {
            return new RelaxedUserAgent();
        }
    }
    
    class SecureUserAgent extends UserAgentAdapter {
        public ExternalResourceSecurity 
            getExternalResourceSecurity(ParsedURL resourcePURL,
                                        ParsedURL docPURL){
            return new NoLoadExternalResourceSecurity();
            
        }

    }

    class RelaxedUserAgent extends UserAgentAdapter {
        public ExternalResourceSecurity 
            getExternalResourceSecurity(ParsedURL resourcePURL,
                                        ParsedURL docPURL){
            return new RelaxedExternalResourceSecurity(resourcePURL,
                                                       docPURL);
            
        }

    }

}
