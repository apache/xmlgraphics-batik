/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.ref.Reference;

/**
 * This class is here to workaround a javadoc problem. It is only used by
 * <code>GraphicsNode</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
final class BufferedImageHintKey extends RenderingHints.Key {
    BufferedImageHintKey(int number) { super(number); }

    public boolean isCompatibleValue(Object val) {
        if (val == null)
            return true;

        if (!(val instanceof Reference))
            return false;
        Reference ref = (Reference)val;
        val = ref.get();
        if (val == null)
            return true;
        if (val instanceof BufferedImage)
            return true;

        return false;
    }
}

