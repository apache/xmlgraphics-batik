/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * Provides the actual implementation for the LinearGradientPaint
 * This is where the pixel processing is done.
 * 
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see java.awt.PaintContext
 * @see java.awt.Paint
 * @see java.awt.GradientPaint
 */
final class LinearGradientPaintContext extends MultipleGradientPaintContext {
    
    /**
     * The following invariants are used to process the gradient value from 
     * a device space coordinate, (X, Y):
     * g(X, Y) = dgdX*X + dgdY*Y + gc
     */
    private float dgdX, dgdY, gc;    
           
    /** 
     * Constructor for LinearGradientPaintContext.
     *
     *  @param cm {@link ColorModel} that receives
     *  the <code>Paint</code> data. This is used only as a hint.
     *
     *  @param deviceBounds the device space bounding box of the 
     *  graphics primitive being rendered
     *
     *  @param userBounds the user space bounding box of the 
     *  graphics primitive being rendered
     * 
     *  @param t the {@link AffineTransform} from user
     *  space into device space (gradientTransform should be 
     *  concatenated with this)
     *
     *  @param hints the hints that the context object uses to choose
     *  between rendering alternatives
     *
     *  @param start gradient start point, in user space
     *
     *  @param end gradient end point, in user space
     *
     *  @param fractions the fractions specifying the gradient distribution
     *
     *  @param colors the gradient colors
     *
     *  @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
     *
     *  @param colorSpace which colorspace to use for interpolation, 
     *  either SRGB or LINEAR_RGB
     *
     */
    public LinearGradientPaintContext(ColorModel cm,
                                      Rectangle deviceBounds,
                                      Rectangle2D userBounds,
                                      AffineTransform t,
                                      RenderingHints hints,
                                      Point2D dStart,
                                      Point2D dEnd,
                                      float[] fractions,
                                      Color[] colors, 
                                      MultipleGradientPaint.CycleMethodEnum 
                                      cycleMethod,
                                      MultipleGradientPaint.ColorSpaceEnum 
                                      colorSpace)
        throws NoninvertibleTransformException
    {	
        super(cm, deviceBounds, userBounds, t, hints, fractions, 
              colors, cycleMethod, colorSpace);
        
      	//call superclass method to calculate the gradients
        calculateGradientFractions();

        // Use single precision floating points
        Point2D.Float start = new Point2D.Float((float)dStart.getX(),
                                                (float)dStart.getY());
        Point2D.Float end = new Point2D.Float((float)dEnd.getX(),
                                              (float)dEnd.getY());
        
        // A given point in the raster should take on the same color as its
        // projection onto the gradient vector.
        // Thus, we want the projection of the current position vector
        // onto the gradient vector, then normalized with respect to the
        // length of the gradient vector, giving a value which can be mapped into
        // the range 0-1.
        // projection = currentVector dot gradientVector / length(gradientVector)
        // normalized = projection / length(gradientVector)

        float dx = end.x - start.x; // change in x from start to end
        float dy = end.y - start.y; // change in y from start to end
        float dSq = dx*dx + dy*dy; // total distance squared
	
        //avoid repeated calculations by doing these divides once.
        float constX = dx/dSq;
        float constY = dy/dSq;
	
        dgdX = a00*constX + a10*constY;//incremental change along gradient for +x
        dgdY = a01*constX + a11*constY;//incremental change along gradient for +y

        //constant, incorporates the translation components from the matrix
        gc = (a02-start.x)*constX + (a12-start.y)*constY;	       	
    }
    
    /**
     * Return a Raster containing the colors generated for the graphics
     * operation.  This is where the area is filled with colors distributed
     * linearly.
     *
     * @param x,y,w,h The area in device space for which colors are
     * generated.
     *
     */
    protected void fillRaster(int[] pixels, int off, int adjust, 
                              int x, int y, int w, int h) {
	
        float g = 0;     //current value for row gradients
	
        int rowLimit = off + w;  //Used to end iteration on rows   
	
        //constant which can be pulled out of the inner loop
        float initConst = (dgdX*x) + gc;
	
        for(int i=0; i<h; i++){ //for every row
            g = initConst + dgdY*(y+i); //initialize current value to be start.
	    
            while(off < rowLimit){ //for every pixel in this row.
                pixels[off++] = indexIntoGradientsArrays(g); //get the color
                g += dgdX; //incremental change in g
            }
	    
            off += adjust; //change in off from row to row
            rowLimit = off + w; //rowlimit is width + offset.
        }
    }
    
    
}
