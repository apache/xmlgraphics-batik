/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

/**
 * This class provides an adapter for the CSSStyleSheetChangeListener
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class CSSStyleSheetChangeAdapter
    extends    CSSStyleDeclarationChangeAdapter
    implements CSSStyleSheetChangeListener {
    /**
     * Called before a CSS rule will be changed.
     */
    public void cssStyleRuleChangeStart(CSSStyleRuleChangeEvent evt) {
    }

    /**
     * Called a CSS rule change has been cancelled.
     */
    public void cssStyleRuleChangeCancel(CSSStyleRuleChangeEvent evt) {
    }

    /**
     * Called after a CSS rule was changed.
     */
    public void cssStyleRuleChangeEnd(CSSStyleRuleChangeEvent evt) {
    }

    /**
     * Called when a selector list was changed.
     */
    public void selectorListChange(SelectorListChangeEvent evt) {
    }

    /**
     * Called when a CSSRule has been added to the style sheet.
     */
    public void cssRuleAdded(CSSRuleEvent evt) {
    }

    /**
     * Called when a CSSRule has been removed to the style sheet.
     */
    public void cssRuleRemoved(CSSRuleEvent evt) {
    }
}
