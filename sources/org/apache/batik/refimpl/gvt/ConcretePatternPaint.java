/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.gvt.filter.FilterRegion;

import org.apache.batik.util.awt.geom.AffineTransformSource;

/**
 * Concrete implementation of the <tt>PatternPaint</tt> interface
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcretePatternPaint implements PatternPaint {
    /**
     * The <tt>GraphicsNode</tt> that this <tt>Paint</tt> uses to
     * produce the pixel pattern
     */
    private GraphicsNode node;

    /**
     * The region to which this paint is constrained
     */
    private FilterRegion patternRegion;

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

    /**
     * Source for an addition transform to apply to the 
     * pattern content node
     */
    private AffineTransformSource nodeTransformSource;

    /**
     * @param node Used to generate the paint pixel pattern
     * @param nodeTransformSource Source for an additional transform to 
     *        set on the pattern content node.
     * @param patternRegion Region to which this paint is constrained
     * @param overflow controls whether or not the patternRegion
     *        clips the pattern node.
     * @param patternTransform additional transform added on
     *        top of the user space to device space transform.
     */
    public ConcretePatternPaint(GraphicsNode node,
                                AffineTransformSource nodeTransformSource,
                                FilterRegion patternRegion,
                                boolean overflow,
                                AffineTransform patternTransform){
        if(node == null){
            throw new IllegalArgumentException();
        }

        if(patternRegion == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
        this.nodeTransformSource = nodeTransformSource;
        this.patternRegion = patternRegion;
        this.overflow = overflow;
        this.patternTransform = patternTransform;
    }

    public GraphicsNode getGraphicsNode(){
        return node;
    }

    public Rectangle2D getPatternRect(){
        return patternRegion.getRegion();
    }

    public boolean isOverflow(){
        return overflow;
    }

    public AffineTransform getPatternTransform(){
        return patternTransform;
    }

    public PaintContext createContext(ColorModel cm, Rectangle 
                                      deviceBounds,
                                      Rectangle2D userBounds, 
                                      AffineTransform xform,
                                      RenderingHints hints) {
        System.out.println("deviceBounds   : " + deviceBounds);

        //
        // Concatenate the patternTransform to xform
        //
        if(patternTransform != null){
            xform = new AffineTransform(xform);
            xform.concatenate(patternTransform);

            // Modify area of interest accordingly
            try{
                AffineTransform patternTransformInv = patternTransform.createInverse();
                Shape aoi = (Shape)hints.get(GraphicsNode.KEY_AREA_OF_INTEREST);
                if(aoi != null){
                    hints = new RenderingHints(hints);
                    hints.put(GraphicsNode.KEY_AREA_OF_INTEREST,
                              patternTransformInv.createTransformedShape(aoi));
                }
            }catch(NoninvertibleTransformException e){
            }
        }

        return new ConcretePatternPaintContext(cm, xform,
                                               hints, node, 
                                               nodeTransformSource,
                                               patternRegion,
                                               overflow);
    }
    
    public int getTransparency(){
        return TRANSLUCENT;
    }
}
