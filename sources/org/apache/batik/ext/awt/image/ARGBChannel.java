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
