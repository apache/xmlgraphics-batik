/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractNode;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGZoomAndPan;

/**
 * This class provides support for SVGZoomAndPan features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGZoomAndPanSupport implements SVGConstants {

    /**
     * This class does not need to be instantiated.
     */
    protected SVGZoomAndPanSupport() {
    }
    
    /**
     * Sets the zoomAndPan attribute value.
     */
    public static void setZoomAndPan(Element elt, short val)
	throws DOMException {
	switch (val) {
	case SVGZoomAndPan.SVG_ZOOMANDPAN_DISABLE:
	    elt.setAttributeNS(null, SVG_ZOOM_AND_PAN_ATTRIBUTE,
                               SVG_DISABLE_VALUE);
	    break;
	case SVGZoomAndPan.SVG_ZOOMANDPAN_MAGNIFY:
	    elt.setAttributeNS(null, SVG_ZOOM_AND_PAN_ATTRIBUTE,
                               SVG_MAGNIFY_VALUE);
	    break;
	default:
	    throw ((AbstractNode)elt).createDOMException
		(DOMException.INVALID_MODIFICATION_ERR,
		 "zoom.and.pane",
		 new Object[] { new Integer(val) });
	}
    }

    /**
     * Returns the ZoomAndPan attribute value.
     */
    public static short getZoomAndPan(Element elt) {
	String s = elt.getAttributeNS(null, SVG_ZOOM_AND_PAN_ATTRIBUTE);
	if (s.equals(SVG_MAGNIFY_VALUE)) {
	    return SVGZoomAndPan.SVG_ZOOMANDPAN_MAGNIFY;
	}
	return SVGZoomAndPan.SVG_ZOOMANDPAN_DISABLE;
    }
}
