/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.Condition;
import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.CombinatorCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMAndCondition extends AbstractCombinatorCondition {
    /**
     * Creates a new CombinatorCondition object.
     */
    public CSSOMAndCondition(Condition c1, Condition c2) {
	super(c1, c2);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_AND_CONDITION;
    }

    /**
     * Tests whether this condition matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return ((ExtendedCondition)getFirstCondition()).match(e, pseudoE) &&
               ((ExtendedCondition)getSecondCondition()).match(e, pseudoE);
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	return "" + getFirstCondition() + getSecondCondition();
    }
}
