/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * Interface for implementing filter resolution.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterResRable8Bit extends AbstractRable 
    implements FilterResRable{

    /**
     * Filter resolution along the x-axis
     */
    private int filterResolutionX = -1;

    /**
     * Filter resolution along the y-axis
     */
    private int filterResolutionY = -1;

    public FilterResRable8Bit() {
        // System.out.println("Using FilterResRable8bit...");
    }
        

    /**
     * Returns the source to be cropped.
     */
    public Filter getSource() {
        return (Filter)srcs.get(0);
    }
    
    /**
     * Sets the source to be cropped
     * @param src image to offset.
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns the resolution along the X axis.
     */
    public int getFilterResolutionX(){
        return filterResolutionX;
    }

    /**
     * Sets the resolution along the X axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     * Negative values are illegal.
     */
    public void setFilterResolutionX(int filterResolutionX){
        if(filterResolutionX < 0){
            throw new IllegalArgumentException();
        }
        this.filterResolutionX = filterResolutionX;
    }
    
    /**
     * Returns the resolution along the Y axis.
     */
    public int getFilterResolutionY(){
        return filterResolutionY;
    }

    /**
     * Sets the resolution along the Y axis, i.e., the maximum
     * size for intermediate images along that axis.
     * If the Y-value is less than zero, the scale applied to 
     * the rendered images is computed to preserve the image's aspect ratio
     */
    public void setFilterResolutionY(int filterResolutionY){
        this.filterResolutionY = filterResolutionY;
    }
    

    /**
     * Cached Rendered image at filterRes.
     */
    Reference resRed = null;
    float     resScale = 0;

    private float getResScale() {
        return resScale;
    }

    private RenderedImage getResRed(RenderingHints hints) {
        Rectangle2D imageRect = getBounds2D();
        double resScaleX = getFilterResolutionX()/imageRect.getWidth();
        double resScaleY = getFilterResolutionY()/imageRect.getHeight();

        
        // System.out.println("filterRes X " + filterResolutionX + 
        //                    " Y : " + filterResolutionY);

        float resScale = (float)Math.min(resScaleX, resScaleY);

        RenderedImage ret;
        if (resScale == this.resScale) {
            // System.out.println("Matched");
            ret = (RenderedImage)resRed.get();
            if (ret != null)
                return ret;
        }

        AffineTransform resUsr2Dev;
        resUsr2Dev = AffineTransform.getScaleInstance(resScale, resScale);
        
        //
        // Create a new RenderingContext
        //
        RenderContext newRC = new RenderContext(resUsr2Dev, null, hints);

        ret = getSource().createRendering(newRC);

        // This is probably justified since the whole reason to use
        // The filterRes attribute is because the filter chain is
        // expensive, otherwise you should let it evaluate at
        // screen resolution always - right?
        ret = new TileCacheRed(GraphicsUtil.wrap(ret));
        this.resScale = resScale;
        this.resRed   = new SoftReference(ret);

        return ret;
    }

    

    /**
     * 
     */
    public RenderedImage createRendering(RenderContext renderContext) {
        // Get user space to device space transform
        AffineTransform usr2dev = renderContext.getTransform();
        if(usr2dev == null){
            usr2dev = new AffineTransform();
        }

        RenderingHints hints = renderContext.getRenderingHints();
        
        // As per specification, a value of zero for the 
        // x-axis or y-axis causes the filter to produce
        // nothing.
        // The processing is done as follows:
        // + if the x resolution is zero, this is a no-op
        //   else compute the x scale.
        // + if the y resolution is zero, this is a no-op
        //   else compute the y resolution from the x scale
        //   and compute the corresponding y scale.
        // + if the y or x scale is less than one, insert 
        //   an AffineRable.
        //   Else, return the source as is.
        int filterResolutionX = getFilterResolutionX();
        int filterResolutionY = getFilterResolutionY();
        // System.out.println("FilterResRable: " + filterResolutionX + "x" +
        //                    filterResolutionY);

        if ((filterResolutionX <= 0) || (filterResolutionY == 0))
            return null;
        
        // Find out the renderable area
        Rectangle2D imageRect = getBounds2D();
        Rectangle   devRect;
        devRect = usr2dev.createTransformedShape(imageRect).getBounds();

        // Now, compare the devRect with the filter
        // resolution hints
        float scaleX = 1;
        if(filterResolutionX < devRect.width)
            scaleX = filterResolutionX / (float)devRect.width;

        float scaleY = 1;
        if(filterResolutionY < 0)
            scaleY = scaleX;
        else if(filterResolutionY < devRect.height)
            scaleY = filterResolutionY / (float)devRect.height;

        // Only resample if either scaleX or scaleY is
        // smaller than 1
        if ((scaleX >= 1) && (scaleY >= 1))
            return getSource().createRendering(renderContext);

        // System.out.println("Using Fixed Resolution...");

        // Using fixed resolution image since we need an image larger
        // than this.
        RenderedImage resRed   = getResRed(hints);
        float         resScale = getResScale();

        AffineTransform residualAT;
        residualAT = new AffineTransform(usr2dev.getScaleX()/resScale,
                                         usr2dev.getShearY()/resScale,
                                         usr2dev.getShearX()/resScale,
                                         usr2dev.getScaleY()/resScale,
                                         usr2dev.getTranslateX(),
                                         usr2dev.getTranslateY());

        // org.ImageDisplay.showImage("AT: " + newUsr2Dev, result);

        return new AffineRed(GraphicsUtil.wrap(resRed), residualAT, hints);
    }
}

