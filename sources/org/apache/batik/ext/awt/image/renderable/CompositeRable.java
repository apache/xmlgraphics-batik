/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.util.List;
import java.awt.color.ColorSpace;

/**
 * Composites a list of images according to a single composite rule.
 * the image are applied in the order they are in the List given.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface CompositeRable extends Filter {
      /**
       * The sources to be composited togeather.
       * @param srcs The list of images to be composited by the composite rule.
       */
    public void setSources(List srcs);

      /**
       * Set the composite rule to use for combining the sources.
       * @param cr Composite rule to use.
       */
    public void setCompositeRule(CompositeRule cr);

      /**
       * Get the composite rule in use for combining the sources.
       * @return Composite rule currently in use.
       */
    public CompositeRule getCompositeRule();

      /**
       * Set the colorspace to perform compositing in
       * @param cs ColorSpace to use.
       */
    public void setCompositeColorSpace(ColorSpace cs);

      /**
       * Get the colorspace to that compositing will be performed in
       * @return ColorSpace for compositing.
       */
    public ColorSpace getCompositeColorSpace();

}
