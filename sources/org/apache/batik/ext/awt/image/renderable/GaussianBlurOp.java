/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

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

import org.apache.batik.ext.awt.image.GraphicsUtil;

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
    static final float precision = 0.499f;

    /*
     * sRGB ColorSpace instance used for compatibility checking
     */
    static private final ColorSpace sRGB = 
        ColorSpace.getInstance(ColorSpace.CS_sRGB);

    /*
     * Linear RGB ColorSpace instance used for compatibility checking
     */
    static private final ColorSpace lRGB =
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
    final private int dX, dY;

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

    private Kernel makeQualityKernelX(){
        return new Kernel(2*radiusX+1, 1,
                          computeQualityKernelData(radiusX, stdDeviationX));
    }

    private Kernel makeQualityKernelY(){
        return new Kernel(1, 2*radiusY+1,
                          computeQualityKernelData(radiusY, stdDeviationY));
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

        conv = new ConvolveOp [2];

        // compute the radiusX here
        float areaSum = 0f;
        float item;
        // stdDeviationX and stdDeviationY are greater than or
        // equal to 2. The use fast rendering.
        if ((stdDeviationX < 2) ||
            hints.VALUE_RENDER_QUALITY.equals(hints.get(hints.KEY_RENDERING))){
              // Start with 1/2 the zero box enery.   
            areaSum = (float)(0.5/(stdDeviationX*SQRT2PI));
            int i=1;
            while (areaSum < precision) {
                item =  (float)(Math.pow(Math.E, -i*i/
                                         (2*stdDeviationX*stdDeviationX)) /
                                (stdDeviationX*SQRT2PI));
                areaSum += item;
                i++;
            }
            radiusX = i;
            dX = 0;
            conv[0] = new ConvolveOp(makeQualityKernelX());
        } else {
              //compute d
            dX = (int)Math.floor(DSQRT2PI*stdDeviationX+0.5f);
            int r = dX/2;
            if (dX%2 == 0) 
                radiusX = 3*r-1; // even case
            else
                radiusX = 3*r;   // Odd case
        }
        
        if ((stdDeviationY < 2) ||
            hints.VALUE_RENDER_QUALITY.equals(hints.get(hints.KEY_RENDERING))){
              // compute the radiusY here
              // Start with 1/2 the zero box enery.
            areaSum = (float)(0.5/(stdDeviationY*SQRT2PI));
            int j=1;
            while (areaSum < precision){
                item =  (float)(Math.pow(Math.E, -j*j/
                                         (2*stdDeviationY*stdDeviationY)) /
                                (stdDeviationY*SQRT2PI));
                areaSum += item;
                j++;
            }
            radiusY = j;
            dY = 0;
            conv[1] = new ConvolveOp(makeQualityKernelY());
        } else {
              //compute d
            dY = (int)Math.floor(DSQRT2PI*stdDeviationY+0.5f);
            int r = dY/2;
            if (dY%2 == 0) 
                radiusY = 3*r-1; // even case
            else
                radiusY = 3*r;   // Odd case
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
        if((!cs.equals(sRGB)) && (!cs.equals(lRGB)))
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
           &&
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
        if (destCM==null)
            destCM = ColorModel.getRGBdefault();
        
        WritableRaster wr;
        wr = destCM.createCompatibleWritableRaster(src.getWidth(),
                                                   src.getHeight());
        checkCompatible(destCM, wr.getSampleModel());

        return new BufferedImage(destCM, wr,
                                 destCM.isAlphaPremultiplied(), null);
    }

    private void specialProcessRow(Raster src, WritableRaster dest){
        System.out.println("Error!");
    }
    private void specialProcessColumn(Raster src, WritableRaster dest){
        System.out.println("Error!");
    }


    private WritableRaster boxFilterH(Raster src, WritableRaster dest,
                                      int skipX, int skipY, 
                                      int boxSz, int loc) {

        final int w = src.getWidth();
        final int h = src.getHeight();

          // Check if the raster is wide enough to do _any_ work
        if (w < (2*skipX)+boxSz) return dest;
        if (h < (2*skipY))       return dest;

        final SinglePixelPackedSampleModel srcSPPSM = 
            (SinglePixelPackedSampleModel)src.getSampleModel();

        final SinglePixelPackedSampleModel dstSPPSM = 
            (SinglePixelPackedSampleModel)dest.getSampleModel();
        
        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = srcSPPSM.getScanlineStride();
        final int dstScanStride = dstSPPSM.getScanlineStride();

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int srcOff 
            = (srcDB.getOffset() + 
               srcSPPSM.getOffset
               (src.getMinX()-src.getSampleModelTranslateX(),
                src.getMinY()-src.getSampleModelTranslateY()));
        final int dstOff 
            = (dstDB.getOffset() +
               dstSPPSM.getOffset
               (dest.getMinX()-dest.getSampleModelTranslateX(),
                dest.getMinY()-dest.getSampleModelTranslateY()));

        // Access the pixel value array
        final int srcPixels[] = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];

        final int [] bufferA = new int [boxSz];
        final int [] bufferR = new int [boxSz];
        final int [] bufferG = new int [boxSz];
        final int [] bufferB = new int [boxSz];

          // Fixed point normalization factor (8.24)
        int scale = (1<<24)/boxSz;
        
        /*
         * System.out.println("Info: srcOff: " + srcOff + 
         *                    " x: " + skipX +
         *                    " y: " + skipY +
         *                    " w: " + w +
         *                    " h: " + h +
         *                    " boxSz " + boxSz +
         *                    " srcStride: " + srcScanStride);
         */

        for (int y=skipY; y<(h-skipY); y++) {
            int sp = srcOff + y*srcScanStride;
            int dp = dstOff + y*dstScanStride;
            int rowEnd = sp + (w-skipX);

            int k=0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;

            sp += skipX;
            int end  = sp+boxSz;

            while (sp < end) {
                final int currentPixel = srcPixels[sp];
                sumA += bufferA[k] =  currentPixel>>>24;
                sumR += bufferR[k] = (currentPixel>>16)&0xff;
                sumG += bufferG[k] = (currentPixel>>8)&0xff;
                sumB += bufferB[k] =  currentPixel&0xff;
                k++;
                sp++;
            }

            dp += skipX + loc;
            destPixels[dp] = (( (sumA*scale)&0xFF000000)       |
                              (((sumR*scale)&0xFF000000)>>>8)  |
                              (((sumG*scale)&0xFF000000)>>>16) |
                              (((sumB*scale)&0xFF000000)>>>24));
            dp++;

            k=0;
            while (sp < rowEnd) {
                final int currentPixel = srcPixels[sp];
                sumA -= bufferA[k];
                sumA += bufferA[k] =  currentPixel>>>24;

                sumR -= bufferR[k];
                sumR += bufferR[k] = (currentPixel>> 16)&0xff;

                sumG -= bufferG[k];
                sumG += bufferG[k] = (currentPixel>>  8)&0xff;

                sumB -= bufferB[k];
                sumB += bufferB[k] =  currentPixel     &0xff;

                destPixels[dp] = (( (sumA*scale)&0xFF000000)       |
                                  (((sumR*scale)&0xFF000000)>>>8)  |
                                  (((sumG*scale)&0xFF000000)>>>16) |
                                  (((sumB*scale)&0xFF000000)>>>24));
                k = (k+1)%boxSz;
                sp++;
                dp++;
            }
        }
        return dest;
    }

    private WritableRaster boxFilterV(Raster src, WritableRaster dest,
                                      int skipX, int skipY, 
                                      int boxSz, int loc) {

        final int w = src.getWidth();
        final int h = src.getHeight();

          // Check if the raster is wide enough to do _any_ work
        if (w < (2*skipX))       return dest;
        if (h < (2*skipY)+boxSz) return dest;

        final SinglePixelPackedSampleModel srcSPPSM = 
            (SinglePixelPackedSampleModel)src.getSampleModel();

        final SinglePixelPackedSampleModel dstSPPSM = 
            (SinglePixelPackedSampleModel)dest.getSampleModel();
        
        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = srcSPPSM.getScanlineStride();
        final int dstScanStride = dstSPPSM.getScanlineStride();

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int srcOff 
            = (srcDB.getOffset() + 
               srcSPPSM.getOffset
               (src.getMinX()-src.getSampleModelTranslateX(),
                src.getMinY()-src.getSampleModelTranslateY()));
        final int dstOff 
            = (dstDB.getOffset() +
               dstSPPSM.getOffset
               (dest.getMinX()-dest.getSampleModelTranslateX(),
                dest.getMinY()-dest.getSampleModelTranslateY()));


        // Access the pixel value array
        final int srcPixels [] = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];

        final int [] bufferA = new int [boxSz];
        final int [] bufferR = new int [boxSz];
        final int [] bufferG = new int [boxSz];
        final int [] bufferB = new int [boxSz];

          // Fixed point normalization factor (8.24)
        final int scale = (1<<24)/boxSz;

        /*
         * System.out.println("Info: srcOff: " + srcOff + 
         *                    " x: " + skipX +
         *                    " y: " + skipY +
         *                    " w: " + w +
         *                    " h: " + h +
         *                    " boxSz " + boxSz +
         *                    " srcStride: " + srcScanStride);
         */

        for (int x=skipX; x<(w-skipX); x++) {
            int sp = srcOff + x;
            int dp = dstOff + x;
            int colEnd = sp + (h-skipY)*srcScanStride;

            int k=0;
            int sumA = 0;
            int sumR = 0;
            int sumG = 0;
            int sumB = 0;

            sp += skipY*srcScanStride;
            int end  = sp+(boxSz*srcScanStride);

            while (sp < end) {
                final int currentPixel = srcPixels[sp];
                sumA += bufferA[k] =  currentPixel>>>24;
                sumR += bufferR[k] = (currentPixel>> 16)&0xff;
                sumG += bufferG[k] = (currentPixel>>  8)&0xff;
                sumB += bufferB[k] =  currentPixel      &0xff;
                k++;
                sp+=srcScanStride;
            }


            dp += (skipY + loc)*dstScanStride;
            destPixels[dp] = (( (sumA*scale)&0xFF000000)       |
                              (((sumR*scale)&0xFF000000)>>>8)  |
                              (((sumG*scale)&0xFF000000)>>>16) |
                              (((sumB*scale)&0xFF000000)>>>24));
            dp+=dstScanStride;
            k=0;
            while (sp < colEnd) {
                final int currentPixel = srcPixels[sp];
                sumA -= bufferA[k];
                sumA += bufferA[k] =  currentPixel>>>24;

                sumR -= bufferR[k];
                sumR += bufferR[k] = (currentPixel>>16)&0xff;

                sumG -= bufferG[k];
                sumG += bufferG[k] = (currentPixel>>8)&0xff;

                sumB -= bufferB[k];
                sumB += bufferB[k] =  currentPixel&0xff;

                destPixels[dp] = (( (sumA*scale)&0xFF000000)       |
                                  (((sumR*scale)&0xFF000000)>>>8)  |
                                  (((sumG*scale)&0xFF000000)>>>16) |
                                  (((sumB*scale)&0xFF000000)>>>24));

                k = (k+1)%boxSz;
                sp+=srcScanStride;
                dp+=dstScanStride;
            }
        }
        return dest;
    }

    public WritableRaster filter(Raster src, WritableRaster dest){

        if(src==null)
            throw new IllegalArgumentException
                ("Src should not be null");

        //check destation
        if(dest!=null)
            checkCompatible(dest.getSampleModel());
        else
            dest = createCompatibleDestRaster(src);

          // For the blur box approx we can use dest as our intermediate
          // otherwise we let it default to null which means we create a new
          // one...
        WritableRaster tmpR = null;
        if (conv[1] == null) 
            tmpR = dest;


          // this lets the Vertical conv know how much is junk, so it
          // doesn't bother to convolve the edges
        int skipX;

        if (conv[0] != null) {
            tmpR = conv[0].filter(src, tmpR);
            skipX = radiusX;
        } else {
            if (tmpR == null)
                tmpR = createCompatibleDestRaster(src);

            if (dX%2 == 0){
                tmpR = boxFilterH(src,  tmpR, 0,    0,   dX,   dX/2);
                tmpR = boxFilterH(tmpR, tmpR, dX/2, 0,   dX,   dX/2-1);
                tmpR = boxFilterH(tmpR, tmpR, dX-1, 0,   dX+1, dX/2);
            } else {
                tmpR = boxFilterH(src,  tmpR, 0,    0,   dX, dX/2);
                tmpR = boxFilterH(tmpR, tmpR, dX/2, 0,   dX, dX/2);
                tmpR = boxFilterH(tmpR, tmpR, dX-1, 0,   dX, dX/2);
            }
            skipX = 3*(dX/2)-1;
        }

        if (conv[1] != null) {
            dest = conv[1].filter(tmpR, dest);
        } else {
            if (dY%2 == 0){
                dest = boxFilterV(tmpR, dest, skipX, 0,    dY,   dY/2);
                dest = boxFilterV(dest, dest, skipX, dY/2, dY,   dY/2-1);
                dest = boxFilterV(dest, dest, skipX, dY-1, dY+1, dY/2);
            }
            else {
                dest = boxFilterV(tmpR, dest, skipX, 0,    dY, dY/2);
                dest = boxFilterV(dest, dest, skipX, dY/2, dY, dY/2);
                dest = boxFilterV(dest, dest, skipX, dY-1, dY, dY/2);
            }
        }
        return dest;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        if (src == null)
            throw new NullPointerException("Source image should not be null");

        BufferedImage origSrc   = src;
        BufferedImage finalDest = dest;

        if (!isCompatible(src.getColorModel(), src.getSampleModel())) {
            BufferedImage newSrc;
            src = new BufferedImage(src.getWidth(), src.getHeight(),
                                    BufferedImage.TYPE_INT_ARGB_PRE);
            GraphicsUtil.copyData(origSrc, src);
        }
        else if (!src.isAlphaPremultiplied()) {
            // Get a Premultipled CM.
            ColorModel    srcCM, srcCMPre;
            srcCM    = src.getColorModel();
            srcCMPre = GraphicsUtil.coerceColorModel(srcCM, true);

            src = new BufferedImage(srcCMPre, src.getRaster(),
                                    true, null);
            
            GraphicsUtil.copyData(origSrc, src);
        }


        if (dest == null) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                          BufferedImage.TYPE_INT_ARGB_PRE);
            finalDest = dest;
        } else if (!isCompatible(dest.getColorModel(),
                                 dest.getSampleModel())) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(),
                                     BufferedImage.TYPE_INT_ARGB_PRE);
        } else if (!dest.isAlphaPremultiplied()) {
            // Get a Premultipled CM.
            ColorModel    dstCM, dstCMPre;
            dstCM    = dest.getColorModel();
            dstCMPre = GraphicsUtil.coerceColorModel(dstCM, true);

            dest = new BufferedImage(dstCMPre, finalDest.getRaster(),
                                     true, null);
        }

        filter(src.getRaster(), dest.getRaster());

        // Check to see if we need to 'fix' our source (divide out alpha).
        if ((src.getRaster() == origSrc.getRaster()) &&
            (src.isAlphaPremultiplied() != origSrc.isAlphaPremultiplied())) {
            // Copy our source back the way it was...
            GraphicsUtil.copyData(src, origSrc);
        }

        // Check to see if we need to store our result...
        if ((dest.getRaster() != finalDest.getRaster()) ||
            (dest.isAlphaPremultiplied() != finalDest.isAlphaPremultiplied())){
            /*System.out.println("Dest: " + dest.isAlphaPremultiplied() +
                               " finalDest: " + 
                               finalDest.isAlphaPremultiplied());*/

            // Coerce our source back the way it was...
            GraphicsUtil.copyData(dest, finalDest);
        }

        return finalDest;
    }
}
