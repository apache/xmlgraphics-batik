/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * This class provides an abstract implementation of the {@link
 * org.w3c.css.sac.CombinatorCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractCombinatorCondition
    implements CombinatorCondition,
               ExtendedCondition {
    /**
     * The first condition.
     */
    protected Condition firstCondition;

    /**
     * The second condition.
     */
    protected Condition secondCondition;

    /**
     * Creates a new CombinatorCondition object.
     */
    protected AbstractCombinatorCondition(Condition c1, Condition c2) {
	firstCondition = c1;
	secondCondition = c2;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	AbstractCombinatorCondition c = (AbstractCombinatorCondition)obj;
	return c.firstCondition.equals(firstCondition) &&
	       c.secondCondition.equals(secondCondition);
    }

    /**
     * Returns the specificity of this condition.
     */
    public int getSpecificity() {
	return ((ExtendedCondition)getFirstCondition()).getSpecificity() +
               ((ExtendedCondition)getSecondCondition()).getSpecificity();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.CombinatorCondition#getFirstCondition()}.
     */    
    public Condition getFirstCondition() {
	return firstCondition;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.CombinatorCondition#getSecondCondition()}.
     */    
    public Condition getSecondCondition() {
	return secondCondition;
    }
}
