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
 * Provides the actual implementation for the RadialGradientPaint.
 * This is where the pixel processing is done.  A RadialGradienPaint
 * only supports circular gradients, but it should be possible to scale
 * the circle to look approximately elliptical, by means of a
 * gradient transform passed into the RadialGradientPaint constructor.
 *
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 *
 */
final class RadialGradientPaintContext extends MultipleGradientPaintContext {  
    
    /** True when (focus == center)  */
    private boolean isSimpleFocus = false;

    /** True when (cycleMethod == NO_CYCLE) */
    private boolean isNonCyclic = false;
       
    /** Radius of the outermost circle defining the 100% gradient stop. */
    private float radius;   
    
    /** Variables representing center and focus points. */
    private float centerX, centerY, focusX, focusY;     

    /** Radius of the gradient circle squared. */
    private float radiusSq; 
        
    /** Constant part of X, Y user space coordinates. */
    private float constA, constB;
       
    /** This value represents the solution when focusX == X.  It is called
     * trivial because it is easier to calculate than the general case.
     */
    private float trivial;       
    
    /** Amount for offset when clamping focus. */
    private static final float SCALEBACK = .97f;
    
    /** 
     * Constructor for RadialGradientPaintContext.
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
     *  @param cx the center point in user space of the circle defining 
     *  the gradient.  The last color of the gradient is mapped to the 
     *  perimeter of this circle X coordinate
     *
     *  @param cy the center point in user space of the circle defining 
     *  the gradient.  The last color of the gradient is mapped to the 
     *  perimeter of this circle Y coordinate
     *     
     *  @param r the radius of the circle defining the extents of the 
     *  color gradient
     *
     *  @param fx the point in user space to which the first color is mapped
     *  X coordinate
     *
     *  @param fy the point in user space to which the first color is mapped
     *  Y coordinate
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
    public RadialGradientPaintContext(ColorModel cm,
				      Rectangle deviceBounds,
				      Rectangle2D userBounds,
				      AffineTransform t,
				      RenderingHints hints,
				      float cx, float cy,
				      float r,
				      float fx, float fy,
				      float[] fractions,
				      Color[] colors,
				      MultipleGradientPaint.CycleMethodEnum 
				      cycleMethod,
				      MultipleGradientPaint.ColorSpaceEnum 
				      colorSpace)
	throws NoninvertibleTransformException
    {       	
	super(cm, deviceBounds, userBounds, t, hints, fractions, colors, 
	      cycleMethod, colorSpace);

	//call superclass method to calculate the gradients
	calculateGradientFractions();
	
	//copy some parameters.
	centerX = cx;
	centerY = cy;	
	focusX = fx;
	focusY = fy;
	radius = r;
	
	this.isSimpleFocus = (focusX == centerX) && (focusY == centerY);
	this.isNonCyclic = (cycleMethod == RadialGradientPaint.NO_CYCLE);
	
	//for use in the quadractic equation
	radiusSq = radius * radius;

	float dX = focusX - centerX;
	float dY = focusY - centerY;

	double dist = Math.sqrt((dX * dX) + (dY * dY));

	//test if distance from focus to center is greater than the radius
	if (dist > radius) { //clamp focus to radius

	    if (dY == 0) {  //avoid divide by zero
		focusY = centerY;
		focusX = centerX + (radius * SCALEBACK);
	    }
	    else {	    
		double angle = Math.atan2(dY, dX);
			
		//x = r cos theta, y = r sin theta
		focusX = (float)Math.floor((SCALEBACK * radius * 
					    Math.cos(angle))) + centerX;

		focusY = (float)Math.floor((SCALEBACK * radius * 
					    Math.sin(angle))) + centerY;
	    }	    	   
	}

	//calculate the solution to be used in the case where X == focusX
	//in cyclicCircularGradientFillRaster
	dX = focusX - centerX;
	trivial = (float)Math.sqrt(radiusSq - (dX * dX));

	// constant parts of X, Y user space coordinates 
	constA = a02 - centerX;
	constB = a12 - centerY;

	this.calculateFixedPointSqrtLookupTable();
    }   
    
    /**
     * Return a Raster containing the colors generated for the graphics
     * operation.
     * @param x,y,w,h The area in device space for which colors are
     * generated.
     */
    protected void fillRaster(int pixels[], int off, int adjust,
			      int x, int y, int w, int h) {
	
  	if (isSimpleFocus && isNonCyclic && isSimpleLookup) {
	    fixedPointSimplestCaseNonCyclicFillRaster(pixels, off, adjust, x, 
						      y, w, h);
	}      
	
	else {
	    cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
	}
	
    }    
    
