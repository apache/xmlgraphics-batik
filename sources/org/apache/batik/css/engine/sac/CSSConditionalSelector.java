/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.ConditionalSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSConditionalSelector
    implements ConditionalSelector,
	       ExtendedSelector {

    /**
     * The simple selector.
     */
    protected SimpleSelector simpleSelector;

    /**
     * The condition.
     */
    protected Condition condition;

    /**
     * Creates a new ConditionalSelector object.
     */
    public CSSConditionalSelector(SimpleSelector s, Condition c) {
	simpleSelector = s;
	condition      = c;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	CSSConditionalSelector s = (CSSConditionalSelector)obj;
	return s.simpleSelector.equals(simpleSelector) &&
	       s.condition.equals(condition);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
	return SAC_CONDITIONAL_SELECTOR;
    }

    /**
     * Tests whether this selector matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return ((ExtendedSelector)getSimpleSelector()).match(e, pseudoE) &&
	       ((ExtendedCondition)getCondition()).match(e, pseudoE);
    }

    /**
     * Returns the specificity of this selector.
     */
    public int getSpecificity() {
	return ((ExtendedSelector)getSimpleSelector()).getSpecificity() +
	       ((ExtendedCondition)getCondition()).getSpecificity();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionalSelector#getSimpleSelector()}.
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.ConditionalSelector#getCondition()}.
     */    
    public Condition getCondition() {
	return condition;
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
	return "" + simpleSelector + condition;
    }
}
