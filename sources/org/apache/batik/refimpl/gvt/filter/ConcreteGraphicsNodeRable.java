/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

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

/**
 * This implementation of RenderableImage will render its input
 * GraphicsNode into a BufferedImage upon invokation of one of its
 * createRendering methods.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ConcreteGraphicsNodeRable implements GraphicsNodeRable{
    /**
     * IDENTITY Transform. Used when the node transform is null
     */
    static private final AffineTransform IDENTITY = new AffineTransform();

    /**
     * GraphicsNode this image can render
     */
    private GraphicsNode node;

    /**
     * Area covered by this image
     */
    private Rectangle2D.Float imageRect;

    /**
     * Time stamp
     */
    private long stamp = 0;

    /**
     * Returns the current modification timestamp on this Renderable
     * node.  This value will change whenever cached output data becomes
     * invalid.
     * @return Current modification timestamp value.
     */
    public long getTimeStamp() { return stamp; }

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
     * Always throws an exception.
     */
    public Shape getDependencyRegion(int srcIndex,
                                     Rectangle2D outputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }

    /**
     * Always throws an exception
     */
    public Shape getDirtyRegion(int srcIndex,
                                Rectangle2D inputRgn) {
        throw new IndexOutOfBoundsException
            ("Nonexistant source requested.");
    }


    public Shape getFilterExtent() {
        return getBounds2D();
    }

    /**
     * @param GraphicsNode this image should represent
     */
    public ConcreteGraphicsNodeRable(GraphicsNode node){
        if(node == null)
            throw new IllegalArgumentException();

        this.node = node;

    }

    /**
     * Initializes this image's members
     */
    public Rectangle2D getBounds2D(){
        Rectangle2D imageRect = (Rectangle2D)(node.getPrimitiveBounds().clone());

        return imageRect;
    }

    /**
     * Returns a vector of RenderableImages that are the sources of
     * image data for this RenderableImage. Note that this method may
     * return an empty vector, to indicate that the image has no sources,
     * or null, to indicate that no information is available.
     *
     * @return a (possibly empty) Vector of RenderableImages, or null.
     */
    public Vector getSources(){
        return null;
    }

    /**
     * Gets a property from the property set of this image.
     * If the property name is not recognized, java.awt.Image.UndefinedProperty
     * will be returned.
     *
     * @param name the name of the property to get, as a String.
     * @return a reference to the property Object, or the value
     *         java.awt.Image.UndefinedProperty.
     */
    public Object getProperty(String name){
        return Image.UndefinedProperty;
    }

    /**
     * Returns a list of names recognized by getProperty.
     */
    public String[] getPropertyNames(){
        return null;
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
     * Gets the width in user coordinate space.  By convention, the
     * usual width of a RenderableImage is equal to the image's aspect
     * ratio (width divided by height).
     *
     * @return the width of the image in user coordinates.
     */
    public float getWidth(){
        return (float)getBounds2D().getWidth();
    }

    /**
     * Gets the height in user coordinate space.  By convention, the
     * usual height of a RenderedImage is equal to 1.0F.
     *
     * @return the height of the image in user coordinates.
     */
    public float getHeight(){
        return (float)getBounds2D().getHeight();
    }

    /**
     * Gets the minimum X coordinate of the rendering-independent image data.
     */
    public float getMinX(){
        return (float)getBounds2D().getX();
    }

    /**
     * Gets the minimum Y coordinate of the rendering-independent image data.
     */
    public float getMinY(){
        return (float)getBounds2D().getY();
    }

    /**
     * Creates a RenderedImage instance of this image with width w, and
     * height h in pixels.  The RenderContext is built automatically
     * with an appropriate usr2dev transform and an area of interest
     * of the full image.  All the rendering hints come from hints
     * passed in.
     *
     * <p> If w == 0, it will be taken to equal
     * Math.round(h*(getWidth()/getHeight())).
     * Similarly, if h == 0, it will be taken to equal
     * Math.round(w*(getHeight()/getWidth())).  One of
     * w or h must be non-zero or else an IllegalArgumentException
     * will be thrown.
     *
     * <p> The created RenderedImage may have a property identified
     * by the String HINTS_OBSERVED to indicate which RenderingHints
     * were used to create the image.  In addition any RenderedImages
     * that are obtained via the getSources() method on the created
     * RenderedImage may have such a property.
     *
     * @param w the width of rendered image in pixels, or 0.
     * @param h the height of rendered image in pixels, or 0.
     * @param hints a RenderingHints object containg hints.
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createScaledRendering(int w, int h,
                                               RenderingHints hints) {
        RenderedImage renderedImage = null;
        Rectangle2D imageRect2D = getBounds2D();
        Rectangle2D.Float imageRect =
            new Rectangle2D.Float((float)imageRect2D.getX(),
                                  (float)imageRect2D.getY(),
                                  (float)imageRect2D.getWidth(),
                                  (float)imageRect2D.getHeight());
        if((imageRect.width == 0) ||
               (imageRect.height == 0)) {
            renderedImage =
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        } else {
            float scaleX = w/imageRect.width;
            float scaleY = h/imageRect.height;

            AffineTransform scale
                = AffineTransform.getScaleInstance(scaleX,
                                                   scaleY);

            RenderContext context = new RenderContext(scale, hints);
            renderedImage = createRendering(context);
        }
        return renderedImage;
    }

    /**
     * Returnd a RenderedImage instance of this image with a default
     * width and height in pixels.  The RenderContext is built
     * automatically with an appropriate usr2dev transform and an area
     * of interest of the full image.  The rendering hints are
     * empty.  createDefaultRendering may make use of a stored
     * rendering for speed.
     *
     * @return a RenderedImage containing the rendered data.
     */
    public RenderedImage createDefaultRendering(){
        return createScaledRendering((int)getBounds2D().getWidth(),
                                     (int)getBounds2D().getHeight(),
                                     null);
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
        Rectangle2D.Float imageRect =
            new Rectangle2D.Float((float)imageRect2D.getX(),
                                  (float)imageRect2D.getY(),
                                  (float)imageRect2D.getWidth(),
                                  (float)imageRect2D.getHeight());
        Rectangle renderableArea
            = usr2dev.createTransformedShape(imageRect).getBounds();

        // Now, take area of interest into account. It is
        // defined in user space.
        Shape usrAOI = renderContext.getAreaOfInterest();
        if(usrAOI == null)
            usrAOI = imageRect;

        Rectangle devAOI
            = usr2dev.createTransformedShape(usrAOI).getBounds();

        // The rendered area is the interesection of the
        // renderable area and the device AOI bounds
        final Rectangle renderedArea
            = renderableArea.createIntersection(devAOI).getBounds();

        RenderedImage renderedImage = null;
        if((renderedArea.width == 0)
           ||
           (renderedArea.height == 0)){
            // CHANGE: RETURN NULL
            // If there is no intersection, return a fully
            // transparent image, 1x1
            renderedImage
                = new BufferedImage(1, 1,
                                    BufferedImage.TYPE_INT_ARGB_PRE);
        }
        else{
            // There is a non-empty intersection. Render into
            // that image
            System.out.println("rendered area: " + renderedArea);
            BufferedImage offScreen
                = new BufferedImage(renderedArea.width,
                                    renderedArea.height,
                                    BufferedImage.TYPE_INT_ARGB_PRE);

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

            // Invoke primitive paint.
            node.primitivePaint(g,
                                GraphicsNodeRenderContext.getGraphicsNodeRenderContext(renderContext));

            g.dispose();

            renderedImage = new ConcreteBufferedImageCachableRed
                (offScreen, renderedArea.x, renderedArea.y);
        }

        System.out.println("ConcreteGraphicsNodeRable done");
        return renderedImage;
    }
}
