/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * This class provides an abstract implementation of the
 * {@link org.w3c.css.sac.CombinatorCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractCombinatorCondition
    implements CombinatorCondition {

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
     * <b>SAC</b>: Implements {@link CombinatorCondition#getFirstCondition()}.
     */    
    public Condition getFirstCondition() {
	return firstCondition;
    }

    /**
     * <b>SAC</b>: Implements {@link CombinatorCondition#getSecondCondition()}.
     */
    public Condition getSecondCondition() {
	return secondCondition;
    }
}
