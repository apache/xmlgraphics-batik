/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.FloodRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

/**
 * Concrete implementation of the FloodRable interface.
 * This fills the input image with a given flood color
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */

public class ConcreteFloodRable extends AbstractRable
    implements FloodRable {

    /**
     * Color to use to flood the floodRegion
     */
    Color floodColor;

    /**
     * Region to fill with floodColor
     */
    FilterRegion floodRegion;

    /**
     * @param floodRegion region to be filled with floodColor
     * @param floodColor color to use to flood the floodRegion
     */
    public ConcreteFloodRable(FilterRegion floodRegion, 
                              Color floodColor) {
        setFloodColor(floodColor);
        setFloodRegion(floodRegion);
    }

    /**
     * Set the flood fill color
     * @param color The color to use when flood filling the input image
     */
    public void setFloodColor(Color color) {
        if (color == null) {
            // create a transparent flood fill
            floodColor = new Color(0, 0, 0, 0);
        } else {
            floodColor = color;
        }
    }

    /**
     * Get the flood fill color.
     * @return the color used to flood fill the input image
     */
    public Color getFloodColor() {
        // Color is immutable, we can return it
        return floodColor;
    }

    public Rectangle2D getBounds2D() {
        Rectangle2D floodRegionRect = floodRegion.getRegion();
        return floodRegionRect;
    }

    /**
     * Returns the flood region
     */
    public FilterRegion getFloodRegion(){
        return floodRegion;
    }

    /**
     * Sets the flood region
     */
    public void setFloodRegion(FilterRegion floodRegion){
        if(floodRegion == null){
            throw new IllegalArgumentException();
        }

        this.floodRegion = floodRegion;
    }

    /**
     * Create a RenderedImage that is filled with the current
     * flood fill color
     * @param rc The current render context
     * @return A RenderedImage with the flood fill
     */

    public RenderedImage createRendering(RenderContext rc) {
        Rectangle2D newFloodRegionRect = getBounds2D();

        // Get user space to device space transform

        AffineTransform usr2dev = rc.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }

        // Find out the renderable area
        // <!> FIX ME CHANGE: DO INTEGER APPROX.

        Rectangle2D imageRect = getBounds2D();

        // Now, take area of interest into account. It is
        // defined in user space.

        Shape userAOI = rc.getAreaOfInterest();
        if (userAOI == null) {
            userAOI = imageRect;
        }

        // intersect the filter area and the AOI in user space
        Rectangle2D userSpaceRenderableArea =
            imageRect.createIntersection(userAOI.getBounds2D());

        // The rendered area is the interesection of the
        // user space renderable area and the user space AOI bounds
        final Rectangle renderedArea
            = usr2dev.createTransformedShape(userSpaceRenderableArea).getBounds();

        if ((renderedArea.width == 0) || (renderedArea.height == 0)) {
            // If there is no intersection, return null
            return null;
        }

        // There is a non-empty intersection. Render into
        // that image
        BufferedImage offScreen
            = new BufferedImage(renderedArea.width,
                                renderedArea.height,
                                BufferedImage.TYPE_INT_ARGB) {
                    public int getMinX(){
                        return renderedArea.x;
                    }

                    public int getMinY(){
                        return renderedArea.y;
                        }
                        };

        Graphics2D g = offScreen.createGraphics();

        // a simple fill such as this probably doesn't consider
        // rendering hints, but I'll set them anyway
        RenderingHints hints = rc.getRenderingHints();
        if (hints != null) {
            g.setRenderingHints(hints);
        }

        // do the usr2dev transform - just in case this becomes a
        // flood paint rather than the simple color fill
        g.translate(-renderedArea.x, -renderedArea.y);
        g.transform(usr2dev);

        // set the flood color as the paint
        g.setPaint(getFloodColor());

        // fill the user space renderable area, this is the
        // area that was used to create the device space offscreen
        // image
        g.fill(userSpaceRenderableArea);

        g.dispose();

        return offScreen;

    }

}
