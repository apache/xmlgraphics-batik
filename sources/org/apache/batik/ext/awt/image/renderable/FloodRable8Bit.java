/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Color;
import java.awt.Paint;
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

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;

/**
 * Concrete implementation of the FloodRable interface.
 * This fills the input image with a given flood paint
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */

public class FloodRable8Bit extends AbstractRable
    implements FloodRable {

    /**
     * Paint to use to flood the floodRegion
     */
    Paint floodPaint;

    /**
     * Region to fill with floodPaint
     */
    Rectangle2D floodRegion;

    /**
     * @param floodRegion region to be filled with floodPaint
     * @param floodPaint paint to use to flood the floodRegion
     */
    public FloodRable8Bit(Rectangle2D floodRegion, 
                              Paint floodPaint) {
        setFloodPaint(floodPaint);
        setFloodRegion(floodRegion);
    }

    /**
     * Set the flood fill paint
     * @param paint The paint to use when flood filling the input image
     */
    public void setFloodPaint(Paint paint) {
        if (paint == null) {
            // create a transparent flood fill
            floodPaint = new Color(0, 0, 0, 0);
        } else {
            floodPaint = paint;
        }
    }

    /**
     * Get the flood fill paint.
     * @return the paint used to flood fill the input image
     */
    public Paint getFloodPaint() {
        // Paint is immutable, we can return it
        return floodPaint;
    }

    public Rectangle2D getBounds2D() {

        return (Rectangle2D)floodRegion.clone();
    }

    /**
     * Returns the flood region
     */
    public Rectangle2D getFloodRegion(){
        return (Rectangle2D)floodRegion.clone();
    }

    /**
     * Sets the flood region
     */
    public void setFloodRegion(Rectangle2D floodRegion){
        if(floodRegion == null){
            throw new IllegalArgumentException();
        }

        this.floodRegion = floodRegion;
    }

    /**
     * Create a RenderedImage that is filled with the current
     * flood fill paint
     * @param rc The current render context
     * @return A RenderedImage with the flood fill
     */

    public RenderedImage createRendering(RenderContext rc) {
        // Get user space to device space transform
        AffineTransform usr2dev = rc.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }

        Rectangle2D imageRect = getBounds2D();

        // Now, take area of interest into account. It is
        // defined in user space.
        Rectangle2D userAOI;
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi     = imageRect;
            userAOI = imageRect;
        } else {
            userAOI = aoi.getBounds2D();

            // No intersection with the area of interest so return null..
            if (imageRect.intersects(userAOI) == false) 
                return null;

            // intersect the filter area and the AOI in user space
            Rectangle2D.intersect(imageRect, userAOI, userAOI);
        }

        // The rendered area is the interesection of the
        // user space renderable area and the user space AOI bounds
        final Rectangle renderedArea
            = usr2dev.createTransformedShape(userAOI).getBounds();

        if ((renderedArea.width <= 0) || (renderedArea.height <= 0)) {
            // If there is no intersection, return null
            return null;
        }

        // There is a non-empty intersection. Render into
        // that image
        BufferedImage offScreen
            = new BufferedImage(renderedArea.width,
                                renderedArea.height,
                                BufferedImage.TYPE_INT_ARGB);

        // System.out.println("renderedArea x/y/w/h: " + renderedArea.x + " / " + renderedArea.y + " / " + renderedArea.width + " / " + renderedArea.height);
        Graphics2D g = GraphicsUtil.createGraphics(offScreen,
                                                   rc.getRenderingHints());

        // do the usr2dev transform - just in case this becomes a
        // flood paint rather than the simple paint fill
        g.translate(-renderedArea.x, -renderedArea.y);
        g.transform(usr2dev);

        // set the flood paint as the paint
        g.setPaint(getFloodPaint());

        // fill the user space renderable area, this is the
        // area that was used to create the device space offscreen
        // image
        g.fill(userAOI);

        g.dispose();

        return new BufferedImageCachableRed(offScreen, 
                                            renderedArea.x,
                                            renderedArea.y);
    }
}
