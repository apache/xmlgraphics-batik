/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGZoomAndPan;

/**
 * This class provides support for SVGZoomAndPan features.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGZoomAndPanSupport {
    /**
     * The zoomAndPan attribute name.
     */
    public final static String ZOOM_AND_PAN = "zoomAndPan";

    /**
     * The disable zoomAndPan attribute value.
     */
    public final static String DISABLE = "disable";

    /**
     * The magnify zoomAndPan attribute value.
     */
    public final static String MAGNIFY = "magnify";

    /**
     * The disable zoomAndPan attribute value.
     */
    public final static String ZOOM = "zoom";

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
	    elt.setAttribute(ZOOM_AND_PAN, DISABLE);
	    break;
	case SVGZoomAndPan.SVG_ZOOMANDPAN_MAGNIFY:
	    elt.setAttribute(ZOOM_AND_PAN, MAGNIFY);
	    break;
	case SVGZoomAndPan.SVG_ZOOMANDPAN_ZOOM:
	    elt.setAttribute(ZOOM_AND_PAN, ZOOM);
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
	String s = elt.getAttribute(ZOOM_AND_PAN);
	if (s.equals(MAGNIFY)) {
	    return SVGZoomAndPan.SVG_ZOOMANDPAN_MAGNIFY;
	}
	if (s.equals(ZOOM)) {
	    return SVGZoomAndPan.SVG_ZOOMANDPAN_ZOOM;
	}
	return SVGZoomAndPan.SVG_ZOOMANDPAN_DISABLE;
    }
}
