/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

/**
 * LinearTransfer.java
 *
 * This class defines the Linear type transfer function for the
 * feComponentTransfer filter, as defined in chapter 15, section 11
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$
 *
 * @see  org.apache.batik.refimpl.gvt.filter.ComponentTransferOp
 */

public class LinearTransfer implements TransferFunction {
    /**
     * This byte array stores the lookuptable data
     */
    public byte [] lutData;

    /**
     * The slope of the linear function
     */
    public float slope;

    /**
     * The intercept of the linear function
     */
    public float intercept;

    /**
     * Two floats as the input for the function
     */
    public LinearTransfer(float slope, float intercept){
        this.slope = slope;
        this.intercept = intercept;
    }

    /*
     * This method will build the lut data. Each entry's
     * value is in form of "slope*C+intercept"
     */
    private void buildLutData(){
        lutData = new byte [256];
        int j, value;
        float scaledInt = (intercept*255f)+0.5f;
        for (j=0; j<=255; j++){
            value = (int)(slope*j+scaledInt);
            if(value < 0){
                value = 0;
            }
            else if(value > 255){
                value = 255;
            }
            lutData[j] = (byte)(0xff & value);
        }

        /*System.out.println("Linear : " + slope + " / " + intercept);
        for(j=0; j<=255; j++){
            System.out.print("[" + j + "] = " + (0xff & lutData[j]) + " ");
        }

        System.out.println();*/
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
