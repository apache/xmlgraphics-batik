/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.TileGenerator;
import org.apache.batik.ext.awt.image.rendered.TileStore;
import org.apache.batik.ext.awt.image.rendered.TileCache;

import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * This implementation of RenderableImage will render its input
 * GraphicsNode on demand for tiles.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GraphicsNodeRed8Bit extends AbstractRed 
    implements TileGenerator {

    /**
     * GraphicsNode this image can render
     */
    private GraphicsNode node;

    private AffineTransform node2dev;

    private RenderingHints  hints;

    private TileStore tiles;

    private GraphicsNodeRenderContext gnrc;

    private boolean usePrimitivePaint;

    public GraphicsNodeRed8Bit(GraphicsNode node,
                               AffineTransform node2dev,
                               GraphicsNodeRenderContext gnrc,
                               boolean usePrimitivePaint,
                               RenderingHints  hints) {
        super(); // We _must_ call init...

        this.node              = node;
        this.node2dev          = node2dev;
        this.hints             = hints;
        this.gnrc              = gnrc;
        this.usePrimitivePaint = usePrimitivePaint;

        // Calculate my bounds by applying the affine transform to
        // my input data..

        AffineTransform at = node2dev;
        Rectangle2D bounds2D;
        if (usePrimitivePaint) {
            bounds2D = node.getPrimitiveBounds(gnrc);
        } else {
            bounds2D = node.getPrimitiveBounds(gnrc);
            // When not using Primitive paint we return out bounds in
            // the nodes parent's user space.  This makes sense since
            // this is the space that we will draw our selves into
            // (since paint unlike primitivePaint incorporates the
            // transform from our user space to our parents user
            // space).
            AffineTransform nodeAt = node.getTransform();
            if (nodeAt != null) {
                at = (AffineTransform)at.clone();
                at.concatenate(nodeAt);
            }
        }
        Rectangle   bounds = at.createTransformedShape(bounds2D).getBounds();

        ColorModel cm = GraphicsUtil.sRGB_Unpre;
        
        int tw = bounds.width;
        if (tw > 128) tw = 128;
        int th = bounds.height;
        if (th > 128) th = 128;

        // fix my sample model so it makes sense given my size.
        SampleModel sm = cm.createCompatibleSampleModel(tw, th);

        // Finish initializing our base class...
        init((CachableRed)null, bounds, cm, sm, 0, 0, null);

        tiles = TileCache.getTileGrid(this, this);
    }

    public WritableRaster copyData(WritableRaster wr) {
        copyToRaster(wr);
        // genRect(wr, true);
        return wr;
    }

    public Raster getTile(int x, int y) {
        return tiles.getTile(x, y);
    }

    public Raster genTile(int x, int y) {
        WritableRaster wr = makeTile(x, y);

        genRect(wr, false);
        return wr;
    }

    public void genRect(WritableRaster wr, boolean clear) {
        BufferedImage offScreen
            = new BufferedImage(cm, 
                                wr.createWritableTranslatedChild(0,0),
                                cm.isAlphaPremultiplied(),
                                null);

        Graphics2D g = GraphicsUtil.createGraphics(offScreen, hints);
        if (clear) {
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, wr.getWidth(), wr.getHeight());
            g.setComposite(AlphaComposite.SrcOver);
        }
        g.translate(-wr.getMinX(), -wr.getMinY());

        // Set transform
        g.transform(node2dev);


        try {
            // Invoke primitive paint.
            if (usePrimitivePaint)
                node.primitivePaint(g, gnrc);
            else
                node.paint (g, gnrc);
        } catch (InterruptedException ie) {
            // Do nothing just leave...
        } finally {
            g.dispose();
        }
    }
}




