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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGAElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAElement
    extends    SVGURIReferenceGraphicsElement
    implements SVGAElement {
    /**
     * The reference to the target attribute.
     */
    protected WeakReference targetReference;

    /**
     * Creates a new SVGOMAElement object.
     */
    public SVGOMAElement() {
    }

    /**
     * Creates a new SVGOMAElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMAElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "a";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGAElement#getTarget()}.
     */
    public SVGAnimatedString getTarget() {
	SVGAnimatedString result;
	if (targetReference == null ||
	    (result = (SVGAnimatedString)targetReference.get()) == null) {
	    result = new SVGOMAnimatedString(this, null, "target");
	    targetReference = new WeakReference(result);
	}
	return result;
    } 
}
