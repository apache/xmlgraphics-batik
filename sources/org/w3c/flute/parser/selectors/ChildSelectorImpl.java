/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 *
 * $Id$
 */
package org.w3c.flute.parser.selectors;

import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class ChildSelectorImpl implements DescendantSelector {

    Selector       parent;
    SimpleSelector child;

    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_CHILD_SELECTOR;
    }

    /**
     * Creates a new ChildSelectorImpl
     */
    public ChildSelectorImpl(Selector parent, SimpleSelector child) {
        this.parent = parent;
	this.child = child;
    }
    
        
    /**
     * Returns the parent selector.
     */    
    public Selector getAncestorSelector() {
	return parent;
    }

    /*
     * Returns the simple selector.
     */    
    public SimpleSelector getSimpleSelector() {
	return child;
    }
}
