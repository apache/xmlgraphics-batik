/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

import org.w3c.dom.css.CSSRule;

/**
 * This class represents the events which get fired whenever a
 * CSS property value is changed.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class CSSRuleEvent {
    /**
     * The source of this event.
     */
    protected Object source;

    /**
     * The associated CSS rule.
     */
    protected CSSRule cssRule;

    /**
     * Creates a new CSSRuleEvent object.
     * @param source The source of this event.
     * @param rule The rule associated with this event.
     */
    public CSSRuleEvent(Object source, CSSRule rule) {
	this.source = source;
	cssRule = rule;
    }

    /**
     * Returns the event source.
     */
    public Object getSource() {
	return source;
    }

    /**
     * Returns the associated CSSRule.
     */
    public CSSRule getCssRule() {
	return cssRule;
    }
}
