/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImageOp;
import java.awt.image.RasterOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.Kernel;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import java.awt.image.ConvolveOp;
import java.awt.image.ColorConvertOp;

/**
 * This class provides an implementation for the SVG
 * feGaussianBlurOp filter, as defined in chapter 15, section 17
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */

public class GaussianBlurOp implements BufferedImageOp, RasterOp {
    /**
     * Constant: sqrt(2*PI)
     */
    static final float SQRT2PI = (float)Math.sqrt(2*Math.PI);

    /**
     * Constant: 3*sqrt(2*PI)/4
     */
    static final float DSQRT2PI = SQRT2PI*3f/4f;

    /**
     * Constant: precision used in computation of the Kernel radius
     */
    static final float precision = 0.499999999999999999f;

    /*
     * sRGB ColorSpace instance used for compatibility checking
     */
    private final ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    /*
     * Linear RGB ColorSpace instance used for compatibility checking
     */
    private final ColorSpace lRGB =
        ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);

    /**
     * Standard Deviation on X-axis
     */
    private float stdDeviationX;

    /**
     * Standard Deviation on Y-axis
     */
    private float stdDeviationY;

    /**
     * The diameter of the approximation blur-box
     */
    final private int d;
    /**
     * The X-axis radius of the Kernel
     */
    final private int radiusX;

    /**
     * The Y-axis radius of the Kernel
     */
    final private int radiusY;

    /*
     * Whether to use convolveOp.
     */
    private boolean useConvolveOp;

    /**
     * This hint is used by the user to choose between
     * exact or approximate filtering
     */
    private RenderingHints hints;


    /*
     * This array stores the ConvolveOp objects used to
     * do the filtering
     */
    ConvolveOp conv [];

    public int getRadiusX() { return radiusX; }

    public int getRadiusY() { return radiusY; }

    /*
     * Here we compute the data for the one-dimensional kernel of
     * length '2*radius + 1'
     *
     * @param radius stdDeviationX or stdDeviationY.
     * @see #makeQualityKernels */
    private float [] computeQualityKernelData(int radius, float stdDev){
        final float kernelData[] = new float [radius * 2 + 1];
        float sum = 0; // Used to normalise the kernel
        final int w = 2*radius+1; // Kernel width

        for(int i=0; i<w; i++){
            kernelData[i] = (float)(Math.pow(Math.E, -(i-radius)*(i-radius)/
                                             (2*stdDev*stdDev)) /
                                    (SQRT2PI*stdDev));
            sum += kernelData[i];
        }

        // Normalise: make elements sum to 1
        for(int i=0; i<w; i++){
            kernelData[i] /= sum;
        }

        return kernelData;
    }

    private Kernel [] makeQualityKernels(){
        Kernel result [];
        Kernel kernelX = new Kernel(2*radiusX+1, 1,
                                    computeQualityKernelData(radiusX,
                                                             stdDeviationX));
        Kernel kernelY = new Kernel(1, 2*radiusY+1,
                                    computeQualityKernelData(radiusY,
                                                             stdDeviationY));
        result = new Kernel []{kernelX, kernelY};
        return result;
    }

    /**
     * The constructor will make the convolveOp filters
     * according to the standard deviation and rendering hint provided
     * by the user
     */

    public GaussianBlurOp(double stdDeviationX,
                          double stdDeviationY,
                          RenderingHints hints) {
        this.stdDeviationX = (float)stdDeviationX;
        this.stdDeviationY = (float)stdDeviationY;
        this.hints = hints;
        if ( (stdDeviationX != stdDeviationY)
             ||
             hints.VALUE_RENDER_QUALITY.equals(hints.get(hints.KEY_RENDERING))
             ||
             (stdDeviationX < 2)
             ) {

            // compute the radiusX here
            float areaSum = 0f;
            float item;
            int i=0, j=0;
            while (areaSum < precision){
                item =  (float)(Math.pow(Math.E, -i*i/
                                         (2*stdDeviationX*stdDeviationX)) /
                                (stdDeviationX*SQRT2PI));
                areaSum += item;
                i++;
            }
            radiusX = i;
            // compute the radiusY here
            areaSum = 0f;
            while (areaSum < precision){
                item =  (float)(Math.pow(Math.E, -j*j/
                                         (2*stdDeviationY*stdDeviationY)) /
                                (stdDeviationY*SQRT2PI));
                areaSum += item;
                j++;
            }
            radiusY = j;
            d = 0;
            Kernel k[] = makeQualityKernels();
            useConvolveOp = true;
            conv = new ConvolveOp [] { new ConvolveOp(k[0]),
                                           new ConvolveOp(k[1])};
        }
        else {
            // stdDeviationX and stdDeviationY are equal and greater than or
            // equal to 2. The user asked for fast rendering.

            //compute d
            d = (int)Math.floor(DSQRT2PI*stdDeviationX+0.5f);
            radiusX = d/2;
            radiusY = d/2;
            useConvolveOp = false;
        }
    }

    public GaussianBlurOp(double stdDeviation, RenderingHints hints){
        this(stdDeviation, stdDeviation, hints);
    }

    public Rectangle2D getBounds2D(Raster src){
        checkCompatible(src.getSampleModel());
        return new Rectangle(src.getMinX(),  src.getMinY(),
                             src.getWidth(), src.getHeight());
    }

    public Rectangle2D getBounds2D(BufferedImage src){
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public Point2D getPoint2D(Point2D srcPt, Point2D destPt){
        // This operation does not affect pixel location
        if(destPt==null)
            destPt = new Point2D.Float();
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
    }

    private void checkCompatible(ColorModel  colorModel,
				 SampleModel sampleModel){
        ColorSpace cs = colorModel.getColorSpace();

        // Check that model is sRGB or linear RGB
        if((cs != sRGB) || (cs != lRGB))
            throw new IllegalArgumentException
                ("Expected CS_sRGB or CS_LINEAR_RGB color model");

        // Check ColorModel is of type DirectColorModel
        if(!(colorModel instanceof DirectColorModel))
            throw new IllegalArgumentException
                ("colorModel should be an instance of DirectColorModel");

        // Check transfer type
        if(sampleModel.getDataType() != DataBuffer.TYPE_INT)
            throw new IllegalArgumentException
                ("colorModel's transferType should be DataBuffer.TYPE_INT");

        // Check red, green, blue and alpha mask
        DirectColorModel dcm = (DirectColorModel)colorModel;
        if(dcm.getRedMask() != 0x00ff0000)
            throw new IllegalArgumentException
                ("red mask in source should be 0x00ff0000");
        if(dcm.getGreenMask() != 0x0000ff00)
            throw new IllegalArgumentException
                ("green mask in source should be 0x0000ff00");
        if(dcm.getBlueMask() != 0x000000ff)
            throw new IllegalArgumentException
                ("blue mask in source should be 0x000000ff");
        if(dcm.getAlphaMask() != 0xff000000)
            throw new IllegalArgumentException
                ("alpha mask in source should be 0xff000000");
    }

    private boolean isCompatible(ColorModel  colorModel,
				 SampleModel sampleModel){
        ColorSpace cs = colorModel.getColorSpace();
        // Check that model is sRGB or linear RGB
        if((cs != ColorSpace.getInstance(ColorSpace.CS_sRGB))
           ||
           (cs != ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB)))
            return false;

        // Check ColorModel is of type DirectColorModel
        if(!(colorModel instanceof DirectColorModel))
            return false;

        // Check transfer type
        if(sampleModel.getDataType() != DataBuffer.TYPE_INT)
            return false;

        // Check red, green, blue and alpha mask
        DirectColorModel dcm = (DirectColorModel)colorModel;
        if(dcm.getRedMask() != 0x00ff0000)
            return false;
        if(dcm.getGreenMask() != 0x0000ff00)
            return false;
        if(dcm.getBlueMask() != 0x000000ff)
            return false;
        if(dcm.getAlphaMask() != 0xff000000)
            return false;
        return true;
    }

    private void checkCompatible(SampleModel model){
        // Check model is ok: should be SinglePixelPackedSampleModel
        if(!(model instanceof SinglePixelPackedSampleModel))
            throw new IllegalArgumentException
                ("GaussianBlurOp only works with Rasters "+
                 "using SinglePixelPackedSampleModels");
        // Check number of bands
        int nBands = model.getNumBands();
        if(nBands!=4)
            throw new IllegalArgumentException
                ("GaussianBlurOp only words with Rasters having 4 bands");

        // Check that integer packed.
        if(model.getDataType()!=DataBuffer.TYPE_INT)
            throw new IllegalArgumentException
                ("GaussianBlurOp only works with Rasters using DataBufferInt");
        // Check bit masks
        int bitOffsets[]=((SinglePixelPackedSampleModel)model).getBitOffsets();
        for(int i=0; i<bitOffsets.length; i++){
            if(bitOffsets[i]%8 != 0)
                throw new IllegalArgumentException
                    ("GaussianBlurOp only works with Rasters using 8 bits " +
                     "per band : " + i + " : " + bitOffsets[i]);
        }
    }

    public RenderingHints getRenderingHints(){
        return this.hints;
    }

    public WritableRaster createCompatibleDestRaster(Raster src){
        checkCompatible(src.getSampleModel());
        // Src Raster is OK: create a similar Raster for destination.
        return src.createCompatibleWritableRaster();
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src,
                                                   ColorModel  destCM){
        WritableRaster wr;
        wr = destCM.createCompatibleWritableRaster(src.getWidth(),
                                                   src.getHeight());
        BufferedImage dest = null;
        if (destCM==null)
            destCM = ColorModel.getRGBdefault();
        else
            checkCompatible(destCM, wr.getSampleModel());

        dest = new BufferedImage(destCM, wr,
                                 destCM.isAlphaPremultiplied(), null);
        return dest;
    }

    private void specialProcessRow(Raster src, WritableRaster dest){
        System.out.println("Error!");
    }
    private void specialProcessColumn(Raster src, WritableRaster dest){
        System.out.println("Error!");
    }

    private WritableRaster oddApproximation(Raster src, WritableRaster dest){
        final int w = src.getWidth();
        final int h = src.getHeight();

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int srcOff = srcDB.getOffset();
        final int dstOff = dstDB.getOffset();

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = ((SinglePixelPackedSampleModel)
                                   src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)
                                   dest.getSampleModel()).getScanlineStride();

        // Access the pixel value array
        final int srcPixels[] = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];

        // The pointer of src and dest indicating where the pixel values are
        int sp, dp, cp;

        // Declaration for the circular buffer's implementation
        // bufferHead points to the leftmost element in the circular buffer
        int bufferHead;

        // Temp variables to store Pixel values
        int pel, currentPixel, lastPixel;

        // Current length of the buffer
        int l;

        //
        // The first round: process by row
        //

        if (w<d/2){
            specialProcessRow(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int i=0; i<h; i++){
                // initialization of pointers, indice
                // at the head of each row
                sp = srcOff + i*srcScanStride;
                dp = dstOff + i*dstScanStride;
                bufferHead = 0;

                //
                // j=0 : Compute the first max/min value and store
                //       pixel values in the circular buffer for later use
                //
                pel = srcPixels[sp++];
                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusX; k++){
                    currentPixel = srcPixels[sp++];
                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l = radiusX+1;
                destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                    (((sumR/l)<<16) & 0x00ff0000) |
                                    (((sumG/l)<<8)  & 0x0000ff00) |
                                    (  sumB/l       & 0x000000ff));

                //
                // 1 <= j <= radiusX : The left margin of each row.
                //
                for (int j=1; j<=radiusX; j++){
                    final int lastJ = j + radiusX;
                    lastPixel = srcPixels[sp++];
                    bufferA[lastJ] = (lastPixel>>24)&0xff;
                    bufferR[lastJ] = (lastPixel>>16)&0xff;
                    bufferG[lastJ] = (lastPixel>>8)&0xff;
                    bufferB[lastJ] = lastPixel&0xff;
                    sumA += bufferA[lastJ];
                    sumR += bufferR[lastJ];
                    sumG += bufferG[lastJ];
                    sumB += bufferB[lastJ];
                    l++;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l &       0x000000ff));
                }

                //
                // radiusX +1 <= j <= w-1-radiusX :
                //     Inner body of the row, between left and right margins
                //
                for (int j=radiusX+1; j<=w-1-radiusX; j++){
                    lastPixel = srcPixels[sp++];

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // w-radiusX <= j < w : The right margin of the row
                //

                for (int j=w-radiusX; j<w; j++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }
            }
        }

        //
        // The second round: process by column
        //

        // When the image size is smaller than the
        // Kernel size
        if (h<2*radiusY){
            specialProcessColumn(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int j=0; j<w; j++){
                // initialization of pointers, indice
                // at the head of each column
                dp = dstOff + j;
                cp = dstOff + j;
                bufferHead = 0;

                // i=0 : The first pixel
                pel = destPixels[cp];
                cp += dstScanStride;

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8) &0xff;
                bufferB[0] =  pel     &0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];

                for (int k=1; k<=radiusY; k++){
                    currentPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8) &0xff;
                    bufferB[k] =  currentPixel     &0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l = radiusY+1;
                destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                  (((sumR/l)<<16) & 0x00ff0000) |
                                  (((sumG/l)<<8)  & 0x0000ff00) |
                                  (  sumB/l       & 0x000000ff));
                dp += dstScanStride;

                // 1 <= i <= radiusY : The upper margin of each row
                for (int i=1; i<=radiusY; i++){
                    final int lastI = i+radiusY;
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[lastI] = (lastPixel>>24)&0xff;
                    bufferR[lastI] = (lastPixel>>16)&0xff;
                    bufferG[lastI] = (lastPixel>>8)&0xff;
                    bufferB[lastI] = lastPixel&0xff;
                    sumA += bufferA[lastI];
                    sumR += bufferR[lastI];
                    sumG += bufferG[lastI];
                    sumB += bufferB[lastI];
                    l++;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                }

                //
                // radiusY +1 <= i <= h-1-radiusY:
                //    inner body of the column between upper and lower margins
                //

                for (int i=radiusY+1; i<=h-1-radiusY; i++){
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // h-radiusY <= i <= h-1 : The lower margin of the column
                //

                for (int i= h-radiusY; i<h; i++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }
                // return to the beginning of the next column
            }
        }// end of the second round!
        return dest;
    }

    private WritableRaster evenApproximation(Raster src, WritableRaster dest) {
        final int w = src.getWidth();
        final int h = src.getHeight();

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int srcOff = srcDB.getOffset();
        final int dstOff = dstDB.getOffset();

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = ((SinglePixelPackedSampleModel)
                                   src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)
                                   dest.getSampleModel()).getScanlineStride();

        // Access the pixel value array
        final int srcPixels[] = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];

        // The pointer of src and dest indicating where the pixel values are
        int sp, dp, cp;

        // Declaration for the circular buffer's implementation
        // These are the circular buffers' head pointer and
        // the index pointers

        // bufferHead points to the leftmost element in the circular buffer
        int bufferHead;

        // Temp variables
        int pel, currentPixel, lastPixel;

        // Current length of the circular buffer, for use of averaging
        int l;

        //
        // This is the first blur box:
        // one pixel left to the center
        //

        //
        // The first round: process by row
        //

        if (w<d/2){
            specialProcessRow(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int i=0; i<h; i++){
                // initialization of pointers, indice
                // at the head of each row
                sp = srcOff + i*srcScanStride;
                dp = dstOff + i*dstScanStride;
                bufferHead = 0;

                //
                // j=0 : Initialization, compute the max/min and
                //       index array for the use of other pixels.
                //
                pel = srcPixels[sp++];

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusX; k++){
                    currentPixel = srcPixels[sp++];

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l = radiusX+1;
                destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                    (((sumR/l)<<16) & 0x00ff0000) |
                                    (((sumG/l)<<8)  & 0x0000ff00) |
                                    (  sumB/l       & 0x000000ff));

                //
                // 1 <= j <= radiusX-1 : The left margin of each row.
                //
                for (int j=1; j<=radiusX-1; j++){
                    final int lastJ = j + radiusX;
                    lastPixel = srcPixels[sp++];
                    bufferA[lastJ] = (lastPixel>>24)&0xff;
                    bufferR[lastJ] = (lastPixel>>16)&0xff;
                    bufferG[lastJ] = (lastPixel>>8)&0xff;
                    bufferB[lastJ] = lastPixel&0xff;
                    sumA += bufferA[lastJ];
                    sumR += bufferR[lastJ];
                    sumG += bufferG[lastJ];
                    sumB += bufferB[lastJ];
                    l++;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                }

                //
                // radiusX <= j <= w-1-radiusX : Inner body of the row, between
                //                               left and right margins
                //
                for (int j=radiusX; j<=w-1-radiusX; j++){
                    lastPixel = srcPixels[sp++];

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp++] = (((sumA/l)<<24) & 0xff000000) | (((sumR/l)<<16) & 0xff0000) | (((sumG/l)<<8) & 0xff00) | (sumB/l & 0xff);
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // w-radiusX <= j < w : The right margin of the row
                //

                for (int j=w-radiusX; j<w; j++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }
            }
        }

        //
        // The second round: process by column
        //

        // When the image size is smaller than the
        // Kernel size
        if (h<2*radiusY){
            specialProcessColumn(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int j=0; j<w; j++){
                // initialization of pointers, indice
                // at the head of each column
                dp = dstOff + j;
                cp = dstOff + j;
                bufferHead = 0;

                // i=0 : The first pixel
                pel = destPixels[cp];
                cp += dstScanStride;

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusY; k++){
                    currentPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];

                }
                l= radiusY +1;
                destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                  (((sumR/l)<<16) & 0x00ff0000) |
                                  (((sumG/l)<<8)  & 0x0000ff00) |
                                  (  sumB/l       & 0x000000ff));
                dp += dstScanStride;

                // 1 <= i <= radiusY-1 : The upper margin of each row
                for (int i=1; i<=radiusY-1; i++){
                    final int lastI = i+radiusY;
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[lastI] = (lastPixel>>24)&0xff;
                    bufferR[lastI] = (lastPixel>>16)&0xff;
                    bufferG[lastI] = (lastPixel>>8)&0xff;
                    bufferB[lastI] = lastPixel&0xff;
                    sumA += bufferA[lastI];
                    sumR += bufferR[lastI];
                    sumG += bufferG[lastI];
                    sumB += bufferB[lastI];
                    l++;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                }

                //
                // radiusY +1 <= i <= h-1-radiusY:
                //    inner body of the column between upper and lower margins
                //

                for (int i=radiusY; i<=h-1-radiusY; i++){
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // h-radiusY <= i <= h-1 : The lower margin of the column
                //

                for (int i= h-radiusY; i<h; i++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }
                // return to the beginning of the next column
            }
        }//

        // End of the first blur box!

        //
        // This is the second blur box:
        // one pixel right to the center
        //

        //
        // The first round: process by row
        //

        if (w<d/2){
            specialProcessRow(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int i=0; i<h; i++){
                // initialization of pointers, indice
                // at the head of each row
                sp = srcOff + i*srcScanStride;
                dp = dstOff + i*dstScanStride;
                bufferHead = 0;

                //
                // j=0 : Initialization, compute the max/min and
                //       index array for the use of other pixels.
                //
                pel = srcPixels[sp++];

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusX-1; k++){
                    currentPixel = srcPixels[sp++];

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l= radiusX +1;
                destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                    (((sumR/l)<<16) & 0x00ff0000) |
                                    (((sumG/l)<<8)  & 0x0000ff00) |
                                    (  sumB/l       & 0x000000ff));

                //
                // 1 <= j <= radiusX : The left margin of each row.
                //
                for (int j=1; j<=radiusX; j++){
                    final int lastJ = j+radiusX-1;
                    lastPixel = srcPixels[sp++];
                    bufferA[lastJ] = (lastPixel>>24)&0xff;
                    bufferR[lastJ] = (lastPixel>>16)&0xff;
                    bufferG[lastJ] = (lastPixel>>8)&0xff;
                    bufferB[lastJ] = lastPixel&0xff;
                    sumA += bufferA[lastJ];
                    sumR += bufferR[lastJ];
                    sumG += bufferG[lastJ];
                    sumB += bufferB[lastJ];
                    l++;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                }

                //
                // radiusX+1 <= j <= w-radiusX : Inner body of the row, between
                //                               left and right margins
                //
                for (int j=radiusX+1; j<=w-radiusX; j++){
                    lastPixel = srcPixels[sp++];

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // w-radiusX+1 <= j < w : The right margin of the row
                //

                for (int j=w-radiusX+1; j<w; j++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }
            }
        }

        //
        // The second round: process by column
        //

        // When the image size is smaller than the
        // Kernel size
        if (h<2*radiusY){
            specialProcessColumn(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d];
            final int [] bufferR = new int [d];
            final int [] bufferG = new int [d];
            final int [] bufferB = new int [d];
            int sumA, sumR, sumG, sumB;
            for (int j=0; j<w; j++){
                // initialization of pointers, indice
                // at the head of each column
                dp = dstOff + j;
                cp = dstOff + j;
                bufferHead = 0;

                // i=0 : The first pixel
                pel = destPixels[cp];
                cp += dstScanStride;

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusY-1; k++){
                    currentPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];

                }
                l = radiusY+1;
                destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                  (((sumR/l)<<16) & 0x00ff0000) |
                                  (((sumG/l)<<8)  & 0x0000ff00) |
                                  (  sumB/l       & 0x000000ff));
                dp += dstScanStride;

                // 1 <= i <= radiusY : The upper margin of each row
                for (int i=1; i<=radiusY; i++){
                    final int lastI = i+radiusY-1;
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[lastI] = (lastPixel>>24)&0xff;
                    bufferR[lastI] = (lastPixel>>16)&0xff;
                    bufferG[lastI] = (lastPixel>>8)&0xff;
                    bufferB[lastI] = lastPixel&0xff;
                    sumA += bufferA[lastI];
                    sumR += bufferR[lastI];
                    sumG += bufferG[lastI];
                    sumB += bufferB[lastI];
                    l++;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                }

                //
                // radiusY +1 <= i <= h-radiusY:
                //    inner body of the column between upper and lower margins
                //

                for (int i=radiusY+1; i<=h-radiusY; i++){
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // h-radiusY+1 <= i <= h-1 : The lower margin of the column
                //

                for (int i= h-radiusY+1; i<h; i++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }
                // return to the beginning of the next column
            }
        }//

        // End of the second blur box!

        //
        // The third box blur: of width d+1
        //

        //
        // The first round: process by row
        //

        if (w<d/2){
            specialProcessRow(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d+1];
            final int [] bufferR = new int [d+1];
            final int [] bufferG = new int [d+1];
            final int [] bufferB = new int [d+1];
            int sumA, sumR, sumG, sumB;
            for (int i=0; i<h; i++){
                // initialization of pointers, indice
                // at the head of each row
                sp = srcOff + i*srcScanStride;
                dp = dstOff + i*dstScanStride;
                bufferHead = 0;

                //
                // j=0 : Compute the first max/min value and store
                //       pixel values in the circular buffer for later use
                //
                pel = srcPixels[sp++];
                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];
                for (int k=1; k<=radiusX; k++){
                    currentPixel = srcPixels[sp++];
                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l = radiusX+1;
                destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                    (((sumR/l)<<16) & 0x00ff0000) |
                                    (((sumG/l)<<8)  & 0x0000ff00) |
                                    (  sumB/l       & 0x000000ff));

                //
                // 1 <= j <= radiusX : The left margin of each row.
                //
                for (int j=1; j<=radiusX; j++){
                    final int lastJ = j + radiusX;
                    lastPixel = srcPixels[sp++];
                    bufferA[lastJ] = (lastPixel>>24)&0xff;
                    bufferR[lastJ] = (lastPixel>>16)&0xff;
                    bufferG[lastJ] = (lastPixel>>8)&0xff;
                    bufferB[lastJ] = lastPixel&0xff;
                    sumA += bufferA[lastJ];
                    sumR += bufferR[lastJ];
                    sumG += bufferG[lastJ];
                    sumB += bufferB[lastJ];
                    l++;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                }

                //
                // radiusX +1 <= j <= w-1-radiusX : Inner body of the row, between
                //                               left and right margins
                //
                for (int j=radiusX+1; j<=w-1-radiusX; j++){
                    lastPixel = srcPixels[sp++];

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));

                    bufferHead = (bufferHead+1)%d;
                }

                //
                // w-radiusX <= j < w : The right margin of the row
                //

                for (int j=w-radiusX; j<w; j++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp++] = ((((sumA/l)<<24) & 0xff000000) |
                                        (((sumR/l)<<16) & 0x00ff0000) |
                                        (((sumG/l)<<8)  & 0x0000ff00) |
                                        (  sumB/l       & 0x000000ff));
                    bufferHead = (bufferHead+1)%d;
                }
            }
        }

        //
        // The second round: process by column
        //

        // When the image size is smaller than the
        // Kernel size
        if (h<2*radiusY){
            specialProcessColumn(src, dest);
        }

        // when the size is large enough, we can
        // use standard optimization method
        else {
            final int [] bufferA = new int [d+1];
            final int [] bufferR = new int [d+1];
            final int [] bufferG = new int [d+1];
            final int [] bufferB = new int [d+1];
            int sumA, sumR, sumG, sumB;
            for (int j=0; j<w; j++){
                // initialization of pointers, indice
                // at the head of each column
                dp = dstOff + j;
                cp = dstOff + j;
                bufferHead = 0;

                // i=0 : The first pixel
                pel = destPixels[cp];
                cp += dstScanStride;

                bufferA[0] = (pel>>24)&0xff;
                bufferR[0] = (pel>>16)&0xff;
                bufferG[0] = (pel>>8)&0xff;
                bufferB[0] = pel&0xff;

                sumA = bufferA[0];
                sumR = bufferR[0];
                sumG = bufferG[0];
                sumB = bufferB[0];

                for (int k=1; k<=radiusY; k++){
                    currentPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[k] = (currentPixel>>24)&0xff;
                    bufferR[k] = (currentPixel>>16)&0xff;
                    bufferG[k] = (currentPixel>>8)&0xff;
                    bufferB[k] = currentPixel&0xff;
                    sumA += bufferA[k];
                    sumR += bufferR[k];
                    sumG += bufferG[k];
                    sumB += bufferB[k];
                }
                l= radiusY+1;
                destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                  (((sumR/l)<<16) & 0x00ff0000) |
                                  (((sumG/l)<<8)  & 0x0000ff00) |
                                  (  sumB/l       & 0x000000ff));
                dp += dstScanStride;

                // 1 <= i <= radiusY : The upper margin of each row
                for (int i=1; i<=radiusY; i++){
                    final int lastI = i+radiusY;
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    bufferA[lastI] = (lastPixel>>24)&0xff;
                    bufferR[lastI] = (lastPixel>>16)&0xff;
                    bufferG[lastI] = (lastPixel>>8)&0xff;
                    bufferB[lastI] = lastPixel&0xff;
                    sumA += bufferA[lastI];
                    sumR += bufferR[lastI];
                    sumG += bufferG[lastI];
                    sumB += bufferB[lastI];
                    l++;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                }

                //
                // radiusY +1 <= i <= h-1-radiusY:
                //    inner body of the column between upper and lower margins
                //

                for (int i=radiusY+1; i<=h-1-radiusY; i++){
                    lastPixel = destPixels[cp];
                    cp += dstScanStride;

                    sumA -= bufferA[bufferHead];
                    bufferA[bufferHead] = (lastPixel>>24)&0xff;
                    sumA += bufferA[bufferHead];

                    sumR -= bufferR[bufferHead];
                    bufferR[bufferHead] = (lastPixel>>16)&0xff;
                    sumR += bufferR[bufferHead];

                    sumG -= bufferG[bufferHead];
                    bufferG[bufferHead] = (lastPixel>>8)&0xff;
                    sumG += bufferG[bufferHead];

                    sumB -= bufferB[bufferHead];
                    bufferB[bufferHead] = lastPixel&0xff;
                    sumB += bufferB[bufferHead];

                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }

                //
                // h-radiusY <= i <= h-1 : The lower margin of the column
                //

                for (int i= h-radiusY; i<h; i++){
                    sumA -= bufferA[bufferHead];
                    sumR -= bufferR[bufferHead];
                    sumG -= bufferG[bufferHead];
                    sumB -= bufferB[bufferHead];
                    l--;
                    destPixels[dp] = ((((sumA/l)<<24) & 0xff000000) |
                                      (((sumR/l)<<16) & 0x00ff0000) |
                                      (((sumG/l)<<8)  & 0x0000ff00) |
                                      (  sumB/l       & 0x000000ff));
                    dp += dstScanStride;
                    bufferHead = (bufferHead+1)%d;
                }
                // return to the beginning of the next column
            }
        }// end of the second round!

        // End of the third blur box!
        return dest;
    }

    public WritableRaster filter(Raster src, WritableRaster dest){
        WritableRaster d0, d1, finalDest;
        if (useConvolveOp){
            d0 = conv[0].filter(src, null);
            return conv[1].filter(d0, dest);
        }

        if(src==null)
            throw new IllegalArgumentException
                ("Src should not be null");

        //check destation
        if(dest!=null)
            checkCompatible(dest.getSampleModel());
        else
            dest = createCompatibleDestRaster(src);

        // when the diameter is even
        if (d%2 == 0){
            finalDest = evenApproximation(src, dest);
        }
        else {
            d0 = oddApproximation(src, dest);
            d1 = oddApproximation(d0, dest);
            finalDest = oddApproximation(d1, dest);
        }
        return finalDest;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        if (src == null && dest == null)
            throw new NullPointerException
                ("src image should not be null if dest is null");

        if (dest == null){
            dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                     BufferedImage.TYPE_INT_ARGB_PRE);

            filter(src.getRaster(), dest.getRaster());
            return dest;
        }

        BufferedImage finalDest = dest;
        ColorModel    sRGBCM = ColorModel.getRGBdefault();
        ColorModel    srcCM = src.getColorModel();
        ColorModel    dstCM;
        boolean srcNeedUnMultiply = false;
        boolean dstNeedUnMultiply = false;

        if(srcCM.equals(sRGBCM)){
            srcCM.coerceData(src.getRaster(), true);
            srcNeedUnMultiply = true;
        }
        else{
            BufferedImage newSrc;
            newSrc = new BufferedImage(src.getWidth(), src.getHeight(),
                                       BufferedImage.TYPE_INT_ARGB_PRE);

            ColorConvertOp op = new ColorConvertOp(null);
            src = op.filter(src, newSrc);
        }


        // Now, check destination. If compatible, it is used
        // as is. Otherwise a temporary image is used.
        if(!isCompatible(dest.getColorModel(),
			 dest.getSampleModel()))
            dest = createCompatibleDestImage(src, null);

        dstCM = dest.getColorModel();
        if(dstCM.equals(sRGBCM)){
            dstCM.coerceData(dest.getRaster(), true);
            dstNeedUnMultiply = true;
        }
        else{
            ColorConvertOp op = new ColorConvertOp(null);
            BufferedImage newDest;
            // premultiplied
            newDest = new BufferedImage(src.getWidth(),
                                        src.getHeight(),
                                        BufferedImage.TYPE_INT_ARGB_PRE);
            dest = filter(dest, newDest);
        }
        filter(src.getRaster(), dest.getRaster());

        if(srcNeedUnMultiply)
            srcCM.coerceData(src.getRaster(), false);

        if(dstNeedUnMultiply)
            dstCM.coerceData(dest.getRaster(), false);

        if(dest != finalDest){
            ColorConvertOp op = new ColorConvertOp(null);
            op.filter(dest, finalDest);
        }

        return finalDest;
    }
}
