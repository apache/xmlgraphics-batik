/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.geom.AffineTransform;

/**
 * Adjusts the input images coordinate system by a general Affine transform
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface AffineRable extends Filter {
      /**
       * Returns the source to be offset.
       */
    public Filter getSource();

      /**
       * Sets the source to be offset.
       * @param src image to offset.
       */
    public void setSource(Filter src);

      /**
       * Set the affine.
       * @param affine the new Affine transform for the filter.
       */
    public void setAffine(AffineTransform affine);

      /**
       * Get the current affine.
       * @return The current affine transform for the filter.
       */
    public AffineTransform getAffine();
}


