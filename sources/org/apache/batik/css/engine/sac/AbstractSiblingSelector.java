/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * This class provides an abstract implementation of the {@link
 * org.w3c.css.sac.SiblingSelector} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractSiblingSelector
    implements SiblingSelector,
	       ExtendedSelector {

    /**
     * The node type.
     */
    protected short nodeType;

    /**
     * The selector.
     */
    protected Selector selector;

    /**
     * The simple selector.
     */
    protected SimpleSelector simpleSelector;

    /**
     * Creates a new SiblingSelector object.
     */
    protected AbstractSiblingSelector(short type,
                                      Selector sel,
                                      SimpleSelector simple) {
        nodeType = type;
	selector = sel;
	simpleSelector = simple;
    }

    /**
     * Returns the node type.
     */
    public short getNodeType() {
        return nodeType;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj.getClass() != getClass())) {
	    return false;
	}
	AbstractSiblingSelector s = (AbstractSiblingSelector)obj;
	return s.simpleSelector.equals(simpleSelector);
    }

    /**
     * Returns the specificity of this selector.
     */
    public int getSpecificity() {
	return ((ExtendedSelector)selector).getSpecificity() +
       	       ((ExtendedSelector)simpleSelector).getSpecificity();
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SiblingSelector#getSelector()}.
     */    
    public Selector getSelector() {
	return selector;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.SiblingSelector#getSiblingSelector()}.
     */    
    public SimpleSelector getSiblingSelector() {
	return simpleSelector;
    }
}
