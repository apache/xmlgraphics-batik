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
 * This class tests the hasChildNodes method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ReplaceChildTest extends AbstractTest {
    public static String ERROR_GET_ELEMENT_BY_ID_FAILED 
        = "error.get.element.by.id.failed";

    public static String ENTRY_KEY_ID 
        = "entry.key.id";

    protected String testFileName;
    protected String rootTag;
    protected String targetId;

    public ReplaceChildTest(String file,
                             String root,
                             String id) {
        testFileName = file;
        rootTag = root;
        targetId = id;
    }

    public TestReport runImpl() throws Exception {
        String parser =
            XMLResourceDescriptor.getXMLParserClassName();

        DocumentFactory df 
            = new SAXDocumentFactory
            (GenericDOMImplementation.getDOMImplementation(), parser);

        File f = (new File(testFileName));
        URL url = f.toURL();
        Document doc = df.createDocument(null,
                                         rootTag,
                                         url.toString(),
                                         url.openStream());

        
        Element e = doc.getElementById(targetId);

        if (e == null){
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(ERROR_GET_ELEMENT_BY_ID_FAILED);
            report.addDescriptionEntry(ENTRY_KEY_ID,
                                       targetId);
            report.setPassed(false);
            return report;
        }

        Element fc = null;
        for (Node n = e.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == n.ELEMENT_NODE) {
                fc = (Element)n;
                break;
            }
        }
        Element ne = doc.createElementNS(null, "elt4");
        e.replaceChild(ne, fc);

        if (ne.getParentNode() != e || fc.getParentNode() != null) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(report.ERROR_TEST_FAILED);
            report.setPassed(false);
            return report;
        }
        return reportSuccess();
    }
}
