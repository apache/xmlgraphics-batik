/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.util.List;
import java.util.Map;
import java.awt.color.ColorSpace;
import java.awt.image.RenderedImage;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

/**
 * This is an abstract base class that adds the ability to specify the
 * Color Space that the operation should take place in (linear sRGB or 
 * gamma corrected sRBG).
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public abstract class AbstractColorInterpRable extends AbstractRable {

    /**
     * Indicates if the operation should be done in linear or gamma
     * corrected sRGB.
     */
    protected boolean csLinear = true;

    /**
     * void constructor. The subclass must call one of the
     * flavors of init before the object becomes usable.
     * This is useful when the proper parameters to the init
     * method need to be computed in the subclasses constructor.  */
    protected AbstractColorInterpRable() {
        super();
    }

    /**
     * Construct an Abstract Rable from src.
     * @param src will be the first (and only) member of the srcs
     * Vector. The bounds of src are also used to set the bounds of
     * this renderable.
     */
    protected AbstractColorInterpRable(Filter src) {
        super(src);
    }

    /**
     * Construct an Abstract Rable from src and props.
     * @param src will also be set as the first (and only) member of
     * the srcs Vector.
     * @param props use to initialize the properties on this renderable image.
     */
    protected AbstractColorInterpRable(Filter src, Map props) {
        super(src, props);
    }

    /**
     * Construct an Abstract Rable from a list of sources.
     * @param srcs This is used to initialize the srcs Vector.
     * The bounds of this renderable will be the union of the bounds
     * of all the sources in srcs.  All the members of srcs must be
     * CachableRable otherwise an error will be thrown.
     */
    protected AbstractColorInterpRable(List srcs) {
        super(srcs);
    }

    /**
     * Construct an Abstract Rable from a list of sources, and bounds.
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be CachableRable otherwise an error
     * will be thrown.
     * @param props use to initialize the properties on this renderable image.
     */
    protected AbstractColorInterpRable(List srcs, Map props) {
        super(srcs, props);
    }

    /**
     * Returns true if this operation is to be performed in
     * the linear sRGB colorspace, returns false if the
     * operation is performed in gamma corrected sRGB.
     */
    public boolean isCSLinear() { return csLinear; }

    /**
     * Sets the colorspace the operation will be performed in.
     * @param csLinear if true this operation will be performed in the
     * linear sRGB colorspace, if false the operation will be performed in
     * gamma corrected sRGB.  
     */
    public void setCSLinear(boolean csLinear) {
        touch();
        this.csLinear = csLinear;
    }

    public ColorSpace getOperationColorSpace() {
        if (csLinear) 
            return ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
        else 
            return ColorSpace.getInstance(ColorSpace.CS_sRGB);
    }

    protected CachableRed convertSourceCS(CachableRed cr) {
        if (csLinear)
            return GraphicsUtil.convertToLsRGB(cr);
        else
            return GraphicsUtil.convertTosRGB(cr);
    }

    protected CachableRed convertSourceCS(RenderedImage ri) {
        return convertSourceCS(GraphicsUtil.wrap(ri));
    }
}
