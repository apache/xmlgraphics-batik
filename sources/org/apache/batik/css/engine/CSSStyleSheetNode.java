/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

/**
 * This interface must be implemented by the DOM nodes which represent
 * CSS style-sheets.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSStyleSheetNode {

    /**
     * Returns the StyleSheet object this node represents. The result
     * is null if no style-sheet is available.
     */
    StyleSheet getCSSStyleSheet();
}
