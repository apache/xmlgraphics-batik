/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.dom.Element;

/**
 * This class implements the {@link org.w3c.css.sac.ElementSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMPseudoElementSelector extends AbstractElementSelector {
    /**
     * Creates a new ElementSelector object.
     */
    public CSSOMPseudoElementSelector(String uri, String name) {
	super(uri, name);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
	return SAC_PSEUDO_ELEMENT_SELECTOR;
    }

    /**
     * Tests whether this selector matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return getLocalName().equalsIgnoreCase(pseudoE);
    }

    /**
     * Returns the specificity of this selector.
     */
    public int getSpecificity() {
	return 0;
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
	return ":" + getLocalName();
    }
}
