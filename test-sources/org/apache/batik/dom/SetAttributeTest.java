/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.*;

import java.io.*;
import java.net.*;
import org.apache.batik.dom.util.*;
import org.apache.batik.util.*;

import org.apache.batik.test.*;

/**
 * @author <a href="mailto:shillion@ilog.fr">Stephane Hillion</a>
 * @version $Id$
 */
public class SetAttributeTest extends AbstractTest {
    protected String testFileName;
    protected String rootTag;
    protected String targetId;
    protected String targetAttribute;
    protected String targetValue;

    protected String parserClassName = XMLResourceDescriptor.getXMLParserClassName();

    public static String ERROR_GET_ELEMENT_BY_ID_FAILED 
        = "error.get.element.by.id.failed";

    public static String ENTRY_KEY_ID 
        = "entry.key.id";

    public SetAttributeTest(String testFileName,
                            String rootTag,
                            String targetId,
                            String targetAttribute,
                            String targetValue){
        this.testFileName = testFileName;
        this.rootTag = rootTag;
        this.targetId = targetId;
        this.targetAttribute = targetAttribute;
        this.targetValue = targetValue;
    }

    public String getParserClassName(){
        return parserClassName;
    }

    public void setParserClassName(String parserClassName){
        this.parserClassName = parserClassName;
    }

    public TestReport runImpl() throws Exception {
        DocumentFactory df 
            = new SAXDocumentFactory(GenericDOMImplementation.getDOMImplementation(), 
                                     parserClassName);

        File f = (new File(testFileName));
        URL url = f.toURL();
        Document doc = df.createDocument(null,
                                         rootTag,
                                         url.toString(),
                                         url.openStream());

        
        Element e = doc.getElementById(targetId);

        if(e == null){
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(ERROR_GET_ELEMENT_BY_ID_FAILED);
            report.addDescriptionEntry(ENTRY_KEY_ID,
                                       targetId);
            report.setPassed(false);
            return report;
        }
            
            
        e.setAttribute(targetAttribute, targetValue);
        if(targetValue.equals(e.getAttribute(targetAttribute))){
            return reportSuccess();
        }
        DefaultTestReport report = new DefaultTestReport(this);
        report.setErrorCode(report.ERROR_TEST_FAILED);
        report.setPassed(false);
        return report;
    }
}

