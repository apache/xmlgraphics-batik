/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This interface represents elements with an overrideStyle attribute.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface OverrideStyleElement {
    /**
     * Whether the element has an override style.
     */
    boolean hasOverrideStyle(String pseudoElt);

    /**
     * This method is used to retrieve the override style declaration for this
     * element and a specified pseudo-element. 
     * @param pseudoElt  The pseudo element or <code>null</code> if none. 
     * @return  The override style declaration. 
     */
    CSSStyleDeclaration getOverrideStyle(String pseudoElt);
}
