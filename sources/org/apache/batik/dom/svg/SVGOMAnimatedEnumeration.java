/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

/**
 * This class implements the {@link SVGAnimatedEnumeration} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedEnumeration
    implements SVGAnimatedEnumeration {

    /**
     * The Short constants.
     */
    protected final static Short[] SHORTS = {
        new Short((short)0),
        new Short((short)1),
        new Short((short)2),
        new Short((short)3),
        new Short((short)4),
        new Short((short)5),
        new Short((short)6),
        new Short((short)7),
        new Short((short)8),
        new Short((short)9),
    };

    /**
     * The associated element.
     */
    protected SVGOMElement element;

    /**
     * The associated attribute namespace URI.
     */
    protected String attributeNsURI;

    /**
     * The associated attribute name.
     */
    protected String attributeName;

    /**
     * The string-short map.
     */
    protected Map stringShortMap;

    /**
     * The short-string map.
     */
    protected Map shortStringMap;

    /**
     * The default value producer.
     */
    protected DefaultAttributeValueProducer defaultValueProducer;

    /**
     * Creates a new SVGAnimatedEnumeration object.
     * @param elt The associated element.
     * @param nsURI The associated element namespace URI.
     * @param attr The associated attribute name.
     * @param strShort The enumeration values. The keys are strings and the
     *                 values are Short objects.
     * @param shortStr The enumeration values. The keys are Short objects and
     *                 the values are strings.
     * @param def The default value producer.
     */
    public SVGOMAnimatedEnumeration(SVGOMElement elt, String nsURI, String attr,
                                    Map strShort, Map shortStr,
                                    DefaultAttributeValueProducer def) {
	element = elt;
	attributeNsURI = nsURI;
	attributeName = attr;
        stringShortMap = strShort;
        shortStringMap = shortStr;
        defaultValueProducer = def;
    }

    /**
     * Creates a new Short object.
     */
    public static Short createShort(short s) {
        if (s < 0 || s >= SHORTS.length) {
            return new Short(s);
        }
        return SHORTS[s];
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedEnumeration#getBaseVal()}.
     */
    public short getBaseVal() {
        Attr a = element.getAttributeNodeNS(attributeNsURI, attributeName);
        if (a != null) {
            Short s = (Short)stringShortMap.get(a.getValue());
            if (s != null) {
                return s.shortValue();
            }
            return 0; // Unknown
        }
        String s = defaultValueProducer.getDefaultAttributeValue();
        return ((Short)stringShortMap.get(s)).shortValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedNumber#setBaseVal(short)}.
     */
    public void setBaseVal(short baseVal) throws DOMException {
        if (baseVal == 0 || baseVal > shortStringMap.size()) {
            element.setAttributeNS(attributeNsURI, attributeName, "");
        }
        element.setAttributeNS(attributeNsURI, attributeName,
                               (String)shortStringMap.get(createShort(baseVal))); 
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedEnumeration#getAnimVal()}.
     */
    public short getAnimVal() {
	throw new RuntimeException(" !!! TODO: SVGOMAnimatedEnumeration.getAnimVal()");
    }
}