    /**
     * This code works in the simplest of cases, where the focus == center 
     * point, the gradient is noncyclic, and the gradient lookup method is 
     * fast (single array index, no conversion necessary).
     *
     */         
    private void fixedPointSimplestCaseNonCyclicFillRaster(int pixels[], 
							   int off,
							   int adjust, 
							   int x, int y, 
							   int w, int h) {
	
	float g;//value between 0 and 1 specifying position in the gradient
        float iSq;  // Square distance index
        float p;  // Squrare root penetration in square root interval
	//this factor is used to scale the index calculation by the array size
	//and 1/radius (normalize the distance)
	float indexFactor = fastGradientArraySize / radius;      
	float factor = 16;  //2^16 factor for converting to/from floating point
	
	//constant part of X and Y coordinates for the entire raster
	float constX = (a00*x) + (a01*y) + constA;
	float constY = (a10*x) + (a11*y) + constB;
	float deltaX = indexFactor * a00; //incremental change in dX
	float deltaY = indexFactor * a10; //incremental change in dY
	float dX, dY; //the current distance from center
	int indexer = off;//used to index pixels array
	int i, j; //indexing variables
	int precalc = w+adjust;  //precalculate this number
	int fixedArraySizeSq=
	    (fastGradientArraySize * fastGradientArraySize) << 4;
	int gFixed;//fixed point integer
	int gIndex;//non-fixed-pt integer number used to index gradient array
	int iSqInt; // Square distance index       		   
	
	// For every point in the raster, calculate the color at that point
	for(j = 0; j < h; j++){ //for every row
	    
	    //constants from column to column
	    
	    //x and y (in user space) of the first pixel of this row
	    dX = indexFactor * ((a01*j) + constX);
	    dY = indexFactor * ((a11*j) + constY);	   	   

	    // these values below here allow for an incremental calculation
	    // of dX^2 + dY^2 
	    int temp = (int)((deltaX * deltaX) + (deltaY * deltaY));
	    //initialize to be equal to distance squared
	    gFixed = (int)(((dY * dY) + (dX * dX)) * factor);
	    int gFixeddelta =  (int)(((((deltaY * dY) + (deltaX * dX))* 2) + 
				      temp) * factor);	 
	    int gFixeddeltadelta =(int)((temp * 2) * factor);
	    
	    //for every column (inner loop begins here)
 	    for (i = 0; i < w; i++) {	       
		//determine the distance to the center
		
		//since this is a non cyclic fill raster, crop at "1" and 0
		if (gFixed > fixedArraySizeSq) {
		    gIndex = fastGradientArraySize;
		}
		
		else if (gFixed < 0) { 
		    gIndex = 0;		    
		}
		
		else {
		    iSq = (gFixed >>> 4) / sqStepFloat;
		    		  
		    iSqInt = (int)iSq; //chop off fractional part
		    
		    p = iSq - iSqInt;		    
		    gIndex = (int)((p * sqrtLutFixed[iSqInt + 1]) + 
				   ((1-p) * sqrtLutFixed[iSqInt]));
		}
		
		pixels[indexer + i] = gradient[gIndex]; 
				
		//incremental calculation
		gFixed += gFixeddelta;
		gFixeddelta += gFixeddeltadelta;		
	    }	  
	    indexer += precalc;
	}
    }
    
    /** Used to limit the size of the square root lookup table */
    private int MAX_PRECISION = 256;
    
    /** Length of a square distance intervale in the lookup table */
    private float sqStepFloat; 
    
    /** Square root lookup table */
    private int sqrtLutFixed[] = new int[MAX_PRECISION];
    
    /**
     * Build square root lookup table
     */       
    private void calculateFixedPointSqrtLookupTable() {	      
	sqStepFloat = (fastGradientArraySize  * fastGradientArraySize) 
	    / (MAX_PRECISION - 2);
	
	// The last two values are the same so that linear square root 
	// interpolation can happen on the maximum reachable element in the 
	// lookup table (precision-2)
	int i;
	for (i = 0; i < MAX_PRECISION - 1; i++) {
	    sqrtLutFixed[i] = (int)(Math.sqrt(i*sqStepFloat));
	}
	sqrtLutFixed[i] = sqrtLutFixed[i-1];	
    }
    
