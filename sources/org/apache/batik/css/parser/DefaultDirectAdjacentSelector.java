/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class provides an implementation for the
 * {@link org.w3c.css.sac.DescendantSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class DefaultDirectAdjacentSelector extends AbstractSiblingSelector {

    /**
     * Creates a new DefaultDirectAdjacentSelector object.
     */
    public DefaultDirectAdjacentSelector(short type,
                                         Selector parent,
                                         SimpleSelector simple) {
	super(type, parent, simple);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Selector#getSelectorType()}.
     */
    public short getSelectorType() {
	return SAC_DIRECT_ADJACENT_SELECTOR;
    }

    /**
     * Returns a representation of the selector.
     */
    public String toString() {
	return getSelector() + " + " + getSiblingSelector();
    }
}
