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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * Concrete implementation of the AffineRable interface.
 * This adjusts the input images coordinate system by a general affine
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class AffineRable8Bit 
    extends    AbstractRable
    implements AffineRable, PaintRable {

    AffineTransform affine;
    AffineTransform invAffine;

    public AffineRable8Bit(Filter src, AffineTransform affine) {
        init(src);
        setAffine(affine);
    }

    public Rectangle2D getBounds2D() {
        Filter src = getSource();
        Rectangle2D r = src.getBounds2D();
        return affine.createTransformedShape(r).getBounds2D();
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
        init(src);
    }

      /**
       * Set the affine transform.
       * @param affine the new Affine transform to apply.
       */
    public void setAffine(AffineTransform affine) {
        touch();
        this.affine = affine;
        try {
            invAffine = affine.createInverse();
        } catch (NoninvertibleTransformException e) {
            invAffine = null;
        }
    }

      /**
       * Get the Affine.
       * @return the Affine transform currently in effect.
       */
    public AffineTransform getAffine() {
        return (AffineTransform)affine.clone();
    }

    /**
     * Should perform the equivilent action as 
     * createRendering followed by drawing the RenderedImage.
     *
     * @param g2d The Graphics2D to draw to.
     * @return true if the paint call succeeded, false if
     *         for some reason the paint failed (in which 
     *         case a createRendering should be used).
     */
    public boolean paintRable(Graphics2D g2d) {
        AffineTransform at = g2d.getTransform();

        g2d.transform(getAffine());
        GraphicsUtil.drawImage(g2d, getSource());

        g2d.setTransform(at);

        return true;
    }


    public RenderedImage createRendering(RenderContext rc) {
        // Degenerate Affine no output image..
        if (invAffine == null) return null;

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // Map the area of interest to our input...
        Shape aoi = rc.getAreaOfInterest();
        if (aoi != null)
            aoi = invAffine.createTransformedShape(aoi);

        // update the current affine transform
        AffineTransform at = rc.getTransform();
        at.concatenate(affine);

        // Return what our input creates (it should factor in our affine).
        return getSource().createRendering(new RenderContext(at, aoi, rh));
    }

    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");
        if (invAffine == null)
            return null;
        return invAffine.createTransformedShape(outputRgn);
    }

    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex != 0)
            throw new IndexOutOfBoundsException("Affine only has one input");
        return affine.createTransformedShape(inputRgn);
    }

}
