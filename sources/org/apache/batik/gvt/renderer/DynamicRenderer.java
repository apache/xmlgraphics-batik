/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * Simple implementation of the Renderer that supports dynamic updates.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DynamicRenderer extends StaticRenderer {

    /**
     * Constructs a new dynamic renderer with the specified buffer image.
     */
    public DynamicRenderer() {
        super();
    }

    public DynamicRenderer(RenderingHints rh,
                           AffineTransform at){
        super(rh, at);
    }

    List damagedAreas;

    /**
     * Repaints the associated GVT tree under the list of <tt>areas</tt>.
     * 
     * If double buffered is true and this method completes cleanly it
     * will set the result of the repaint as the image returned by
     * getOffscreen otherwise the old image will still be returned.
     * If double buffered is false it is possible some effects of
     * the failed rendering will be visible in the image returned
     * by getOffscreen.
     *
     * @param areas a List of regions to be repainted, in the current
     * user space coordinate system.  
     */
    public void repaint(List areas) throws InterruptedException {
        if (areas == null)
            return;

        // long t0 = System.currentTimeMillis();

        CachableRed cr;
        WritableRaster syncRaster;
        WritableRaster copyRaster;

        updateWorkingBuffers();
        if ((rootCR == null)           ||
            (workingBaseRaster == null))
            return;

        cr = rootCR;
        syncRaster = workingBaseRaster;
        copyRaster = workingRaster;

        Rectangle srcR = rootCR.getBounds();
        Rectangle dstR = workingRaster.getBounds();
        if ((dstR.x < srcR.x) ||
            (dstR.y < srcR.y) ||
            (dstR.x+dstR.width  > srcR.x+srcR.width) ||
            (dstR.y+dstR.height > srcR.y+srcR.height))
            cr = new PadRed(cr, dstR, PadMode.ZERO_PAD, null);

        List devRects = new ArrayList(areas.size());
        Iterator iter = areas.iterator();
        Rectangle dr = copyRaster.getBounds();
        float dmgSz = 0f;
        while (iter.hasNext()) {
            Shape s = (Shape)iter.next();
            Rectangle r;
            r = usr2dev.createTransformedShape(s).getBounds();
            if (!dr.intersects(r)) continue;
            r = dr.intersection(r);
            devRects.add(r);
            dmgSz += r.width*(float)r.height;
        }

        boolean repaintAll = (dmgSz > offScreenWidth*offScreenHeight*0.9f);

        // Ensure only one thread works on baseRaster at a time...
        synchronized (syncRaster) {
            // System.out.println("Dynamic:");
            if (repaintAll) {
                // System.out.println("Repainting All");
                cr.copyData(copyRaster);
            } else {
                if ((isDoubleBuffered)      &&
                    (currentRaster != null) && 
                    (damagedAreas  != null)) {
                    iter = damagedAreas.iterator();
                    Rectangle sr = currentRaster.getBounds();
                    while (iter.hasNext()) {
                        Rectangle r = (Rectangle)iter.next();
                        Raster src = currentRaster.createWritableChild
                            (r.x, r.y, r.width, r.height, r.x, r.y, null);
                        GraphicsUtil.copyData(src, copyRaster);
                    }
                }

                iter = devRects.iterator();
                while (iter.hasNext()) {
                    Rectangle r = (Rectangle)iter.next();
                    WritableRaster dst = copyRaster.createWritableChild
                        (r.x, r.y, r.width, r.height, r.x, r.y, null);
                    cr.copyData(dst);
                }
            }
        }

        if (Thread.currentThread().isInterrupted())
            return;

        // System.out.println("Dmg: "   + damagedAreas);
        // System.out.println("Areas: " + devRects);

        // Swap the buffers if the rendering completed cleanly.
        BufferedImage tmpBI = workingOffScreen;
        
        workingBaseRaster = currentBaseRaster;
        workingRaster     = currentRaster;
        workingOffScreen  = currentOffScreen;
        
        currentRaster     = copyRaster;
        currentBaseRaster = syncRaster;
        currentOffScreen  = tmpBI;
        
        damagedAreas = devRects;
    }
}
