/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class NullOp implements BufferedImageOp {
    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        java.awt.Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dest;
    }

    public Rectangle2D getBounds2D(BufferedImage src){
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }


    /**
     * Creates a destination image compatible with the source.
     */
    public BufferedImage createCompatibleDestImage (BufferedImage src,
                                                    ColorModel destCM){
        BufferedImage dest = null;
        if(destCM==null)
            destCM = src.getColorModel();

        dest = new BufferedImage(destCM, destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
                                 destCM.isAlphaPremultiplied(), null);

        return dest;
    }

    /**
     * Returns the location of the destination point given a
     * point in the source image.  If DestPt is non-null, it
     * will be used to hold the return value.
     */
    public Point2D getPoint2D (Point2D srcPt, Point2D destPt){
        // This operation does not affect pixel location
        if(destPt==null)
            destPt = new Point2D.Double();
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
    }

    /**
     * Returns the rendering hints for this BufferedImageOp.  Returns
     * null if no hints have been set.
     */
    public RenderingHints getRenderingHints(){
        return null;
    }
}

