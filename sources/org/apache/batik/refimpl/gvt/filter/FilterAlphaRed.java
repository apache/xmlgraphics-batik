/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;

import java.awt.color.ColorSpace;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;


/**
 * This strips out the source alpha channel into a one band image.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class FilterAlphaRed extends AbstractRed {

    /**
     * Construct an alpah channel from the given src, according to
     * the SVG masking rules.
     *
     * @param src The image to convert to an alpha channel (mask image)
     */
    public FilterAlphaRed(CachableRed src) {
        super(src, src.getBounds(), 
              src.getColorModel(),
              src.getSampleModel(),
              src.getTileGridXOffset(),
              src.getTileGridYOffset(),
              null);

        props.put(FilterAsAlphaRable.PROPERTY_COLORSPACE,
                  FilterAsAlphaRable.VALUE_COLORSPACE_ALPHA);
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed srcRed = (CachableRed)getSources().get(0);

        SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1)
            // Already one band of data so we just use it...
            return srcRed.copyData(wr);

        
        Raster srcRas = srcRed.getData(wr.getBounds());
        AbstractRed.copyBand(srcRas, srcRas.getNumBands()-1, wr, 
                             wr.getNumBands()-1);
        return wr;
    }

}    
