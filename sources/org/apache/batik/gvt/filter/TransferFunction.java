/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.gvt.filter;

/**
 * Defines the interface for all the <tt>ComponentTransferOp</tt>
 * transfer functions, which can all be converted to a lookup table
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com">Sheng Pei</a>
 * @version $Id$
 */
public interface TransferFunction {
    byte [] getLookupTable();
}
