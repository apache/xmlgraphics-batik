/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;

import java.util.Vector;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;

/**
 * This implementation of RenderableImage will render its input
 * GraphicsNode into a BufferedImage upon invokation of one of its
 * createRendering methods.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GraphicsNodeRable8Bit 
    extends    AbstractRable 
    implements GraphicsNodeRable {

    /**
     * Should GraphicsNodeRable call primitivePaint or Paint.
     */
    private boolean usePrimitivePaint = true;

    /**
     * The current render context for the filtered GraphicsNode.
     */
    private GraphicsNodeRenderContext gnrc = null;

    /**
     * Returns true if this Rable get's it's contents by calling
     * primitivePaint on the associated <tt>GraphicsNode</tt> or
     * false if it uses paint.
     */
    public boolean getUsePrimitivePaint() {
        return usePrimitivePaint;
    }

    /**
     * Set to true if this Rable should get it's contents by calling
     * primitivePaint on the associated <tt>GraphicsNode</tt> or false
     * if it should use paint.  
     */
    public void setUsePrimitivePaint(boolean usePrimitivePaint) {
        this.usePrimitivePaint = usePrimitivePaint;
    }

    /**
     * GraphicsNode this image can render
     */
    private GraphicsNode node;

    /**
     * Returns the <tt>GraphicsNode</tt> rendered by this image
     */
    public GraphicsNode getGraphicsNode(){
        return node;
    }

    /**
     * Sets the <tt>GraphicsNode</tt> this image should render
     */
    public void setGraphicsNode(GraphicsNode node){
        if(node == null){
            throw new IllegalArgumentException();
        }

        this.node = node;
    }

    /**
     * @param node The GraphicsNode this image should represent
     */
    public GraphicsNodeRable8Bit(GraphicsNode node, 
                                     GraphicsNodeRenderContext gnrc){
        if(node == null)
            throw new IllegalArgumentException();

        this.node = node;
        this.gnrc = gnrc;
        this.usePrimitivePaint = true;
    }

    /**
     * @param node      the GraphicsNode this image should represent
     * @param usePrimitivePaint indicates if the image should
     *        include any filters or mask operations on <tt>node</tt>
     */
    public GraphicsNodeRable8Bit(GraphicsNode node, 
                                     boolean      usePrimitivePaint){
        if(node == null)
            throw new IllegalArgumentException();

        this.node = node;
        this.usePrimitivePaint = usePrimitivePaint;
    }

    /**
     * Returns the bounds of this Rable in the user coordinate system.
     */
    public Rectangle2D getBounds2D(){
        if (usePrimitivePaint)
            return (Rectangle2D)(node.getPrimitiveBounds(
                                 getGraphicsNodeRenderContext()).clone());

        // When not using Primitive paint we return out bounds in our
        // parent's user space.  This makes sense since this is the
        // space that we will draw our selves into (since paint unlike
        // primitivePaint incorporates the transform from our user
        // space to our parents user space).
        AffineTransform at     = node.getTransform();
        Rectangle2D     bounds = 
                        node.getBounds(getGraphicsNodeRenderContext());
        
        return at.createTransformedShape(bounds).getBounds2D();
    }

    /**
     * Returns true if successive renderings (that is, calls to
     * createRendering() or createScaledRendering()) with the same arguments
     * may produce different results.  This method may be used to
     * determine whether an existing rendering may be cached and
     * reused.  It is always safe to return true.
     */
    public boolean isDynamic(){
        return false;
    }

    /**
     * Creates a RenderedImage that represented a rendering of this image
     * using a given RenderContext.  This is the most general way to obtain a
     * rendering of a RenderableImage.
     *
     * <p> The created RenderedImage may have a property identified
     * by the String HINTS_OBSERVED to indicate which RenderingHints
     * (from the RenderContext) were used to create the image.
     * In addition any RenderedImages
     * that are obtained via the getSources() method on the created
     * RenderedImage may have such a property.
     *
     * @param renderContext the RenderContext to use to produce the rendering.
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createRendering(RenderContext renderContext){
        // Get user space to device space transform
        AffineTransform usr2dev = renderContext.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }

        // Find out the renderable area
        Rectangle2D imageRect2D = getBounds2D();
        Rectangle renderableArea
            = usr2dev.createTransformedShape(imageRect2D).getBounds();

        // Now, take area of interest into account. It is
        // defined in user space.
        Shape usrAOI = renderContext.getAreaOfInterest();
        if(usrAOI == null)
            usrAOI = imageRect2D;

        Rectangle devAOI
            = usr2dev.createTransformedShape(usrAOI).getBounds();


        // The rendered area is the interesection of the renderable
        // area and the device AOI bounds. if this is empty return
        // null.
        if (renderableArea.intersects(devAOI) == false)
            return null;

        final Rectangle renderedArea = renderableArea.intersection(devAOI);

        // System.out.println("RenderedArea: " + renderedArea);

        if (   (renderedArea.width == 0)
            || (renderedArea.height == 0))
            return null;


            // If there is no intersection, return a fully
            // transparent image, 1x1
        BufferedImage offScreen
            = new BufferedImage(renderedArea.width,
                                renderedArea.height,
                                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = offScreen.createGraphics();

        g.translate(-renderedArea.x, -renderedArea.y);

        // Set hints. Use the one of the GraphicsNodeRenderContext, not
        // those of this invocation.
        // CHANGE : GET HINTS FROM renderContext.
        RenderingHints hints = renderContext.getRenderingHints();
        if(hints != null){
            g.setRenderingHints(hints);
        }

        // Set transform
        g.transform(usr2dev);

        // Clip
        g.clip(renderContext.getAreaOfInterest());
            
        try {
            // Invoke primitive paint.
            if (usePrimitivePaint)
                node.primitivePaint (g, getGraphicsNodeRenderContext());
            else
                node.paint (g, getGraphicsNodeRenderContext());
        } catch (InterruptedException ie) {
            g.dispose();
            return null;
        }
            
        g.dispose();

        return new BufferedImageCachableRed
            (offScreen, renderedArea.x, renderedArea.y);

    }

    protected void setGraphicsNodeRenderContext(GraphicsNodeRenderContext rc) {
        this.gnrc = rc;
    }

    protected GraphicsNodeRenderContext getGraphicsNodeRenderContext() {
        return gnrc;
    }

}
