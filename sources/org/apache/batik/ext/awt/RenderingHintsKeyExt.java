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
 * Contains additional RenderingHints Keys, such as 
 * KEY_AREA_OF_INTEREST
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class RenderingHintsKeyExt {
    public static final int KEY_BASE;

    /**
     * Hint as to the transcoding destination.
     */
    public static final RenderingHints.Key KEY_TRANSCODING;

    public static final String VALUE_TRANSCODING_PRINTING = 
        new String("Printing");
    
    /**
     * Key for the AOI hint. This hint is used to propagate the AOI to Paint
     * and PaintContext instances.
     */
    public static final RenderingHints.Key KEY_AREA_OF_INTEREST;

    /**
     * Hint for the destination of the rendering when it is a BufferedImage
     * This works around the fact that Java 2D sometimes lies about the
     * attributes of the Graphics2D device, when it is an image.
     *
     * It is strongly suggested that you use
     * org.apache.batik.ext.awt.image.GraphicsUtil.createGraphics to
     * create a Graphics2D from a BufferedImage, this will ensure that
     * the proper things are done in the processes of creating the
     * Graphics.  */
    public static final RenderingHints.Key KEY_BUFFERED_IMAGE;

    /**
     * Hint to source that we only want an alpha channel.
     * The source should follow the SVG spec for how to
     * convert ARGB, RGB, Grey and AGrey to just an Alpha channel.
     */
    public static final RenderingHints.Key KEY_COLORSPACE;

    static {
        int base = 10100;
        RenderingHints.Key trans=null, aoi=null, bi=null, cs=null;
        while (true) {
            int val = base;

            try {
                trans = new TranscodingHintKey   (val++);
                aoi   = new AreaOfInterestHintKey(val++);
                bi    = new BufferedImageHintKey (val++);
                cs    = new ColorSpaceHintKey    (val++);
            } catch (Exception e) {
                System.err.println
                    ("You have loaded the Batik jar files more than once\n" +
                     "in the same JVM this is likely a problem with the\n" +
                     "way you are loading the Batik jar files.");
                
                base = (int)(Math.random()*2000000);
                continue;
            }
            break;
        }
        KEY_BASE             = base;
        KEY_TRANSCODING      = trans;
        KEY_AREA_OF_INTEREST = aoi;
        KEY_BUFFERED_IMAGE   = bi;
        KEY_COLORSPACE       = cs;
    }

    /**
     * Do not authorize creation of instances of that class
     */
    private RenderingHintsKeyExt(){
    }
}
