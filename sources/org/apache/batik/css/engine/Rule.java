/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

/**
 * This interface represents a CSS rule.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Rule {
    
    /**
     * Returns a constant identifying the rule type.
     */
    short getType();
    

    /**
     * Returns a printable representation of this rule.
     */
    String toString(CSSEngine eng);
}
