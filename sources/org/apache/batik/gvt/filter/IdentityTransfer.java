/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

/**
 * IdentityTransfer.java
 *
 * This class defines the Identity type transfer function for the
 * feComponentTransfer filter, as defined in chapter 15, section 11
 * of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$
 *
 * @see  org.apache.batik.gvt.filter.ComponentTransferOp
 */
public class IdentityTransfer implements TransferFunction {
    /**
     * This byte array stores the lookuptable data
     */
    public byte [] lutData;

    /*
     * This method will build the lut data. Each entry
     * has the value as its index.
     */
    private void buildLutData(){
        lutData = new byte [256];
        // as Identity, the lookup table contains
        // the same value as the index
        for (int j=0; j<=255; j++){
            lutData[j] = (byte)j;
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
