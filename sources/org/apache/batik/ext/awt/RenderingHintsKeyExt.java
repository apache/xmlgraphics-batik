/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
    /**
     * Hint as to the transcoding destination.
     */
    public static final RenderingHints.Key KEY_TRANSCODING =
        new TranscodingHintKey();

    public static final String VALUE_TRANSCODING_PRINTING = 
        new String("Printing");
    
    /**
     * Key for the AOI hint. This hint is used to propagate the AOI to Paint
     * and PaintContext instances.
     */
    public static final RenderingHints.Key KEY_AREA_OF_INTEREST =
        new AreaOfInterestHintKey();

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
    public static final RenderingHints.Key KEY_BUFFERED_IMAGE =
        new BufferedImageHintKey();

    /**
     * Do not authorize creation of instances of that class
     */
    private RenderingHintsKeyExt(){
    }
}
