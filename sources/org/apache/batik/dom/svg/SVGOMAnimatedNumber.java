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
import org.w3c.dom.svg.SVGAnimatedNumber;

/**
 * This class implements the {@link SVGAnimatedNumber} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedNumber
    implements SVGAnimatedNumber,
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
     * The default value.
     */
    protected float defaultValue;

    /**
     * Creates a new SVGOMAnimatedNumber.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param val The default value, if the attribute is not specified.
     */
    public SVGOMAnimatedNumber(AbstractElement elt,
                               String ns,
                               String ln,
                               float  val) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
        defaultValue = val;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedNumber#getBaseVal()}.
     */
    public float getBaseVal() {
        Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
        if (attr == null) {
            return defaultValue;
        }
        return Float.parseFloat(attr.getValue());
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedNumber#setBaseVal(float)}.
     */
    public void setBaseVal(float baseVal) throws DOMException {
        element.setAttributeNS(namespaceURI, localName,
                               String.valueOf(baseVal));
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedNumber#getAnimVal()}.
     */
    public float getAnimVal() {
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
