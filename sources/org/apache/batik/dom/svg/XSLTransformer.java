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

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.apache.batik.util.SVGConstants;

import org.apache.trax.Processor;
import org.apache.trax.Result;
import org.apache.trax.Templates;
import org.apache.trax.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class manages the XSL stylesheets transformations on a
 * SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XSLTransformer {
    /**
     * The XSL namespace URI.
     */
    public final static String XSL_NAMESPACE_URI =
        "http://www.w3.org/1999/XSL/Transform";

    /**
     * Returns the document resulting of the XSL stylesheets
     * transformations.
     */
    public static Reader transform(Reader r, List sl) {
        try {
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

            Result res = new Result(w);
            tr.transform(new InputSource(r), res);

            return new StringReader(w.getBuffer().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * Returns a list which contains the URIs and &lt;xsl:stylesheet&gt;
     * elements referenced in the given element.
     */
    public static List getStyleSheets(Node node, String uri)
        throws Exception {
        List result = new LinkedList();

        for (Node n = node; n != null; n = n.getNextSibling()) {
            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                if (XSL_NAMESPACE_URI.equals(n.getNamespaceURI()) &&
                    n.getLocalName().equals("stylesheet")) {
                    Document d = GenericDOMImplementation.
                        getDOMImplementation().
                        createDocument(XSL_NAMESPACE_URI,
                                       "stylesheet", null);
                    d.removeChild(d.getDocumentElement());
                    Node nd = d.importNode(n, true);
                    d.appendChild(nd);
                    
                    result.add(uri);
                    result.add(d);
                } else {
                    result.addAll(getStyleSheets(n.getFirstChild(), uri));
                }
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                if (n.getNodeName().equals("xml-stylesheet")) {
                    HashTable attrs = new HashTable();
                    attrs.put("alternate", "no");
                    attrs.put("media", "all");
                    DOMUtilities.parseStyleSheetPIData(n.getNodeValue(), attrs);
                    

                    String type = (String)attrs.get("type");
                    
                    if ("text/xsl".equals(type)) {
                        DocumentFactory df =
                            new DocumentFactory(GenericDOMImplementation.
                                                getDOMImplementation(),
                                   "org.apache.crimson.parser.XMLReaderImpl");
                        String href = (String)attrs.get("href");
                        URL url = new URL(new URL(uri), href);
                        InputSource is = new InputSource(url.toString());
                        Document doc = df.createDocument(XSL_NAMESPACE_URI,
                                                         "stylesheet",
                                                         url.toString(),
                                                         is);
                        result.add(href);
                        result.add(doc);
                    }
                }
            }
        }

        return result;
    }
}
