/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.extension.svg;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.LinearTransfer;
import org.apache.batik.ext.awt.image.TransferFunction;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
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
