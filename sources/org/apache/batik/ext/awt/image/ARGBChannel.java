/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.io.Serializable;

/**
 * Enumerated type for an ARGB Channel selector.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class ARGBChannel implements Serializable{
    /**
     * Types.
     * 
     */
    public static final int CHANNEL_A = 3;
    public static final int CHANNEL_R = 2;
    public static final int CHANNEL_G = 1;
    public static final int CHANNEL_B = 0;

    /**
     * Strings used to get a more readable output when
     * a value is displayed.
     */
    public static final String RED = "Red";
    public static final String GREEN = "Green";
    public static final String BLUE = "Blue";
    public static final String ALPHA = "Alpha";

    /**
     * Channel values
     */
    public static final ARGBChannel R 
        = new ARGBChannel(CHANNEL_R, RED);
    public static final ARGBChannel G 
        = new ARGBChannel(CHANNEL_G, GREEN);
    public static final ARGBChannel B 
        = new ARGBChannel(CHANNEL_B, BLUE);
    public static final ARGBChannel A 
        = new ARGBChannel(CHANNEL_A, ALPHA);

    /**
     * All values
     */
    private static final 
        ARGBChannel[] enumValues = {R, G, B, A};

    private String desc;
    private int val;

    /** 
     * Constructor is private so that no instances other than
     * the ones in the enumeration can be created.
     * @see #readResolve
     */
    private ARGBChannel(int val, String desc){
        this.desc = desc;
        this.val = val;
    }
    
    /**
     * @return description
     */
    public String toString(){
        return desc;
    }

    /**
     * Convenience for enumeration switching
     * @return value
     */
    public int toInt(){
        return val;
    }


    /**
     * This is called by the serialization code before it returns an unserialized
     * object. To provide for unicity of instances, the instance that was read
     * is replaced by its static equivalent
     */
    public Object readResolve() {
        switch(val){
        case CHANNEL_R:
            return R;
        case CHANNEL_G:
            return G;
        case CHANNEL_B:
            return B;
        case CHANNEL_A:
            return A;
        default:
            throw new Error("Unknown ARGBChannel value");
        }
    }
}
