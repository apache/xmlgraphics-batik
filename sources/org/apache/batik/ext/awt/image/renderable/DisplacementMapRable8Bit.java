/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.renderable.RenderContext;

import java.util.List;

import org.apache.batik.ext.awt.image.GraphicsUtil;

import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.DisplacementMapOp;
import org.apache.batik.ext.awt.image.rendered.AffineRed;

/**
 * Implements a DisplacementMap operation, which takes pixel values from
 * another image to spatially displace the input image
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com>Sheng Pei</a>
 * @version $Id$
 */
public class DisplacementMapRable8Bit 
    extends AbstractColorInterpRable
    implements DisplacementMapRable{
    /**
     * Displacement scale factor
     */
    private double scale;

    /**
     * Defines which channel in the second source is used
     * to displace along the x axis
     */
    private ARGBChannel xChannelSelector;

    /**
     * Defines which channel in the second source is used
     * to displace along the y axis.
     */
    private ARGBChannel yChannelSelector;

    public DisplacementMapRable8Bit(List sources,
                                        double scale,
                                        ARGBChannel xChannelSelector,
                                        ARGBChannel yChannelSelector){
        setSources(sources);
        setScale(scale);
        setXChannelSelector(xChannelSelector);
        setYChannelSelector(yChannelSelector);
    }

    public Rectangle2D getBounds2D(){
        return ((Filter)(getSources().elementAt(0))).getBounds2D();
    }

    /**
     * The displacement scale factor
     * @param scale can be any number.
     */
    public void setScale(double scale){
        this.scale = scale;
    }

    /**
     * Returns the displacement scale factor
     */
    public double getScale(){
        return scale;
    }

    /**
     * Sets this filter sources. 
     */
    public void setSources(List sources){
        if(sources.size() != 2){
            throw new IllegalArgumentException();
        }
        init(sources, null);
    }

    /**
     * Select which component values will be used
     * for displacement along the X axis
     * @param xChannelSelector value is among R,
     * G, B and A.
     */
    public void setXChannelSelector(ARGBChannel xChannelSelector){
        if(xChannelSelector == null){
            throw new IllegalArgumentException();
        }

        this.xChannelSelector = xChannelSelector;
    }

    /**
     * Returns the xChannelSelector
     */
    public ARGBChannel getXChannelSelector(){
        return xChannelSelector;
    }

    /**
     * Select which component values will be used
     * for displacement along the Y axis
     * @param yChannelSelector value is among R,
     * G, B and A.
     */
    public void setYChannelSelector(ARGBChannel yChannelSelector){
        if(yChannelSelector == null){
            throw new IllegalArgumentException();
        }

        this.yChannelSelector = yChannelSelector;
    }

    /**
     * Returns the yChannelSelector
     */
    public ARGBChannel getYChannelSelector(){
        return yChannelSelector;
    }

    public RenderedImage createRendering(RenderContext rc) {
        // The source image to be displaced.
        Filter displaced = (Filter)getSources().elementAt(0);
        // The map giving the displacement.
        Filter map = (Filter)getSources().elementAt(1);

        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        // This splits out the scale from the rest of 
        // the transformation. 
        double sx = at.getScaleX();
        double sy = at.getScaleY();

        double shx = at.getShearX();
        double shy = at.getShearY();

        double tx = at.getTranslateX();
        double ty = at.getTranslateY();

        // The Scale is the "hypotonose" of the matrix vectors.
        double atScaleX = Math.sqrt(sx*sx + shy*shy);
        double atScaleY = Math.sqrt(sy*sy + shx*shx);

        // Now, apply the filter
        //
        int scaleX = (int)(scale*atScaleX);
        int scaleY = (int)(scale*atScaleY);

        // If both scale factors are zero then we don't
        // affect the source image so just return it...
        if ((scaleX == 0) && (scaleY == 0))
            return displaced.createRendering(rc);

        
        AffineTransform srcAt
            = AffineTransform.getScaleInstance(atScaleX, atScaleY);

        Shape origAOI = rc.getAreaOfInterest();
        if (origAOI == null)
            origAOI = getBounds2D();

        Rectangle2D aoiR = origAOI.getBounds2D();

        // Get a rendering from the displacement map
        PadRable mapPad = new PadRable8Bit(map, aoiR, PadMode.ZERO_PAD);

        RenderContext srcRc = new RenderContext(srcAt, aoiR, rh);
        RenderedImage mapRed = mapPad.createRendering(srcRc);

        if(mapRed == null){
            return null;
        }

        // Grow the area of interest in user space. to account for
        // the max surround needs of displacement map.
        aoiR = new Rectangle2D.Double(aoiR.getX()      - scale/2,
                                      aoiR.getY()      - scale/2,
                                      aoiR.getWidth()  + scale,
                                      aoiR.getHeight() + scale);

        Rectangle2D displacedRect = displaced.getBounds2D();
        if (aoiR.intersects(displacedRect) == false)
            return null;

        aoiR = aoiR.createIntersection(displacedRect);
        srcRc = new RenderContext(srcAt, aoiR, rh);
        RenderedImage displacedRed = displaced.createRendering(srcRc);

        if(displacedRed == null){
            return null;
        }

        mapRed = convertSourceCS(mapRed);

        //
        // Build a BufferedImages from the two sources
        //

        // Get Raster for displacedRed
        Raster displacedRas = displacedRed.getData();

        ColorModel disCM = displacedRed.getColorModel();
        // Make sure displaced is premultiplied
        disCM = GraphicsUtil.coerceData((WritableRaster)displacedRas,
                                        disCM, true);

        // Get Raster for mapRed
        Raster mapRas    = mapRed.getData();
        ColorModel mapCM = mapRed.getColorModel();
        // ensure map isn't pre-multiplied.
        GraphicsUtil.coerceData((WritableRaster)mapRas, mapCM, false);

        
        DisplacementMapOp op 
            = new DisplacementMapOp(xChannelSelector,
                                    yChannelSelector,
                                    scaleX, scaleY,
                                    mapRas);

        WritableRaster destRas = op.filter(displacedRas, null);
        destRas = destRas.createWritableTranslatedChild(0,0);

        BufferedImage destBI = new BufferedImage(disCM, destRas, 
                                                 disCM.isAlphaPremultiplied(),
                                                 null);
        //
        // Apply the non scaling part of the transform now,
        // if different from identity.
        //
        AffineTransform resAt
            = new AffineTransform(sx/atScaleX, shy/atScaleX,
                                  shx/atScaleY,  sy/atScaleY,
                                  tx, ty);

        final int minX = mapRed.getMinX();
        final int minY = mapRed.getMinY();

        CachableRed cr 
            = new BufferedImageCachableRed(destBI, minX, minY);

        if(!resAt.isIdentity()){
            cr = new AffineRed(cr, resAt, rh);
        }

        return cr;


    }

    /**
     * Returns the region of input data is is required to generate
     * outputRgn.
     * @param srcIndex  The source to do the dependency calculation for.
     * @param outputRgn The region of output you are interested in
     *  generating dependencies for.  The is given in the user coordiate
     *  system for this node.
     * @return The region of input required.  This is in the user
     * coordinate system for the source indicated by srcIndex.
     */
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn){
        // NOTE: This needs to grow the region!!!
        //       Morphology actually needs a larger area of input than
        //       it outputs.
        return super.getDependencyRegion(srcIndex, outputRgn);
    }

    /**
     * This calculates the region of output that is affected by a change
     * in a region of input.
     * @param srcIndex The input that inputRgn reflects changes in.
     * @param inputRgn the region of input that has changed, used to
     *  calculate the returned shape.  This is given in the user
     *  coordinate system of the source indicated by srcIndex.
     * @return The region of output that would be invalid given
     *  a change to inputRgn of the source selected by srcIndex.
     *  this is in the user coordinate system of this node.
     */
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn){
        // NOTE: This needs to grow the region!!!
        //       Changes in the input region affect a larger area of
        //       output than the input.
        return super.getDirtyRegion(srcIndex, inputRgn);
    }

}
