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

package org.apache.batik.apps.svgbrowser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.util.PreferenceManager;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
     * The XML encoding used to store properties
     */
    public static final String PREFERENCE_ENCODING = "8859_1";

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
            r = new BufferedReader(new InputStreamReader(is, PREFERENCE_ENCODING));
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
            w = new BufferedWriter(new OutputStreamWriter(os, PREFERENCE_ENCODING));

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
