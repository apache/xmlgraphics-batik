/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.ColorSpaceHintKey;

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

        props.put(ColorSpaceHintKey.PROPERTY_COLORSPACE,
                  ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }

    public WritableRaster copyData(WritableRaster wr) {
        // new Exception("FilterAlphaRed: ").printStackTrace();
        // Get my source.
        CachableRed srcRed = (CachableRed)getSources().get(0);

        SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1)
            // Already one band of data so we just use it...
            return srcRed.copyData(wr);

        PadRed.ZeroRecter.zeroRect(wr);
        Raster srcRas = srcRed.getData(wr.getBounds());
        AbstractRed.copyBand(srcRas, srcRas.getNumBands()-1, wr, 
                             wr.getNumBands()-1);
        return wr;
    }

}    
