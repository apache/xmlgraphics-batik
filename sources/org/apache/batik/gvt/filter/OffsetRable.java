/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

/**
 * Adjusts the input images coordinate system by dx, dy.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface OffsetRable extends Filter {
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
       * Set the x offset.
       * @param dx the amount to offset in the x direction
       */
    public void setXoffset(double dx);

      /**
       * Get the x offset.
       * @return the amount to offset in the x direction
       */
    public double getXoffset();

      /**
       * Set the y offset.
       * @param dy the amount to offset in the y direction
       */
    public void setYoffset(double dy);

      /**
       * Get the y offset.
       * @return the amount to offset in the y direction
       */
    public double getYoffset();
}
