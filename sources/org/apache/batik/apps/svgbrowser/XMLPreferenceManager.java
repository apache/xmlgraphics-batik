/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.util.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.batik.util.XMLResourceDescriptor;

/**
 * An extension of {@link PreferenceManager} which store the preference
 * in XML.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class XMLPreferenceManager extends PreferenceManager {
    
    /**
     * The XML parser
     */
    protected String xmlParserClassName;

    /**
     * Creates a preference manager.
     * @param prefFileName the name of the preference file.
     */
    public XMLPreferenceManager(String prefFileName){
        this(prefFileName, null, 
             XMLResourceDescriptor.getXMLParserClassName());
    }

    /**
     * Creates a preference manager.
     * @param prefFileName the name of the preference file.
     * @param defaults where to get defaults value if the value is
     * not specified in the file.
     */
    public XMLPreferenceManager(String prefFileName,
                                Map defaults){
        this(prefFileName, defaults, 
             XMLResourceDescriptor.getXMLParserClassName());
    }

    /**
     * Creates a preference manager.
     * @param prefFileName the name of the preference file.
     * @param parser The XML parser class name.
     */
    public XMLPreferenceManager(String prefFileName, String parser) {
        this(prefFileName, null, parser);
    }

    /**
     * Creates a preference manager with a default values
     * initialization map.
     * @param prefFileName the name of the preference file.
     * @param defaults where to get defaults value if the value is
     * not specified in the file.
     * @param parser The XML parser class name.
     */
    public XMLPreferenceManager(String prefFileName, Map defaults, String parser) {
        super(prefFileName, defaults);
        internal = new XMLProperties();
        xmlParserClassName = parser;
    }

    /**
     * To store the preferences.
     */
    protected class XMLProperties extends Properties {

        /**
         * Reads a property list (key and element pairs) from the input stream.
         * The stream is assumed to be using the ISO 8859-1 character encoding.
         */
        public synchronized void load(InputStream is) throws IOException {
            BufferedReader r;
            r = new BufferedReader(new InputStreamReader(is, "8859_1"));
            DocumentFactory df = new SAXDocumentFactory
                (GenericDOMImplementation.getDOMImplementation(),
                 xmlParserClassName);
            Document doc = df.createDocument("http://xml.apache.org/batik/preferences",
                                             "preferences",
                                             null,
                                             r);
            Element elt = doc.getDocumentElement();
            for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equals("property")) {
                        String name = ((Element)n).getAttributeNS(null, "name");
                        
                        StringBuffer cont = new StringBuffer();
                        for (Node c = n.getFirstChild();
                             c != null;
                             c = c.getNextSibling()) {
                            if (c.getNodeType() == Node.TEXT_NODE) {
                                cont.append(c.getNodeValue());
                            } else {
                                break;
                            }
                        }
                        String val = cont.toString();
                        put(name, val);
                    }
                }
            }
        }

        /**
         * Writes this property list (key and element pairs) in this
         * <code>Properties</code> table to the output stream in a format suitable
         * for loading into a <code>Properties</code> table using the
         * <code>load</code> method.
         * The stream is written using the ISO 8859-1 character encoding.
         */
        public synchronized void store(OutputStream os, String header)
            throws IOException {
            BufferedWriter w;
            w = new BufferedWriter(new OutputStreamWriter(os, "8859_1"));

            Map m = new HashMap();
            enumerate(m);

            w.write("<preferences xmlns=\"http://xml.apache.org/batik/preferences\">\n");

            Iterator it = m.keySet().iterator();
            while (it.hasNext()) {
                String n = (String)it.next();
                String v = (String)m.get(n);
                
                w.write("<property name=\"" + n + "\">");
                w.write(DOMUtilities.contentToString(v));
                w.write("</property>\n");
            }

            w.write("</preferences>\n");
            w.flush();
        }

        /**
         * Enumerates all key/value pairs in the specified m.
         * @param m the map
         */
        private synchronized void enumerate(Map m) {
            if (defaults != null) {
                Iterator it = m.keySet().iterator();
                while (it.hasNext()) {
                    Object k = it.next();
                    m.put(k, defaults.get(k));
                }
            }
            Iterator it = keySet().iterator();
            while (it.hasNext()) {
                Object k = it.next();
                m.put(k, get(k));
            }
        }
        
    }
}
