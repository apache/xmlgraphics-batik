/****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;

/**
 * This class creates a noise pattern conform to the one defined for
 * the feTurbulence filter of the SVG specification. It can be used by
 * classes implementing specific interfaces, such as the TurbulenceOp
 * and TurbulencePaintContext classes.
 *
 * @author     <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class TurbulencePatternGenerator{
    /**
     * Inner class to store tile stitching info.
     * #see
     */
    static final class StitchInfo {
        /**
         * Width of the integer lattice tile
         */
        int width;

        /**
         * Height of the integer lattice tile
         */
        int height;

        /**
         * Value beyond which values are wrapped on
         * the x-axis.
         * @see #noise2Stitch
         */
        int wrapX;

        /**
         * Value beyond which values are wrapped on
         * the y-axis.
         * @see #noise2Stitch
         */
        int wrapY;

        /**
         * Default constructor
         */
        StitchInfo(){
        }

        /**
         * Copy constructor
         */
        StitchInfo(StitchInfo stitchInfo){
            this.width = stitchInfo.width;
            this.height = stitchInfo.height;
            this.wrapX = stitchInfo.wrapX;
            this.wrapY = stitchInfo.wrapY;
        }

        /*
         * Adjustst the StitchInfo for when the frequency has been
         * doubled.
         *
         *  width = tileWidth*baseFrequencyX
         *  height = tileHeight*baseFrequencyY
         *  minY = tileY*baseFrequencyY + PerlinN
         *  wrapX = tileX*baseFrequencyX + PerlinN + width
         *  wrapY = tileY*baseFrequencyY + PerlinN + height
         *
         */
        final void doubleFrequency(){
            width *= 2;
            height *= 2;
            wrapX *= 2;
            wrapY *= 2;
            wrapX -= PerlinN;
            wrapY -= PerlinN;
        }
    }

    /**
     * Used when stitching is on
     */
    private StitchInfo stitchInfo;

    /**
     * Identity transform, default used when null input in generatePattern
     * @see #generatePattern
     */
    private static final AffineTransform IDENTITY = new AffineTransform();

    /**
     *  x-axis base frequency for the noise function along the x-axis
     */
    private double baseFrequencyX;

    /**
     * y-axis base frequency for the noise function along the y-axis
     */
    private double baseFrequencyY;

    /**
     * Number of octaves in the noise function
     */
    private int numOctaves;

    /**
     * Starting number for the pseudo random number generator
     */
    private int seed;

    /**
     * Defines whether frequencies should be adjusted so as to avoid discontinuities in case
     * frequencies do not match image boundaries.
     */
    private boolean stitchTiles;

    /**
     * Defines the tile for the turbulence function
     */
    private Rectangle2D tile;

    /**
     * Defines whether the filter performs a fractal noise or a turbulence function
     */
    private boolean isFractalNoise;

    /**
     * List of channels that the generator produces.
     */
    private int channels[];

    /**
     * Produces results in the range [1, 2**31 - 2].
     * Algorithm is: r = (a * r) mod m
     * where a = 16807 and m = 2**31 - 1 = 2147483647
     * See [Park & Miller], CACM vol. 31 no. 10 p. 1195, Oct. 1988
     * To test: the algorithm should produce the result 1043618065
     * as the 10,000th generated number if the original seed is 1.
     */
    private static final int RAND_m = 2147483647; /* 2**31 - 1 */
    private static final int RAND_a = 16807; /* 7**5; primitive root of m */
    private static final int RAND_q = 127773; /* m / a */
    private static final int RAND_r = 2836; /* m % a */

    private static final int BSize = 0x100;
    private static final int BM = 0xff;
    private static final double PerlinN = 0x1000;
    private static final int NP = 12 /* 2^PerlinN */;
    private static final int NM = 0xfff;
    private final int latticeSelector[] = new int[BSize + BSize + 2];
    private final double gradient[][][] = new double[4][BSize + BSize + 2][2];


    public double getBaseFrequencyX(){
        return baseFrequencyX;
    }

    public double getBaseFrequencyY(){
        return baseFrequencyY;
    }

    public int getNumOctaves(){
        return numOctaves;
    }

    public int getSeed(){
        return seed;
    }

    public boolean isStitchTiles(){
        return stitchTiles;
    }

    public Rectangle2D getTile(){
        return (Rectangle2D)tile.clone();
    }

    public boolean isFractalNoise(){
        return isFractalNoise;
    }

    public boolean[] getChannels(){
        boolean channels[] = new boolean[4];
        for(int i=0; i<this.channels.length; i++)
            channels[this.channels[i]] = true;

        return channels;
    }

    public final int setupSeed(int seed){
        if (seed <= 0) seed = -(seed % (RAND_m - 1)) + 1;
        if (seed > RAND_m - 1) seed = RAND_m - 1;
        return seed;
    }

    public final int random(int seed){
      int result = RAND_a * (seed % RAND_q) - RAND_r * (seed / RAND_q);
        if (result <= 0) result += RAND_m;
        return result;
    }

    private void init(int seed){
        double s = 0;
        int i=0, j=0, k=0;
        seed = setupSeed(seed);

        for(i = 0; i < BSize; i++)
            latticeSelector[i] = i;

        for(k = 0; k < 4; k++){
            for(i = 0; i < BSize; i++){
                for (j = 0; j < 2; j++)
                    gradient[k][i][j] = (double)(((seed = random(seed)) % (BSize + BSize)) - BSize) /
                        BSize;
                s = Math.sqrt(gradient[k][i][0] * gradient[k][i][0] + gradient[k][i][1] *
                              gradient[k][i][1]);
                gradient[k][i][0] /= s;
                gradient[k][i][1] /= s;
            }
        }
        while(--i > 0){
            k = latticeSelector[i];
            j = (seed = random(seed)) % BSize;
            latticeSelector[i] = latticeSelector[j];
            latticeSelector[j] = k;
        }
        for(i = 0; i < BSize + 2; i++){
            latticeSelector[BSize + i] = latticeSelector[i];
            for(k = 0; k < 4; k++)
                for(j = 0; j < 2; j++)
                    gradient[k][BSize + i][j] = gradient[k][i][j];
        }
    }


    private static final double s_curve(final double t) {
        return (t * t * (3.f - 2.f * t) );
    }

    private static final double lerp(double t, double a, double b) {
        return ( a + t * (b - a) );
    }

    private final void noise2(final double noise[], 
                              final double vec0, final double vec1,
                              final int channels[]){
        int bx0=0, bx1=0, by0=0, by1=0, b00=0, b10=0, b01=0, b11=0;
        double rx0=0, rx1=0, ry0=0, ry1=0, q[], sx=0, sy=0, a=0, b=0, t=0, u=0, v=0;
        int i=0, j=0;
        t = vec0 + PerlinN;
        bx0 = ((int)t) & BM;
        bx1 = (bx0+1) & BM;
        rx0 = t - (int)t;
        rx1 = rx0 - 1.0f;
        t = vec1 + PerlinN;
        by0 = ((int)t) & BM;
        by1 = (by0+1) & BM;
        ry0 = t - (int)t;
        ry1 = ry0 - 1.0f;
        i = latticeSelector[bx0];
        j = latticeSelector[bx1];
        b00 = latticeSelector[i + by0];
        b10 = latticeSelector[j + by0];
        b01 = latticeSelector[i + by1];
        b11 = latticeSelector[j + by1];
        sx = s_curve(rx0);
        sy = s_curve(ry0);

        for(i=0; i<channels.length; i++){
            q = gradient[channels[i]][b00]; u = rx0 * q[0] + ry0 * q[1];
            q = gradient[channels[i]][b10]; v = rx1 * q[0] + ry0 * q[1];
            a = lerp(sx, u, v);
            q = gradient[channels[i]][b01]; u = rx0 * q[0] + ry1 * q[1];
            q = gradient[channels[i]][b11]; v = rx1 * q[0] + ry1 * q[1];
            b = lerp(sx, u, v);
            noise[channels[i]] = lerp(sy, a, b);
        }

    }

    /**
     * This version of the noise function implements stitching.
     * If any of the lattice is on the right or bottom edge, the
     * function uses the the latice on the other side of the
     * tile, i.e., the left or right edge.
     */
    private final void noise2Stitch(final double noise[],
                                    final double vec0, final double vec1,
                                    final int channels[],
                                    final StitchInfo stitchInfo){
        int bx0=0, bx1=0, by0=0, by1=0, b00=0, b10=0, b01=0, b11=0;
        double rx0=0, rx1=0, ry0=0, ry1=0, q[], sx=0, sy=0, a=0, b=0, t=0, u=0, v=0;
        int i=0, j=0;
        t = vec0  + PerlinN;
        bx0 = ((int)t);
        bx1 = bx0+1;
        rx0 = t - (int)t;
        rx1 = rx0 - 1.0f;
        t = vec1 + PerlinN;
        by0 = ((int)t);
        by1 = by0+1;
        ry0 = t - (int)t;
        ry1 = ry0 - 1.0f;

        // Stitch lattice tile x coordinates
        bx0 = bx0 >= stitchInfo.wrapX? bx0 - stitchInfo.width : bx0;
        bx1 = bx1 >= stitchInfo.wrapX? bx1 - stitchInfo.width : bx1;

        // Stitch lattice tile y coordinates
        by0 = by0 >= stitchInfo.wrapY? by0 - stitchInfo.height : by0;
        by1 = by1 >= stitchInfo.wrapY? by1 - stitchInfo.height : by1;

        bx0 &= BM;
        bx1 &= BM;
        by0 &= BM;
        by1 &= BM;

        i = latticeSelector[bx0];
        j = latticeSelector[bx1];
        b00 = latticeSelector[i + by0];
        b10 = latticeSelector[j + by0];
        b01 = latticeSelector[i + by1];
        b11 = latticeSelector[j + by1];
        sx = s_curve(rx0);
        sy = s_curve(ry0);

        for(i=0; i<channels.length; i++){
            q = gradient[channels[i]][b00]; u = rx0 * q[0] + ry0 * q[1];
            q = gradient[channels[i]][b10]; v = rx1 * q[0] + ry0 * q[1];
            a = lerp(sx, u, v);
            q = gradient[channels[i]][b01]; u = rx0 * q[0] + ry1 * q[1];
            q = gradient[channels[i]][b11]; v = rx1 * q[0] + ry1 * q[1];
            b = lerp(sx, u, v);
            noise[channels[i]] = lerp(sy, a, b);
        }

    }

    /**
     * This is the heart of the turbulence calculation. It returns 'turbFunctionResult', as
     * defined in the spec.
     * @param rgb array for the four color components
     * @param point x and y coordinates of the point to process.
     * @param fSum array used to avoid reallocating double array for each pixel
     * @param noise array used to avoid reallocating double array for each pixel
     * @param numOctaves number of octaves (may be limited so that spatial frequency below
     *        half a pixel are not processed).
     * @param channels channels for which values should be computed
     */
    private final void turbulence(final int rgb[], 
                                  double pointX, 
                                  double pointY,
                                  final double fSum[],
                                  final double noise[], final int numOctaves, final int channels[]){
        fSum[0] = fSum[1] = fSum[2] = fSum[3] = noise[0] = noise[1] = noise[2] = noise[3] = 0;
        double ratio = 1;
        int i=0;
        pointX *= baseFrequencyX;
        pointY *= baseFrequencyY;
        for(int nOctave = 0; nOctave < numOctaves; nOctave++){
            noise2(noise, pointX, pointY, channels);

            for(i=0; i<channels.length; i++){
                noise[channels[i]] = noise[channels[i]]<0?-noise[channels[i]]:noise[channels[i]];
                fSum[channels[i]] += (noise[channels[i]] / ratio);
            }

            ratio *= 2;
            pointX *= 2;
            pointY *= 2;
        }

        rgb[0] = rgb[1] = rgb[2] = rgb[3] = 0;
        for(i=0; i<channels.length; i++){
            rgb[channels[i]] = (int)(fSum[channels[i]] * 255);
            rgb[channels[i]] = rgb[channels[i]] < 0? 0:rgb[channels[i]];
            rgb[channels[i]] = rgb[channels[i]] > 255? 255:rgb[channels[i]];
        }
    }

    /**
     * This is the heart of the turbulence calculation. It returns 'turbFunctionResult', as
     * defined in the spec.
     * @param rgb array for the four color components
     * @param point x and y coordinates of the point to process.
     * @param fSum array used to avoid reallocating double array for each pixel
     * @param noise array used to avoid reallocating double array for each pixel
     * @param numOctaves number of octaves (may be limited so that spatial frequency below
     *        half a pixel are not processed).
     * @param channels channels for which values should be computed
     */
    private final void turbulenceStitch(final int rgb[], 
                                        double pointX, double pointY,
                                        final double fSum[],
                                        final double noise[], final int numOctaves, final int channels[]){
        fSum[0] = fSum[1] = fSum[2] = fSum[3] = noise[0] = noise[1] = noise[2] = noise[3] = 0;
        double ratio = 1;
        int i=0;
        pointX *= baseFrequencyX;
        pointY *= baseFrequencyY;
        StitchInfo stitchInfo = new StitchInfo(this.stitchInfo);
        for(int nOctave = 0; nOctave < numOctaves; nOctave++){
            noise2Stitch(noise, pointX, pointY, channels, stitchInfo);

            for(i=0; i<channels.length; i++){
                noise[channels[i]] = noise[channels[i]]<0?-noise[channels[i]]:noise[channels[i]];
                fSum[channels[i]] += (noise[channels[i]] / ratio);
            }

            ratio *= 2;
            pointX *= 2;
            pointY *= 2;

            stitchInfo.doubleFrequency();
        }

        rgb[0] = rgb[1] = rgb[2] = rgb[3] = 0;
        for(i=0; i<channels.length; i++){
            rgb[channels[i]] = (int)(fSum[channels[i]] * 255);
            rgb[channels[i]] = rgb[channels[i]] < 0? 0:rgb[channels[i]];
            rgb[channels[i]] = rgb[channels[i]] > 255? 255:rgb[channels[i]];
        }
    }

    /**
     * This is the heart of the turbulence calculation. It returns 'turbFunctionResult', as
     * defined in the spec.
     * @param rgb array for the four color components
     * @param point x and y coordinates of the point to process.
     * @param fSum array used to avoid reallocating double array for each pixel
     * @param noise array used to avoid reallocating double array for each pixel
     * @param numOctaves number of octaves (may be limited so that spatial frequency below
     *        half a pixel are not processed).
     * @param channels channels for which values should be computed
     */
    private final void turbulenceFractal(final int rgb[], 
                                         double pointX, 
                                         double pointY,
                                         final double fSum[],
                                         final double noise[], final int numOctaves, final int channels[]){
        fSum[0] = fSum[1] = fSum[2] = fSum[3] = noise[0] = noise[1] = noise[2] = noise[3] = 0;
        // double vec[] = new double[2];
        double ratio = 1;
        int i=0;
        pointX *= baseFrequencyX;
        pointY *= baseFrequencyY;
        for(int nOctave = 0; nOctave < numOctaves; nOctave++){
            noise2(noise, pointX, pointY, channels);

            for(i=0; i<channels.length; i++)
                fSum[channels[i]] += (noise[channels[i]] / ratio);

            ratio *= 2;
            pointX *= 2;
            pointY *= 2;
        }

        rgb[0] = rgb[1] = rgb[2] = rgb[3] = 0;
        for(i=0; i<channels.length; i++){
            fSum[channels[i]] += 1;
            fSum[channels[i]] /= 2;
            rgb[channels[i]] = (int)(fSum[channels[i]] * 255);
            rgb[channels[i]] = rgb[channels[i]] < 0? 0:rgb[channels[i]];
            rgb[channels[i]] = rgb[channels[i]] > 255? 255:rgb[channels[i]];
        }

    }

    /**
     * This is the heart of the turbulence calculation. It returns 'turbFunctionResult', as
     * defined in the spec.
     * @param rgb array for the four color components
     * @param point x and y coordinates of the point to process.
     * @param fSum array used to avoid reallocating double array for each pixel
     * @param noise array used to avoid reallocating double array for each pixel
     * @param numOctaves number of octaves (may be limited so that spatial frequency below
     *        half a pixel are not processed).
     * @param channels channels for which values should be computed
     */
    private final void turbulenceFractalStitch(final int rgb[], 
                                               double pointX,
                                               double pointY,
                                               double fSum[],
                                               final double noise[], 
                                               final int numOctaves, 
                                               final int channels[]){
        fSum[0] = fSum[1] = fSum[2] = fSum[3] = noise[0] = noise[1] = noise[2] = noise[3] = 0;
        // double vec[] = new double[2];
        double ratio = 1;
        int i=0;
        pointX *= baseFrequencyX;
        pointY *= baseFrequencyY;
        StitchInfo stitchInfo = new StitchInfo(this.stitchInfo);

        for(int nOctave = 0; nOctave < numOctaves; nOctave++){
            noise2Stitch(noise, pointX, pointY, channels, stitchInfo);

            for(i=0; i<channels.length; i++)
                fSum[channels[i]] += (noise[channels[i]] / ratio);

            ratio *= 2;
            pointX *= 2;
            pointY *= 2;

            stitchInfo.doubleFrequency();
        }

        rgb[0] = rgb[1] = rgb[2] = rgb[3] = 0;
        for(i=0; i<channels.length; i++){
            fSum[channels[i]] += 1;
            fSum[channels[i]] /= 2;
            rgb[channels[i]] = (int)(fSum[channels[i]] * 255);
            rgb[channels[i]] = rgb[channels[i]] < 0? 0:rgb[channels[i]];
            rgb[channels[i]] = rgb[channels[i]] > 255? 255:rgb[channels[i]];
        }

    }

    /**
     * Generates a Perlin noise pattern into dest Raster.
     *
     * @param txf image space to noise space transform. The 'noise space' is the
     *        space where the spatial characteristics of the noise are defined.
     * @param des Raster where the pattern should be generated.
     */
    public void generatePattern(WritableRaster dest, AffineTransform txf){
        if(txf == null)
            txf = IDENTITY;

        //
        // First, check input arguments
        //
        if(dest!=null)
            checkCompatible(dest.getSampleModel());
        else
            throw new IllegalArgumentException("Cannot generate a noise pattern on a null raster");

        //
        // Now, limit the number of octaves so that we do not get frequencies
        // below half a pixel.
        //
        // If d is the distance between to pixels in user space, then,
        // numOctavesMax = -(log2(d) + log2(bf))
        // along one axis.
        //
        // The maximum distance along each axis is processed by computing the
        // inverse transform of 'maximum' vectors from device space to the filter space
        // and determining the maximum component along each axis.
        //

        double vecX[] = {.5, 0};
        double vecY[] = {0, .5};
        txf.deltaTransform(vecX, 0, vecX, 0, 1);
        txf.deltaTransform(vecY, 0, vecY, 0, 1);

        double dx = Math.max(Math.abs(vecX[0]), Math.abs(vecY[0]));
        int maxX = -(int)Math.round((Math.log(dx) + Math.log(baseFrequencyX))/Math.log(2));

        double dy = Math.max(Math.abs(vecX[1]), Math.abs(vecY[1]));
        int maxY = -(int)Math.round((Math.log(dy) + Math.log(baseFrequencyY))/Math.log(2));

        int numOctaves = this.numOctaves;
        numOctaves = numOctaves > maxX? maxX : numOctaves;
        numOctaves = numOctaves > maxY? maxY : numOctaves;

        if(numOctaves < 1 && this.numOctaves > 1)
            numOctaves = 1;

        int w = dest.getWidth();
        int h = dest.getHeight();

        // Access the integer buffer for the destination Raster
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        SinglePixelPackedSampleModel sppsm;
        int minX = dest.getMinX();
        int minY = dest.getMinY();
        sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        int dstOff = dstDB.getOffset() +
            sppsm.getOffset(minX - dest.getSampleModelTranslateX(),
                            minY - dest.getSampleModelTranslateY());

        int dstScanStride = sppsm.getScanlineStride();
        final int destPixels[] = dstDB.getBankData()[0];
        int dstAdjust = dstScanStride - w;

        // Generate pixel pattern now
        int r=0, g=0, b=0, a=0;
        int dp=dstOff;
        double rx=0, ry=0, tpoint_0=0, tpoint_1=0;
        final int rgb[] = new int[4];
        final int rgb2[] = new int[4];
        final int rgb3[] = new int[4];
        final int rgb4[] = new int[4];
        final int rgb5[] = new int[4];
        final double fSum[] = {0, 0, 0, 0};
        final double noise[] = {0, 0, 0, 0};

        // To avoid doing an inverse transform on each pixel, transform
        // the image space unit vectors and process how much of a delta
        // this is in filter space.
        double tx[] = {1, 0};
        double ty[] = {0, 1};
        txf.deltaTransform(tx, 0, tx, 0, 1);
        txf.deltaTransform(ty, 0, ty, 0, 1);
        double tx0 = tx[0];
        double tx1 = tx[1];
        double ty0 = ty[0];
        double ty1 = ty[1];
        double p[] = {minX, minY};
        txf.transform(p, 0, p, 0, 1);

        if(isFractalNoise){
            if(!stitchTiles){
                for(int i=0; i<h; i++){
                    double point_0 = p[0];
                    double point_1 = p[1];

                    for(int j=0; j<w; j++){
                        turbulenceFractal(rgb, point_0, point_1, fSum, noise, 
                                          numOctaves, channels);

                        // Modify RGB value.
                        destPixels[dp] =
                            (rgb[3]<<24 & 0xff000000) |
                            (rgb[0]<<16 & 0xff0000) |
                            (rgb[1]<<8  & 0xff00) |
                            (rgb[2]     & 0xff);
                        dp++;
                        point_0 += tx0;
                        point_1 += tx1;
                    }
                    p[0] += ty0;
                    p[1] += ty1;
                    dp += dstAdjust;
                }
            }

            else{
                for(int i=0; i<h; i++){
                    double point_0 = p[0];
                    double point_1 = p[1];

                    for(int j=0; j<w; j++){
                        turbulenceFractalStitch(rgb, point_0, point_1,
                                                fSum, noise, 
                                                numOctaves, channels);

                        // Modify RGB value.
                        destPixels[dp] =
                            (rgb[3]<<24 & 0xff000000) |
                            (rgb[0]<<16 & 0xff0000) |
                            (rgb[1]<<8  & 0xff00) |
                            (rgb[2]     & 0xff);
                        dp++;
                        point_0 += tx0;
                        point_1 += tx1;
                    }
                    p[0] += ty0;
                    p[1] += ty1;
                    dp += dstAdjust;
                }
            }
        }
        else{ // Loop for turbulence noise
            if(!stitchTiles){
                for(int i=0; i<h; i++){
                    double point_0 = p[0];
                    double point_1 = p[1];

                    for(int j=0; j<w; j++){
                        turbulence(rgb, point_0, point_1, fSum, noise, 
                                   numOctaves, channels);

                        // Modify RGB value.
                        destPixels[dp] =
                            (rgb[3]<<24 & 0xff000000) |
                            (rgb[0]<<16 & 0xff0000) |
                            (rgb[1]<<8  & 0xff00) |
                            (rgb[2]     & 0xff);
                        dp++;
                        point_0 += tx0;
                        point_1 += tx1;
                    }
                    p[0] += ty0;
                    p[1] += ty1;
                    dp += dstAdjust;
                }
            }
            else{
                for(int i=0; i<h; i++){
                    double point_0 = p[0];
                    double point_1 = p[1];

                    for(int j=0; j<w; j++){
                        turbulenceStitch(rgb, point_0, point_1, fSum, noise, 
                                         numOctaves, channels);

                        // Modify RGB value.
                        destPixels[dp] =
                            (rgb[3]<<24 & 0xff000000) |
                            (rgb[0]<<16 & 0xff0000) |
                            (rgb[1]<<8  & 0xff00) |
                            (rgb[2]     & 0xff);
                        dp++;
                        point_0 += tx0;
                        point_1 += tx1;
                    }
                    p[0] += ty0;
                    p[1] += ty1;
                    dp += dstAdjust;
                }
            }
        }
    }

    /**
     * Checks that input SampleModel is compatible with TurbulenceOp.
     * The TurbulencePatternGenerator only operates on SinglePixelPackedSampleModels with
     * 4 bands, each band using 8 bits and pixels packed into integers.
     *
     * @param model the input SampleModel
     * @throws IllegalArgumentException if Raster does not have the expected structure (See above
     *         description).
     */
    private void checkCompatible(SampleModel model){
        // Check model is ok: should be SinglePixelPackedSampleModel
        if(!(model instanceof SinglePixelPackedSampleModel))
            throw new IllegalArgumentException("TurbulenceOp only works with Rasters using SinglePixelPackedSampleModels");

        // Check number of bands
        int nBands = model.getNumBands();
        if(nBands!=4)
            throw new IllegalArgumentException("TurbulenceOp only words with Rasters having 4 bands");

        // Check that integer packed.
        if(model.getDataType()!=DataBuffer.TYPE_INT)
            throw new IllegalArgumentException("TurbulenceOp only works with Rasters using DataBufferInts");

        // Check bit masks
        int bitOffsets[] = ((SinglePixelPackedSampleModel)model).getBitOffsets();
        for(int i=0; i<bitOffsets.length; i++){
            if(bitOffsets[i]%8 != 0)
                throw new IllegalArgumentException("TurbulenceOp only works with Rasters using 8 bits per band : " + i + " : " + bitOffsets[i]);
        }
    }

    /**
     * @param baseFrequencyX x-axis base frequency for the noise
     * function along the x-axis
     * @param baseFrequencyY y-axis base frequency for the noise
     *        function along the x-axis
     * @param numOctaves number of octaves in the noise
     *        function. Positive integral value.
     * @param seed starting number for the pseudo random number generator
     * @param stitchTiles defines whether frequencies should be
     *        adjusted so as to avoid discontinuities.
     * @param isFractalNoise defines whether the filter performs a
     *        fractal noise or a turbulence function.
     * @param tile defines the tile size. May be null if stitchTiles
     *        is false. Otherwise, should not be null.
     * @param channels boolean array defining which of the sRGB
     *        channels should contain noise. 0 is red, 1 is green, 2
     *        is blue and 3 is alpha.  
     */
    public TurbulencePatternGenerator(double baseFrequencyX, 
                                      double baseFrequencyY, int numOctaves,
                                      int     seed, 
                                      boolean stitchTiles, 
                                      boolean isFractalNoise,
                                      Rectangle2D tile, 
                                      boolean channels[]){
        this.baseFrequencyX = baseFrequencyX;
        this.baseFrequencyY = baseFrequencyY;
        this.numOctaves = numOctaves;
        this.seed = seed;
        this.stitchTiles = stitchTiles;
        this.isFractalNoise = isFractalNoise;
        this.tile = tile;

        if((channels == null) || (channels.length == 0))
            throw new IllegalArgumentException("Cannot process null channel array. channels should have length=4");
        else{
            boolean tmpChannels[] = new boolean[channels.length];
            System.arraycopy(channels, 0, tmpChannels, 0, channels.length);
            channels = tmpChannels;
            int nChannels = 0;
            for(int i=0; i<channels.length; i++)
                if(channels[i]) nChannels++;

            this.channels = new int[nChannels];
            int curChannel = 0;
            for(int i=0; i<channels.length; i++)
                if(channels[i]) this.channels[curChannel++] = i;

        }



        if((stitchTiles == true) && (tile == null))
            throw new IllegalArgumentException("tile should not be null when stitchTile is true");

        if(stitchTiles){
            //
            // Adjust frequencies to the tile size
            //
            double lowFreq = Math.floor(tile.getWidth()*baseFrequencyX)/tile.getWidth();
            double highFreq = Math.ceil(tile.getWidth()*baseFrequencyX)/tile.getWidth();
            if(baseFrequencyX/lowFreq < highFreq/baseFrequencyX)
                this.baseFrequencyX = lowFreq;
            else
                this.baseFrequencyX = highFreq;

            lowFreq = Math.floor(tile.getHeight()*baseFrequencyY)/tile.getHeight();
            highFreq = Math.ceil(tile.getHeight()*baseFrequencyY)/tile.getHeight();
            if(baseFrequencyY/lowFreq < highFreq/baseFrequencyY)
                this.baseFrequencyY = lowFreq;
            else
                this.baseFrequencyY = highFreq;

            //
            // Now, process the initial latice grid size to compute the minimum
            // and maximum latice values on each axis.
            //
            stitchInfo = new StitchInfo();
            stitchInfo.width = ((int)(tile.getWidth()*this.baseFrequencyX));
            stitchInfo.height = ((int)(tile.getHeight()*this.baseFrequencyY));
            stitchInfo.wrapX = ((int)(tile.getX()*this.baseFrequencyX + PerlinN + stitchInfo.width));
            stitchInfo.wrapY = ((int)(tile.getY()*this.baseFrequencyY + PerlinN + stitchInfo.height));

            // Protect agains zero frequencies.
            // Setting values to 1 will not affect the result of the computations.
            if(stitchInfo.width == 0) stitchInfo.width = 1;
            if(stitchInfo.height == 0) stitchInfo.height = 1;

            // System.out.println("minLatticeX = " + minLatticeX + " minLatticeY = " + minLatticeY + " maxLatticeX = " + maxLatticeX + " maxLatticeY = " + maxLatticeY);
        }

        init(seed);
    }

}
