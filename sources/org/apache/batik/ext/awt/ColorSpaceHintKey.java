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

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

/**
 * TranscodingHint as to what the destination of the drawing is.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public final class ColorSpaceHintKey extends RenderingHints.Key {

    /**
     * Notice to source that we prefer an Alpha RGB Image.
     */
    public static Object VALUE_COLORSPACE_ARGB  = new Object();

    /**
     * Notice to source that we will not use Alpha Channel but
     * we still want RGB data.
     */
    public static Object VALUE_COLORSPACE_RGB   = new Object();

    /**
     * Notice to source that we only want Greyscale data (no Alpha).
     */
    public static Object VALUE_COLORSPACE_GREY  = new Object();

    /**
     * Notice to source that we only want Greyscale data with
     * an alpha channel.
     */
    public static Object VALUE_COLORSPACE_AGREY = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should simply render alpha (no conversion)
     */
    public static Object VALUE_COLORSPACE_ALPHA = new Object();

    /**
     * Notice to source that we only want an alpha channel.
     * The source should follow the SVG spec for how to
     * convert ARGB, RGB, Grey and AGrey to just an Alpha channel.
     */
    public static Object VALUE_COLORSPACE_ALPHA_CONVERT = new Object();

    public static final String PROPERTY_COLORSPACE =
        "org.apache.batik.gvt.filter.Colorspace";

    /** 
     * Note that this is package private.
     */
    ColorSpaceHintKey(int number) { super(number); }

    public boolean isCompatibleValue(Object val) {
        if (val == VALUE_COLORSPACE_ARGB)          return true;
        if (val == VALUE_COLORSPACE_RGB)           return true;
        if (val == VALUE_COLORSPACE_GREY)          return true;
        if (val == VALUE_COLORSPACE_AGREY)         return true;
        if (val == VALUE_COLORSPACE_ALPHA)         return true;
        if (val == VALUE_COLORSPACE_ALPHA_CONVERT) return true;
        return false;
    }
}

