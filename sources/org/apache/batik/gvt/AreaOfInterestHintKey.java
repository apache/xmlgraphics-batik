/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.RenderingHints;
import java.awt.Shape;

/**
 * This class is here to workaround a javadoc problem. It is only used by
 * <code>GraphicsNode</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
final class AreaOfInterestHintKey extends RenderingHints.Key {
    AreaOfInterestHintKey() {
        super(1000);
    }
    public boolean isCompatibleValue(Object val) {
        boolean isCompatible = true;
        if ((val != null) && !(val instanceof Shape)) {
            isCompatible = false;
        }
        return isCompatible;
    }
}

