/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

/**
 * This is a typesafe enumeration of the standard Composite rules for
 * the CompositeRable operation. (over, in, out, atop, xor, arith)
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public final class PadMode implements java.io.Serializable {
      /** Pad edges with zeros */
    public static final int MODE_ZERO_PAD = 1;

      /** Pad edges by replicating edge pixels */
    public static final int MODE_REPLICATE = 2;

      /** Pad edges by wrapping around edge pixels */
    public static final int MODE_WRAP = 3;

      /** Pad edges with zeros */
    public static final PadMode ZERO_PAD = new PadMode(MODE_ZERO_PAD);

      /** Pad edges by replicating edge pixels */
    public static final PadMode REPLICATE = new PadMode(MODE_REPLICATE);

      /** Pad edges by replicating edge pixels */
    public static final PadMode WRAP = new PadMode(MODE_WRAP);

    /**
     * Returns the mode of this pad mode.
     */
    public int getMode() {
        return mode;
    }

      /**
       * The pad mode for this object.
       */
    private int mode;

    private PadMode(int mode) {
        this.mode = mode;
    }

    /**
     * This is called by the serialization code before it returns
     * an unserialized object. To provide for unicity of
     * instances, the instance that was read is replaced by its
     * static equivalent. See the serialiazation specification for
     * further details on this method's logic.
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        switch(mode){
        case MODE_ZERO_PAD:
            return ZERO_PAD;
        case MODE_REPLICATE:
            return REPLICATE;
        case MODE_WRAP:
            return WRAP;
        default:
            throw new Error("Unknown Pad Mode type");
        }
    }
}
