/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * This interface is the placeholder for SVG application informations.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGContext {

    /**
     * Returns the size of a px CSS unit in millimeters.
     */
    float getPixelUnitToMillimeter();

    /**
     * Returns the size of a px CSS unit in millimeters.
     * This will be removed after next release.
     * @see #getPixelUnitToMillimeter();
     */
    float getPixelToMM();

    /**
     * Returns the tight bounding box in current user space (i.e.,
     * after application of the transform attribute, if any) on the
     * geometry of all contained graphics elements, exclusive of
     * stroke-width and filter effects).
     */
    Rectangle2D getBBox();

    /**
     * Returns the transform from the global transform space to pixels.
     */
    AffineTransform getScreenTransform();

    /**
     * Sets the transform to be used from the global transform space to pixels.
     */
    void setScreenTransform(AffineTransform at);

    /**
     * Returns the transformation matrix from current user units
     * (i.e., after application of the transform attribute, if any) to
     * the viewport coordinate system for the nearestViewportElement.
     */
    AffineTransform getCTM();

    /**
     * Returns the global transformation matrix from the current
     * element to the root.
     */
    AffineTransform getGlobalTransform();

    /**
     * Returns the width of the viewport which directly contains the
     * associated element.
     */
    float getViewportWidth();

    /**
     * Returns the height of the viewport which directly contains the
     * associated element.
     */
    float getViewportHeight();

    /**
     * Returns the font-size on the associated element.
     */
    float getFontSize();
}
