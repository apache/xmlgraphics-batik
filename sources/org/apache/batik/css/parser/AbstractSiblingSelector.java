/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

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
    implements SiblingSelector {

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
