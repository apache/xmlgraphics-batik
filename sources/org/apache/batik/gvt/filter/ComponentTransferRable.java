/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

/**
 * Defines the interface expected from a component 
 * transfer operation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface ComponentTransferRable extends Filter {
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
     * Returns the transfer function for the alpha channel
     */
    public ComponentTransferFunction getAlphaFunction();

    /**
     * Sets the transfer function for the alpha channel
     */
    public void setAlphaFunction(ComponentTransferFunction alphaFunction);

    /**
     * Returns the transfer function for the red channel
     */
    public ComponentTransferFunction getRedFunction();

    /**
     * Sets the transfer function for the red channel
     */
    public void setRedFunction(ComponentTransferFunction redFunction);

    /**
     * Returns the transfer function for the green channel
     */
    public ComponentTransferFunction getGreenFunction();

    /**
     * Sets the transfer function for the green channel
     */
    public void setGreenFunction(ComponentTransferFunction greenFunction);

    /**
     * Returns the transfer function for the blue channel
     */
    public ComponentTransferFunction getBlueFunction();

    /**
     * Sets the transfer function for the blue channel
     */
    public void setBlueFunction(ComponentTransferFunction blueFunction);
}
