/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
            break;
        default:
            throw new IllegalArgumentException();
        }

        /**
         * Apply the requested intent into the profile
         */
        if(intent != AUTO){
            byte[] hdr = p.getData(ICC_Profile.icSigHead);
            hdr[ICC_Profile.icHdrRenderingIntent] = (byte)intent;
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
        return toRGB(values);
    }

    /**
     * Relative colorimetric needs to happen through CIEXYZ
     * conversion
     */
    public float[] relativeColorimetricToRGB(float[] values){
        float[] ciexyz = toCIEXYZ(values);
        return sRGB.fromCIEXYZ(ciexyz);
    }

    /**
     * Absolute colorimetric. NOT IMPLEMENTED.
     * Temporarily returns same as perceptual
     */
    public float[] absoluteColorimetricToRGB(float[] values){
        return perceptualToRGB(values);
    }

    /**
     * Saturation. NOT IMPLEMENTED. Temporarily returns same
     * as perceptual.
     */
    public float[] saturationToRGB(float[] values){
        return perceptualToRGB(values);
    }
}
