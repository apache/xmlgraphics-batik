/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

/**
 * This class provides an adapter for the CSSStyleDeclarationChangeListener
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class CSSStyleDeclarationChangeAdapter
    implements CSSStyleDeclarationChangeListener {
    /**
     * Called when a CSS property is changed.
     */
    public void cssPropertyChange(CSSPropertyChangeEvent evt) {
    }

    /**
     * Called before a CSS declaration will be changed.
     */
    public void cssStyleDeclarationChangeStart
        (CSSStyleDeclarationChangeEvent evt) {
    }

    /**
     * Called a CSS declaration change has been cancelled.
     */
    public void cssStyleDeclarationChangeCancel
        (CSSStyleDeclarationChangeEvent evt) {
    }

    /**
     * Called after a CSS declaration was changed.
     */
    public void cssStyleDeclarationChangeEnd
        (CSSStyleDeclarationChangeEvent evt) {
    }
}
