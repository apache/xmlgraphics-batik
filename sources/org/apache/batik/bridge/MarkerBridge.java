/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.Marker;
import org.w3c.dom.Element;

/**
 * Factory class for vending <tt>Marker</tt> objects.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface MarkerBridge extends Bridge {

    /**
     * Creates a <tt>Marker</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param markerElement the element that represents the marker
     * @param paintedElement the element that references the marker element
     */
    Marker createMarker(BridgeContext ctx,
                        Element markerElement,
                        Element paintedElement);
}

