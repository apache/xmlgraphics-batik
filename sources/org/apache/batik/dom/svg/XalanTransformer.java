/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.xalan.xslt.StylesheetRoot;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTProcessor;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTResultTarget;

import org.w3c.dom.Node;

/**
 * This class transform stylesheets with xalan.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XalanTransformer {
    /**
     * Returns the document resulting of the XSL stylesheets
     * transformations.
     */
    public static Reader transform(Reader r, List sl) {
        try {
            XSLTProcessor p = XSLTProcessorFactory.getProcessor();

            Iterator it = sl.iterator();
            while (it.hasNext()) {
                String uri = (String)it.next();
                Node n = (Node)it.next();

                XSLTInputSource is = new XSLTInputSource(n);
                is.setSystemId(uri);
                StylesheetRoot ss = p.processStylesheet(is);

                StringWriter w = new StringWriter();

                ss.process(new XSLTInputSource(r), new XSLTResultTarget(w));
                r = new StringReader(w.getBuffer().toString());
            }

            /*
            Processor p = Processor.newInstance("xslt");

            Iterator it = sl.iterator();
            String uri = (String)it.next();
            Node n = (Node)it.next();

            Templates t = p.processFromNode(n, uri);
            Transformer tr = t.newTransformer();
            tr.setParent(XMLReaderFactory.createXMLReader
                         ("org.apache.crimson.parser.XMLReaderImpl"));
            
            while (it.hasNext()) {
                uri = (String)it.next();
                n = (Node)it.next();

                t = p.processFromNode(n);
                Transformer tmp = tr;
                tr = t.newTransformer();
                tr.setParent(tmp);
            }

            StringWriter w = new StringWriter();

            tr.transform(new StreamSource(r), new StreamResult(w));

            return new StringReader(w.getBuffer().toString());
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }    
}
