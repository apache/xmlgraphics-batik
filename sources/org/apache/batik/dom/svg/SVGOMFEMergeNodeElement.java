/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEMergeNodeElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEMergeNodeElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEMergeNodeElement
    extends    SVGOMElement
    implements SVGFEMergeNodeElement {

    /**
     * The reference to the in attribute.
     */
    protected WeakReference inReference;

    /**
     * Creates a new SVGOMFEMergeNodeElement object.
     */
    public SVGOMFEMergeNodeElement() {
    }

    /**
     * Creates a new SVGOMFEMergeNodeElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEMergeNodeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_FE_MERGE_NODE;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEMergeNodeElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
	SVGAnimatedString result;
	if (inReference == null ||
	    (result = (SVGAnimatedString)inReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, ATTR_IN);
	    inReference = new WeakReference(result);
	}
	return result;
    }
}
