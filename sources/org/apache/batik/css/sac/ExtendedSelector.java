/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.sac;

import org.w3c.css.sac.Selector;
import org.w3c.dom.Element;

/**
 * This interface extends the {@link org.w3c.css.sac.Selector}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ExtendedSelector extends Selector {
    /**
     * Tests whether this selector matches the given element.
     */
    boolean match(Element e, String pseudoE);

    /**
     * Returns the specificity of this selector.
     */
    int getSpecificity();
}
