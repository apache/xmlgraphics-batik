/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.keys;

import java.awt.Paint;
import org.apache.batik.transcoder.TranscodingHints;

/**
 * A transcoding Key represented as a background paint.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class PaintKey extends TranscodingHints.Key {

    public boolean isCompatibleValue(Object v) {
        return (v instanceof Paint);
    }
}
