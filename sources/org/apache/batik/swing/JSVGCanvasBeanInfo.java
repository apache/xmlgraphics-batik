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

package org.apache.batik.swing;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

/**
 * A <tt>BeanInfo</tt> for the <tt>JSVGCanvas</tt>.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class JSVGCanvasBeanInfo extends SimpleBeanInfo {

    /** A color 16x16 icon. */
    protected Image iconColor16x16;

    /** A greyscale 16x16 icon. */
    protected Image iconMono16x16;

    /** A color 32x32 icon. */
    protected Image iconColor32x32;

    /** A greyscale 32x32 icon. */
    protected Image iconMono32x32;

    /**
     * Constructs a new <tt>BeanInfo</tt> for a <tt>JSVGCanvas</tt>.
     */
    public JSVGCanvasBeanInfo() {
        iconColor16x16 = loadImage("resources/batikColor16x16.gif");
        iconMono16x16 = loadImage("resources/batikMono16x16.gif");
        iconColor32x32 = loadImage("resources/batikColor32x32.gif");
        iconMono32x32 = loadImage("resources/batikMono32x32.gif");
    }

    /**
     * Returns an icon for the specified type.
     */
    public Image getIcon(int iconType) {
        switch(iconType) {
        case ICON_COLOR_16x16:
            return iconColor16x16;
        case ICON_MONO_16x16:
            return iconMono16x16;
        case ICON_COLOR_32x32:
            return iconColor32x32;
        case ICON_MONO_32x32:
            return iconMono32x32;
        default:
            return null;
        }
    }
}

