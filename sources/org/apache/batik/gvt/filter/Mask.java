/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

import java.awt.geom.Rectangle2D;

/**
 * Implements a masking operation.  This masks the source by the result
 * of converting the GraphicsNode to a mask image.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public interface Mask extends Filter {
    /**
     * The region to which this mask applies
     */
    public Rectangle2D getFilterRegion();

    /**
     * Returns the filter region to which this mask applies
     */
    public void setFilterRegion(Rectangle2D filterRegion);

    /**
     * The source to be masked by the mask node.
     * @param src The Image to be masked.
     */
    public void setSource(Filter src);
    
    /**
     * This returns the current image being masked by the mask node.
     * @returns The image to mask
     */
    public Filter getSource();
    
    /**
     * Set the masking image to that described by gn.
     * If gn is an rgba image then the alpha is premultiplied and then
     * the rgb is converted to alpha via the standard feColorMatrix
     * rgb to luminance conversion.
     * In the case of an rgb only image, just the rgb to luminance
     * conversion is performed.
     * @param gn The graphics node that defines the mask image.
     */
    public void setMaskNode(GraphicsNode gn);
    
    /**
     * Returns the Graphics node that the mask operation will use to
     * define the masking image.
     * @return The graphics node that defines the mask image.
     */
    public GraphicsNode getMaskNode();
}
