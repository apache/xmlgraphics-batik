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

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.AffineRable;

/**
 * The PatternPaint class provides a way to fill a Shape with a a pattern
 * defined as a GVT Tree.
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

    /*
     * The basic tile to fill the region with.
     * we replicate this out in the Context.
     */
    private Filter tile;

    /**
     * Controls whether or not the pattern overflows
     * the pattern tile
     */
    private boolean overflow;

    /**
     * Constructs a new <tt>PatternPaint</tt>.
     *
     * @param node Used to generate the paint pixel pattern
     * @param patternRegion Region to which this paint is constrained
     * @param overflow controls whether or not the node can overflow
     *        the patternRegion.
     * @param patternTransform additional transform added on
     *        top of the user space to device space transform.
     */
    public PatternPaint(GraphicsNode node,
                        GraphicsNodeRenderContext gnrc,
                        Rectangle2D patternRegion,
                        boolean overflow,
                        AffineTransform patternTransform){

        if (node == null) {
            throw new IllegalArgumentException();
        }

        if (patternRegion == null) {
            throw new IllegalArgumentException();
        }

        this.node             = node;
        this.gnrc             = gnrc;
        this.patternRegion    = patternRegion;
        this.overflow         = overflow;
        this.patternTransform = patternTransform;
        
        // Wrap the input node so that the primitivePaint
        // in GraphicsNodeRable takes the filter, clip....
        // into account.
        CompositeGraphicsNode comp = new CompositeGraphicsNode();
        comp.getChildren().add(node);
        GraphicsNodeRable gnr = new GraphicsNodeRable8Bit(comp, gnrc);

        Rectangle2D padBounds = (Rectangle2D)patternRegion.clone();

        // When there is overflow, make sure we take the full node bounds into
        // account.
        if (overflow) {
            Rectangle2D nodeBounds = comp.getBounds(gnrc);
            // System.out.println("Comp Bounds    : " + nodeBounds);
            // System.out.println("Node Bounds    : " + node.getBounds(gnrc));
            padBounds.add(nodeBounds);
        }

        // System.out.println("Pattern region : " + patternRegion);
        // System.out.println("Node txf       : " + node.getTransform());
        tile = new PadRable8Bit(gnr, padBounds, PadMode.ZERO_PAD);
    }

    /**
     * Returns the graphics node that define the pattern.
     */
    public GraphicsNode getGraphicsNode(){
        return node;
    }

    /**
     * Returns the pattern region.
     */
    public Rectangle2D getPatternRect(){
        return (Rectangle2D)patternRegion.clone();
    }

    /**
     * Returns the additional transform of the pattern paint.
     */
    public AffineTransform getPatternTransform(){
        return patternTransform;
    }

    /**
     * Creates and returns a context used to generate the pattern.
     */
    public PaintContext createContext(ColorModel      cm, 
                                      Rectangle       deviceBounds,
                                      Rectangle2D     userBounds,
                                      AffineTransform xform,
                                      RenderingHints  hints) {

        // System.out.println("userBounds : " + userBounds);
        // System.out.println("patternTransform : " + patternTransform);

        // Concatenate the patternTransform to xform
        if (patternTransform != null) {
            xform = new AffineTransform(xform);
            xform.concatenate(patternTransform);
	    
            try {
                AffineTransform patternTransformInv = 
		    patternTransform.createInverse();
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

    /**
     * Returns the transparency mode for this pattern paint.
     */
    public int getTransparency(){
        return TRANSLUCENT;
    }
}
