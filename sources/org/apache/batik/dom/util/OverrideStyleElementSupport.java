/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class provides support for CSS override style.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class OverrideStyleElementSupport {
    /**
     * The factory used to create CSSStyleDeclaration objects.
     */
    protected CSSStyleDeclarationFactory factory;

    /**
     * The table of override styles.
     */
    protected HashTable overrideStyles;

    /**
     * Creates a new OverrideStyleElementSupport object.
     */
    public OverrideStyleElementSupport(CSSStyleDeclarationFactory f) {
	factory = f;
    }

    /**
     * Whether the element has an override style.
     * @param pseudoElt The pseudo element or <code>null</code> if none. 
     */
    public boolean hasOverrideStyle(String pseudoElt) {
	if (overrideStyles == null) {
	    return false;
	}
	pseudoElt = (pseudoElt == null) ? "" : pseudoElt;
	return overrideStyles.get(pseudoElt) != null;
    }

    /**
     * This method is used to retrieve the override style declaration for this
     * element and a specified pseudo-element. 
     * @param elt  The element whose style is to be modified.  This parameter 
     *   cannot be null. 
     * @param pseudoElt  The pseudo element or <code>null</code> if none. 
     * @return  The override style declaration. 
     */
    public CSSStyleDeclaration getOverrideStyle(String pseudoElt) {
	if (overrideStyles == null) {
	    overrideStyles = new HashTable();
	}
	pseudoElt = (pseudoElt == null) ? "" : pseudoElt;
	CSSStyleDeclaration result;
        result = (CSSStyleDeclaration)overrideStyles.get(pseudoElt);
	if (result == null) {
	    result = factory.createCSSStyleDeclaration();
	    overrideStyles.put(pseudoElt, result);
	}
	return result;
    }
}
