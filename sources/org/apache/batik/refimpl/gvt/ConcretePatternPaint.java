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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.gvt.filter.FilterRegion;

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
     * @param node Used to generate the paint pixel pattern
     * @param patternRegion Region to which this paint is constrained
     */
    public ConcretePatternPaint(GraphicsNode node,
                                FilterRegion patternRegion){
        if(node == null){
            throw new IllegalArgumentException();
        }

        if(patternRegion == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
        this.patternRegion = patternRegion;
    }

    public GraphicsNode getGraphicsNode(){
        return node;
    }

    public Rectangle2D getPatternRect(){
        return patternRegion.getRegion();
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
                                      Rectangle2D userBounds, AffineTransform xform,
                                      RenderingHints hints) {
        return new ConcretePatternPaintContext(cm, deviceBounds, 
                                               userBounds, xform,
                                               hints, node, patternRegion);
    }
    
    public int getTransparency(){
        return TRANSLUCENT;
    }
}
