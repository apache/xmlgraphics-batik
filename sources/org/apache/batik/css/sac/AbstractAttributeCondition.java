/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.AttributeCondition;

/**
 * This class provides an abstract implementation of the {@link
 * org.w3c.css.sac.AttributeCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractAttributeCondition
    implements AttributeCondition,
	       ExtendedCondition {
    /**
     * The attribute value.
     */
    protected String value;

    /**
     * Creates a new AbstractAttributeCondition object.
     */
    protected AbstractAttributeCondition(String value) {
	this.value = value;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	AbstractAttributeCondition c = (AbstractAttributeCondition)obj;
	return c.value.equals(value);
    }

    /**
     * Returns the specificity of this condition.
     */
    public int getSpecificity() {
	return 1 << 8;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.AttributeCondition#getValue()}.
     */
    public String getValue() {
	return value;
    }
}
