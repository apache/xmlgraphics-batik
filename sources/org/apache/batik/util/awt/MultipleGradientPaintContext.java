/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.lang.ref.WeakReference;

/** This is the superclass for all PaintContexts which use a multiple color
 * gradient to fill in their raster. It provides the actual color interpolation
 * functionality.  Subclasses only have to deal with using the gradient to fill
 * pixels in a raster.
 *
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 *
 */
abstract class MultipleGradientPaintContext implements PaintContext {

    /**
     * PaintContext's ColorModel ARGB if colors are not all opaque.
     * RGB otherwise.
     */
    protected ColorModel model;

    /** Color model used if gradient colors are all opaque */
    private static ColorModel xrgbmodel =
        new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);

     /** The cached colorModel */
    protected static ColorModel cachedModel;

    /** The cached raster, which is reusable among instances */
    protected static WeakReference cached;

    /** Raster is reused whenever possible */
    protected Raster saved;

    /** The method to use when painting out of the gradient bounds. */
    protected MultipleGradientPaint.CycleMethodEnum cycleMethod;

    /** The colorSpace in which to perform the interpolation */
    protected MultipleGradientPaint.ColorSpaceEnum colorSpace;

    /** Elements of the inverse transform matrix. */
    protected float a00, a01, a10, a11, a02, a12;

    /** This boolean specifies wether we are in simple lookup mode, where an
     * input value between 0 and 1 may be used to directly index into a single
     * array of gradient colors.  If this boolean value is false, then we have
     * to use a 2-step process where we have to determine which gradient array
     * we fall into, then determine the index into that array.
     */
    protected boolean isSimpleLookup = true;

    /** Size of gradients array for scaling the 0-1 index when looking up
     *  colors the fast way.
     */
    protected int fastGradientArraySize;

    /**
     * Array which contains the interpolated color values for each interval,
     * used by calculateSingleArrayGradient().  It is protected for possible
     * direct access by subclasses.
     */
    protected int[] gradient;

    /** Array of gradient arrays, one array for each interval.  Used by
     *  calculateMultipleArrayGradient().
     */
    private int[][] gradients;

    /** Normalized intervals array */
    private float[] normalizedIntervals;

    /** fractions array */
    private float[] fractions;

    /** Non-normalized intervals array */
    private float[] intervals;

    /** Gradient colors */
    private Color[] colors;

    /** Used to determine if gradient colors are all opaque */
    private int transparencyTest;

    /** Colorspace conversion lookup tables */
    private static final int SRGBtoLinearRGB[] = new int[256];
    private static final int LinearRGBtoSRGB[] = new int[256];

    //build the tables
    static{
        for (int k = 0; k < 256; k++) {
            SRGBtoLinearRGB[k] = convertSRGBtoLinearRGB(k);
            LinearRGBtoSRGB[k] = convertLinearRGBtoSRGB(k);
        }
    }

    /** Constant number of max colors between any 2 arbitrary colors.
     * Used for creating and indexing gradients arrays.
     */
    private static final int GRADIENT_SIZE = 256;
    private static final int GRADIENT_SIZE_INDEX = GRADIENT_SIZE -1;

    /** Maximum length of the fast single-array.  If the estimated array size
     * is greater than this, switch over to the slow lookup method.
     * No particular reason for choosing this number, but it seems to provide
     * satisfactory performance for the common case (fast lookup).
     */
    private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;

    /** Length of the 2D slow lookup gradients array. */
    private int gradientsLength;

   /** Constructor for superclass. Does some initialization, but leaves most
    * of the heavy-duty math for calculateGradient(), so the subclass may do
    * some other manipulation beforehand if necessary.  This is not possible
    * if this computation is done in the superclass constructor which always
    * gets called first.
    **/
    public MultipleGradientPaintContext(ColorModel cm,
                                        Rectangle deviceBounds,
                                        Rectangle2D userBounds,
                                        AffineTransform t,
                                        RenderingHints hints,
                                        float[] fractions,
                                        Color[] colors,
                                        MultipleGradientPaint.CycleMethodEnum
                                        cycleMethod,
                                        MultipleGradientPaint.ColorSpaceEnum
                                        colorSpace)
        throws NoninvertibleTransformException
    {
        //We have to deal with the cases where the 1st gradient stop is not
        //equal to 0 and/or the last gradient stop is not equal to 1.
        //In both cases, create a new point and replicate the previous
        //extreme point's color.

        boolean fixFirst = false;
        boolean fixLast = false;

        //if the first gradient stop is not equal to zero, fix this condition
        if (fractions[0] != 0f) {
            fixFirst = true;
        }

        //if the first gradient stop is not equal to one, fix this condition
        if (fractions[fractions.length - 1] != 1f) {
            fixLast = true;
        }

        //copy the arrays, leaving room for the new first and last stops
        if (fixFirst && fixLast) {
            this.fractions = new float[fractions.length + 2];
            System.arraycopy(fractions, 0, this.fractions,
                             1, fractions.length);
            this.fractions[0] = 0f;
            this.fractions[this.fractions.length - 1] = 1f;

            this.colors = new Color[colors.length + 2];
            System.arraycopy(colors, 0, this.colors,
                             1, colors.length);

            this.colors[0] = colors[0];
            this.colors[this.colors.length - 1] = colors[colors.length - 1];
        }
        //copy the arrays, shifting over to make room for the new first stops
        else if (fixFirst) {
            this.fractions = new float[fractions.length + 1];
            System.arraycopy(fractions, 0, this.fractions,
                             1, fractions.length);
            this.fractions[0] = 0f;

            this.colors = new Color[colors.length + 1];
            System.arraycopy(colors, 0, this.colors,
                             1, colors.length);
            this.colors[0] = colors[0];
        }
        //copy the arrays, leaving room for the new last stops
        else if (fixLast) {
            this.fractions = new float[fractions.length + 1];
            System.arraycopy(fractions, 0, this.fractions,
                             0, fractions.length);
            this.fractions[this.fractions.length - 1] = 1f;

            this.colors = new Color[colors.length + 1];
            System.arraycopy(colors, 0, this.colors,
                             0, colors.length);
            this.colors[this.colors.length - 1] = colors[colors.length - 1];
        }
        else { //don't fix anything, just copy the arrays.
            this.fractions = new float[fractions.length];
            System.arraycopy(fractions, 0, this.fractions,
                             0, fractions.length);

            this.colors = new Color[colors.length];
            System.arraycopy(colors, 0, this.colors,
                             0, colors.length);
        }

        //this will store the intervals (distances) between gradient stops
        intervals = new float[this.fractions.length - 1];

        float currentPosition = this.fractions[0];
        float previousPosition = 0;

        //convert from fractions into intervals, check that values are in
        //the proper range and progress in increasing order from 0 to 1
        for (int i = 1; i < this.fractions.length; i++) {

            if (currentPosition < 0f || currentPosition > 1f) {
                throw new IllegalArgumentException("Keyframe values should " +
                                                   "be in the range 0 to 1: " +
                                                   currentPosition);
            }

            previousPosition = currentPosition;
            currentPosition = this.fractions[i];

            if (currentPosition < previousPosition) {
                throw
                    new IllegalArgumentException("Keyframe fractions must be" +
                                                 " increasing: " +
                                                 currentPosition);
            }

            //interval distance is equal to the difference in positions
            intervals[i-1] = currentPosition - previousPosition;
        }

        //copy the non-normalized intervals array
        normalizedIntervals = new float[intervals.length];
        System.arraycopy(intervals, 0,
                         normalizedIntervals, 0,
                         intervals.length);

        // Normalize intervals and check values are positive.
        float sum = 0;

        for(int i = 0; i < intervals.length; i++) {
            sum += intervals[i];
        }

        for(int i = 0; i < normalizedIntervals.length; i++) {
            normalizedIntervals[i] /= sum;
        }

        // The inverse transform is needed to from device to user space.
        // Get all the components of the inverse transform matrix.
        AffineTransform tInv = t.createInverse();
        double m[] = new double[6];
        tInv.getMatrix(m);
        a00 = (float)m[0];
        a10 = (float)m[1];
        a01 = (float)m[2];
        a11 = (float)m[3];
        a02 = (float)m[4];
        a12 = (float)m[5];

        //copy some flags
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;

    }


    /** This function is the meat of this class.  It calculates an array of
     * gradient colors based on an array of fractions and color values at those
     * fractions.
     */
    protected final void calculateGradientFractions() {

        //if interpolation should occur in Linear RGB space, convert the
        //colors using the lookup table
        if (colorSpace == LinearGradientPaint.LINEAR_RGB) {
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new Color(SRGBtoLinearRGB[colors[i].getRed()],
                                      SRGBtoLinearRGB[colors[i].getGreen()],
                                      SRGBtoLinearRGB[colors[i].getBlue()]);
            }
        }

        //initialize to be fully opaque for ANDing with colors
        transparencyTest = 0xff000000;

        //array of interpolation arrays
        gradients = new int[fractions.length - 1][];
        gradientsLength = gradients.length;

        // Find smallest interval
        int n = normalizedIntervals.length;

        float Imin = 1;

        for(int i = 0; i < n; i++) {
            Imin = (Imin > normalizedIntervals[i]) ?
                normalizedIntervals[i] : Imin;
        }

        //estimate the size of the entire gradients array.
        //This is to prevent a tiny interval from causing the size of array to
        //explode.  If the estimated size is too large, break to using
        //seperate arrays for each interval, and using an indexing scheme at
        //look-up time.
        int estimatedSize = 0;

        if (Imin == 0) {
            estimatedSize = Integer.MAX_VALUE;
        } else {
            for (int i = 0; i < normalizedIntervals.length; i++) {
                estimatedSize += (normalizedIntervals[i]/Imin) * GRADIENT_SIZE;
            }
        }

        if (estimatedSize > MAX_GRADIENT_ARRAY_SIZE) {
            //slow method
            calculateMultipleArrayGradient();
        } else {
            //fast method
            calculateSingleArrayGradient(Imin);
        }

        // Use the most 'economical' model.
        if((transparencyTest >>> 24) == 0xff)
            model = xrgbmodel;
        else
            model = ColorModel.getRGBdefault();
    }


    /**
     * FAST LOOKUP METHOD
     *
     * This method calculates the gradient color values and places them in a
     * single int array, gradient[].  It does this by allocating space for
     * each interval based on its size relative to the smallest interval in
     * the array.  The smallest interval is allocated 255 interpolated values
     * (the maximum number of unique in-between colors in a 24 bit color
     * system), and all other intervals are allocated
     * size = (255 * the ratio of their size to the smallest interval).
     *
     * This scheme expedites a speedy retrieval because the colors are
     * distributed along the array according to their user-specified
     * distribution.  All that is needed is a relative index from 0 to 1.
     *
     * The only problem with this method is that the possibility exists for
     * the array size to balloon in the case where there is a
     * disproportionately small gradient interval.  In this case the other
     * intervals will be allocated huge space, but much of that data is
     * redundant.  We thus need to use the space conserving scheme below.
     *
     * @param Imin the size of the smallest interval
     *
     */
    private void calculateSingleArrayGradient(float Imin) {

        //set the flag so we know later it is a non-simple lookup
        isSimpleLookup = true;

        int rgb1; //2 colors to interpolate
        int rgb2;

        int gradientsTot = 1; //the eventual size of the single array

        //for every interval (transition between 2 colors)
        for(int i=0; i < gradients.length; i++){

            //create an array whose size is based on the ratio to the
            //smallest interval.
            int nGradients = (int)((normalizedIntervals[i]/Imin)*255f);
            gradientsTot += nGradients;
            gradients[i] = new int[nGradients];

            //the the 2 colors (keyframes) to interpolate between
            rgb1 = colors[i].getRGB();
            rgb2 = colors[i+1].getRGB();

            //fill this array with the colors in between rgb1 and rgb2
            interpolate(rgb1, rgb2, gradients[i]);

            //if the colors are opaque, transparency should still be 0xff000000
            transparencyTest &= rgb1;
            transparencyTest &= rgb2;
        }

        // Put all gradients in a single array
        gradient = new int[gradientsTot];
        int curOffset = 0;
        for(int i = 0; i < gradients.length; i++){
            System.arraycopy(gradients[i], 0, gradient,
                             curOffset, gradients[i].length);
            curOffset += gradients[i].length;
        }
        gradient[gradient.length-1] = colors[colors.length-1].getRGB();

        //if interpolation occurred in Linear RGB space, convert the
        //gradients back to SRGB using the lookup table
        if (colorSpace == LinearGradientPaint.LINEAR_RGB) {

            for (int i = 0; i < gradient.length; i++) {
                gradient[i] = convertEntireColorLinearRGBtoSRGB(gradient[i]);
            }
        }

        fastGradientArraySize = gradient.length - 1;
    }


    /**
     * SLOW LOOKUP METHOD
     *
     * This method calculates the gradient color values for each interval and
     * places each into its own 255 size array.  The arrays are stored in
     * gradients[][].  (255 is used because this is the maximum number of
     * unique colors between 2 arbitrary colors in a 24 bit color system)
     *
     * This method uses the minimum amount of space (only 255 * number of
     * intervals), but it aggravates the lookup procedure, because now we
     * have to find out which interval to select, then calculate the index
     * within that interval.  This causes a significant performance hit,
     * because it requires this calculation be done for every point in
     * the rendering loop.
     *
     * For those of you who are interested, this is a classic example of the
     * time-space tradeoff.
     *
     */
    private void calculateMultipleArrayGradient() {

        //set the flag so we know later it is a non-simple lookup
        isSimpleLookup = false;

        int rgb1; //2 colors to interpolate
        int rgb2;

        //for every interval (transition between 2 colors)
        for(int i=0; i < gradients.length; i++){

            //create an array of the maximum theoretical size for each interval
            gradients[i] = new int[GRADIENT_SIZE];

            //get the the 2 colors
            rgb1 = colors[i].getRGB();
            rgb2 = colors[i+1].getRGB();

            //fill this array with the colors in between rgb1 and rgb2
            interpolate(rgb1, rgb2, gradients[i]);

            //if the colors are opaque, transparency should still be 0xff000000
            transparencyTest &= rgb1;
            transparencyTest &= rgb2;
        }

        //if interpolation occurred in Linear RGB space, convert the
        //gradients back to SRGB using the lookup table
        if (colorSpace == LinearGradientPaint.LINEAR_RGB) {

            for (int j = 0; j < gradients.length; j++) {
                for (int i = 0; i < gradients[j].length; i++) {
                    gradients[j][i] =
                        convertEntireColorLinearRGBtoSRGB(gradients[j][i]);
                }
            }
        }
    }

    /** Yet another helper function.  This one linearly interpolates between
     * 2 colors, filling up the output array.
     *
     * @param rgb1 the start color
     * @param rgb2 the end color
     * @param output the output array of colors... assuming this is not null.
     *
     */
    private void interpolate(int rgb1, int rgb2, int[] output) {

        int a1, r1, g1, b1, da, dr, dg, db; //color components

        //step between interpolated values.
        float stepSize = 1/(float)output.length;

        //extract color components from packed integer
        a1 = (rgb1 >> 24) & 0xff;
        r1 = (rgb1 >> 16) & 0xff;
        g1 = (rgb1 >>  8) & 0xff;
        b1 = (rgb1      ) & 0xff;
        //calculate the total change in alpha, red, green, blue
        da = ((rgb2 >> 24) & 0xff) - a1;
        dr = ((rgb2 >> 16) & 0xff) - r1;
        dg = ((rgb2 >>  8) & 0xff) - g1;
        db = ((rgb2      ) & 0xff) - b1;

        //for each step in the interval calculate the in-between color by
        //multiplying the normalized current position by the total color change
        //(.5 is added to prevent truncation round-off error)
        for (int i = 0; i < output.length; i++) {
            output[i] =
                (((int) ((a1 + i * da * stepSize) + .5) << 24)) |
                (((int) ((r1 + i * dr * stepSize) + .5) << 16)) |
                (((int) ((g1 + i * dg * stepSize) + .5) <<  8)) |
                (((int) ((b1 + i * db * stepSize) + .5)      ));
        }
    }


    /** Yet another helper function.  This one extracts the color components
     * of an integer RGB triple, converts them from LinearRGB to SRGB, then
     * recompacts them into an int.
     */
    private int convertEntireColorLinearRGBtoSRGB(int rgb) {

        int a1, r1, g1, b1; //color components

        //extract red, green, blue components
        a1 = (rgb >> 24) & 0xff;
        r1 = (rgb >> 16) & 0xff;
        g1 = (rgb >> 8) & 0xff;
        b1 = rgb & 0xff;

        //use the lookup table
        r1 =  LinearRGBtoSRGB[r1];
        g1 =  LinearRGBtoSRGB[g1];
        b1 =  LinearRGBtoSRGB[b1];

        //re-compact the components
        return ((a1 << 24) |
                (r1 << 16) |
                (g1 << 8) |
                b1);
    }


    /** Helper function to index into the gradients array.  This is necessary
     * because each interval has an array of colors with uniform size 255.
     * However, the color intervals are not necessarily of uniform length, so
     * a conversion is required.
     *
     * @param position the unmanipulated position.  want to map this into the
     * range 0 to 1
     *
     * @returns integer color to display
     *
     */
    protected final int indexIntoGradientsArrays(float position) {

        //first, manipulate position value depending on the cycle method.

        if (cycleMethod == MultipleGradientPaint.NO_CYCLE) {

            if (position > 1) { //upper bound is 1
                position = 1;
            }

            else if (position < 0) { //lower bound is 0
                position = 0;
            }
        }

        else if (cycleMethod == MultipleGradientPaint.REPEAT) {
            //get the fractional part
            //(modulo behavior discards integer component)
            position = position - (int)position;

            //position should now be between -1 and 1

            if (position < 0) {
                position = position + 1; //force it to be in the range 0-1
            }
        }

        else {  //cycleMethod == MultipleGradientPaint.REFLECT

            if (position < 0) {
                position = -position; //take absolute value
            }

            int part = (int)position; //take the integer part

            position = position - part; //get the fractional part

            if ((part & 0x00000001) == 1) { //if integer part is odd
                position = 1 - position; //want the reflected color instead
            }
        }

        //now, get the color based on this 0-1 position:

        if (isSimpleLookup) { //easy to compute: just scale index by array size
            return gradient[(int)(position * fastGradientArraySize)];
        }

        else { //more complicated computation, to save space

            //for all the gradient interval arrays
            for (int i = 0; i < gradientsLength; i++) {

                if (position < fractions[i+1]) { //this is the array we want

                    float delta = position - fractions[i];

                    //this is the interval we want.
                    int index = (int)((delta / normalizedIntervals[i])
                                      * (GRADIENT_SIZE_INDEX));

                    return gradients[i][index];
                }
            }

        }

        return gradients[gradients.length - 1][GRADIENT_SIZE_INDEX];
    }

    /** Helper function to convert a color component in sRGB space to linear
     * RGB space.  Used to build a static lookup table.
     */
    private static int convertSRGBtoLinearRGB(int color) {

        float input, output;

        input = ((float) color) / 255.0f;
        if (input <= 0.04045f) {
            output = input / 12.92f;
        }
        else {
            output = (float) Math.pow((input + 0.055) / 1.055, 2.4);
        }
        int o = Math.round(output * 255.0f);

        return o;
    }

     /** Helper function to convert a color component in linear RGB space to
      *  SRGB space. Used to build a static lookup table.
      */
    private static int convertLinearRGBtoSRGB(int color) {

        float input, output;

        input = ((float) color) / 255.0f;

        if (input <= 0.0031308) {
            output = input * 12.92f;
        }
        else {
            output = (1.055f *
                ((float) Math.pow(input, (1.0 / 2.4)))) - 0.055f;
        }

        int o = Math.round(output * 255.0f);

        return o;
    }


    /** Superclass getRaster... */
    public final Raster getRaster(int x, int y, int w, int h) {
        //
        // If working raster is big enough, reuse it. Otherwise,
        // build a large enough new one.
        //
        Raster raster = saved;
        if (raster == null || raster.getWidth() < w || raster.getHeight() < h)
            {
                raster = getCachedRaster(model, w, h);
                saved = raster;
            }
        //
        // Access raster internal int array. Because we use a DirectColorModel,
        // we know the DataBuffer is of type DataBufferInt and the SampleModel
        // is SinglePixelPackedSampleModel.
        // Adjust for initial offset in DataBuffer and also for the scanline
        // stride.
        //
        DataBufferInt rasterDB = (DataBufferInt)raster.getDataBuffer();
        int[] pixels = rasterDB.getBankData()[0];
        int off = rasterDB.getOffset();
        int scanlineStride = ((SinglePixelPackedSampleModel)
                              raster.getSampleModel()).getScanlineStride();
        int adjust = scanlineStride - w;

        fillRaster(pixels, off, adjust, x, y, w, h); //delegate to subclass.

        return raster;
    }

    /** Subclasses should implement this. */
    protected abstract void fillRaster(int pixels[], int off, int adjust,
                                       int x, int y, int w, int h);


    /** Took this cacheRaster code from GradientPaint. It appears to recycle
     * rasters for use by any other instance, as long as they are sufficiently
     * large.
     */
    protected final
    static synchronized Raster getCachedRaster(ColorModel cm, int w, int h) {
        if (cm == cachedModel) {
            if (cached != null) {
                Raster ras = (Raster) cached.get();
                if (ras != null &&
                    ras.getWidth() >= w &&
                    ras.getHeight() >= h)
                    {
                        cached = null;
                        return ras;
                    }
            }
        }
        return cm.createCompatibleWritableRaster(w, h);
    }

    /** Took this cacheRaster code from GradientPaint. It appears to recycle
     * rasters for use by any other instance, as long as they are sufficiently
     * large.
     */
    protected final
    static synchronized void putCachedRaster(ColorModel cm, Raster ras) {
        if (cached != null) {
            Raster cras = (Raster) cached.get();
            if (cras != null) {
                int cw = cras.getWidth();
                int ch = cras.getHeight();
                int iw = ras.getWidth();
                int ih = ras.getHeight();
                if (cw >= iw && ch >= ih) {
                    return;
                }
                if (cw * ch >= iw * ih) {
                    return;
                }
            }
        }
        cachedModel = cm;
        cached = new WeakReference(ras);
    }

    /**
     * Release the resources allocated for the operation.
     */
    public final void dispose() {
        if (saved != null) {
            putCachedRaster(model, saved);
            saved = null;
        }
    }

    /**
     * Return the ColorModel of the output.
     */
    public final ColorModel getColorModel() {
        return model;
    }
}

