/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;
import java.awt.Shape;

/**
 * TranscodingHint as to what the destination of the drawing is.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
final class TranscodingHintKey extends RenderingHints.Key {

    TranscodingHintKey() {
        super(10100);
    }

    public boolean isCompatibleValue(Object val) {
        boolean isCompatible = true;
        if ((val != null) && !(val instanceof String)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}

