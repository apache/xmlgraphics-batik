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

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

/**
 * Concrete implementation of the PadRable interface.
 * This pads the image to a specified rectangle in user coord system.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class PadRable8Bit extends AbstractRable
    implements PadRable, PaintRable {

    PadMode           padMode;
    Rectangle2D       padRect;

    public PadRable8Bit(Filter src,
                        Rectangle2D padRect,
                        PadMode     padMode) {
        super.init(src, null);
        this.padRect = padRect;
        this.padMode = padMode;
    }

    /**
     * Returns the source to be affine.
     */
    public Filter getSource() {
        return (Filter)srcs.get(0);
    }

    /**
     * Sets the source to be affine.
     * @param src image to affine.
     */
    public void setSource(Filter src) {
        super.init(src, null);
    }

    public Rectangle2D getBounds2D() {
        return (Rectangle2D)padRect.clone();
    }

    /**
     * Set the current rectangle for padding.
     * @param rect the new rectangle to use for pad.
     */
    public void setPadRect(Rectangle2D rect) {
        touch();
        this.padRect = rect;
    }

    /**
     * Get the current rectangle for padding
     * @returns Rectangle currently in use for pad.
     */
    public Rectangle2D getPadRect() {
        return (Rectangle2D)padRect.clone();
    }

    /**
     * Set the current extension mode for pad
     * @param mode the new pad mode
     */
    public void setPadMode(PadMode padMode) {
        touch();
        this.padMode = padMode;
    }

    /**
     * Get the current extension mode for pad
     * @returns Mode currently in use for pad
     */
    public PadMode getPadMode() {
        return padMode;
    }

    /**
     * Should perform the equivilent action as 
     * createRendering followed by drawing the RenderedImage to 
     * Graphics2D, or return false.
     *
     * @param g2d The Graphics2D to draw to.
     * @return true if the paint call succeeded, false if
     *         for some reason the paint failed (in which 
     *         case a createRendering should be used).
     */
    public boolean paintRable(Graphics2D g2d) {
        // This optimization only apply if we are using
        // SrcOver.  Otherwise things break...
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c))
            return false;

        if (getPadMode() != PadMode.ZERO_PAD)
            return false;

        Rectangle2D padBounds = getPadRect();

        Shape clip = g2d.getClip();
        g2d.clip(padBounds);
        GraphicsUtil.drawImage(g2d, getSource());
        g2d.setClip(clip);
        return true;
    }

    public RenderedImage createRendering(RenderContext rc) {
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Filter src = getSource();
        Shape aoi = rc.getAreaOfInterest();

        if(aoi == null){
            aoi = getBounds2D();
        }

        AffineTransform usr2dev = rc.getTransform();

        // We only depend on our source for stuff that is inside
        // our bounds and his bounds (remember our bounds may be
        // tighter than his in one or both directions).
        Rectangle2D srect = src.getBounds2D();
        Rectangle2D rect  = getBounds2D();
        Rectangle2D arect = aoi.getBounds2D();
        
        // System.out.println("Rects Src:" + srect +
        //                    "My: " + rect +
        //                    "AOI: " + arect);
        if (arect.intersects(rect) == false)
            return null;
        Rectangle2D.intersect(arect, rect, arect);

        RenderedImage ri = null;
        if (arect.intersects(srect) == true) {
            srect = (Rectangle2D)srect.clone();
            Rectangle2D.intersect(srect, arect, srect);
            
            RenderContext srcRC = new RenderContext(usr2dev, srect, rh);
            ri = src.createRendering(srcRC);

            // System.out.println("Pad filt: " + src + " R: " +
            //                    src.getBounds2D());
        }

        // No source image so create a 1,1 transparent one...
        if (ri == null)
            ri = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        // org.apache.batik.test.gvt.ImageDisplay.showImage("Paded: ", ri);
        // System.out.println("RI: " + ri + " R: " + srect);

        CachableRed cr = GraphicsUtil.wrap(ri);
            
        arect = usr2dev.createTransformedShape(arect).getBounds2D();
            
        // System.out.println("Pad rect : " + arect);
        // Use arect (my bounds intersect area of interest)
        cr = new PadRed(cr, arect.getBounds(), padMode, rh);
        return cr;
    }

    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");

        // We only depend on our source for stuff that is inside
        // our bounds and his bounds (remember our bounds may be
        // tighter than his in one or both directions).
        Rectangle2D srect = getSource().getBounds2D();
        if (srect.intersects(outputRgn) == false)
            return new Rectangle2D.Float();
        Rectangle2D.intersect(srect, outputRgn, srect);

        Rectangle2D bounds = getBounds2D();
        if (srect.intersects(bounds) == false)
            return new Rectangle2D.Float();
        Rectangle2D.intersect(srect, bounds, srect);
        return srect;
    }

    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");

        inputRgn = (Rectangle2D)inputRgn.clone();
        Rectangle2D bounds = getBounds2D();
        // Changes in the input region don't propogate outside our
        // bounds.
        if (inputRgn.intersects(bounds) == false)
            return new Rectangle2D.Float();
        Rectangle2D.intersect(inputRgn, bounds, inputRgn);
        return inputRgn;
    }

}
