/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;




import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

/**
 * Concrete implementation of the AffineRable interface.
 * This adjusts the input images coordinate system by a general affine
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class AffineRable8Bit extends AbstractRable
    implements AffineRable {

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
