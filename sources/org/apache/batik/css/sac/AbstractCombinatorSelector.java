/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.CombinatorSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class provides an abstract implementation of the {@link
 * org.w3c.css.sac.CombinatorSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCombinatorSelector
    implements CombinatorSelector,
	       ExtendedSelector {
    /**
     * The parent selector.
     */
    protected Selector parentSelector;

    /**
     * The simple selector.
     */
    protected SimpleSelector simpleSelector;

    /**
     * Creates a new CombinatorSelector object.
     */
    protected AbstractCombinatorSelector(Selector parent,
                                         SimpleSelector simple) {
	parentSelector = parent;
	simpleSelector = simple;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	AbstractCombinatorSelector s = (AbstractCombinatorSelector)obj;
	return s.simpleSelector.equals(simpleSelector);
    }

    /**
     * Returns the specificity of this selector.
     */
    public int getSpecificity() {
	return ((ExtendedSelector)parentSelector).getSpecificity() +
       	       ((ExtendedSelector)simpleSelector).getSpecificity();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.CombinatorSelector#getParentSelector()}.
     */    
    public Selector getParentSelector() {
	return parentSelector;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.CombinatorSelector#getSimpleSelector()}.
     */    
    public SimpleSelector getSimpleSelector() {
	return simpleSelector;
    }
}
