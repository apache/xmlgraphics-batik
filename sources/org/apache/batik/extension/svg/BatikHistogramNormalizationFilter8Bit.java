/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.TransferFunction;
import org.apache.batik.ext.awt.image.LinearTransfer;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.ComponentTransferRed;

public class BatikHistogramNormalizationFilter8Bit
    extends      AbstractColorInterpolationRable
    implements   BatikHistogramNormalizationFilter {

    private float trim = .01f;

    /**
     * Sets the source of the operation
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns the source of the operation
     */
    public Filter getSource(){
        return (Filter)getSources().get(0);
    }

    /**
     * Returns the trim percent for this normalization.
     */
    public float getTrim() {
        return trim;
    }

    /**
     * Sets the trim percent for this normalization.
     */
    public void setTrim(float trim) {
        this.trim = trim;
        touch();
    }

    public BatikHistogramNormalizationFilter8Bit(Filter src, float trim) {
        setSource(src);
        setTrim(trim);
    }

    protected int [] histo = null;
    protected float slope, intercept;

    /**
     * This method computes the histogram of the image and
     * from that the appropriate clipping points, which leads
     * to a slope and intercept for a LinearTransfer function
     *
     * @param rc We get the set of rendering hints from rc.
     */
    public void computeHistogram(RenderContext rc) {
        if (histo != null) 
            return;

        Filter src = getSource();
        
        float scale = 100f/src.getWidth();
        float yscale = 100f/src.getHeight();

        if (scale > yscale) scale=yscale;
        
        AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        rc = new RenderContext(at, rc.getRenderingHints());
        RenderedImage histRI = getSource().createRendering(rc);

        histo = new HistogramRed(convertSourceCS(histRI)).getHistogram();

        int t = (int)(histRI.getWidth()*histRI.getHeight()*trim+0.5);
        int c, i;
        for (c=0, i=0; i<255; i++) {
            c+=histo[i];
            // System.out.println("C[" + i + "] = " + c + "  T: " + t);
            if (c>=t) break;
        }
        int low = i;
        
        for (c=0, i=255; i>0; i--) {
            c+=histo[i];
            // System.out.println("C[" + i + "] = " + c + "  T: " + t);
            if (c>=t) break;
        }
        int hi = i;

        slope = 255f/(hi-low);
        intercept = (slope*-low)/255f;
    }

    
    public RenderedImage createRendering(RenderContext rc) {
        //
        // Get source's rendered image
        //
        RenderedImage srcRI = getSource().createRendering(rc);

        if(srcRI == null)
            return null;

        computeHistogram(rc);

        SampleModel sm = srcRI.getSampleModel();
        int bands = sm.getNumBands();

        // System.out.println("Slope, Intercept: " + slope + ", " + intercept);
        TransferFunction [] tfs = new TransferFunction[bands];
        TransferFunction    tf  = new LinearTransfer(slope, intercept);
        for (int i=0; i<tfs.length; i++) 
            tfs[i] = tf;

        return new ComponentTransferRed(convertSourceCS(srcRI), tfs, null);
    }
}
