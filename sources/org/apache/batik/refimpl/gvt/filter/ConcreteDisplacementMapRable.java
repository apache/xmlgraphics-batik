/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.ARGBChannel;
import org.apache.batik.gvt.filter.CachableRed;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.DisplacementMapRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;

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

/**
 * Implements a DisplacementMap operation, which takes pixel values from
 * another image to spatially displace the input image
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com>Sheng Pei</a>
 * @version $Id$
 */
public class ConcreteDisplacementMapRable 
    extends AbstractRable
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

    public ConcreteDisplacementMapRable(List sources,
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

        AffineTransform srcAt
            = AffineTransform.getScaleInstance(atScaleX, atScaleY);

        Filter displaced = (Filter)getSources().elementAt(0);

        //
        // First, get a rendering from the first source
        //
        /*Rectangle2D aoi = displaced.getBounds2D();
        aoi.intersect(rc.getAreaOfInterest().getBounds2D(), aoi, aoi);
        aoi.setRect(aoi.getX() - scale/2,
                    aoi.getY() - scale/2,
                    aoi.getWidth() + scale,
                    aoi.getHeight() + scale);*/
        Rectangle2D aoi = rc.getAreaOfInterest().getBounds2D();
        aoi.setRect(aoi.getX() - scale/2,
                    aoi.getY() - scale/2,
                    aoi.getWidth() + scale,
                    aoi.getHeight() + scale);
        Rectangle2D displacedRect = displaced.getBounds2D();
        if (aoi.intersects(displacedRect) == false)
            return null;

        aoi = aoi.createIntersection(displacedRect);
        RenderContext srcRc = new RenderContext(srcAt, aoi, rh);
        RenderedImage displacedRed = displaced.createRendering(srcRc);

        if(displacedRed == null){
            return null;
        }

        //
        // Now, get a rendering from the displacement map
        //
        Filter map = (Filter)getSources().elementAt(1);
        PadRable mapPad 
            = new ConcretePadRable(map, aoi, PadMode.ZERO_PAD);

        RenderedImage mapRed = mapPad.createRendering(srcRc);

        if(mapRed == null){
            return null;
        }

        //
        // Build a BufferedImages from the two sources
        //

        // Build BufferedImage for displacedRed
        ColorModel cm = displacedRed.getColorModel();
        Raster rr = displacedRed.getData();
        Point pt = new Point(0, 0);
        WritableRaster wr 
            = Raster.createWritableRaster(rr.getSampleModel(),
                                          rr.getDataBuffer(),
                                          pt);
        BufferedImage displacedBI
            = new BufferedImage(cm, wr, 
                                cm.isAlphaPremultiplied(), null);

        // Build a BufferedImage for mapRed
        cm = mapRed.getColorModel();
        rr = mapRed.getData();
        wr = Raster.createWritableRaster(rr.getSampleModel(),
                                         rr.getDataBuffer(),
                                         pt);
        
        BufferedImage mapBI
            = new BufferedImage(cm, wr, 
                                cm.isAlphaPremultiplied(), null);
        
        
        //
        // Now, apply the filter
        //
        int scaleX = (int)(scale*atScaleX);
        int scaleY = (int)(scale*atScaleY);
        
        DisplacementMapOp op 
            = new DisplacementMapOp(xChannelSelector,
                                    yChannelSelector,
                                    scaleX, scaleY,
                                    mapBI);

        BufferedImage destBI = op.filter(displacedBI, null);
        
        //
        // Apply the non scaling part of the transform now,
        // if different from identity.
        //
        AffineTransform resAt
            = new AffineTransform(sx/atScaleX, shy/atScaleX,
                                  shx/atScaleY,  sy/atScaleY,
                                  tx, ty);

        final int minX = displacedRed.getMinX();
        final int minY = displacedRed.getMinY();

        CachableRed cr 
            = new ConcreteBufferedImageCachableRed(destBI, minX, minY);

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
