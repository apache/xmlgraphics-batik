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
import org.apache.batik.dom.*;
import org.apache.batik.dom.util.*;
import org.apache.batik.util.*;

import org.apache.batik.test.*;

/**
 * This class tests the non-deep cloneNode method for elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CloneElementTest extends AbstractTest {
    protected String testFileName;
    protected String rootTag;
    protected String targetId;

    public CloneElementTest(String file, String root, String id) {
        testFileName = file;
        rootTag = root;
        targetId = id;
    }
    
    public TestReport runImpl() throws Exception {
        String parser =
            XMLResourceDescriptor.getXMLParserClassName();

        SAXDocumentFactory df =
            new SAXDocumentFactory
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
            report.setErrorCode("error.get.element.by.id.failed");
            report.addDescriptionEntry("entry.key.id", targetId);
            report.setPassed(false);
            return report;
        }

        Element celt = (Element)e.cloneNode(false);

        NamedNodeMap attrs = e.getAttributes();

        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            String ns = attr.getNamespaceURI();
            String name = (ns == null)
                ? attr.getNodeName()
                : attr.getLocalName();
            String val = attr.getNodeValue();
            String val2 = celt.getAttributeNS(ns, name);
            if (!val.equals(val2)) {
                DefaultTestReport report = new DefaultTestReport(this);
                report.setErrorCode("error.attr.comparison.failed");
                report.addDescriptionEntry("entry.attr.name", name);
                report.addDescriptionEntry("entry.attr.value1", val);
                report.addDescriptionEntry("entry.attr.value2", val2);
                report.setPassed(false);
                return report;
            }
        }

        return reportSuccess();
    }
}
