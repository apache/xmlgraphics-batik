/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.sac.CSSOMConditionFactory;
import org.apache.batik.css.sac.CSSOMSelectorFactory;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 * This class implements the {@link org.w3c.dom.css.CSSRule} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCSSRule implements CSSRule {

    /**
     * The selector factory.
     */
    public final static SelectorFactory SELECTOR_FACTORY =
        CSSOMSelectorFactory.INSTANCE;
    
    /**
     * The condition factory.
     */
    public final static ConditionFactory CONDITION_FACTORY =
        CSSOMConditionFactory.INSTANCE;

    /**
     * The parent StyleSheet.
     */
    protected CSSStyleSheet parentStyleSheet;

    /**
     * The parent rule.
     */
    protected CSSRule parentRule;

    /**
     * Creates a new AbstractCSSRule object.
     */
    protected AbstractCSSRule(CSSStyleSheet ss, CSSRule pr) {
        parentStyleSheet = ss;
        parentRule = pr;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSRule#getParentStyleSheet()}.
     * @return {@link #parentStyleSheet}.
     */
    public CSSStyleSheet getParentStyleSheet() {
        return parentStyleSheet;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSRule#getParentRule()}.
     * @return {@link #parentRule}.
     */
    public CSSRule getParentRule() {
        return parentRule;
    }
}
