/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.gvt.filter.FilterResRable;
import org.apache.batik.gvt.filter.Filter;

/**
 * Interface for implementing filter resolution.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteFilterResRable extends AbstractRable 
    implements FilterResRable{

    /**
     * Filter resolution along the x-axis
     */
    private int filterResolutionX = -1;

    /**
     * Filter resolution along the y-axis
     */
    private int filterResolutionY = -1;

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
     * 
     */
    public RenderedImage createRendering(RenderContext renderContext){
        // Get user space to device space transform
        AffineTransform usr2dev = renderContext.getTransform();
        if(usr2dev == null){
            usr2dev = new AffineTransform();
        }
        
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
        float filterResolutionX = this.filterResolutionX;
        float filterResolutionY = this.filterResolutionY;

        // Find out the renderable area
        Rectangle2D imageRect = getBounds2D();
        Rectangle   devRect;
        devRect = usr2dev.createTransformedShape(imageRect).getBounds();

        if(filterResolutionX > 1) {
            // Now, compare the devRect with the filter
            // resolution hints
            float scaleX = 1;
            float scaleY = 1;
            if(filterResolutionX < devRect.width){
                scaleX = filterResolutionX / (float)devRect.width;
            }

            if(filterResolutionY != 0){
                if(filterResolutionY < 0){
                    filterResolutionY = scaleX*(float)devRect.height;
                }

                if(filterResolutionY < devRect.height) {
                    scaleY = filterResolutionY / (float)devRect.height;
                }
                
                // Only resample if either scaleX or scaleY is
                // smaller than 1
                RenderableImage localSource = getSource();
                RenderContext localRenderContext = renderContext;
                
                if((scaleX < 1) || (scaleY < 1)){
                    // System.out.println("filterRes X " + filterResolutionX + 
                    //                    " Y : " + filterResolutionY);

                    scaleX = scaleX < scaleY ? scaleX : scaleY;
                    scaleY = scaleX;

                    //
                    // Create a rendering that will be less than
                    // or equal to filterResolutionX by filterResolutionY.
                    //
                    AffineTransform newUsr2Dev 
                        = AffineTransform.getScaleInstance(scaleX, scaleY);
                    
                    newUsr2Dev.concatenate(usr2dev);
                    
                    //
                    // Create a new RenderingContext
                    //
                    RenderContext newRenderContext 
                        = (RenderContext)renderContext.clone();
                    newRenderContext.setTransform(newUsr2Dev);
                    
                    //
                    // Now, use an AffineRable that will apply the
                    // resampling
                    //
                    AffineTransform resampleTxf 
                        = AffineTransform.getScaleInstance(1/scaleX, 1/scaleY);
                    
                    RenderedImage result = null;
                    result = localSource.createRendering(newRenderContext);
                    if (result != null)
                        result = new AffineRed
                            (ConcreteRenderedImageCachableRed.wrap(result),
                             resampleTxf, renderContext.getRenderingHints());

                    return result;
                }

                return localSource.createRendering(localRenderContext);
            }
        }

        return null;
    }
}

