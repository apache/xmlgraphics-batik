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

package org.apache.batik.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.MissingResourceException;

/**
 * This class describes the XML resources needed to use the various batik
 * modules.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class XMLResourceDescriptor {

    /**
     * The XML parser class name key.
     */
    public final static String XML_PARSER_CLASS_NAME_KEY =
        "org.xml.sax.driver";

    /**
     * The CSS parser class name key.
     */
    public final static String CSS_PARSER_CLASS_NAME_KEY =
        "org.w3c.css.sac.driver";

    /**
     * The resources file name
     */
    public final static String RESOURCES =
        "resources/XMLResourceDescriptor.properties";

    /**
     * The resource bundle
     */
    protected static Properties parserProps = null;;

    /**
     * The class name of the XML parser to use.
     */
    protected static String xmlParserClassName;

    /**
     * The class name of the CSS parser to use.
     */
    protected static String cssParserClassName;

    protected static synchronized Properties getParserProps() {
        if (parserProps != null) return parserProps;

        parserProps = new Properties();
        try { 
            Class cls = XMLResourceDescriptor.class;
            InputStream is = cls.getResourceAsStream(RESOURCES);
            parserProps.load(is);
        } catch (IOException ioe) { 
            throw new MissingResourceException(ioe.getMessage(),
                                               RESOURCES, null); 
        }
        return parserProps;
    }

    /**
     * Returns the class name of the XML parser to use.
     *
     * <p>This method first checks if any XML parser has been specified using
     * the <tt>setXMLParserClassName</tt> method. If any, this method will
     * return the value of the property 'org.xml.sax.driver' specified in the
     * <tt>resources/XMLResourceDescriptor.properties</tt> resource file.
     */
    public static String getXMLParserClassName() {
        if (xmlParserClassName == null) {
            xmlParserClassName = getParserProps().getProperty
                (XML_PARSER_CLASS_NAME_KEY);
        }
        return xmlParserClassName;
    }

    /**
     * Sets the class name of the XML parser to use.
     *
     * @param xmlParserClassName the classname of the XML parser
     */
    public static void setXMLParserClassName(String xmlParserClassName) {
        XMLResourceDescriptor.xmlParserClassName = xmlParserClassName;
    }

    /**
     * Returns the class name of the CSS parser to use.
     *
     * <p>This method first checks if any CSS parser has been
     * specified using the <tt>setCSSParserClassName</tt> method. If
     * any, this method will return the value of the property
     * 'org.w3c.css.sac.driver' specified in the
     * <tt>resources/XMLResourceDescriptor.properties</tt> resource
     * file.
     */
    public static String getCSSParserClassName() {
        if (cssParserClassName == null) {
            cssParserClassName = getParserProps().getProperty
                (CSS_PARSER_CLASS_NAME_KEY);
        }
        return cssParserClassName;
    }

    /**
     * Sets the class name of the CSS parser to use.
     *
     * @param cssParserClassName the classname of the CSS parser
     */
    public static void setCSSParserClassName(String cssParserClassName) {
        XMLResourceDescriptor.cssParserClassName = cssParserClassName;
    }
}
