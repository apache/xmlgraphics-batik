/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts an custom BufferedImageOp object into
 * an equivalent SVG filter.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.SVGBufferedImageOp
 */
public class SVGCustomBufferedImageOp extends AbstractSVGFilterConverter{
    public static final String ERROR_EXTENSION_HANDLER_NULL = "extensionHandler should not be null";

    /**
     * BufferedImageOp conversion is handed to the extensionHandler.
     * This class keeps track of already converted BufferedImageOps
     */
    private ExtensionHandler extensionHandler;

    /**
     * @param domFactory for use by SVGCustomBufferedImageOp to build Elements
     */
    public SVGCustomBufferedImageOp(Document domFactory,
                                    ExtensionHandler extensionHandler){
        super(domFactory);

        if(extensionHandler == null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);

        this.extensionHandler = extensionHandler;
    }

    /**
     * @param filter the BufferedImageOp object to convert to SVG
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return an SVGFilterDescriptor mapping the SVG
     *         BufferedImageOp equivalent to the input BufferedImageOp.
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter,
                                     Rectangle filterRect){
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)descMap.get(filter);

        if(filterDesc == null){
            // First time this filter is used. Request handler
            // to do the convertion
            filterDesc
                = extensionHandler.handleFilter(filter, filterRect, domFactory);

            if(filterDesc != null){
                Element def = filterDesc.getDef();
                if(def != null)
                    defSet.add(def);
                descMap.put(filter, filterDesc);
            }
            else{
                System.err.println("SVGCustomBufferedImageOp:: ExtensionHandler could not convert filter");
            }
        }

        return filterDesc;
    }

}



class NullOp implements BufferedImageOp {
    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        java.awt.Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
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