    /** Fill the raster, cycling the gradient colors when a point falls outside
     *  of the perimeter of the 100% stop circle.          
     * 
     *  This calculation first computes the intersection point of the line
     *  from the focus through the current point in the raster, and the
     *  perimeter of the gradient circle.
     * 
     *  Then it determines the percentage distance of the current point along
     *  that line (focus is 0%, perimeter is 100%). 
     *
     *  Equation of a circle centered at (a,b) with radius r:
     *  (x-a)^2 + (y-b)^2 = r^2
     *  Equation of a line with slope m and y-intercept b
     *  y = mx + b
     *  replacing y in the cirlce equation and solving using the quadratic
     *  formula produces the following set of equations.  Constant factors have
     *  been extracted out of the inner loop.
     *
     */   
    private void cyclicCircularGradientFillRaster(int pixels[], int off, 
						  int adjust, 
						  int x, int y, 
						  int w, int h) {

	// Constant part of the C factor of the quadratic equation
	final double constC = 
	    -(radiusSq) + (centerX * centerX) + (centerY * centerY);
	double A; //coefficient of the quadratic equation (Ax^2 + Bx + C = 0)
	double B; //coefficient of the quadratic equation
	double C; //coefficient of the quadratic equation
	double slope; //slope of the focus-perimeter line
	double yintcpt; //y-intercept of the focus-perimeter line
	double solutionX;//intersection with circle X coordinate
	double solutionY;//intersection with circle Y coordinate       
       	final float constX = (a00*x) + (a01*y) + a02;//const part of X coord
	final float constY = (a10*x) + (a11*y) + a12; //const part of Y coord
       	final float precalc2 = 2 * centerY;//const in inner loop quad. formula
	final float precalc3 =-2 * centerX;//const in inner loop quad. formula
	float X; // User space point X coordinate 
	float Y; // User space point Y coordinate
	float g;//value between 0 and 1 specifying position in the gradient
        float det; //determinant of quadratic formula (should always be >0)
	float currentToFocusSq;//sq distance from the current pt. to focus
	float intersectToFocusSq;//sq distance from the intersect pt. to focus
	float deltaXSq; //temp variable for a change in X squared.
	float deltaYSq; //temp variable for a change in Y squared.
	int indexer = off; //index variable for pixels array
	int i, j; //indexing variables for FOR loops
	int pixInc = w+adjust;//incremental index change for pixels array
	
	for (j = 0; j < h; j++) { //for every row
	    
	    X = (a01*j) + constX; //constants from column to column
	    Y = (a11*j) + constY;
	    
	    //for every column (inner loop begins here)
	    for (i = 0; i < w; i++) {	       			
	
		// special case to avoid divide by zero
		if (X == focusX) {		   
		    solutionX = focusX;
		    
		    solutionY = centerY;
		    
		    solutionY += (Y > focusY)?trivial:-trivial;
		}
		
		else {    
		    
		    //slope of the focus-current line
		    slope =   (Y - focusY) / (X - focusX);
		    
		    yintcpt = Y - (slope * X); //y-intercept of that same line
		    
		    //use the quadratic formula to calculate the intersection
		    //point		  
		    A = (slope * slope) + 1; 
		    
		    B =  precalc3 + (-2 * slope * (centerY - yintcpt));
		    
		    C =  constC + (yintcpt* (yintcpt - precalc2));
		    
		    det = (float)Math.sqrt((B * B) - ( 4 * A * C));
		    
		    solutionX = -B;
		    
		    //choose the positive or negative root depending
		    //on where the X coord lies with respect to the focus.
		    solutionX += (X < focusX)?-det:det;
		    
		    solutionX = solutionX / (2 * A);//divisor
		    
		    solutionY = (slope * solutionX) + yintcpt;
		}	                    	

		//calculate the square of the distance from the current point 
		//to the focus and the square of the distance from the 
		//intersection point to the focus. Want the squares so we can
		//do 1 square root after division instead of 2 before.

		deltaXSq = X - focusX;
		deltaXSq = deltaXSq * deltaXSq;

		deltaYSq = Y - focusY;
		deltaYSq = deltaYSq * deltaYSq;

		currentToFocusSq = deltaXSq + deltaYSq;

		deltaXSq = (float)solutionX - focusX;
		deltaXSq = deltaXSq * deltaXSq;

		deltaYSq = (float)solutionY - focusY;
		deltaYSq = deltaYSq * deltaYSq;

		intersectToFocusSq = deltaXSq + deltaYSq;

		//want the percentage (0-1) of the current point along the 
		//focus-circumference line
		g = (float)Math.sqrt(currentToFocusSq / intersectToFocusSq);
	       	       		
		//save the color at this point
		pixels[indexer + i] = indexIntoGradientsArrays(g);
		
		X += a00; //incremental change in X, Y
		Y += a10;	
	    } //end inner loop
	    indexer += pixInc;
	} //end outer loop
    }
    
}
