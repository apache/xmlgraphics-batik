/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;

/**
 * Pads image to the given Rectangle (the rect may be smaller than the
 * image in which case this is actually a crop). The rectangle is
 * specified in the user coordinate system of this Renderable.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public interface PadRable extends Filter {
      /**
       * Returns the source to be padded
       */
    public Filter getSource();

      /**
       * Sets the source to be padded
       * @param src image to offset.
       */
    public void setSource(Filter src);

      /**
       * Set the current rectangle for padding.
       * @param rect the new rectangle to use for pad.
       */
    public void setPadRect(Rectangle2D rect);

      /**
       * Get the current rectangle for padding
       * @returns Rectangle currently in use for pad.
       */
    public Rectangle2D getPadRect();

      /**
       * Set the current extension mode for pad
       * @param mode the new pad mode
       */
    public void setPadMode(PadMode mode);

      /**
       * Get the current extension mode for pad
       * @returns Mode currently in use for pad
       */
    public PadMode getPadMode();
}
