/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.image.GraphicsUtil;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.ProfileRed;

/**
 * Implements the interface expected from a color matrix
 * operation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ProfileRable extends  AbstractRable{
    
    private ICCColorSpaceExt colorSpace;

    /**
     * Instances should be built through the static
     * factory methods
     */
    public ProfileRable(Filter src, ICCColorSpaceExt colorSpace){
        super(src);
        this.colorSpace = colorSpace;
    }

    /**
     * Sets the source of the blur operation
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns the source of the blur operation
     */
    public Filter getSource(){
        return (Filter)getSources().get(0);
    }

    /**
     * Sets the ColorSpace of the Profile operation
     */
    public void setColorSpace(ICCColorSpaceExt colorSpace){
        this.colorSpace = colorSpace;
    }

    /**
     * Returns the ColorSpace of the Profile operation
     */
    public ICCColorSpaceExt getColorSpace(){
        return colorSpace;
    }

    public RenderedImage createRendering(RenderContext rc) {
        //
        // Get source's rendered image
        //
        RenderedImage srcRI = getSource().createRendering(rc);

        if(srcRI == null)
            return null;

        CachableRed srcCR = GraphicsUtil.wrap(srcRI);
        return new ProfileRed(srcCR, colorSpace);
    }
}
