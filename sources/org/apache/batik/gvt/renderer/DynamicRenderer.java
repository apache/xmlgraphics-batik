/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import java.util.List;
import java.util.Iterator;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.geom.RectListManager;

/**
 * Simple implementation of the Renderer that supports dynamic updates.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DynamicRenderer extends StaticRenderer {

    final static int COPY_OVERHEAD      = 1000;
    final static int COPY_LINE_OVERHEAD = 10;

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

    RectListManager damagedAreas;

    protected CachableRed setupCache(CachableRed img) {
        // Don't do any caching of content for dynamic case
        return img;
    }

    public void flush(Rectangle r) {
        // Since we don't cache we don't need to flush
        return;
    }

    /**
     * Flush a list of rectangles of cached image data.
     */
    public void flush(List areas) {
        // Since we don't cache we don't need to flush
        return;
    }

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
    // long lastFrame = -1;
    public void repaint(List areas) {
        if (areas == null)
            return;

        // long t0 = System.currentTimeMillis();
        // if (lastFrame != -1) {
        //     System.out.println("InterFrame time: " + (t0-lastFrame));
        // }
        // lastFrame = t0;

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
        // System.out.println("RootCR: " + srcR);
        Rectangle dstR = workingRaster.getBounds();
        if ((dstR.x < srcR.x) ||
            (dstR.y < srcR.y) ||
            (dstR.x+dstR.width  > srcR.x+srcR.width) ||
            (dstR.y+dstR.height > srcR.y+srcR.height))
            cr = new PadRed(cr, dstR, PadMode.ZERO_PAD, null);

        Rectangle [] devRects = new Rectangle[areas.size()];
        Iterator iter = areas.iterator();
        Rectangle dr = copyRaster.getBounds();
        float dmgSz = 0f;
        int sz=0;
        // System.out.println("DR: " + dr);
        while (iter.hasNext()) {
            Shape s = (Shape)iter.next();
            s = usr2dev.createTransformedShape(s);
            Rectangle2D r2d = s.getBounds2D();
            int x0 = (int)Math.floor(r2d.getX());
            int y0 = (int)Math.floor(r2d.getY());
            int x1 = (int)Math.ceil(r2d.getX()+r2d.getWidth());
            int y1 = (int)Math.ceil(r2d.getY()+r2d.getHeight());
            // Rectangle r = new Rectangle(x0, y0, x1-x0+1, y1-y0+1);

            // This rectangle must be outset one pixel to ensure
            // it includes the effects of anti-aliasing on object.s
            Rectangle r = new Rectangle(x0-1, y0-1, x1-x0+3, y1-y0+3);

            // System.out.println("  Rect   [" + sz+ "]: " + r);
            //System.out.println("  Rect2D [" + sz+ "]: " + r2d);
            if (!dr.intersects(r)) continue;
            r = dr.intersection(r);
            devRects[sz++] = r;
            dmgSz += r.width*(float)r.height;
        }
        RectListManager devRLM =null;
        try {
             devRLM = new RectListManager(devRects, 0, sz);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Merge the repaint rectangles...
        devRLM.mergeRects(COPY_OVERHEAD, COPY_LINE_OVERHEAD);
        boolean repaintAll = (dmgSz > offScreenWidth*offScreenHeight*0.9f);

        // Ensure only one thread works on baseRaster at a time...
        synchronized (syncRaster) {
            // System.out.println("Dynamic:");
            if (repaintAll) {
                // System.out.println("Repainting All");
                cr.copyData(copyRaster);
            } else {
                java.awt.Graphics2D g2d = null;
                if (false) {
                    BufferedImage tmpBI = new BufferedImage
                        (workingOffScreen.getColorModel(),
                         copyRaster.createWritableTranslatedChild(0, 0),
                         workingOffScreen.isAlphaPremultiplied(), null);
                    g2d = GraphicsUtil.createGraphics(tmpBI);
                    g2d.translate(-copyRaster.getMinX(), 
                                  -copyRaster.getMinY());
                }

                if ((isDoubleBuffered) &&
                    (currentRaster != null) && 
                    (damagedAreas  != null)) {
                    
                    damagedAreas.subtract(devRLM, COPY_OVERHEAD, 
                                          COPY_LINE_OVERHEAD);
                    damagedAreas.mergeRects(COPY_OVERHEAD, 
                                            COPY_LINE_OVERHEAD); 

                    iter = damagedAreas.iterator();
                    Rectangle sr = currentRaster.getBounds();

                    while (iter.hasNext()) {
                        Rectangle r = (Rectangle)iter.next();
                        // System.out.println("Copy: " + r);
                        Raster src = currentRaster.createWritableChild
                            (r.x, r.y, r.width, r.height, r.x, r.y, null);
                        GraphicsUtil.copyData(src, copyRaster);
                        if (g2d != null) {
                            g2d.setPaint(new java.awt.Color(0,0,255,50));
                            g2d.fill(r);
                            g2d.setPaint(new java.awt.Color(0,0,0,50));
                            g2d.draw(r);
                        }
                    }
                }

                iter = devRLM.iterator();
                while (iter.hasNext()) {
                    Rectangle r = (Rectangle)iter.next();
                    // System.out.println("Render: " + r);
                    WritableRaster dst = copyRaster.createWritableChild
                        (r.x, r.y, r.width, r.height, r.x, r.y, null);
                    cr.copyData(dst);
                    if (g2d != null) {
                        g2d.setPaint(new java.awt.Color(255,0,0,50));
                        g2d.fill(r);
                        g2d.setPaint(new java.awt.Color(0,0,0,50));
                        g2d.draw(r);
                    }
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
        
        damagedAreas = devRLM;
    }
}
