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

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.CompositeRed;
import org.apache.batik.ext.awt.image.rendered.FloodRed;

/**
 * Composites a list of images according to a single composite rule.
 * the image are applied in the order they are in the List given.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class CompositeRable8Bit
    extends    AbstractColorInterpolationRable
    implements CompositeRable, PaintRable {

    protected CompositeRule rule;

    public CompositeRable8Bit(List srcs,
                              CompositeRule rule,
                              boolean csIsLinear) {
        super(srcs);

        setColorSpaceLinear(csIsLinear);

        this.rule = rule;
    }

      /**
       * The sources to be composited togeather.
       * @param srcs The list of images to be composited by the composite rule.
       */
    public void setSources(List srcs) {
        init(srcs, null);
    }

      /**
       * Set the composite rule to use for combining the sources.
       * @param cr Composite rule to use.
       */
    public void setCompositeRule(CompositeRule cr) {
        touch();
        this.rule = rule;
    }

      /**
       * Get the composite rule in use for combining the sources.
       * @return Composite rule currently in use.
       */
    public CompositeRule getCompositeRule() {
        return this.rule;
    }

    /**
     * Should perform the equivilent action as
     * createRendering followed by drawing the RenderedImage to
     * Graphics2D, or return false.
     *
     * @param g2d The Graphics2D to draw to.
     * @return true if the paint call succeeded, false if
     *         for some reason the paint failed (in which
     *         case a createRendering should be used).
     */
    public boolean paintRable(Graphics2D g2d) {
        // This optimization only apply if we are using
        // SrcOver.  Otherwise things break...
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c))
            return false;

        // For the over mode we can just draw them in order...
        if (getCompositeRule() != CompositeRule.OVER)
            return false;

        ColorSpace crCS = getOperationColorSpace();
        ColorSpace g2dCS = GraphicsUtil.getDestinationColorSpace(g2d);
        if ((g2dCS == null) || (g2dCS != crCS)) {
            return false;
        }

        // System.out.println("drawImage : " + g2dCS +
        //                    crCS);
        Iterator i = getSources().iterator();
        while (i.hasNext()) {
            GraphicsUtil.drawImage(g2d, (Filter)i.next());
        }
        return true;
    }

    public RenderedImage createRendering(RenderContext rc) {
        if (srcs.size() == 0)
            return null;

        // Just copy over the rendering hints.
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) rh = new RenderingHints(null);

        // update the current affine transform
        AffineTransform at = rc.getTransform();

        Shape aoi = rc.getAreaOfInterest();
        Rectangle2D aoiR;
        if (aoi == null)
            aoiR = getBounds2D();
        else {
            aoiR = aoi.getBounds2D();
            Rectangle2D bounds2d = getBounds2D();
            if (bounds2d.intersects(aoiR) == false)
                return null;

            Rectangle2D.intersect(aoiR, bounds2d, aoiR);
        }

        Rectangle devRect = at.createTransformedShape(aoiR).getBounds();

        rc = new RenderContext(at, aoiR, rh);

        Vector srcs = new Vector();

        Iterator i = getSources().iterator();
        while (i.hasNext()) {
            // Get the source to work with...
            Filter filt = (Filter)i.next();

            // Get our sources image...
            RenderedImage ri = filt.createRendering(rc);
            if (ri != null) {
                CachableRed cr;
                cr = convertSourceCS(ri);
                srcs.add(cr);
            } else {

                // Blank image...
                switch (rule.getRule()) {
                case CompositeRule.RULE_IN:
                    // For Mode IN One blank image kills all output
                    // (including any "future" images to be drawn).
                    return null;

                case CompositeRule.RULE_OUT:
                    // For mode OUT blank image clears output
                    // up to this point, so ignore inputs to this point.
                    srcs.clear();
                    break;

                case CompositeRule.RULE_ARITHMETIC:
                    srcs.add(new FloodRed(devRect));
                    break;

                default:
                    // All other cases we simple pretend the image didn't
                    // exist (fully transparent image has no affect).
                    break;
                }
            }
        }

        if (srcs.size() == 0)
            return null;

        // System.out.println("Done General: " + rule);
        CachableRed cr = new CompositeRed(srcs, rule);
        return cr;
    }
}
