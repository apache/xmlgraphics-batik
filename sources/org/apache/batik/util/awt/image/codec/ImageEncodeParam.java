/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.image.codec;

import java.io.Serializable;

/**
 * An empty (marker) interface to be implemented by all image encoder
 * parameter classes.
 *
 * <p><b> This interface is not a committed part of the JAI API.  It may
 * be removed or changed in future releases of JAI.</b>
 */
public interface ImageEncodeParam extends
    ImageDecodeParam, Cloneable, Serializable {
}
