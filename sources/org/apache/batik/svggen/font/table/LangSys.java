/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class LangSys {

    private int lookupOrder;
    private int reqFeatureIndex;
    private int featureCount;
    private int[] featureIndex;
    
    /** Creates new LangSys */
    protected LangSys(RandomAccessFile raf) throws IOException {
        lookupOrder = raf.readUnsignedShort();
        reqFeatureIndex = raf.readUnsignedShort();
        featureCount = raf.readUnsignedShort();
        featureIndex = new int[featureCount];
        for (int i = 0; i < featureCount; i++) {
            featureIndex[i] = raf.readUnsignedShort();
        }
    }
    
    protected boolean isFeatureIndexed(int n) {
        for (int i = 0; i < featureCount; i++) {
            if (featureIndex[i] == n) {
                return true;
            }
        }
        return false;
    }

}

