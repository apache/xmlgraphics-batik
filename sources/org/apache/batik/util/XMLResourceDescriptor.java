/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.util.Locale;
import java.util.ResourceBundle;

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
        "org.apache.batik.util.resources.XMLResourceDescriptor";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The class name of the XML parser to use.
     */
    protected static String xmlParserClassName;

    /**
     * The class name of the CSS parser to use.
     */
    protected static String cssParserClassName;

    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
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
            xmlParserClassName = bundle.getString(XML_PARSER_CLASS_NAME_KEY);
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
            cssParserClassName = bundle.getString(CSS_PARSER_CLASS_NAME_KEY);
        }
        return cssParserClassName;
    }

    /**
     * Sets the class name of the CSS parser to use.
     *
     * @param cssParserClassName the classname of the CSS parser
     */
    public static void setCSSParserClassName(String cssParserClassName) {
        XMLResourceDescriptor.cssParserClassName = xmlParserClassName;
    }
}
