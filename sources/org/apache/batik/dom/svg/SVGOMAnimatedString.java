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
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class implements the {@link SVGAnimatedString} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedString
    implements SVGAnimatedString,
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
     * Creates a new SVGOMAnimatedString.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     */
    public SVGOMAnimatedString(AbstractElement elt,
                               String ns,
                               String ln) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedString#getBaseVal()}.
     */
    public String getBaseVal() {
        return element.getAttributeNS(namespaceURI, localName);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedString#setBaseVal(string)}.
     */
    public void setBaseVal(String baseVal) throws DOMException {
        element.setAttributeNS(namespaceURI, localName, baseVal);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedString#getAnimVal()}.
     */
    public String getAnimVal() {
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
