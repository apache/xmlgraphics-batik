/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.lang.Math;

/**
 * This class defines the Discrete type transfer function for the
 * feComponentTransfer filter, as defined in chapter 15, section 11
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$
 *
 * @see  org.apache.batik.gvt.filter.ComponentTransferOp
 */
public class DiscreteTransfer implements TransferFunction {
    /**
     * This byte array stores the lookuptable data
     */
    public byte [] lutData;

    /**
     * This int array is the input table values from the user
     */
    public int [] tableValues;

    /*
     * The number of the input table's elements
     */
    private int n;

    /**
     * The input is an int array which will be used
     * later to construct the lut data
     */
    public DiscreteTransfer(int [] tableValues){
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }

    /*
     * This method will build the lut data. Each entry
     * has the value as its index.
     */
    private void buildLutData(){
        lutData = new byte [256];
        int i, j;
        for (j=0; j<=255; j++){
            i = (int)(Math.floor(j*n/255f));
            if(i == n){
                i = n-1;
            }
            lutData[j] = (byte)(tableValues[i] & 0xff);
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
