/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.codec;

import org.apache.batik.test.*;
import org.apache.batik.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

/**
 * This test validates the PNGEncoder operation when combined with
 * Base64 encoding.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Base64PNGEncoderTest extends PNGEncoderTest {
    /**
     * Template method for building the PNG output stream
     */
    public OutputStream buildOutputStream(ByteArrayOutputStream bos){
        return new Base64EncoderStream(bos);
    }

    /**
     * Template method for building the PNG input stream
     */
    public InputStream buildInputStream(ByteArrayOutputStream bos){
        ByteArrayInputStream bis 
            = new ByteArrayInputStream(bos.toByteArray());

        return new Base64DecodeStream(bis);
    }
}
