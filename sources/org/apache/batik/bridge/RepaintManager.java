/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Rectangle;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.batik.gvt.UpdateTracker;

import org.apache.batik.gvt.renderer.ImageRenderer;

/**
 * This class manages the rendering of a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class RepaintManager {
    /**
     * The renderer used to repaint the buffer.
     */
    protected ImageRenderer renderer;

    /**
     * Creates a new repaint manager.
     */
    public RepaintManager(ImageRenderer r) {
        renderer = r;
    }
    
    /**
     * Updates the rendering buffer.
     * @param aoi The area of interest in the renderer space units.
     * @return the list of the rectangles to repaint.
     */
    public List updateRendering(List areas) throws InterruptedException {
        renderer.flush(areas);
        List rects = new ArrayList(areas.size());
        AffineTransform at = renderer.getTransform();

        Iterator i = areas.iterator();
        while (i.hasNext()) {
            Shape s = (Shape)i.next();
            s = at.createTransformedShape(s);
            Rectangle2D r2d = s.getBounds2D();
            int x0 = (int)Math.floor(r2d.getX());
            int y0 = (int)Math.floor(r2d.getY());
            int x1 = (int)Math.ceil(r2d.getX()+r2d.getWidth());
            int y1 = (int)Math.ceil(r2d.getY()+r2d.getHeight());
            // This rectangle must be outset one pixel to ensure
            // it includes the effects of anti-aliasing on object.s
            Rectangle r = new Rectangle(x0-1, y0-1, x1-x0+3, y1-y0+3);
                
            rects.add(r);
        }

        renderer.repaint(areas);
        return rects;
    }

    /**
     * Sets up the renderer so that it is ready to render for the new
     * 'context' defined by the user to device transform, double buffering
     * state, area of interest and width/height.
     * @param u2d The user to device transform.
     * @param dbr Whether the double buffering should be used.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     */
    public void setupRenderer(AffineTransform u2d,
                              boolean dbr,
                              Shape aoi,
                              int width,
                              int height) {
        renderer.setTransform(u2d);
        renderer.setDoubleBuffered(dbr);
        renderer.updateOffScreen(width, height);
        renderer.clearOffScreen();
    }

    /**
     * Returns the renderer's offscreen, i.e., the current state as rendered
     * by the associated renderer.
     */
    public BufferedImage getOffScreen(){
        return renderer.getOffScreen();
    }

}
