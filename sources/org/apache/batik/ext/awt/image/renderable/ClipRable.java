/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;

/**
 * Implements a clip operation.  This is similar to the mask operation
 * except it uses a '1 bit' mask (it's normally anti-aliased, but
 * shouldn't have any fluctions in side the outline of the shape.).
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public interface ClipRable extends Filter {

      /**
       * The source to be clipped by the outline of the clip node.
       * @param src The Image to be clipped.
       */
    public void setSource(Filter src);

      /**
       * This returns the current image being clipped by the clip node.
       * @returns The image to clip
       */
    public Filter getSource();

    /**
     * Set the clip path to use.
     * The path will be filled with opaque white, to define the
     * the clipping mask.
     * @param clipPath The clip path to use
     */
    public void setClipPath(Shape clipPath);

      /**
       * Returns the Shape that the Clip will use to
       * define the clip path.
       * @return The shape that defines the clip path.
       */
    public Shape getClipPath();
}
