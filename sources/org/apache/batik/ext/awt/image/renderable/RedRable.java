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

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;

/**
 * RasterRable This is used to wrap a Rendered Image back into the
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class RedRable
    extends    AbstractRable {
    CachableRed src;

    public RedRable(CachableRed src) {
        super((Filter)null);
        this.src = src;
    }

    public CachableRed getSource() {
        return src;
    }

    public Object getProperty(String name) {
        return src.getProperty(name);
    }

    public String [] getPropertyNames() {
        return src.getPropertyNames();
    }

    public Rectangle2D getBounds2D() {
        return getSource().getBounds();
    }

    public RenderedImage createDefaultRendering() {
        return getSource();
    }


    public RenderedImage createRendering(RenderContext rc) {
        // System.out.println("RedRable Create Rendering: " + this);

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        Rectangle aoiR;
        if (aoi != null) 
            aoiR = aoi.getBounds();
        else
            aoiR = getBounds2D().getBounds();

        // get the current affine transform
        AffineTransform at = rc.getTransform();

        // For high quality output we should really apply a Gaussian
        // Blur when we are scaling the image down significantly this
        // helps to prevent aliasing in the result image.
        CachableRed cr = getSource();

        if (aoiR.intersects(cr.getBounds()) == false)
            return null;

        if (at.isIdentity()) {
            // System.out.println("Using as is");
            return cr;
        }

        if ((at.getScaleX() == 1.0) && (at.getScaleY() == 1.0) &&
            (at.getShearX() == 0.0) && (at.getShearY() == 0.0)) {
            int xloc = (int)(cr.getMinX()+at.getTranslateX());
            int yloc = (int)(cr.getMinY()+at.getTranslateY());
            double dx = xloc - (cr.getMinX()+at.getTranslateX());
            double dy = yloc - (cr.getMinY()+at.getTranslateY());
            if (((dx > -0.0001) && (dx < 0.0001)) &&
                ((dy > -0.0001) && (dy < 0.0001))) {
                // System.out.println("Using TranslateRed");
                return new TranslateRed(cr, xloc, yloc);
            }
        }

        // System.out.println("Using Full affine: " + at);
        return new AffineRed(cr, at, rh);
    }
}
