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

import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.Condition;

/**
 * @version $Revision$
 * @author  Philippe Le Hegaret
 */
public class ConditionalSelectorImpl implements ConditionalSelector {

    SimpleSelector simpleSelector;
    Condition      condition;

    /**
     * An integer indicating the type of <code>Selector</code>
     */
    public short getSelectorType() {
	return Selector.SAC_CONDITIONAL_SELECTOR;
    }
    

    /**
     * Creates a new ConditionalSelectorImpl
     */
    public ConditionalSelectorImpl(SimpleSelector simpleSelector,
				   Condition condition) {
        this.simpleSelector = simpleSelector;
	this.condition      = condition;
    }
    

    /**
     * Returns the simple selector.
     * <p>The simple selector can't be a <code>ConditionalSelector</code>.</p>
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }

    /**
     * Returns the condition to be applied on the simple selector.
     */    
    public Condition getCondition() {
	return condition;
    }
}

