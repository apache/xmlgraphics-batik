/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.ARGBChannel;

/**
 * This class provides an implementation for the SVG
 * feDisplacementMap filter, as defined in Chapter 15, section 15
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DisplacementMapOp implements RasterOp {
    // Use these to control timing and Nearest Neighbot vs. Bilinear Interp.
    static private final boolean TIME   = false;
    static private final boolean USE_NN = false;

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
    Raster in2;

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
                              int scaleX, int scaleY, 
                              Raster in2){
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
        return new Rectangle(in2.getMinX(), in2.getMinY(), 
                             in2.getWidth(), in2.getHeight());
    }

    public Point2D getPoint2D(Point2D srcPt, Point2D destPt){
        // This operation does not affect pixel location
        if(destPt==null)
            destPt = new Point2D.Float();
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
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
    public WritableRaster createCompatibleDestRaster(Raster src) {
        SampleModel sm = 
            src.getSampleModel().createCompatibleSampleModel
            (in2.getWidth(), in2.getHeight());
        return Raster.createWritableRaster
            (sm, new Point(in2.getMinX(), in2.getMinY()));
    }
    /**
     * @param src the Raster to be filtered
     * @param dest stores the filtered image. If null, a destination will
     *        be created. src and dest can refer to the same Raster, in
     *        which situation the src will be modified.
     */
    public WritableRaster filter(Raster src, WritableRaster dest){
        if(dest!=null) 
            checkCompatible(dest.getSampleModel());
        else 
            dest = createCompatibleDestRaster(src);

        if (USE_NN)
            return filterNN(src,dest);
        else
            return filterBL(src,dest);
    }

    public WritableRaster filterBL(Raster src, WritableRaster dest){

        final int w      = in2.getWidth();
        final int h      = in2.getHeight();
        final int xStart = in2.getMinX()-src.getMinX();
        final int yStart = in2.getMinY()-src.getMinY();
        final int xEnd   = xStart+w;
        final int yEnd   = yStart+h;

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        DataBufferInt in2DB = (DataBufferInt)in2.getDataBuffer();

        // Offset defines where in the stack the real data begin
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)src.getSampleModel();

        final int srcOff = srcDB.getOffset() +
            sppsm.getOffset(src.getMinX() - src.getSampleModelTranslateX(),
                            src.getMinY() - src.getSampleModelTranslateY());

        sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int dstOff = dstDB.getOffset() +
            sppsm.getOffset(in2.getMinX() - dest.getSampleModelTranslateX(),
                            in2.getMinY() - dest.getSampleModelTranslateY());

        sppsm = (SinglePixelPackedSampleModel)in2.getSampleModel();
        final int in2Off = in2DB.getOffset() +
            sppsm.getOffset(in2.getMinX() - in2.getSampleModelTranslateX(),
                            in2.getMinY() - in2.getSampleModelTranslateY());

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int in2ScanStride = ((SinglePixelPackedSampleModel)in2.getSampleModel()).getScanlineStride();

        final int dstAdjust = dstScanStride - w;
        final int in2Adjust = in2ScanStride - w;

        // Access the pixel value array
        final int srcPixels[]  = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];
        final int in2Pixels[]  = in2DB.getBankData()[0];

        // Below is the number of shifts for each axis
        // e.g when xChannel is ALPHA, the pixel needs
        // to be shifted 24, RED 16, GREEN 8 and BLUE 0
        final int xShift = xChannel.toInt()*8;
        final int yShift = yChannel.toInt()*8;

        // The pointer of src and dest indicating where the pixel values are
        int sp = srcOff, dp = dstOff, ip = in2Off;

        // Fixed point representation of scale factor.
        final int fpScaleX = (int)((scaleX/255.0)*(1<<15));
        final int fpScaleY = (int)((scaleY/255.0)*(1<<15));

        final int maxDx       = (scaleX/2)+1;
        final int dangerZoneX = w-maxDx;
        final int maxDy       = (scaleY/2)+1;
        final int dangerZoneY = h-maxDy;

        long start = System.currentTimeMillis();

        final int srcXExt  = src.getWidth()-1;
        final int srcYExt = src.getHeight()-1;

        int sdp, pel, frac, newPel;
        int sp0A, sp0R, sp0G, sp0B;
        int sp1A, sp1R, sp1G, sp1B;
        int pel0A, pel0R, pel0G, pel0B;
        int pel1A, pel1R, pel1G, pel1B;

        int x, y, x0, y0, xDisplace, yDisplace, dPel;
        
        for (y=yStart; y<yEnd; y++) {
            for (x=xStart; x<xEnd; x++){
                dPel = in2Pixels[ip];
                
                xDisplace = fpScaleX*(((dPel>>xShift)&0xff) - 127);
                yDisplace = fpScaleY*(((dPel>>yShift)&0xff) - 127);
                
                x0 = x+(xDisplace>>15);
                y0 = y+(yDisplace>>15);

                if ((x0 < 0) || (x0 > srcXExt) ||
                    (y0 < 0) || (y0 > srcYExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    sdp  = srcOff + y0*srcScanStride + x0;
                    pel  = srcPixels[sdp];
                    sp0A = (pel>>>16) & 0xFF00;
                    sp0R = (pel>>  8) & 0xFF00;
                    sp0G = (pel     ) & 0xFF00;
                    sp0B = (pel<<  8) & 0xFF00;

                    if (x0 != srcXExt)
                        pel = srcPixels[sdp+1];
                    sp1A = (pel>>>16) & 0xFF00;
                    sp1R = (pel>>  8) & 0xFF00;
                    sp1G = (pel     ) & 0xFF00;
                    sp1B = (pel<<  8) & 0xFF00;

                    frac = xDisplace&0x7FFF;
                    pel0A = (sp0A + (((sp1A-sp0A)*frac)>>15)) & 0xFFFF;
                    pel0R = (sp0R + (((sp1R-sp0R)*frac)>>15)) & 0xFFFF;
                    pel0G = (sp0G + (((sp1G-sp0G)*frac)>>15)) & 0xFFFF;
                    pel0B = (sp0B + (((sp1B-sp0B)*frac)>>15)) & 0xFFFF;

                    if (y0 != srcYExt)
                        sdp+=srcScanStride;
                    pel = srcPixels[sdp];
                    sp0A  = (pel>>>16) & 0xFF00;
                    sp0R  = (pel>>  8) & 0xFF00;
                    sp0G  = (pel     ) & 0xFF00;
                    sp0B  = (pel<<  8) & 0xFF00;

                    if (x0 != srcXExt)
                        pel = srcPixels[sdp+1];
                    sp1A  = (pel>>>16) & 0xFF00;
                    sp1R  = (pel>>  8) & 0xFF00;
                    sp1G  = (pel     ) & 0xFF00;
                    sp1B  = (pel<<  8) & 0xFF00;

                    pel1A = (sp0A + (((sp1A-sp0A)*frac)>>15)) & 0xFFFF;
                    pel1R = (sp0R + (((sp1R-sp0R)*frac)>>15)) & 0xFFFF;
                    pel1G = (sp0G + (((sp1G-sp0G)*frac)>>15)) & 0xFFFF;
                    pel1B = (sp0B + (((sp1B-sp0B)*frac)>>15)) & 0xFFFF;

                    frac = yDisplace&0x7FFF;
                    newPel = 
                        (((((pel0A<<15) + 
                            (pel1A-pel0A)*frac)&0x7F800000)<<  1)|
                         ((((pel0R<<15) + 
                            (pel1R-pel0R)*frac)&0x7F800000)>>> 7)|
                         ((((pel0G<<15) + 
                            (pel1G-pel0G)*frac)&0x7F800000)>>>15)|
                         ((((pel0B<<15) + 
                            (pel1B-pel0B)*frac)&0x7F800000)>>>23));

                    destPixels[dp] = newPel;
                }
                
                dp++;
                ip++;
            }

            dp += dstAdjust;
            ip += in2Adjust;
        }

        if (TIME) {
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end-start));
        }
        return dest;
    }// end of the filter() method for Raster

    /**
     * Does displacement map using Nearest neighbor interpolation
     *
     * @param src the Raster to be filtered
     * @param dest stores the filtered image. If null, a destination will
     *        be created. src and dest can refer to the same Raster, in
     *        which situation the src will be modified.
     */
    public WritableRaster filterNN(Raster src, WritableRaster dest) {
        final int w      = in2.getWidth();
        final int h      = in2.getHeight();
        final int xStart = in2.getMinX()-src.getMinX();
        final int yStart = in2.getMinY()-src.getMinY();
        final int xEnd   = xStart+w;
        final int yEnd   = yStart+h;

        // Access the integer buffer for each image.
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
        DataBufferInt in2DB = (DataBufferInt)in2.getDataBuffer();

        // Offset defines where in the stack the real data begin
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)src.getSampleModel();

        final int srcOff = srcDB.getOffset() +
            sppsm.getOffset(src.getMinX() - src.getSampleModelTranslateX(),
                            src.getMinY() - src.getSampleModelTranslateY());

        sppsm = (SinglePixelPackedSampleModel)dest.getSampleModel();
        final int dstOff = dstDB.getOffset() +
            sppsm.getOffset(in2.getMinX() - dest.getSampleModelTranslateX(),
                            in2.getMinY() - dest.getSampleModelTranslateY());

        sppsm = (SinglePixelPackedSampleModel)in2.getSampleModel();
        final int in2Off = in2DB.getOffset() +
            sppsm.getOffset(in2.getMinX() - in2.getSampleModelTranslateX(),
                            in2.getMinY() - in2.getSampleModelTranslateY());

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
        final int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
        final int in2ScanStride = ((SinglePixelPackedSampleModel)in2.getSampleModel()).getScanlineStride();

        final int dstAdjust = dstScanStride - w;
        final int in2Adjust = in2ScanStride - w;

        // Access the pixel value array
        final int srcPixels[]  = srcDB.getBankData()[0];
        final int destPixels[] = dstDB.getBankData()[0];
        final int in2Pixels[]  = in2DB.getBankData()[0];

        // Below is the number of shifts for each axis
        // e.g when xChannel is ALPHA, the pixel needs
        // to be shifted 24, RED 16, GREEN 8 and BLUE 0
        final int xShift = xChannel.toInt()*8;
        final int yShift = yChannel.toInt()*8;

        final int fpScaleX = scaleX*(1<<16)/255;
        final int fpScaleY = scaleY*(1<<16)/255;

        final int srcXExt = src.getWidth()-1;
        final int srcYExt = src.getHeight()-1;

        int delta = (scaleX/2)+1;
        int dz = srcXExt-delta;
        if (delta > xEnd) delta = xEnd;
        if (dz    > xEnd) dz    = xEnd;
        final int maxDx       = delta; 
        final int dangerZoneX = dz;

        delta = (scaleY/2)+1;
        dz = srcYExt-delta;
        if (delta > yEnd) delta = yEnd;
        if (dz    > yEnd) dz    = yEnd;
        final int maxDy       = delta;
        final int dangerZoneY = dz;


        // The pointer of src and dest indicating where the pixel values are
        int dp = dstOff, ip = in2Off;

        long start = System.currentTimeMillis();
        int y=yStart;
        while (y<maxDy) {
            int x=xStart;
            while (x<maxDx) {
                int pel = in2Pixels[ip];
                
                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((x0 < 0) || (x0 > srcXExt) ||
                    (y0 < 0) || (y0 > srcYExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }

            while (x<dangerZoneX) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((y0 < 0) || (y0 > srcYExt)) {
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }

            while (x<xEnd) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((x0 > srcXExt) ||
                    (y0 < 0) || (y0 > srcYExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }
            dp += dstAdjust;
            ip += in2Adjust;
            y++;
        }

        while (y<dangerZoneY) {
            int x=xStart;
            while (x<maxDx) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((x0 < 0) || (x0 > srcXExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }

            while (x<dangerZoneX) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                int sdp = (srcOff + x+(xDisplace>>16) + 
                       (y+(yDisplace>>16))*srcScanStride);
                destPixels[dp] = srcPixels[sdp];
                
                dp++;
                ip++;
                x++;
            }

            while (x<xEnd) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if (x0 > srcXExt) {
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }
            dp += dstAdjust;
            ip += in2Adjust;
            y++;
        }

        while (y<yEnd) {
            int x=xStart;
            while (x<maxDx) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((x0 < 0) || (x0 > srcXExt) ||
                    (y0 > srcYExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }

            while (x<dangerZoneX) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if (y0 > srcYExt) {
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }

            while (x<xEnd) {
                int pel = in2Pixels[ip];

                final int xDisplace = fpScaleX*(((pel>>xShift)&0xff) - 127);
                final int yDisplace = fpScaleY*(((pel>>yShift)&0xff) - 127);

                final int x0 = x+(xDisplace>>16);
                final int y0 = y+(yDisplace>>16);

                if ((x0 > srcXExt) || (y0 > srcYExt)){
                    destPixels[dp] = 0x0;
                }
                else {
                    int sdp = srcOff + y0*srcScanStride + x0;
                    destPixels[dp] = srcPixels[sdp];
                }
                
                dp++;
                ip++;
                x++;
            }
            dp += dstAdjust;
            ip += in2Adjust;
            y++;
        }
        
        if (TIME) {
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end-start));
        }
        return dest;
    }// end of the filter() method for Raster

}




