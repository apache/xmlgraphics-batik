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
import org.w3c.dom.svg.SVGAnimatedBoolean;

/**
 * This class implements the {@link SVGAnimatedBoolean} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedBoolean
    implements SVGAnimatedBoolean,
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
     * The actual boolean value.
     */
    protected boolean baseVal;

    /**
     * The default's attribute value.
     */
    protected String defaultValue;

    /**
     * Whether the mutation comes from this object.
     */
    protected boolean mutate;

    /**
     * Creates a new SVGOMAnimatedBoolean.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param attr The attribute node, if any.
     * @param val The default attribute value, if missing.
     */
    public SVGOMAnimatedBoolean(AbstractElement elt,
                                String ns,
                                String ln,
                                Attr attr,
                                String val) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
        if (attr != null) {
            String s = attr.getValue();
            baseVal = "true".equals(s);
        }
        defaultValue = val;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedBoolean#getBaseVal()}.
     */
    public boolean getBaseVal() {
        return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedBoolean#setBaseVal(boolean)}.
     */
    public void setBaseVal(boolean baseVal) throws DOMException {
        if (this.baseVal != baseVal) {
            mutate = true;
            this.baseVal = baseVal;
            element.setAttributeNS(namespaceURI, localName,
                                   (baseVal) ? "true" : "false");
            mutate = false;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedBoolean#getAnimVal()}.
     */
    public boolean getAnimVal() {
        throw new RuntimeException("!!! TODO: getAnimVal()");
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!mutate) {
            baseVal = "true".equals(newv);
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!mutate) {
            baseVal = "true".equals(newv);
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!mutate) {
            baseVal = "true".equals(defaultValue);
        }
    }
}
