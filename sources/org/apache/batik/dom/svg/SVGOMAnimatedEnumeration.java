/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

/**
 * This class provides an implementation of the {@link
 * SVGAnimatedEnumeration} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedEnumeration
    implements SVGAnimatedEnumeration,
               LiveAttributeValue {
    
    /**
     * The associated element.
     */
    protected AbstractElement element;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * The values in this enumeration.
     */
    protected String[] values;

    /**
     * The default value, if the attribute is not specified.
     */
    protected short defaultValue;

    /**
     * Creates a new SVGOMAnimatedEnumeration.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param val The values in this enumeration.
     * @param def The default value to use.
     */
    public SVGOMAnimatedEnumeration(AbstractElement elt,
                                    String ns,
                                    String ln,
                                    String[] val,
                                    short def) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
        values = val;
        defaultValue = def;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedEnumeration#getBaseVal()}.
     */
    public short getBaseVal() {
        String val = element.getAttributeNS(namespaceURI, localName);
        if (val.length() == 0) {
            return defaultValue;
        }
        for (short i = 0; i < values.length; i++) {
            if (val.equals(values[i])) {
                return i;
            }
        }
        return 0;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGAnimatedEnumeration#setBaseVal(short)}.
     */
    public void setBaseVal(short baseVal) throws DOMException {
        if (baseVal >= 0 && baseVal < values.length) {
            element.setAttributeNS(namespaceURI, localName, values[baseVal]);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedEnumeration#getAnimVal()}.
     */
    public short getAnimVal() {
        throw new RuntimeException("!!! TODO: getAnimVal()");
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
    }
}
