/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Bridge class for the &lt;vkern> element.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class SVGVKernElementBridge extends SVGKernElementBridge {

    /**
     * Constructs a new bridge for the &lt;vkern> element.
     */
    public SVGVKernElementBridge() {}

    /**
     * Returns 'vkern'.
     */
    public String getLocalName() {
        return SVG_VKERN_TAG;
    }

}
