/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.AffineRable;

/**
 * Concrete implementation of the <tt>PatternPaint</tt> interface
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PatternPaint implements Paint {

    /**
     * The <tt>GraphicsNode</tt> that this <tt>Paint</tt> uses to
     * produce the pixel pattern
     */
    private GraphicsNode node;

    /**
     * The <tt>GraphicsNodeRenderContext</tt> for rendering this
     * <tt>Paint</tt>'s node.
     */
    private GraphicsNodeRenderContext gnrc;

    /**
     * The region to which this paint is constrained
     */
    private Rectangle2D patternRegion;

    /**
     * Additional pattern transform, added on top of the
     * user space to device space transform (i.e., before
     * the tiling space
     */
    private AffineTransform patternTransform;

    /**
     * Controls whether or not the pattern clips the
     * the node
     */
    private boolean overflow;

    /*
     * The basic tile to fill the region with.
     * we replicate this out in the Context.
     */
    private Filter tile;

    /**
     * @param node Used to generate the paint pixel pattern
     * @param nodeTransform Additional transform to
     *        set on the pattern content node.
     * @param patternRegion Region to which this paint is constrained
     * @param overflow controls whether or not the patternRegion
     *        clips the pattern node.
     * @param patternTransform additional transform added on
     *        top of the user space to device space transform.
     */
    public PatternPaint(GraphicsNode node,
                        GraphicsNodeRenderContext gnrc,
                        AffineTransform nodeTransform,
                        Rectangle2D patternRegion,
                        boolean overflow,
                        AffineTransform patternTransform){
        if (node == null) {
            throw new IllegalArgumentException();
        }

        if (patternRegion == null) {
            throw new IllegalArgumentException();
        }

        if (nodeTransform == null)
            nodeTransform = new AffineTransform();

        this.node             = node;
        this.gnrc             = gnrc;
        this.patternRegion    = patternRegion;
        this.overflow         = overflow;
        this.patternTransform = patternTransform;

        //
        // adjustTxf applies the nodeTransform first, then
        // the translation to move the node rendering into
        // the pattern region space
        //
        AffineTransform adjustTxf = new AffineTransform();
        adjustTxf.translate(patternRegion.getX(), patternRegion.getY());
        adjustTxf.concatenate(nodeTransform);

        GraphicsNodeRable gnr = new GraphicsNodeRable8Bit(node, gnrc);

        AffineRable atr = new AffineRable8Bit(gnr, adjustTxf);


        Rectangle2D padBounds = (Rectangle2D)patternRegion.clone();
        if(overflow){
            //
            // When there is overflow, make sure we take the
            // full node bounds into account.
            //
            Rectangle2D nodeBounds = node.getBounds(gnrc);
            Rectangle2D adjustedNodeBounds
                = adjustTxf.createTransformedShape(nodeBounds).getBounds2D();

            //System.out.println("adjustedBounds : " + adjustedNodeBounds);
            padBounds.add(adjustedNodeBounds);
        }

        tile = new PadRable8Bit(atr, padBounds, PadMode.ZERO_PAD);
    }

    public GraphicsNode getGraphicsNode(){
        return node;
    }

    public Rectangle2D getPatternRect(){
        return (Rectangle2D)patternRegion.clone();
    }

    public boolean isOverflow(){
        return overflow;
    }

    public AffineTransform getPatternTransform(){
        return patternTransform;
    }

    public PaintContext createContext(ColorModel      cm, 
                                      Rectangle       deviceBounds,
                                      Rectangle2D     userBounds,
                                      AffineTransform xform,
                                      RenderingHints  hints) {
        //
        // Concatenate the patternTransform to xform
        //
        if(patternTransform != null) {
            xform = new AffineTransform(xform);
            xform.concatenate(patternTransform);

            try{
                AffineTransform patternTransformInv 
                    = patternTransform.createInverse();
                userBounds = patternTransformInv.
                    createTransformedShape(userBounds).getBounds2D();
            }
            catch(NoninvertibleTransformException e){  }
        }

        return new PatternPaintContext(cm, xform,
                                       hints, tile,
                                       patternRegion,
                                       userBounds,
                                       overflow);
    }

    public int getTransparency(){
        return TRANSLUCENT;
    }
}
