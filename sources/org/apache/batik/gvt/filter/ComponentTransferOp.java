/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.RenderingHints;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ByteLookupTable;

/**
 * This class provides an implementation for the feComponentTransfer
 * filter, as defined in chapter 15, section 11 of the SVG specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$
 *
 * @see  org.apache.batik.gvt.filter.IdentityTransfer
 * @see  org.apache.batik.gvt.filter.TableTransfer
 * @see  org.apache.batik.gvt.filter.DiscreteTransfer
 * @see  org.apache.batik.gvt.filter.LinearTransfer
 * @see  org.apache.batik.gvt.filter.GammaTransfer
 */
public class ComponentTransferOp extends LookupOp{

    /**
     * The constructor will instantiate a LookupOp instance using
     * the LookupTable object, which is built using the four LUT
     * data obtained by different TransferFunction objects
     * funcs[0] : Alpha component transfer function
     * funcs[1] : Red component transfer function
     * funcs[2] : Green component transfer function
     * funcs[3] : Blue component transfer function
     */
    public ComponentTransferOp(TransferFunction [] funcs,
                               RenderingHints hints){
        super(buildLookupTable(funcs), hints);
    }

    /*
     * This method will build a two-dimensional array containing the four
     * LookupTable data for the four color components of ARGB color model
     */
    private static LookupTable buildLookupTable(TransferFunction [] funcs){
        byte [][] tableData = {funcs[1].getLookupTable(), funcs[2].getLookupTable(),
                               funcs[3].getLookupTable(), funcs[0].getLookupTable()};
        ByteLookupTable lut = new ByteLookupTable(0, tableData);
        return lut;
    }
}
