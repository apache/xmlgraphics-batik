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
 * notified of CSS style declaration changes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public interface CSSStyleDeclarationChangeListener
    extends CSSPropertyChangeListener {
    /**
     * Called before a CSS declaration will be changed.
     */
    void cssStyleDeclarationChangeStart(CSSStyleDeclarationChangeEvent evt);

    /**
     * Called a CSS declaration change has been cancelled.
     */
    void cssStyleDeclarationChangeCancel(CSSStyleDeclarationChangeEvent evt);

    /**
     * Called after a CSS declaration was changed.
     */
    void cssStyleDeclarationChangeEnd(CSSStyleDeclarationChangeEvent evt);
}
