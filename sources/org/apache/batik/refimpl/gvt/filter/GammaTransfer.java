/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import java.lang.Math;

/**
 * GammaTransfer.java
 *
 * This class defines the Gamma type transfer function for the
 * feComponentTransfer filter, as defined in chapter 15, section 11
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$
 *
 * @see  org.apache.batik.refimpl.gvt.filter.ComponentTransferOp
 */
public class GammaTransfer implements TransferFunction {
    /**
     * This byte array stores the lookuptable data
     */
    public byte [] lutData;

    /**
     * The amplitude of the Gamma function
     */
    public float amplitude;

    /**
     * The exponent of the Gamma function
     */
    public float exponent;

    /**
     * The offset of the Gamma function
     */
    public float offset;

    /**
     * Three floats as the input for the Gamma function
     */
    public GammaTransfer(float amplitude, float exponent, float offset){
        this.amplitude = exponent;
        this.exponent = exponent;
        this.offset = offset;
    }

    /*
     * This method will build the lut data. Each entry's
     * value is in form of "amplitude*pow(C, exponent) + offset"
     */
    private void buildLutData(){
        lutData = new byte [256];
        int i, j, v;
        for (j=0; j<=255; j++){
            v = (int)Math.round(255*(amplitude*Math.pow(j/255f, exponent)+offset));
            if(v > 255){
                v = (byte)0xff;
            }
            else if(v < 0){
                v = (byte)0x00;
            }
            lutData[j] = (byte)(v & 0xff);
        }
    }


    /**
     * This method will return the lut data in order
     * to construct a LookUpTable object
     */
    public byte [] getLookupTable(){
        buildLutData();
        return lutData;
    }
}
