/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.DiffuseLightingRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.AffineRed;

/**
 * Implementation of the DiffuseLightRable interface.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class DiffuseLightingRable8Bit 
    extends AbstractRable
    implements DiffuseLightingRable {
    /**
     * Surface Scale
     */
    private double surfaceScale;

    /**
     * Diffuse constant
     */
    private double kd;

    /**
     * Light used for the diffuse lighting computations
     */
    private Light light;

    /**
     * Lit Area
     */
    private Rectangle2D litRegion;

    public DiffuseLightingRable8Bit(Filter src,
                                        Rectangle2D litRegion,
                                        Light light,
                                        double kd,
                                        double surfaceScale){
        super(src, null);
        setLight(light);
        setKd(kd);
        setSurfaceScale(surfaceScale);
        setLitRegion(litRegion);
    }

    /**
     * Returns the source to be filtered
     */
    public Filter getSource(){
        return (Filter)getSources().get(0);
    }

    /**
     * Sets the source to be filtered
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns this filter's bounds
     */
    public Rectangle2D getBounds2D(){
        return (Rectangle2D)(litRegion.clone());
    }

    /**
     * Returns this filter's litRegion
     */
    public Rectangle2D getLitRegion(){
        return getBounds2D();
    }

    /**
     * Set this filter's litRegion
     */
    public void setLitRegion(Rectangle2D litRegion){
        this.litRegion = litRegion;
    }

    /**
     * @return Light object used for the diffuse lighting
     */
    public Light getLight(){
        return light;
    }

    /**
     * @param New Light object
     */
    public void setLight(Light light){
        this.light = light;
    }

    /**
     * @return surfaceScale
     */
    public double getSurfaceScale(){
        return surfaceScale;
    }

    /**
     * Sets the surface scale
     */
    public void setSurfaceScale(double surfaceScale){
        this.surfaceScale = surfaceScale;
    }

    /**
     * @return diffuse constant, or kd.
     */
    public double getKd(){
        return kd;
    }

    /**
     * Sets the diffuse constant, or kd
     */
    public void setKd(double kd){
        this.kd = kd;
    }

    static final boolean SCALE_RESULT=true;

    public RenderedImage createRendering(RenderContext rc){
        Rectangle2D aoi = rc.getAreaOfInterest().getBounds2D();
        if(aoi == null){
            aoi = getBounds2D();
        }

        aoi.intersect(aoi, getBounds2D(), aoi);

        AffineTransform at = rc.getTransform();
        Rectangle bounds = at.createTransformedShape(aoi).getBounds();

        if(bounds.width == 0 || bounds.height == 0){
            return null;
        }

        //
        // DiffuseLightingRed only operates on a scaled space.
        // The following extracts the scale portion of the 
        // user to device transform
        //
        // The source is rendered with the scale-only transform
        // and the rendered result is used as a bumpMap for the 
        // DiffuseLightingRed filter.
        //
        double sx = at.getScaleX();
        double sy = at.getScaleY();

        double shx = at.getShearX();
        double shy = at.getShearY();

        double tx = at.getTranslateX();
        double ty = at.getTranslateY();

         // The Scale is the "hypotonose" of the matrix vectors.
        double scaleX = Math.sqrt(sx*sx + shy*shy);
        double scaleY = Math.sqrt(sy*sy + shx*shx);

        if(scaleX == 0 || scaleY == 0){
            // Non invertible transform
            return null;
        }
        
        if (SCALE_RESULT) {
            scaleX = 1;
            scaleY = 1;
        }
        AffineTransform scale =
            AffineTransform.getScaleInstance(scaleX, scaleY);

        // Build texture from the source
        rc = (RenderContext)rc.clone();
        rc.setAreaOfInterest(aoi);
        rc.setTransform(scale);

        // System.out.println("scaleX / scaleY : " + scaleX + "/" + scaleY);

        RenderingHints rh = rc.getRenderingHints();
        bounds = scale.createTransformedShape(aoi).getBounds();

        PadRed texture 
            = new PadRed(RenderedImageCachableRed.wrap(getSource().createRendering(rc)),
                         bounds,
                         PadMode.ZERO_PAD,
                         rh);

        BumpMap bumpMap = new BumpMap(texture, surfaceScale, scaleX, scaleY);

        DiffuseLightingRed diffuseRed =
            new DiffuseLightingRed(kd,
                                   light,
                                   bumpMap,
                                   bounds,
                                   1/scaleX, 1/scaleY);

        // Return sheared/rotated tiled image
        AffineTransform shearAt =
            new AffineTransform(sx/scaleX, shy/scaleX,
                                shx/scaleY, sy/scaleY,
                                tx, ty);
        
        if(shearAt.isIdentity()){
            // System.out.println("Scale only transform");
            return diffuseRed;
        }
       
        // System.out.println("Transform has translate and/or shear and rotate");
        CachableRed cr 
            = new RenderedImageCachableRed(diffuseRed);

        cr = new AffineRed(cr, shearAt, rh);

        return cr;
        
    }
}

