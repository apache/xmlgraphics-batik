/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

/**
 * Contains additional RenderingHints Keys, such as 
 * KEY_AREA_OF_INTEREST
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class RenderingHintsKeyExt {
    /**
     * Key for the AOI hint. This hint is used to propagate the AOI to Paint
     * and PaintContext instances.
     */
    public static final RenderingHints.Key KEY_AREA_OF_INTEREST =
        new AreaOfInterestHintKey();

    /**
     * Do not authorize creation of instances of that class
     */
    private RenderingHintsKeyExt(){
    }
}
