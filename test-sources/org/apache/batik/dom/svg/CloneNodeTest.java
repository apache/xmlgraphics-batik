/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.*;

import java.io.*;
import java.net.*;
import org.apache.batik.dom.svg.*;
import org.apache.batik.dom.util.*;
import org.apache.batik.util.*;

import org.apache.batik.test.*;

/**
 * This class tests the cloneNode method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CloneNodeTest extends AbstractTest {
    protected String testFileName;
    protected String targetId;

    public CloneNodeTest(String file, String id) {
        testFileName = file;
        targetId = id;
    }
    
    public TestReport runImpl() throws Exception {
        String parser =
            XMLResourceDescriptor.getXMLParserClassName();

        SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(parser);

        File f = (new File(testFileName));
        URL url = f.toURL();
        Document doc = df.createDocument(url.toString(),
                                         url.openStream());
        
        Element e = doc.getElementById(targetId);

        if (e == null){
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("error.get.element.by.id.failed");
            report.addDescriptionEntry("entry.key.id", targetId);
            report.setPassed(false);
            return report;
        }

        Element celt = (Element)e.cloneNode(true);

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
                report.setPassed(false);
                return report;
            }
        }

        return reportSuccess();
    }
}
