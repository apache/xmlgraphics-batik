/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.gvt.GraphicsNode;

/**
 * Implements a clip operation.  This is similar to the mask operation
 * except it only use the outline of the given Graphicsnode.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
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
       * The clipping path to that described by gn. Only the outline
       * of gn will be used (GraphicsNode.getOutline()).
       * @param gn The graphics node that defines the clipping path.
       */
    public void setClipNode(GraphicsNode gn);

      /**
       * Returns the Graphics node that the clip operation will use to
       * define the clipping path.
       * @return The graphics node that defines the clipping path.
       */
    public GraphicsNode getClipNode();
}
