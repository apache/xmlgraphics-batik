/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

/**
 * This interface must be implemented by the objects that want to be
 * notified of CSS style rule changes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSStyleRuleChangeListener
    extends CSSStyleDeclarationChangeListener {
    /**
     * Called before a CSS rule will be changed.
     */
    void cssStyleRuleChangeStart(CSSStyleRuleChangeEvent evt);

    /**
     * Called a CSS rule change has been cancelled.
     */
    void cssStyleRuleChangeCancel(CSSStyleRuleChangeEvent evt);

    /**
     * Called after a CSS rule was changed.
     */
    void cssStyleRuleChangeEnd(CSSStyleRuleChangeEvent evt);

    /**
     * Called when a selector list was changed.
     */
    void selectorListChange(SelectorListChangeEvent evt);
}
