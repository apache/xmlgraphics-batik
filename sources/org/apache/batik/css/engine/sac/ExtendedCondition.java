/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import java.util.Set;

import org.w3c.css.sac.Condition;
import org.w3c.dom.Element;

/**
 * This interface provides additional features to the
 * {@link org.w3c.css.sac.Condition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ExtendedCondition extends Condition {

    /**
     * Tests whether this condition matches the given element.
     */
    boolean match(Element e, String pseudoE);

    /**
     * Returns the specificity of this condition.
     */
    int getSpecificity();

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    void fillAttributeSet(Set attrSet);
}
