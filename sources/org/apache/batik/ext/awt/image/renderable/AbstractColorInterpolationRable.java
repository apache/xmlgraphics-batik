/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractColorInterpolationRable extends AbstractRable {

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
    protected AbstractColorInterpolationRable() {
        super();
    }

    /**
     * Construct an Abstract Rable from src.
     * @param src will be the first (and only) member of the srcs
     * Vector. The bounds of src are also used to set the bounds of
     * this renderable.
     */
    protected AbstractColorInterpolationRable(Filter src) {
        super(src);
    }

    /**
     * Construct an Abstract Rable from src and props.
     * @param src will also be set as the first (and only) member of
     * the srcs Vector.
     * @param props use to initialize the properties on this renderable image.
     */
    protected AbstractColorInterpolationRable(Filter src, Map props) {
        super(src, props);
    }

    /**
     * Construct an Abstract Rable from a list of sources.
     * @param srcs This is used to initialize the srcs Vector.
     * The bounds of this renderable will be the union of the bounds
     * of all the sources in srcs.  All the members of srcs must be
     * CachableRable otherwise an error will be thrown.
     */
    protected AbstractColorInterpolationRable(List srcs) {
        super(srcs);
    }

    /**
     * Construct an Abstract Rable from a list of sources, and bounds.
     * @param srcs This is used to initialize the srcs Vector.  All
     * the members of srcs must be CachableRable otherwise an error
     * will be thrown.
     * @param props use to initialize the properties on this renderable image.
     */
    protected AbstractColorInterpolationRable(List srcs, Map props) {
        super(srcs, props);
    }

    /**
     * Returns true if this operation is to be performed in
     * the linear sRGB colorspace, returns false if the
     * operation is performed in gamma corrected sRGB.
     */
    public boolean isColorSpaceLinear() { return csLinear; }

    /**
     * Sets the colorspace the operation will be performed in.
     * @param csLinear if true this operation will be performed in the
     * linear sRGB colorspace, if false the operation will be performed in
     * gamma corrected sRGB.
     */
    public void setColorSpaceLinear(boolean csLinear) {
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
