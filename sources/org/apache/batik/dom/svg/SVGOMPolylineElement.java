/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGPolylineElement;

/**
 * This class implements {@link SVGPolylineElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMPolylineElement
    extends    SVGGraphicsElement
    implements SVGPolylineElement {

    /**
     * Creates a new SVGOMPolylineElement object.
     */
    protected SVGOMPolylineElement() {
    }

    /**
     * Creates a new SVGOMPolylineElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMPolylineElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_POLYLINE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getPoints()}.
     */
    public SVGPointList getPoints() {
        return SVGAnimatedPointsSupport.getPoints(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getAnimatedPoints()}.
     */
    public SVGPointList getAnimatedPoints() {
        return SVGAnimatedPointsSupport.getAnimatedPoints(this);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMPolylineElement();
    }
}
