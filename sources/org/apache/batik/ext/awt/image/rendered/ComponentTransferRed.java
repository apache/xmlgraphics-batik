/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.RenderingHints;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.TransferFunction;

/**
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ComponentTransferRed extends AbstractRed {
    LookupOp operation;

    /**
     * The constructor will instantiate a LookupOp instance using
     * a LookupOp, which is built using the four LUT
     * data obtained by the TransferFunction objects
     * funcs[0] : Alpha component transfer function
     * funcs[1] : Red component transfer function
     * funcs[2] : Green component transfer function
     * funcs[3] : Blue component transfer function
     */
    public ComponentTransferRed(CachableRed src,
                                TransferFunction [] funcs,
                                RenderingHints hints) {
        super(src, src.getBounds(), 
              GraphicsUtil.coerceColorModel(src.getColorModel(), false),
              src.getSampleModel(),
              null);

        byte [][] tableData = {funcs[1].getLookupTable(), 
                               funcs[2].getLookupTable(),
                               funcs[3].getLookupTable(), 
                               funcs[0].getLookupTable()};

        // Note that we create an anonymous subclass here.
        // For what ever reason this makes the Op work correctly.
        // If you remove this, it seems to get the color channels messed
        // up.  The downside is that I suspect that this means we are
        // falling into a more general, and hence slower case, but
        // at least it works....
        operation  =  new LookupOp(new ByteLookupTable(0, tableData), hints) 
            { };
    }
    
    public WritableRaster copyData(WritableRaster wr){
        CachableRed src = (CachableRed)getSources().elementAt(0);

        wr = src.copyData(wr);
        GraphicsUtil.coerceData(wr, src.getColorModel(), false);

        WritableRaster srcWR = wr.createWritableTranslatedChild(0,0);

        operation.filter(srcWR, srcWR);

        return wr;
    }
}
