/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.Selector;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

import org.apache.batik.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

import org.apache.batik.gvt.text.ConcreteTextSelector;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.renderable.PadMode;

import java.util.Iterator;
import java.util.Stack;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;

/**
 * Simple implementation of the Renderer that simply does static
 * rendering in an offscreen buffer image.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public class StaticRenderer implements ImageRenderer {
    /**
     * Error messages
     */
    private static final String ILLEGAL_ARGUMENT_NULL_OFFSCREEN =
        "offScreen should not be null";
    private static final String ILLEGAL_ARGUMENT_ZERO_WIDTH_OR_HEIGHT =
        "offScreen should have positive width/height";

    /**
     * Tree this Renderer paints.
     */
    protected GraphicsNode treeRoot;

    protected GraphicsNodeRable rootGNR;
    protected CachableRed       rootCR;

    /**
     * Flag for progressive rendering. Not used in this implementation
     */
    private boolean progressivePaintAllowed;

    /**
     * The Selector instance which listens to TextSelection gestures.
     */
    private Selector textSelector = null;

    /**
     * Offscreen image where the Renderer does its rendering
     */
    protected WritableRaster baseRaster;
    protected WritableRaster raster;
    protected BufferedImage offScreen;

    protected int offScreenWidth;
    protected int offScreenHeight;

    /**
     * Passed to the GVT tree to describe the rendering environment
     */
    protected GraphicsNodeRenderContext nodeRenderContext;

    /**
     * The transform to go to device space.
     */
    protected AffineTransform usr2dev;

    /**
     * @param offScreen image where the Renderer should do its rendering
     * @param rc a GraphicsNodeRenderContext which this renderer should use
     */
    public StaticRenderer(GraphicsNodeRenderContext rc){
        setRenderContext(rc);
    }

    /**
     * @param offScreen image where the Renderer should do its rendering
     */
    public StaticRenderer(){

        RenderingHints hints = new RenderingHints(null);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);

        hints.put(RenderingHints.KEY_INTERPOLATION,
                  RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        FontRenderContext fontRenderContext =
            new FontRenderContext(new AffineTransform(), true, true);

        TextPainter textPainter = new StrokingTextPainter();

        GraphicsNodeRableFactory gnrFactory =
            new ConcreteGraphicsNodeRableFactory();

        this.nodeRenderContext =
            new GraphicsNodeRenderContext(new AffineTransform(),
                                          null,
                                          hints,
                                          fontRenderContext,
                                          textPainter,
                                          gnrFactory);
    }


    /**
     * @param rc a GraphicsNodeRenderContext which the Renderer should use
     *           for its rendering
     */
    public void setRenderContext(GraphicsNodeRenderContext rc) {
        this.nodeRenderContext = rc;
        rootGNR    = null;
        rootCR     = null;
        offScreen  = null;
        baseRaster = null;
        raster     = null;
    }

    /**
     * @return the GraphicsNodeRenderContext which the Renderer uses
     *           for its rendering
     */
    public GraphicsNodeRenderContext getRenderContext() {
        return nodeRenderContext;
    }

    public void updateOffScreen(int width, int height) {
        offScreenWidth  = width;
        offScreenHeight = height;
    }

    public synchronized BufferedImage getOffScreen() {
        if (treeRoot == null)
            return null;

        if (rootGNR == null) {
            rootGNR = new GraphicsNodeRable8Bit(treeRoot, 
                                                nodeRenderContext);
            rootCR = null;
        }

        if (rootCR == null) {
            RenderContext rc = new RenderContext
                (nodeRenderContext.getTransform(),
                 null,
                 nodeRenderContext.getRenderingHints());
            
            RenderedImage ri = rootGNR.createRendering(rc);
            if (ri == null) {
                // UGG no rendering so make up a blank one to fullfill
                // our contract.
                offScreen = new BufferedImage(offScreenWidth, 
                                              offScreenHeight,
                                              BufferedImage.TYPE_INT_ARGB);
                raster = offScreen.getRaster();
                rootCR = GraphicsUtil.wrap(offScreen);
                return offScreen;
            }

            rootCR = GraphicsUtil.wrap(ri);
            rootCR = GraphicsUtil.convertTosRGB(rootCR);
        }

        updateRaster(rootCR, offScreenWidth, offScreenHeight);

        offScreen =  new BufferedImage
            (rootCR.getColorModel(), 
             raster.createWritableChild (0, 0, offScreenWidth,
                                         offScreenHeight, 0, 0, null),
             rootCR.getColorModel().isAlphaPremultiplied(), null);

        return offScreen;
    }

    protected void updateRaster(CachableRed cr, int w, int h) {
        SampleModel sm = cr.getSampleModel();
        int tw = sm.getWidth();
        int th = sm.getHeight();
        w = (((w+tw-1)/tw)+1)*tw;
        h = (((h+th-1)/th)+1)*th;

        if ((baseRaster == null) ||
            (baseRaster.getWidth()  < w) ||
            (baseRaster.getHeight() < h)) {
            sm = sm.createCompatibleSampleModel(w, h);
            baseRaster = Raster.createWritableRaster(sm, new Point(0,0));
        }

        int tgx = -cr.getTileGridXOffset();
        int tgy = -cr.getTileGridYOffset();
        int xt, yt;
        if (tgx>=0) xt = tgx/tw;
        else        xt = (tgx-tw+1)/tw;
        if (tgy>=0) yt = tgy/th;
        else        yt = (tgy-th+1)/th;

        int xloc = xt*tw - tgx;
        int yloc = yt*th - tgy;
        
        // System.out.println("Info: [" + 
        //                    xloc + "," + yloc + "] [" + 
        //                    tgx  + "," + tgy  + "] [" +
        //                    xt   + "," + yt   + "] [" +
        //                    tw   + "," + th   + "]");
        // This raster should be aligned with cr's tile grid.
        raster = baseRaster.createWritableChild(0, 0, w, h, xloc, yloc, null);
    }

    /**
     * Disposes all resources of this renderer.
     */
    public void dispose() {
        baseRaster = null;
        raster = null;
        offScreen = null;
        treeRoot = null;
        rootGNR  = null;
        rootCR    = null;
        nodeRenderContext = null;
    }

    /**
     * This associates the given GVT Tree with this renderer.
     * Any previous tree association is forgotten.
     * Not certain if this should be just GraphicsNode, or CanvasGraphicsNode.
     */
    public void setTree(GraphicsNode treeRoot){
        this.treeRoot = treeRoot;
        rootGNR = null;
        rootCR    = null;
        offScreen = null;
        baseRaster = null;
        raster = null;
    }

    /**
     * @return the GVT tree associated with this renderer
     */
    public GraphicsNode getTree(){
        return treeRoot;
    }


    public void clearOffScreen() {
        getOffScreen();
        BufferedImage bi = new BufferedImage
            (rootCR.getColorModel(), baseRaster,
             rootCR.getColorModel().isAlphaPremultiplied(), null);
        Graphics2D g2d = bi.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g2d.dispose();
    }

    /**
     * Forces repaint of provided node. 'node' must be a node in the
     * currently associated GVT tree. Normally there is no need to
     * call this method explicitly as the Renderer listens for changes
     * on all nodes in the tree it is associated with.
     *
     * @param area region to be repainted, in the current user space
     * coordinate system.
     */
    public void repaint(Shape area) throws InterruptedException {
        if (area == null)
            return;

        // First, set the Area Of Interest in the renderContext
        nodeRenderContext.setTransform(usr2dev);
        nodeRenderContext.setAreaOfInterest(area);

        // Render tree
/*        long t0 = System.currentTimeMillis();*/
        if (treeRoot != null) {
            getOffScreen();
            Rectangle srcR = rootCR.getBounds();
            Rectangle dstR = raster.getBounds();
            CachableRed cr = rootCR;
            if ((dstR.x < srcR.x) ||
                (dstR.y < srcR.y) ||
                (dstR.x+dstR.width  > srcR.x+srcR.width) ||
                (dstR.y+dstR.height > srcR.y+srcR.height))
                cr = new PadRed(cr, dstR, PadMode.ZERO_PAD, null);

            cr.copyData(raster);

/*            long t1 = System.currentTimeMillis();
            GraphicsNode copy = treeRoot.renderingClone();
            long t2 = System.currentTimeMillis();
            System.out.println("Rendering time: "+(t1-t0));
            System.out.println("Cloning time: "+(t2-t1));*/
        }
    }

    /**
     * Sets the transform from the current user space (as defined by
     * the top node of the GVT tree, to the associated device space.
     *
     * @param usr2dev the new user space to device space transform. If null,
     *        the identity transform will be set.
     */
    public void setTransform(AffineTransform usr2dev){
        if(usr2dev == null) {
            usr2dev = new AffineTransform();
        }
        this.usr2dev = usr2dev;
        // Update the RenderContext in the nodeRenderContext
        nodeRenderContext.setTransform(usr2dev);
        rootCR = null;
    }

    /**
     * Returns a copy of the transform from the current user space (as
     * defined by the top node of the GVT tree) to the device space (1
     * unit = 1/72nd of an inch / 1 pixel, roughly speaking
     */
    public AffineTransform getTransform(){
        return nodeRenderContext.getTransform();
    }

    /**
     * Returns true if the Renderer is currently allowed to do
     * progressive painting.
     */
    public boolean isProgressivePaintAllowed(){
        return progressivePaintAllowed;
    }

    /**
     * Turns on/off progressive painting. Turning off progressive
     * painting will cause a repaint if any progressive painting has
     * been made.
     */
    public void setProgressivePaintAllowed(boolean progressivePaintAllowed){
        this.progressivePaintAllowed = progressivePaintAllowed;
    }

}
