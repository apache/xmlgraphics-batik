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
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;

/**
 * Interface for implementing filter resolution.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class FilterResRable8Bit extends AbstractRable 
    implements FilterResRable, PaintRable {

    /**
     * Filter resolution along the x-axis
     */
    private int filterResolutionX = -1;

    /**
     * Filter resolution along the y-axis
     */
    private int filterResolutionY = -1;

    public FilterResRable8Bit() {
        // System.out.println("Using FilterResRable8bit...");
    }
        

    public FilterResRable8Bit(Filter src, int filterResX, int filterResY) {
        init(src, null);
        setFilterResolutionX(filterResX);
        setFilterResolutionY(filterResY);
    }

    /**
     * Returns the source to be cropped.
     */
    public Filter getSource() {
        return (Filter)srcs.get(0);
    }
    
    /**
     * Sets the source to be cropped
     * @param src image to offset.
     */
    public void setSource(Filter src){
        init(src, null);
    }

    /**
     * Returns the resolution along the X axis.
     */
    public int getFilterResolutionX(){
        return filterResolutionX;
    }

    /**
     * Sets the resolution along the X axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     * Negative values are illegal.
     */
    public void setFilterResolutionX(int filterResolutionX){
        if(filterResolutionX < 0){
            throw new IllegalArgumentException();
        }
        touch();
        this.filterResolutionX = filterResolutionX;
    }
    
    /**
     * Returns the resolution along the Y axis.
     */
    public int getFilterResolutionY(){
        return filterResolutionY;
    }

    /**
     * Sets the resolution along the Y axis, i.e., the maximum
     * size for intermediate images along that axis.
     * If the Y-value is less than zero, the scale applied to 
     * the rendered images is computed to preserve the image's aspect ratio
     */
    public void setFilterResolutionY(int filterResolutionY){
        touch();
        this.filterResolutionY = filterResolutionY;
    }
    

    /**
     * This returns true if <tt>ri</tt> and all of <tt>ri</tt>'s
     * sources implement the PaintRable interface.  This is used to
     * indicate that the chain has a good potential for bypassing the
     * filterRes operation entirely.  
     * 
     * Ideally there would be a checkPaintRable method in PaintRable
     * that could be used to get a definate answer about a filters
     * ability to draw directly to a Graphics2D (this can sometimes
     * 'fail' because of the way the Graphics2D is currently
     * configured).  
     */
    public boolean allPaintRable(RenderableImage ri) {
        if (!(ri instanceof PaintRable))
            return false;

        Vector v = ri.getSources();
        // No sources and we are PaintRable so the chain is PaintRable.
        if (v == null) return true;
        
        Iterator i = v.iterator();
        while (i.hasNext()) {
            RenderableImage nri = (RenderableImage)i.next();
            // A source is not paintRable so we are not 100% paintRable.
            if (!allPaintRable(nri)) return false;
        }
        
        return true;
    }

    /**
     * This function attempts to distribute the filterRes operation
     * across src.  Right now it knows about two operations, pad and
     * composite.  It's main target is the composite but often pad
     * operations are sprinked in the chain so it needs to know about
     * them.  This list could be extended however if it gets much
     * longer it should probably be rolled into a new 'helper interface'
     * like PaintRable.
     *
     * NOTE: This is essentially a bad hack, but it is a hack that is
     *       recomended by the SVG specification so I do it.
     */
    public boolean distributeAcross(RenderableImage src, Graphics2D g2d) {
        boolean ret;
        if (src instanceof PadRable) {
            PadRable pad = (PadRable)src;
            Shape clip = g2d.getClip();
            g2d.clip(pad.getPadRect());
            ret = distributeAcross(pad.getSource(), g2d);
            g2d.setClip(clip);
            return ret;
        }

        if (src instanceof CompositeRable) {
            CompositeRable comp = (CompositeRable)src;
            if (comp.getCompositeRule() != CompositeRule.OVER)
                return false;

            if (false) {
                // To check colorspaces or to not check colorspaces
                // _that_ is the question...
                ColorSpace crCS  = comp.getOperationColorSpace();
                ColorSpace g2dCS = GraphicsUtil.getDestinationColorSpace(g2d);
                if ((g2dCS == null) || (g2dCS != crCS))
                    return false;
            }

            Vector v = comp.getSources();
            if (v == null) return true;
            ListIterator li = v.listIterator(v.size());
            while (li.hasPrevious()) {
                RenderableImage csrc = (RenderableImage)li.previous();
                if (!allPaintRable(csrc)) {
                    li.next(); 
                    break;
                }
            }

            if (!li.hasPrevious()) {
                // All inputs are PaintRable so just draw directly to
                // the graphics ignore filter res all togeather...
                GraphicsUtil.drawImage(g2d, comp);
                return true;
            }
            
            if (!li.hasNext())
                // None of the trailing inputs are PaintRable so we don't
                // distribute across this at all.
                return false;

            // Now we are in the case where some are paintRable and
            // some aren't.  In this case we create a new
            // CompositeRable with the first ones, to which we apply
            // ourselves (limiting the resolution), and after that
            // we simply draw the remainder...
            int idx = li.nextIndex();  // index of first PaintRable...
            Filter f = new CompositeRable8Bit(v.subList(0, idx),
                                              comp.getCompositeRule(),
                                              comp.isColorSpaceLinear());
            f = new FilterResRable8Bit(f, getFilterResolutionX(),
                                       getFilterResolutionY());
            GraphicsUtil.drawImage(g2d, f);
            while (li.hasNext()) {
                PaintRable pr = (PaintRable)li.next();
                if (!pr.paintRable(g2d)) {
                    // Ugg it failed to paint so we need to filterRes it...
                    Filter     prf  = (Filter)pr;
                    prf = new FilterResRable8Bit(prf, getFilterResolutionX(),
                                                 getFilterResolutionY());
                    GraphicsUtil.drawImage(g2d, prf);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Should perform the equivilent action as 
     * createRendering followed by drawing the RenderedImage.
     *
     * @param g2d The Graphics2D to draw to.
     * @return true if the paint call succeeded, false if
     *         for some reason the paint failed (in which 
     *         case a createRendering should be used).
     */
    public boolean paintRable(Graphics2D g2d) {
        // This is a bit of a hack to implement the suggestion of SVG
        // specification that if the last operation in a filter chain
        // is a SRC_OVER composite and the source is SourceGraphic it
        // should be rendered directly to the canvas (by passing
        // filterRes).  We are actually much more aggressive in
        // implementing this suggestion since we will bypass filterRes
        // for all the trailing elements in a SRC_OVER composite that
        // can be drawn directly to the canvas.

        // System.out.println("Calling FilterResRable paintRable");

        // This optimization only apply if we are using
        // SrcOver.  Otherwise things break...
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c))
            return false;

        Filter src = getSource();
        return distributeAcross(src, g2d);
    }

    /**
     * Cached Rendered image at filterRes.
     */
    Reference resRed = null;
    float     resScale = 0;

    private float getResScale() {
        return resScale;
    }

    private RenderedImage getResRed(RenderingHints hints) {
        Rectangle2D imageRect = getBounds2D();
        double resScaleX = getFilterResolutionX()/imageRect.getWidth();
        double resScaleY = getFilterResolutionY()/imageRect.getHeight();

        
        // System.out.println("filterRes X " + filterResolutionX + 
        //                    " Y : " + filterResolutionY);

        float resScale = (float)Math.min(resScaleX, resScaleY);

        RenderedImage ret;
        if (resScale == this.resScale) {
            // System.out.println("Matched");
            ret = (RenderedImage)resRed.get();
            if (ret != null)
                return ret;
        }

        AffineTransform resUsr2Dev;
        resUsr2Dev = AffineTransform.getScaleInstance(resScale, resScale);
        
        //
        // Create a new RenderingContext
        //
        RenderContext newRC = new RenderContext(resUsr2Dev, null, hints);

        ret = getSource().createRendering(newRC);

        // This is probably justified since the whole reason to use
        // The filterRes attribute is because the filter chain is
        // expensive, otherwise you should let it evaluate at
        // screen resolution always - right?
        ret = new TileCacheRed(GraphicsUtil.wrap(ret));
        this.resScale = resScale;
        this.resRed   = new SoftReference(ret);

        return ret;
    }

    

    /**
     * 
     */
    public RenderedImage createRendering(RenderContext renderContext) {
        // Get user space to device space transform
        AffineTransform usr2dev = renderContext.getTransform();
        if(usr2dev == null){
            usr2dev = new AffineTransform();
        }

        RenderingHints hints = renderContext.getRenderingHints();
        
        // As per specification, a value of zero for the 
        // x-axis or y-axis causes the filter to produce
        // nothing.
        // The processing is done as follows:
        // + if the x resolution is zero, this is a no-op
        //   else compute the x scale.
        // + if the y resolution is zero, this is a no-op
        //   else compute the y resolution from the x scale
        //   and compute the corresponding y scale.
        // + if the y or x scale is less than one, insert 
        //   an AffineRable.
        //   Else, return the source as is.
        int filterResolutionX = getFilterResolutionX();
        int filterResolutionY = getFilterResolutionY();
        // System.out.println("FilterResRable: " + filterResolutionX + "x" +
        //                    filterResolutionY);

        if ((filterResolutionX <= 0) || (filterResolutionY == 0))
            return null;
        
        // Find out the renderable area
        Rectangle2D imageRect = getBounds2D();
        Rectangle   devRect;
        devRect = usr2dev.createTransformedShape(imageRect).getBounds();

        // Now, compare the devRect with the filter
        // resolution hints
        float scaleX = 1;
        if(filterResolutionX < devRect.width)
            scaleX = filterResolutionX / (float)devRect.width;

        float scaleY = 1;
        if(filterResolutionY < 0)
            scaleY = scaleX;
        else if(filterResolutionY < devRect.height)
            scaleY = filterResolutionY / (float)devRect.height;

        // Only resample if either scaleX or scaleY is
        // smaller than 1
        if ((scaleX >= 1) && (scaleY >= 1))
            return getSource().createRendering(renderContext);

        // System.out.println("Using Fixed Resolution...");

        // Using fixed resolution image since we need an image larger
        // than this.
        RenderedImage resRed   = getResRed(hints);
        float         resScale = getResScale();

        AffineTransform residualAT;
        residualAT = new AffineTransform(usr2dev.getScaleX()/resScale,
                                         usr2dev.getShearY()/resScale,
                                         usr2dev.getShearX()/resScale,
                                         usr2dev.getScaleY()/resScale,
                                         usr2dev.getTranslateX(),
                                         usr2dev.getTranslateY());

        // org.ImageDisplay.showImage("AT: " + newUsr2Dev, result);

        return new AffineRed(GraphicsUtil.wrap(resRed), residualAT, hints);
    }
}

