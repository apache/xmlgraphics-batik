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

import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class DirectAdjacentSelectorImpl implements SiblingSelector {

    Selector       child;
    SimpleSelector directAdjacent;

    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_DIRECT_ADJACENT_SELECTOR;
    }

    /**
     * Creates a new DescendantSelectorImpl
     */
    public DirectAdjacentSelectorImpl(Selector child, 
				      SimpleSelector directAdjacent) {
        this.child = child;
	this.directAdjacent = directAdjacent;
    }
    
    public short getNodeType() {
	return 1;
    }
        
    /**
     * Returns the parent selector.
     */    
    public Selector getSelector() {
	return child;
    }

    /*
     * Returns the simple selector.
     */    
    public SimpleSelector getSiblingSelector() {
	return directAdjacent;
    }
}
