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
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FloodRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

/**
 * Concrete implementation of the FloodRable interface.
 * This fills the input image with a given flood paint
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */

public class FloodRable8Bit extends AbstractRable
    implements FloodRable {

    /**
     * Paint to use to flood the floodRegion
     */
    Paint floodPaint;

    /**
     * Region to fill with floodPaint
     */
    Rectangle2D floodRegion;

    /**
     * @param floodRegion region to be filled with floodPaint
     * @param floodPaint paint to use to flood the floodRegion
     */
    public FloodRable8Bit(Rectangle2D floodRegion, 
                              Paint floodPaint) {
        setFloodPaint(floodPaint);
        setFloodRegion(floodRegion);
    }

    /**
     * Set the flood fill paint
     * @param paint The paint to use when flood filling the input image
     */
    public void setFloodPaint(Paint paint) {
        touch();
        if (paint == null) {
            // create a transparent flood fill
            floodPaint = new Color(0, 0, 0, 0);
        } else {
            floodPaint = paint;
        }
    }

    /**
     * Get the flood fill paint.
     * @return the paint used to flood fill the input image
     */
    public Paint getFloodPaint() {
        // Paint is immutable, we can return it
        return floodPaint;
    }

    public Rectangle2D getBounds2D() {

        return (Rectangle2D)floodRegion.clone();
    }

    /**
     * Returns the flood region
     */
    public Rectangle2D getFloodRegion(){
        return (Rectangle2D)floodRegion.clone();
    }

    /**
     * Sets the flood region
     */
    public void setFloodRegion(Rectangle2D floodRegion){
        if(floodRegion == null){
            throw new IllegalArgumentException();
        }

        touch();
        this.floodRegion = floodRegion;
    }

    /**
     * Create a RenderedImage that is filled with the current
     * flood fill paint
     * @param rc The current render context
     * @return A RenderedImage with the flood fill
     */

    public RenderedImage createRendering(RenderContext rc) {
        // Get user space to device space transform
        AffineTransform usr2dev = rc.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }

        Rectangle2D imageRect = getBounds2D();

        // Now, take area of interest into account. It is
        // defined in user space.
        Rectangle2D userAOI;
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi     = imageRect;
            userAOI = imageRect;
        } else {
            userAOI = aoi.getBounds2D();

            // No intersection with the area of interest so return null..
            if (imageRect.intersects(userAOI) == false) 
                return null;

            // intersect the filter area and the AOI in user space
            Rectangle2D.intersect(imageRect, userAOI, userAOI);
        }

        // The rendered area is the interesection of the
        // user space renderable area and the user space AOI bounds
        final Rectangle renderedArea
            = usr2dev.createTransformedShape(userAOI).getBounds();

        if ((renderedArea.width <= 0) || (renderedArea.height <= 0)) {
            // If there is no intersection, return null
            return null;
        }

        CachableRed cr;
        cr = new FloodRed(renderedArea, getFloodPaint());
        // We use a pad because while FloodRed will advertise it's
        // bounds based on renderedArea it will actually provide the
        // flood data anywhere.
        cr = new PadRed(cr, renderedArea, PadMode.ZERO_PAD, null);

        return cr;
    }
}
