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

package org.apache.batik.extension.svg;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * RasterRable This is used to wrap a Rendered Image back into the
 * RenderableImage world.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class MultiResGraphicsNode
    extends AbstractGraphicsNode implements SVGConstants {

    SoftReference [] srcs;
    Element       [] srcElems;
    Dimension     [] minSz;
    Dimension     [] maxSz;
    Rectangle2D      bounds;

    BridgeContext  ctx;

    Element multiImgElem;

    public MultiResGraphicsNode(Element multiImgElem,
                                Rectangle2D  bounds,
                                Element   [] srcElems,
                                Dimension [] minSz,
                                Dimension [] maxSz,
                                BridgeContext ctx) {

        this.multiImgElem = multiImgElem;
        this.srcElems     = new Element  [srcElems.length];
        this.minSz        = new Dimension[srcElems.length];
        this.maxSz        = new Dimension[srcElems.length];
        this.ctx          = ctx;

        for (int i=0; i<srcElems.length; i++) {
            this.srcElems[i] = srcElems[i];
            this.minSz[i]    = minSz[i];
            this.maxSz[i]    = maxSz[i];
        }

        this.srcs = new SoftReference[srcElems.length];
        this.bounds = bounds;
    }

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        // get the current affine transform
        AffineTransform at = g2d.getTransform();

        double scx = Math.sqrt(at.getShearY()*at.getShearY()+
                               at.getScaleX()*at.getScaleX());
        double scy = Math.sqrt(at.getShearX()*at.getShearX()+
                               at.getScaleY()*at.getScaleY());

        GraphicsNode gn = null;
        int idx =-1;
        double w = bounds.getWidth()*scx;
        double minDist = calcDist(w, minSz[0], maxSz[0]);
        int    minIdx = 0;
        // System.err.println("Width: " + w);
        for (int i=0; i<minSz.length; i++) {
            double dist = calcDist(w, minSz[i], maxSz[i]);
            // System.err.println("Dist: " + dist);
            if (dist < minDist) {
                minDist = dist;
                minIdx = i;
            } 
                
            if (((minSz[i] == null) || (w >= minSz[i].width)) &&
                ((maxSz[i] == null) || (w <= maxSz[i].width))) {
                // We have a range match
                // System.err.println("Match: " + i + " " + 
                //                    minSz[i] + " -> " + maxSz[i]);
                if ((idx == -1) || (minIdx == i)) {
                    idx = i;
                }
            }
        }

        if (idx == -1)
            idx = minIdx;
        gn = getGraphicsNode(idx);

        if (gn == null) return;
        // This makes sure that the image 'pushes out' to it's pixel
        // bounderies.
        Rectangle2D gnBounds = gn.getBounds();
        double gnDevW = gnBounds.getWidth()*scx;
        double gnDevH = gnBounds.getHeight()*scy;
        double gnDevX = gnBounds.getX()*scx;
        double gnDevY = gnBounds.getY()*scy;
        double gnDevX0, gnDevX1, gnDevY0, gnDevY1;
        if (gnDevW < 0) {
            gnDevX0 = gnDevX+gnDevW;
            gnDevX1 = gnDevX;
        } else {
            gnDevX0 = gnDevX;
            gnDevX1 = gnDevX+gnDevW;
        }
        if (gnDevH < 0) {
            gnDevY0 = gnDevY+gnDevH;
            gnDevY1 = gnDevY;
        } else {
            gnDevY0 = gnDevY;
            gnDevY1 = gnDevY+gnDevH;
        }
        // This calculate the width/height in pixels given 'worst
        // case' assessment.
        gnDevW = (int)(Math.ceil(gnDevX1)-Math.floor(gnDevX0));
        gnDevH = (int)(Math.ceil(gnDevY1)-Math.floor(gnDevY0));
        scx = (gnDevW/gnBounds.getWidth())/scx;
        scy = (gnDevH/gnBounds.getHeight())/scy;

        // This scales things up slightly so our edges fall on device
        // pixel boundries.
        AffineTransform nat = g2d.getTransform();
        nat = new AffineTransform(nat.getScaleX()*scx, nat.getShearY()*scx,
                                 nat.getShearX()*scy, nat.getScaleY()*scy,
                                 nat.getTranslateX(), nat.getTranslateY());
        g2d.setTransform(nat);

        // double sx = bounds.getWidth()/sizes[idx].getWidth();
        // double sy = bounds.getHeight()/sizes[idx].getHeight();
        // System.err.println("Scale: [" + sx + ", " + sy + "]");

        gn.paint(g2d);
    }

    // This function can be tweaked to any extent.  This is a very
    // simple measure of 'goodness'.  It has two main flaws as is,
    // mostly in regards to distance calc with 'unbounded' ranges.
    // First it doesn't punish if the distance is the wrong way on the
    // unbounded range (so over a max by 10 is the same as under a max
    // by 10) this is compensated by the absolute preference for
    // matches 'in range' above.  The other issue is that unbounded
    // ranages tend to 'win' when the value is near the boundry point
    // since they use distance from the boundry point rather than the
    // middle of the range.  As it is this seems to meet all the
    // requirements of the SVG specification however.
    public double calcDist(double loc, Dimension min, Dimension max) {
        if (min == null) {
            if (max == null) 
                return 10E10; // very large number.
            else
                return Math.abs(loc-max.width);
        } else {
            if (max == null) 
                return Math.abs(loc-min.width);
            else {
                double mid = (max.width+min.width)/2.0;
                return Math.abs(loc-mid);
            }
        }
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        return bounds;
    }

    public Rectangle2D getGeometryBounds(){
        return bounds;
    }

    public Rectangle2D getSensitiveBounds(){
        return bounds;
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        return bounds;
    }

    public GraphicsNode getGraphicsNode(int idx) {
        // System.err.println("Getting: " + idx);
        if (srcs[idx] != null) {
            Object o = srcs[idx].get();
            if (o != null) 
                return (GraphicsNode)o;
        }
        
        try {
            GVTBuilder builder = ctx.getGVTBuilder();
            GraphicsNode gn;
            gn = builder.build(ctx, srcElems[idx]);
            srcs[idx] = new SoftReference(gn);
            return gn;
        } catch (Exception ex) { ex.printStackTrace();  }

        return null;
    }
}    

