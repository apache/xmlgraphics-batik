/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.color;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

/**
 * This class extends the ICCColorSpace class by providing 
 * convenience methods to convert to sRGB using various
 * methods, forcing a givent intent, such as perceptual or
 * relative colorimetric.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ICCColorSpaceExt extends ICC_ColorSpace {
    public static final int PERCEPTUAL = 0;
    public static final int RELATIVE_COLORIMETRIC = 1;
    public static final int ABSOLUTE_COLORIMETRIC = 2;
    public static final int SATURATION = 3;
    public static final int AUTO = 4;

    static final ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    int intent;
    
    public ICCColorSpaceExt(ICC_Profile p, int intent){
        super(p);

        this.intent = intent;
        switch(intent){
        case AUTO:
        case RELATIVE_COLORIMETRIC:
        case ABSOLUTE_COLORIMETRIC:
        case SATURATION:
        case PERCEPTUAL:
            return;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the sRGB value obtained by forcing the 
     * conversion method to the intent passed to the 
     * constructor
     */
    public float[] intendedToRGB(float[] values){
        switch(intent){
            case ABSOLUTE_COLORIMETRIC:
            return absoluteColorimetricToRGB(values);
            case PERCEPTUAL:
            case AUTO:
            return perceptualToRGB(values);
            case RELATIVE_COLORIMETRIC:
            return relativeColorimetricToRGB(values);
            case SATURATION:
            return saturationToRGB(values);
            default:
            throw new Error();
        }
    }

    /**
     * Perceptual conversion is the method implemented by the
     * base class's toRGB method
     */
    public float[] perceptualToRGB(float[] values){
        System.out.println("==> perceptual");
        return toRGB(values);
    }

    /**
     * Relative colorimetric needs to happen through CIEXYZ
     * conversion
     */
    public float[] relativeColorimetricToRGB(float[] values){
        System.out.println("==> relativeColorimetric");
        float[] ciexyz = toCIEXYZ(values);
        return sRGB.fromCIEXYZ(ciexyz);
    }

    /**
     * Absolute colorimetric. NOT IMPLEMENTED
     */
    public float[] absoluteColorimetricToRGB(float[] values){
        throw new Error();
    }

    /**
     * Saturation. NOT IMPLEMENTED
     */
    public float[] saturationToRGB(float[] values){
        throw new Error();
    }
}
