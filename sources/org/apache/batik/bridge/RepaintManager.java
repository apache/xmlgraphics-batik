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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.batik.gvt.UpdateTracker;

import org.apache.batik.gvt.renderer.ImageRenderer;

/**
 * This class manages the rendering of a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RepaintManager {
    
    /**
     * The associated UpdateManager.
     */
    protected UpdateManager updateManager;

    /**
     * The renderer used to repaint the buffer.
     */
    protected ImageRenderer renderer;

    /**
     * Whether or not the manager is active.
     */
    protected boolean enabled;

    /**
     * Creates a new repaint manager.
     */
    public RepaintManager(UpdateManager um, ImageRenderer r) {
        updateManager = um;
        renderer = r;
    }
    
    /**
     * Repaints the dirty areas, if needed.
     */
    public void repaint() {
        UpdateTracker ut = updateManager.getUpdateTracker();
        if (ut.hasChanged()) {
            List dirtyAreas = ut.getDirtyAreas();
            if (dirtyAreas != null) {
                // Calls the UpdateManager methods
                // to allow events to be fired.
                updateManager.modifiedAreas(dirtyAreas);
                updateManager.updateRendering(dirtyAreas);
            }
            ut.clear();
        }
    }

    /**
     * Call this to let the Repaint Manager know that certain areas
     * in the image have been modified and need to be rerendered..
     */
    public void modifiedAreas(List areas) {
        renderer.flush(areas);
    }

    /**
     * Updates the rendering buffer.
     * @param u2d The user to device transform.
     * @param dbr Whether the double buffering should be used.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     * @return the list of the rectangles to repaint.
     */
    public List updateRendering(AffineTransform u2d,
                                boolean dbr,
                                Shape aoi,
                                int width,
                                int height) throws InterruptedException {
        renderer.setTransform(u2d);
        renderer.setDoubleBuffered(dbr);
        renderer.updateOffScreen(width, height);
        renderer.clearOffScreen();
        List l = new ArrayList(1);
        l.add(aoi);
        return updateRendering(l);
    }

    /**
     * Updates the rendering buffer.
     * @param aoi The area of interest in the renderer space units.
     * @return the list of the rectangles to repaint.
     */
    public List updateRendering(List areas) throws InterruptedException {
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
}
