/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import java.util.Set;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides an implementation for the
 * {@link org.w3c.css.sac.DescendantSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSDescendantSelector extends AbstractDescendantSelector {

    /**
     * Creates a new CSSDescendantSelector object.
     */
    public CSSDescendantSelector(Selector ancestor, SimpleSelector simple) {
	super(ancestor, simple);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
	return SAC_DESCENDANT_SELECTOR;
    }

    /**
     * Tests whether this selector matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	ExtendedSelector p = (ExtendedSelector)getAncestorSelector();
	for (Node n = e.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() == Node.ELEMENT_NODE &&
                p.match((Element)n, null)) {
                return ((ExtendedSelector)getSimpleSelector()).match(e,
                                                                     pseudoE);
            }
	}
	return false;
    }

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    public void fillAttributeSet(Set attrSet) {
        ((ExtendedSelector)getSimpleSelector()).fillAttributeSet(attrSet);
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
	return getAncestorSelector() + " " + getSimpleSelector();
    }
}
