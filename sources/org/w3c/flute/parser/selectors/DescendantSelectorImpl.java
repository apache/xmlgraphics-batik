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

import org.w3c.css.sac.CombinatorSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class DescendantSelectorImpl implements CombinatorSelector {

    Selector       parent;
    SimpleSelector simpleSelector;

    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_DESCENDANT_SELECTOR;
    }

    /**
     * Creates a new DescendantSelectorImpl
     */
    public DescendantSelectorImpl(Selector parent, SimpleSelector simpleSelector) {
        this.parent = parent;
	this.simpleSelector = simpleSelector;
    }
    
        
    /**
     * Returns the parent selector.
     */    
    public Selector getParentSelector() {
	return parent;
    }

    /*
     * Returns the simple selector.
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }
}
