/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.CombinatorSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMChildSelector extends AbstractCombinatorSelector {
    /**
     * Creates a new CSSOMChildSelector object.
     */
    public CSSOMChildSelector(Selector parent, SimpleSelector simple) {
	super(parent, simple);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
	return SAC_CHILD_SELECTOR;
    }

    /**
     * Tests whether this selector matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	Node n = e.getParentNode();
	if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
	    return ((ExtendedSelector)getParentSelector()).match((Element)n,
                                                                 null) &&
		   ((ExtendedSelector)getSimpleSelector()).match(e, pseudoE);
	}
	return false;
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
	SimpleSelector s = getSimpleSelector();
	if (s.getSelectorType() == SAC_PSEUDO_ELEMENT_SELECTOR) {
	    return "" + getParentSelector() + s;
	}
	return getParentSelector() + " > " + s;
    }
}
