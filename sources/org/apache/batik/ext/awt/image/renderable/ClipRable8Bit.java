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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;

/**
 * ClipRable implementation
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class ClipRable8Bit
    extends    AbstractRable 
    implements ClipRable {

    /**
     * The node who's outline specifies our mask.
     */
    protected Shape clipPath;

    public ClipRable8Bit(Filter src, Shape clipPath) {
        super(src, null);
        setClipPath(clipPath);
    }

    /**
     * The source to be masked by the mask node.
     * @param src The Image to be masked.
     */
    public void setSource(Filter src) {
        init(src, null);
    }

    /**
     * This returns the current image being masked by the mask node.
     * @returns The image to mask
     */
    public Filter getSource() {
        return (Filter)getSources().get(0);
    }

    /**
     * Set the clip path to use.
     * The path will be filled with opaque white.
     * @param clipPath The clip path to use
     */
    public void setClipPath(Shape clipPath) {
        touch();
        this.clipPath = clipPath;
    }

      /**
       * Returns the Shape that the cliprable will use to
       * define the clip path.
       * @return The shape that defines the clip path.
       */
    public Shape getClipPath() {
        return clipPath;
    }

    /**
     * Pass-through: returns the source's bounds
     */
    public Rectangle2D getBounds2D(){
        return getSource().getBounds2D();
    }

    public RenderedImage createRendering(RenderContext rc) {

        AffineTransform usr2dev = rc.getTransform();

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null)  rh = new RenderingHints(null);

        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) aoi = getBounds2D();

        Rectangle2D rect     = getBounds2D();
        Rectangle2D clipRect = clipPath.getBounds2D();
        Rectangle2D aoiRect  = aoi.getBounds2D();
        
        if (rect.intersects(clipRect) == false)
            return null;
        Rectangle2D.intersect(rect, clipRect, rect);

        
        if (rect.intersects(aoiRect) == false)
            return null;
        Rectangle2D.intersect(rect, aoi.getBounds2D(), rect);

        Rectangle devR = usr2dev.createTransformedShape(rect).getBounds();

        if ((devR.width == 0) || (devR.height == 0))
            return null;
        
        BufferedImage bi = new BufferedImage(devR.width, devR.height,
                                             BufferedImage.TYPE_BYTE_GRAY);

        Shape devShape = usr2dev.createTransformedShape(getClipPath());
        Rectangle devAOIR;
        devAOIR = usr2dev.createTransformedShape(aoi).getBounds();

        Graphics2D g2d = GraphicsUtil.createGraphics(bi, rh);

        if (false) {
            java.util.Set s = rh.keySet();
            java.util.Iterator i = s.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                System.out.println("XXX: " + o + " -> " + rh.get(o));
            }
        }
        g2d.translate(-devR.x, -devR.y);
        g2d.setPaint(Color.white);
        g2d.fill(devShape);
        g2d.dispose();

        RenderedImage ri;
        ri = getSource().createRendering(new RenderContext(usr2dev, rect, rh));

        CachableRed cr, clipCr;
        cr = RenderedImageCachableRed.wrap(ri);
        clipCr = new BufferedImageCachableRed(bi, devR.x, devR.y);
        CachableRed ret = new MultiplyAlphaRed(cr, clipCr);

          // Pad back out to the proper size...
        ret = new PadRed(ret, devAOIR, PadMode.ZERO_PAD, rh);

        return ret;
    }
}
