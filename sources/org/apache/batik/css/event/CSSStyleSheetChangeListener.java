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
 * notified of CSS style sheet changes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public interface CSSStyleSheetChangeListener
    extends CSSStyleRuleChangeListener {
    /**
     * Called when a CSSRule has been added to the style sheet.
     */
    void cssRuleAdded(CSSRuleEvent evt);

    /**
     * Called when a CSSRule has been removed to the style sheet.
     */
    void cssRuleRemoved(CSSRuleEvent evt);
}
