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
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * This class provides an implementation for the SVG
 * feDisplacementMap filter, as defined in Chapter 15, section 15
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DisplacementMapOp implements BufferedImageOp, RasterOp {
    /**
     * The displacement scale factor along the x axis
     */
    private int scaleX;

    /**
     * The displacement scale factor along the y axis
     */
    private int scaleY;

    /**
     * The channel type of the operation on X axis
     */
    private ARGBChannel xChannel;

    /**
     * The channel type of the operation on Y axis
     */
    private ARGBChannel yChannel;

    /**
     * The bufferedImage that provides pixel values for displacement
     * of the image needed to be filtered
     */
    BufferedImage in2;
    Raster in2Raster;
    /*
     * sRGB ColorSpace instance used for compatibility checking
     */
    private final ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    /*
     * Linear RGB ColorSpace instance used for compatibility checking
     */
    private final ColorSpace lRGB = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);

    /**
     * @param scaleX defines the scale factor of the filter operation on the X axis.
     * @param scaleY defines the scale factor of the filter operation on the Y axis
     * @param xChannel defines the channel of in2 whose values will be on X-axis
     *                 operation
     * @param xChannel defines the channel of in2 whose values will be on X-axis
     *                 operation
     * @param in2 defines the input bufferedImage whose component values will be used in
     *            displacment operation
     */
    public DisplacementMapOp (ARGBChannel xChannel, ARGBChannel yChannel, 
                              int scaleX, int scaleY, BufferedImage in2){
        if(xChannel == null){
            throw new IllegalArgumentException();
        }

        if(yChannel == null){
            throw new IllegalArgumentException();
        }

        this.in2 = in2;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.xChannel = xChannel;
        this.yChannel = yChannel;
    }

    public Rectangle2D getBounds2D(Raster src){
        checkCompatible(src.getSampleModel());
        return new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
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
            throw new IllegalArgumentException("DisplacementMapOp only works with Rasters using SinglePixelPackedSampleModels");
        // Check number of bands
        int nBands = model.getNumBands();
        if(nBands!=4)
            throw new IllegalArgumentException("DisplacementMapOp only words with Rasters having 4 bands");
        // Check that integer packed.
        if(model.getDataType()!=DataBuffer.TYPE_INT)
            throw new IllegalArgumentException("DisplacementMapOp only works with Rasters using DataBufferInts");
        // Check bit masks
        int bitOffsets[] = ((SinglePixelPackedSampleModel)model).getBitOffsets();
        for(int i=0; i<bitOffsets.length; i++){
            if(bitOffsets[i]%8 != 0)
                throw new IllegalArgumentException("DisplacementMapOp only works with Rasters using 8 bits per band : " + i + " : " + bitOffsets[i]);
        }
    }

    public RenderingHints getRenderingHints(){
        return null;
    }

    public WritableRaster createCompatibleDestRaster(Raster src){
        checkCompatible(src.getSampleModel());
        // Src Raster is OK: create a similar Raster for destination.
        return src.createCompatibleWritableRaster();
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM){
        BufferedImage dest = null;
        if(destCM==null)
            destCM = ColorModel.getRGBdefault();


        WritableRaster wr;
        wr = destCM.createCompatibleWritableRaster(src.getWidth(),
                                                   src.getHeight());
        checkCompatible(destCM, wr.getSampleModel());
        return new BufferedImage(destCM, wr,
                                 destCM.isAlphaPremultiplied(), null);
    }

    /**
     * @param src the Raster to be filtered
     * @param dest stores the filtered image. If null, a destination will
     *        be created. src and dest can refer to the same Raster, in
     *        which situation the src will be modified.
     */
    public WritableRaster filter(Raster src, WritableRaster dest){

        //check destation
        if(dest!=null) checkCompatible(dest.getSampleModel());
        else {
            if(src==null)
                throw new IllegalArgumentException("src should not be null when dest is null");
            else dest = createCompatibleDestRaster(src);
        }

        final int w = src.getWidth();
        final int h = src.getHeight();
        final int mw = in2Raster.getWidth();
        final int mh = in2Raster.getHeight();

        if (mw < w){
            throw new IllegalArgumentException();
        }

        if (mh < h) {
            throw new IllegalArgumentException();
        }

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        DataBufferInt in2DB = (DataBufferInt)in2Raster.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int srcOff = srcDB.getOffset();
        final int dstOff = dstDB.getOffset();
        final int in2Off = in2DB.getOffset();

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int in2ScanStride = ((SinglePixelPackedSampleModel)in2.getSampleModel()).getScanlineStride();

        final int srcAdjust = srcScanStride - w;
        final int dstAdjust = dstScanStride - w;
        final int in2Adjust = in2ScanStride - w;

        // Access the pixel value array
        final int srcPixels[] = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];
        final int in2Pixels[] = in2DB.getBankData()[0];

        // Below is the number of shifts for each axis
        // e.g when xChannel is ALPHA, the pixel needs
        // to be shifted 24, RED 16, GREEN 8 and BLUE 0
        final int xShift = xChannel.toInt()*8;
        final int yShift = yChannel.toInt()*8;

        // The pointer of src and dest indicating where the pixel values are
        int sp = srcOff, dp = dstOff, ip = in2Off;
        int sdp = 0;

        // The temporary variable to calculate the displacement of the coordinates
        int xDisplace, yDisplace;
        int pel;
        for (int i=0; i<h; i++){
            for (int j=0; j<w; j++){
                pel = in2Pixels[ip];

                xDisplace = (scaleX*((int)(((pel>>xShift)&0xff) - 127)))/255;
                yDisplace = (scaleY*((int)(((pel>>yShift)&0xff) - 127)))/255;

                if ((j+xDisplace < 0) ||
                    (j+xDisplace > w-1) ||
                    (i+yDisplace < 0) ||
                    (i+yDisplace > h-1)){
                    // System.out.println("Overflow : " + i + " / " + j + " / " + xDisplace + " / " + yDisplace);
                    destPixels[dp] = 0xffffffff;
                }
                else {
                    // sdp = sp + xDisplace*srcScanStride + yDisplace;
                    try{
                        sdp = srcOff + (i + yDisplace)*srcScanStride + j + xDisplace;
                        int newPel = srcPixels[sdp];
                        destPixels[dp] = newPel;
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("srcPixels.length : " + srcPixels.length);
                        System.out.println("srcOff           : " + srcOff);
                        System.out.println("w / h            : " + w + " / " + h);
                        System.out.println("srcScanStride    : " + srcScanStride);
                        System.out.println("xDisp / yDisp    : " + xDisplace + " / " + yDisplace);
                        System.out.println("i / j            : " + i + " / " + j);
                        System.out.println("sdp              : " + sdp);
                        System.out.flush();
                        e.printStackTrace();
                        System.out.flush();
                        throw new Error();
                    }
                }
                
                sp++;
                dp++;
                ip++;
            }

            sp += srcAdjust;
            dp += dstAdjust;
            ip += in2Adjust;
        }
        return dest;
    }// end of the filter() method for Raster

    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        if (src == null)
            throw new NullPointerException("src image should not be null if dest is null");

        // Now, check destination. If compatible, it is used as is. Otherwise
        // a temporary image is used.
        BufferedImage finalDest = dest;
        if(dest==null){
            dest = createCompatibleDestImage(src, null);
            finalDest = dest;
        }
        else{
            // Check that the destination ColorModel is compatible
            if(!isCompatible(dest.getColorModel(), dest.getSampleModel()))
                dest = createCompatibleDestImage(src, null);
        }

        if(in2 == null){
            in2 = src;
            in2Raster = src.getRaster();
        }
        else {
            in2Raster = in2.getRaster();
        }
        // We now have two compatible images. We can safely filter the rasters
        filter(src.getRaster(), dest.getRaster());

        // If we had to use a temporary destination, copy the result into the
        // real output image
        if(dest != finalDest){
            ColorConvertOp toDestCM = new ColorConvertOp(null);
            toDestCM.filter(dest, finalDest);
        }
        return dest;
    }
}




