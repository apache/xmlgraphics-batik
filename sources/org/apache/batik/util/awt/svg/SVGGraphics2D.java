/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.AttributedCharacterIterator;
import java.awt.*;
import java.awt.image.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.renderable.*;

import org.w3c.dom.*;

/**
 * This implementation of the java.awt.Graphics2D abstract class
 * allows users to generate SVG (Scalable Vector Graphics) content
 * from Java code.
 *
 * SVGGraphics2D generates a DOM tree whose root is obtained through
 * the getRoot method. Refer to the DOMTreeManager and DOMGroupManager
 * documentation for details on the structure of the generated
 * DOM tree.
 *
 * The SVGGraphics2D class can produce a DOM tree using any implementation
 * that conforms to the W3C DOM 1.0 specification (see http://www.w3.org).
 * At construction time, the SVGGraphics2D must be given a org.w3.dom.Document
 * instance that is used as a factory to create the various nodes in the
 * DOM tree it generates.
 *
 * The various graphic context attributes (e.g., AffineTransform,
 * Paint) are managed by a GraphicContext object.
 *
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.GraphicContext
 * @see                org.apache.batik.util.awt.svg.DOMTreeManager
 * @see                org.apache.batik.util.awt.svg.DOMGroupManager
 * @see                org.apache.batik.util.awt.svg.ImageHandler
 * @see                org.apache.batik.util.awt.svg.ExtensionHandler
 * @see                org.w3c.dom.Document
 */
public class SVGGraphics2D extends Graphics2D implements Cloneable, SVGSyntax{
    /*
     * Error messages
     */
    public static final String ERROR_DOM_FACTORY_NULL = "domFactory should not be null";
    public static final String ERROR_IMAGE_HANDLER_NULL = "imageHandler should not be null";
    public static final String ERROR_EXTENSION_HANDLER_NULL = "extensionHandler should not be null";
    public static final String ERROR_CANVAS_SIZE_NULL = "canvas size should not be null";

    /*
     * Constants definitions
     */
    public static final String DEFAULT_XML_ENCODING = "ISO-8859-1";

    /**
     * Controls the policy for grouping nodes. Once the number of attributes
     * overridden by a child element is greater than DEFAULT_MAX_GC_OVERRIDES,
     * a new group is created.
     *
     * @see org.apache.batik.util.awt.svg.DOMTreeManager
     */
    public static final int DEFAULT_MAX_GC_OVERRIDES = 3;

    /**
     * Factory used by this Graphics2D to create Elements
     * that make the SVG DOM Tree
     */
    private Document domFactory;

    /**
     * Handler that defines how images are referenced in the
     * generated SVG fragment. This allows different strategies
     * to be used to handle images.
     * @see org.apache.batik.util.awt.svg.ImageHandler
     * @see org.apache.batik.util.awt.svg.DefaultImageHandler
     * @see org.apache.batik.util.awt.svg.ImageHandlerPNGEncoder
     * @see org.apache.batik.util.awt.svg.ImageHandlerJPEGEncoder
     */
    private ImageHandler imageHandler;

    /**
     * Current state of the Graphic Context. The GraphicsContext
     * class manages the state of this Graphics2D graphic context
     * attributes.
     */
    private GraphicContext gc;

    /**
     * The DOMTreeManager manages the process of creating
     * and growing the SVG tree. This SVGGraphics2D relies on
     * the DOMTreeManager to process attributes based on the
     * GraphicContext state and create groups when needed.
     */
    private DOMTreeManager domTreeManager;

    /**
     * The DOMGroupManager manages additions to the current group
     * node associated for this Graphics2D object. Once a group
     * is complete, the group manager appends it to the DOM tree
     * through the DOMTreeManager.
     * Note that each SVGGraphics2D instance has its own DOMGroupManager
     * (i.e., its own current group) but that all SVGGraphics2D
     * originating from the same SVGGraphics2D through various
     * createGraphics calls share the same DOMTreeManager.
     */
    private DOMGroupManager domGroupManager;

    /**
     * Used to convert Java 2D API Shape objects to equivalent SVG
     * elements
     */
    private SVGShape shapeConverter;

    /**
     * SVG Canvas size
     */
    private Dimension svgCanvasSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

    /**
     * Text handling strategy
     */
    private boolean textAsShapes = false;

    /**
     * Used to create proper font metrics
     */
    private Graphics2D fmg;

    {
        BufferedImage bi
            = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        fmg = bi.createGraphics();
    }

    /**
     * @return SVG Canvas size, as set in the root svg element
     */
    public Dimension getSVGCanvasSize(){
        return svgCanvasSize;
    }

    /**
     * @param SVG Canvas size
     */
    public void setSVGCanvasSize(Dimension svgCanvasSize){
        if(svgCanvasSize == null)
            throw new IllegalArgumentException(ERROR_CANVAS_SIZE_NULL);

        this.svgCanvasSize = new Dimension(svgCanvasSize);
    }

    /**
     * @return the DOMTreeManager used by this SVGGraphics2D instance
     */
    public DOMTreeManager getDOMTreeManager(){
        return domTreeManager;
    }

    /**
     * @return the Document used as a DOM object factory by this
     *         SVGGraphics2D instance
     */
    public Document getDOMFactory(){
        return domFactory;
    }

    /**
     * @return the ImageHandler used by this SVGGraphics2D instance
     */
    public ImageHandler getImageHandler(){
        return imageHandler;
    }

    /**
     * @return the extension handler used by this SVGGraphics2D instance
     */
    public ExtensionHandler getExtensionHandler(){
        return domTreeManager.extensionHandler;
    }

    /**
     * @param new extension handler this SVGGraphics2D should use
     */
    public void setExtensionHandler(ExtensionHandler extensionHandler){
        if(extensionHandler==null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);

        domTreeManager.setExtensionHandler(extensionHandler);
    }

    /**
     * @param domFactory Factory which will produce Elements for the DOM tree
     *        this Graphics2D generates.
     * @exception IllegalArgumentException if domFactory is null.
     */
    public SVGGraphics2D(Document domFactory){
        this(domFactory, new DefaultImageHandler(), new DefaultExtensionHandler(), false);
    }

    /**
     * @param domFactory Factory which will produce Elements for the DOM tree
     *                    this Graphics2D generates.
     * @param imageHandler defines how images are referenced in the
     *                     generated SVG fragment
     * @param extensionHandler defines how Java 2D API extensions map
     *                         to SVG Nodes.
     * @param textAsShapes if true, all text is turned into SVG shapes in the
     *        convertion. No SVG text is output.
     *
     * @exception IllegalArgumentException if domFactory is null.
     */
    public SVGGraphics2D(Document domFactory,
                         ImageHandler imageHandler,
                         ExtensionHandler extensionHandler,
                         boolean textAsShapes){
        if(domFactory==null)
            throw new IllegalArgumentException(ERROR_DOM_FACTORY_NULL);

        if(imageHandler==null)
            throw new IllegalArgumentException(ERROR_IMAGE_HANDLER_NULL);

        if(extensionHandler==null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);

        this.domFactory = domFactory;
        this.imageHandler = imageHandler;
        this.gc = new GraphicContext(new AffineTransform());
        this.shapeConverter = new SVGShape(domFactory);
        this.domTreeManager = new DOMTreeManager(gc,
                                                 domFactory,
                                                 extensionHandler,
                                                 imageHandler,
                                                 DEFAULT_MAX_GC_OVERRIDES);
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
        this.textAsShapes = textAsShapes;
    }

    /**
     * This private constructor is used in create
     *
     * @see #create
     */
    public SVGGraphics2D(SVGGraphics2D g){
        this.domFactory = g.domFactory;
        this.imageHandler = g.imageHandler;
        this.gc = (GraphicContext)g.gc.clone();
        this.gc.validateTransformStack();
        this.shapeConverter = g.shapeConverter;
        this.domTreeManager = g.domTreeManager;
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
        this.textAsShapes = g.textAsShapes;
    }

    /**
     * @param svgFileName name of the file where SVG content
     *        should be written
     */
    public void stream(String svgFileName) throws IOException {
        stream(svgFileName, false);
    }

    /**
     * @param svgFileName name of the file where SVG content
     *        should be written
     * @param useCss defines whether the output SVG should use CSS style properties
     *               as opposed to plain attributes.
     */
    public void stream(String svgFileName, boolean useCss) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFileName), DEFAULT_XML_ENCODING);
        stream(writer, useCss);
        writer.flush();
        writer.close();
    }

    /**
     * @param writer used to writer out the SVG content
     */
    public void stream(Writer writer) throws IOException {
        stream(writer, false);
    }

    /**
     * @param writer used to writer out the SVG content
     * @param useCss defines whether the output SVG should use CSS style properties
     *               as opposed to plain attributes.
     */
    public void stream(Writer writer, boolean useCss) throws IOException {
        Element svgRoot = getRoot();
        stream(svgRoot, writer, useCss);
    }

    /**
     * @param svgRoot root element to stream out
     */
    public void stream(Element svgRoot, Writer writer) throws IOException{
        stream(svgRoot, writer, false);
    }

    /**
     * @param svgRoot root element to stream out
     * @param writer output
     * @param useCss defines whether the output SVG should use CSS style properties
     *               as opposed to plain attributes.
     */
    public void stream(Element svgRoot, Writer writer, boolean useCss) throws IOException{
        PrintWriter out = new PrintWriter(writer);
        DocumentFragment svgDocument = svgRoot.getOwnerDocument().createDocumentFragment();
        svgDocument.appendChild(svgRoot);

        /*if(useCss){
          Node svgCssDocument = svgDocument;
          SVGCSSStyler.style(svgCssDocument);
          XmlWriter.writeXml(svgCssDocument, writer);
          writer.flush();
          }
          else{
          XmlWriter.writeXml(svgDocument, writer);
          writer.flush();
          }*/
        if(useCss)
            SVGCSSStyler.style(svgDocument);

        XmlWriter.writeXml(svgDocument, writer);
        writer.flush();
    }

    /**
     * Invoking this method will return a set of definition element that
     * contain all the definitions referenced by the attributes generated by
     * the various converters. This also resets the converters.
     */
    public Set getDefinitionSet(){
        return domTreeManager.getDefinitionSet();
    }

    /**
     * Invoking this method will return a reference to the topLevelGroup
     * Element managed by this object. It will also cause this object
     * to start working with a new topLevelGroup.
     *
     * @return top level group
     */
    public Element getTopLevelGroup(){
        return getTopLevelGroup(true);
    }

    /**
     * Invoking this method will return a reference to the topLevelGroup
     * Element managed by this object. It will also cause this object
     * to start working with a new topLevelGroup.
     *
     * @param includeDefinitionSet if true, the definition set is included and
     *        the converters are reset (i.e., they start with an empty set
     *        of definitions).
     * @return top level group
     */
    public Element getTopLevelGroup(boolean includeDefinitionSet){
        return domTreeManager.getTopLevelGroup(includeDefinitionSet);
    }

    /**
     * Sets the topLevelGroup to the input element. This will throw an exception
     * if the input element is not of type 'g' or if it is null.
     */
    public void setTopLevelGroup(Element topLevelGroup){
        domTreeManager.setTopLevelGroup(topLevelGroup);
    }

    /**
     * @return the svg root node of the SVG document associated with this
     *         object
     */
    public Element getRoot(){
        Element svgRoot = domTreeManager.getRoot();
        if(svgCanvasSize != null){
            svgRoot.setAttribute(ATTR_WIDTH, "" + svgCanvasSize.width);
            svgRoot.setAttribute(ATTR_HEIGHT, "" + svgCanvasSize.height);
        }
        return svgRoot;
    }

    /**
     * Creates a new <code>Graphics</code> object that is
     * a copy of this <code>Graphics</code> object.
     * @return     a new graphics context that is a copy of
     *             this graphics context.
     */
    public Graphics create(){
        return new SVGGraphics2D(this);
    }


    /**
     * Translates the origin of the graphics context to the point
     * (<i>x</i>,&nbsp;<i>y</i>) in the current coordinate system.
     * Modifies this graphics context so that its new origin corresponds
     * to the point (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's
     * original coordinate system.  All coordinates used in subsequent
     * rendering operations on this graphics context will be relative
     * to this new origin.
     * @param  x   the <i>x</i> coordinate.
     * @param  y   the <i>y</i> coordinate.
     */
    public void translate(int x, int y){
        gc.translate(x, y);
    }

    /**
     * Gets this graphics context's current color.
     * @return    this graphics context's current color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#setColor
     */
    public Color getColor(){
        return gc.getColor();
    }

    /**
     * Sets this graphics context's current color to the specified
     * color. All subsequent graphics operations using this graphics
     * context use this specified color.
     * @param     c   the new rendering color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#getColor
     */
    public void setColor(Color c){
        gc.setColor(c);
    }

    /**
     * Sets the paint mode of this graphics context to overwrite the
     * destination with this graphics context's current color.
     * This sets the logical pixel operation function to the paint or
     * overwrite mode.  All subsequent rendering operations will
     * overwrite the destination with the current color.
     */
    public void setPaintMode(){
        gc.setComposite(AlphaComposite.SrcOver);
    }

    /**
     * Sets the paint mode of this graphics context to alternate between
     * this graphics context's current color and the new specified color.
     * This specifies that logical pixel operations are performed in the
     * XOR mode, which alternates pixels between the current color and
     * a specified XOR color.
     * <p>
     * When drawing operations are performed, pixels which are the
     * current color are changed to the specified color, and vice versa.
     * <p>
     * Pixels that are of colors other than those two colors are changed
     * in an unpredictable but reversible manner; if the same figure is
     * drawn twice, then all pixels are restored to their original values.
     * @param     c1 the XOR alternation color
     */
    public void setXORMode(Color c1){
        throw new Error("XOR Mode is not supported by Graphics2D SVG Generator");
    }

    /**
     * Gets the current font.
     * @return    this graphics context's current font.
     * @see       java.awt.Font
     * @see       java.awt.Graphics#setFont
     */
    public Font getFont(){
        return gc.getFont();
    }

    /**
     * Sets this graphics context's font to the specified font.
     * All subsequent text operations using this graphics context
     * use this font.
     * @param  font   the font.
     * @see     java.awt.Graphics#getFont
     */
    public void setFont(Font font){
        gc.setFont(font);
    }


    /**
     * Gets the font metrics for the specified font.
     * @return    the font metrics for the specified font.
     * @param     f the specified font
     * @see       java.awt.Graphics#getFont
     * @see       java.awt.FontMetrics
     * @see       java.awt.Graphics#getFontMetrics()
     */
    public FontMetrics getFontMetrics(Font f){
        return fmg.getFontMetrics(f);
    }

    /**
     * Returns the bounding rectangle of the current clipping area.
     * This method refers to the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.
     * If no clip has previously been set, or if the clip has been
     * cleared using <code>setClip(null)</code>, this method returns
     * <code>null</code>.
     * The coordinates in the rectangle are relative to the coordinate
     * system origin of this graphics context.
     * @return      the bounding rectangle of the current clipping area,
     *              or <code>null</code> if no clip is set.
     * @see         java.awt.Graphics#getClip
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public Rectangle getClipBounds(){
        return gc.getClipBounds();
    }


    /**
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.  If there is no
     * current clipping area, either because the clip has never been
     * set, or the clip has been cleared using <code>setClip(null)</code>,
     * the specified rectangle becomes the new clip.
     * This method sets the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use any of the setClip methods.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     * @see #setClip(Shape)
     */
    public void clipRect(int x, int y, int width, int height){
        gc.clipRect(x, y, width, height);
    }


    /**
     * Sets the current clip to the rectangle specified by the given
     * coordinates.  This method sets the user clip, which is
     * independent of the clipping associated with device bounds
     * and window visibility.
     * Rendering operations have no effect outside of the clipping area.
     * @param       x the <i>x</i> coordinate of the new clip rectangle.
     * @param       y the <i>y</i> coordinate of the new clip rectangle.
     * @param       width the width of the new clip rectangle.
     * @param       height the height of the new clip rectangle.
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public void setClip(int x, int y, int width, int height){
        gc.setClip(x, y, width, height);
    }


    /**
     * Gets the current clipping area.
     * This method returns the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.
     * If no clip has previously been set, or if the clip has been
     * cleared using <code>setClip(null)</code>, this method returns
     * <code>null</code>.
     * @return      a <code>Shape</code> object representing the
     *              current clipping area, or <code>null</code> if
     *              no clip is set.
     * @see         java.awt.Graphics#getClipBounds
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public Shape getClip(){
        return gc.getClip();
    }


    /**
     * Sets the current clipping area to an arbitrary clip shape.
     * Not all objects that implement the <code>Shape</code>
     * interface can be used to set the clip.  The only
     * <code>Shape</code> objects that are guaranteed to be
     * supported are <code>Shape</code> objects that are
     * obtained via the <code>getClip</code> method and via
     * <code>Rectangle</code> objects.  This method sets the
     * user clip, which is independent of the clipping associated
     * with device bounds and window visibility.
     * @param clip the <code>Shape</code> to use to set the clip
     * @see         java.awt.Graphics#getClip()
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @since       JDK1.1
     */
    public void setClip(Shape clip){
        gc.setClip(clip);
    }


    /**
     * Copies an area of the component by a distance specified by
     * <code>dx</code> and <code>dy</code>. From the point specified
     * by <code>x</code> and <code>y</code>, this method
     * copies downwards and to the right.  To copy an area of the
     * component to the left or upwards, specify a negative value for
     * <code>dx</code> or <code>dy</code>.
     * If a portion of the source rectangle lies outside the bounds
     * of the component, or is obscured by another window or component,
     * <code>copyArea</code> will be unable to copy the associated
     * pixels. The area that is omitted can be refreshed by calling
     * the component's <code>paint</code> method.
     * @param       x the <i>x</i> coordinate of the source rectangle.
     * @param       y the <i>y</i> coordinate of the source rectangle.
     * @param       width the width of the source rectangle.
     * @param       height the height of the source rectangle.
     * @param       dx the horizontal distance to copy the pixels.
     * @param       dy the vertical distance to copy the pixels.
     */
    public void copyArea(int x, int y, int width, int height,
                         int dx, int dy){
        // No-op
    }


    /**
     * Draws a line, using the current color, between the points
     * <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code>
     * in this graphics context's coordinate system.
     * @param   x1  the first point's <i>x</i> coordinate.
     * @param   y1  the first point's <i>y</i> coordinate.
     * @param   x2  the second point's <i>x</i> coordinate.
     * @param   y2  the second point's <i>y</i> coordinate.
     */
    public void drawLine(int x1, int y1, int x2, int y2){
        Line2D line = new Line2D.Float(x1, y1, x2, y2);
        draw(line);
    }


    /**
     * Fills the specified rectangle.
     * The left and right edges of the rectangle are at
     * <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>.
     * The top and bottom edges are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
     * The resulting rectangle covers an area
     * <code>width</code> pixels wide by
     * <code>height</code> pixels tall.
     * The rectangle is filled using the graphics context's current color.
     * @param         x   the <i>x</i> coordinate
     *                         of the rectangle to be filled.
     * @param         y   the <i>y</i> coordinate
     *                         of the rectangle to be filled.
     * @param         width   the width of the rectangle to be filled.
     * @param         height   the height of the rectangle to be filled.
     * @see           java.awt.Graphics#clearRect
     * @see           java.awt.Graphics#drawRect
     */
    public void fillRect(int x, int y, int width, int height){
        Rectangle rect = new Rectangle(x, y, width, height);
        fill(rect);
    }

    public void drawRect(int x, int y, int width, int height){
        Rectangle rect = new Rectangle(x, y, width, height);
        draw(rect);
    }



    /**
     * Clears the specified rectangle by filling it with the background
     * color of the current drawing surface. This operation does not
     * use the current paint mode.
     * <p>
     * Beginning with Java&nbsp;1.1, the background color
     * of offscreen images may be system dependent. Applications should
     * use <code>setColor</code> followed by <code>fillRect</code> to
     * ensure that an offscreen image is cleared to a specific color.
     * @param       x the <i>x</i> coordinate of the rectangle to clear.
     * @param       y the <i>y</i> coordinate of the rectangle to clear.
     * @param       width the width of the rectangle to clear.
     * @param       height the height of the rectangle to clear.
     * @see         java.awt.Graphics#fillRect(int, int, int, int)
     * @see         java.awt.Graphics#drawRect
     * @see         java.awt.Graphics#setColor(java.awt.Color)
     * @see         java.awt.Graphics#setPaintMode
     * @see         java.awt.Graphics#setXORMode(java.awt.Color)
     */
    public void clearRect(int x, int y, int width, int height){
        Paint paint = gc.getPaint();
        gc.setColor(gc.getBackground());
        fillRect(x, y, width, height);
        gc.setPaint(paint);
    }

    /**
     * Draws an outlined round-cornered rectangle using this graphics
     * context's current color. The left and right edges of the rectangle
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
     * respectively. The top and bottom edges of the rectangle are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
     * @param      x the <i>x</i> coordinate of the rectangle to be drawn.
     * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param      width the width of the rectangle to be drawn.
     * @param      height the height of the rectangle to be drawn.
     * @param      arcWidth the horizontal diameter of the arc
     *                    at the four corners.
     * @param      arcHeight the vertical diameter of the arc
     *                    at the four corners.
     * @see        java.awt.Graphics#fillRoundRect
     */
    public void drawRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight){
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight);
        draw(rect);
    }


    /**
     * Fills the specified rounded corner rectangle with the current color.
     * The left and right edges of the rectangle
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>,
     * respectively. The top and bottom edges of the rectangle are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
     * @param       x the <i>x</i> coordinate of the rectangle to be filled.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       arcWidth the horizontal diameter
     *                     of the arc at the four corners.
     * @param       arcHeight the vertical diameter
     *                     of the arc at the four corners.
     * @see         java.awt.Graphics#drawRoundRect
     */
    public void fillRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight){
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight);
        fill(rect);
    }


    /**
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the
     * rectangle specified by the <code>x</code>, <code>y</code>,
     * <code>width</code>, and <code>height</code> arguments.
     * <p>
     * The oval covers an area that is
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * and <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * @param       x the <i>x</i> coordinate of the upper left
     *                     corner of the oval to be drawn.
     * @param       y the <i>y</i> coordinate of the upper left
     *                     corner of the oval to be drawn.
     * @param       width the width of the oval to be drawn.
     * @param       height the height of the oval to be drawn.
     * @see         java.awt.Graphics#fillOval
     */
    public void drawOval(int x, int y, int width, int height){
        Ellipse2D oval = new Ellipse2D.Float(x, y, width, height);
        draw(oval);
    }


    /**
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param       x the <i>x</i> coordinate of the upper left corner
     *                     of the oval to be filled.
     * @param       y the <i>y</i> coordinate of the upper left corner
     *                     of the oval to be filled.
     * @param       width the width of the oval to be filled.
     * @param       height the height of the oval to be filled.
     * @see         java.awt.Graphics#drawOval
     */
    public void fillOval(int x, int y, int width, int height){
        Ellipse2D oval = new Ellipse2D.Float(x, y, width, height);
        fill(oval);
    }


    /**
     * Draws the outline of a circular or elliptical arc
     * covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees, using the current color.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * The resulting arc covers an area
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the
     *                    upper-left corner of the arc to be drawn.
     * @param        y the <i>y</i>  coordinate of the
     *                    upper-left corner of the arc to be drawn.
     * @param        width the width of the arc to be drawn.
     * @param        height the height of the arc to be drawn.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     *                    relative to the start angle.
     * @see         java.awt.Graphics#fillArc
     */
    public void drawArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle){
        Arc2D arc = new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN);
        draw(arc);
    }


    /**
     * Fills a circular or elliptical arc covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * The resulting arc covers an area
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the
     *                    upper-left corner of the arc to be filled.
     * @param        y the <i>y</i>  coordinate of the
     *                    upper-left corner of the arc to be filled.
     * @param        width the width of the arc to be filled.
     * @param        height the height of the arc to be filled.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     *                    relative to the start angle.
     * @see         java.awt.Graphics#drawArc
     */
    public void fillArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle){
        Arc2D arc = new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.PIE);
        fill(arc);
    }


    /**
     * Draws a sequence of connected lines defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * The figure is not closed if the first point
     * differs from the last point.
     * @param       xPoints an array of <i>x</i> points
     * @param       yPoints an array of <i>y</i> points
     * @param       nPoints the total number of points
     * @see         java.awt.Graphics#drawPolygon(int[], int[], int)
     * @since       JDK1.1
     */
    public void drawPolyline(int xPoints[], int yPoints[],
                             int nPoints){
        if(nPoints > 0){
            GeneralPath path = new GeneralPath();
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i=1; i<nPoints; i++)
                path.lineTo(xPoints[i], yPoints[i]);

            draw(path);
        }
    }

    /**
     * Draws a closed polygon defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code>
     * line segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#fillPolygon
     * @see          java.awt.Graphics#drawPolyline
     */
    public void drawPolygon(int xPoints[], int yPoints[],
                            int nPoints){
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        draw(polygon);
    }


    /**
     * Fills a closed polygon defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code>
     * line segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * <p>
     * The area inside the polygon is defined using an
     * even-odd fill rule, also known as the alternating rule.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void fillPolygon(int xPoints[], int yPoints[],
                            int nPoints){
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        fill(polygon);
    }

    /**
     * Draws the text given by the specified string, using this
     * graphics context's current font and color. The baseline of the
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this
     * graphics context's coordinate system.
     * @param       str      the string to be drawn.
     * @param       x        the <i>x</i> coordinate.
     * @param       y        the <i>y</i> coordinate.
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawChars
     */
    public void drawString(String str, int x, int y){
        drawString(str, (float)x, (float)y);
    }


    /**
     * Draws the text given by the specified iterator, using this
     * graphics context's current color. The iterator has to specify a font
     * for each character. The baseline of the
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this
     * graphics context's coordinate system.
     * @param       iterator the iterator whose text is to be drawn
     * @param       x        the <i>x</i> coordinate.
     * @param       y        the <i>y</i> coordinate.
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawChars
     */
    public void drawString(AttributedCharacterIterator iterator,
                           int x, int y){
        drawString(iterator, (float)x, (float)y);
    }



    /**
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate
     * space. Transparent pixels in the image do not affect whatever
     * pixels are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * @param    img the specified image to be drawn.
     * @param    x   the <i>x</i> coordinate.
     * @param    y   the <i>y</i> coordinate.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             ImageObserver observer){
        Element imageElement = domFactory.createElement(TAG_IMAGE);
        imageElement.setAttribute(ATTR_X, Integer.toString(x));
        imageElement.setAttribute(ATTR_Y, Integer.toString(y));
        imageElement.setAttribute(ATTR_WIDTH, Integer.toString(img.getWidth(null)));
        imageElement.setAttribute(ATTR_HEIGHT, Integer.toString(img.getHeight(null)));
        imageHandler.handleImage(img, imageElement);
        domGroupManager.addElement(imageElement);
        return true;
    }


    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this
     * graphics context's coordinate space, and is scaled if
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the image observer by calling its <code>imageUpdate</code> method.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    width  the width of the rectangle.
     * @param    height the height of the rectangle.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             int width, int height,
                             ImageObserver observer){
        Element imageElement = domFactory.createElement(TAG_IMAGE);
        imageHandler.handleImage(img, imageElement);
        imageElement.setAttribute(ATTR_X, Integer.toString(x));
        imageElement.setAttribute(ATTR_Y, Integer.toString(y));
        imageElement.setAttribute(ATTR_WIDTH, Integer.toString(width));
        imageElement.setAttribute(ATTR_HEIGHT, Integer.toString(height));
        domGroupManager.addElement(imageElement);
        return true;
    }


    /**
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate
     * space.  Transparent pixels are drawn in the specified
     * background color.
     * <p>
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    bgcolor the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             Color bgcolor,
                             ImageObserver observer){
        return drawImage(img, x, y, img.getWidth(null), img.getHeight(null),
                         bgcolor, observer);
    }


    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this
     * graphics context's coordinate space, and is scaled if
     * necessary. Transparent pixels are drawn in the specified
     * background color.
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img       the specified image to be drawn.
     * @param    x         the <i>x</i> coordinate.
     * @param    y         the <i>y</i> coordinate.
     * @param    width     the width of the rectangle.
     * @param    height    the height of the rectangle.
     * @param    bgcolor   the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             int width, int height,
                             Color bgcolor,
                             ImageObserver observer){
        Paint paint = gc.getPaint();
        gc.setPaint(bgcolor);
        fillRect(x, y, width, height);
        gc.setPaint(paint);
        drawImage(img, x, y, observer);

        return true;
    }


    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface. Transparent pixels
     * do not affect whatever pixels are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             ImageObserver observer){
        BufferedImage src = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = src.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        src = src.getSubimage(sx1, sy1, sx2-sx1, sy2-sy1);

        return drawImage(src, dx1, dy1, dx2-dx1, dy2-dy1, observer);
    }


    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface.
     * <p>
     * Transparent pixels are drawn in the specified background color.
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       bgcolor the background color to paint under the
     *                    non-opaque portions of the image.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public boolean drawImage(Image img,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             Color bgcolor,
                             ImageObserver observer){
        Paint paint = gc.getPaint();
        gc.setPaint(bgcolor);
        fillRect(dx1, dy1, dx2-dx1, dy2-dy1);
        gc.setPaint(paint);
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }


    /**
     * Disposes of this graphics context and releases
     * any system resources that it is using.
     * A <code>Graphics</code> object cannot be used after
     * <code>dispose</code>has been called.
     * <p>
     * When a Java program runs, a large number of <code>Graphics</code>
     * objects can be created within a short time frame.
     * Although the finalization process of the garbage collector
     * also disposes of the same system resources, it is preferable
     * to manually free the associated resources by calling this
     * method rather than to rely on a finalization process which
     * may not run to completion for a long period of time.
     * <p>
     * Graphics objects which are provided as arguments to the
     * <code>paint</code> and <code>update</code> methods
     * of components are automatically released by the system when
     * those methods return. For efficiency, programmers should
     * call <code>dispose</code> when finished using
     * a <code>Graphics</code> object only if it was created
     * directly from a component or another <code>Graphics</code> object.
     * @see         java.awt.Graphics#finalize
     * @see         java.awt.Component#paint
     * @see         java.awt.Component#update
     * @see         java.awt.Component#getGraphics
     * @see         java.awt.Graphics#create
     */
    public void dispose(){
        this.domTreeManager.removeGroupManager(this.domGroupManager);
    }

    /**
     * Strokes the outline of a <code>Shape</code> using the settings of the
     * current <code>Graphics2D</code> context.  The rendering attributes
     * applied include the <code>Clip</code>, <code>Transform</code>,
     * <code>Paint</code>, <code>Composite</code> and
     * <code>Stroke</code> attributes.
     * @param s the <code>Shape</code> to be rendered
     * @see #setStroke
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     * @see #transform
     * @see #setTransform
     * @see #clip
     * @see #setClip
     * @see #setComposite
     */
    public void draw(Shape s){
        // Only BasicStroke can be converted to an SVG attribute equivalent.
        // If the GraphicContext's Stroke is not an instance of BasicStroke,
        // then the stroked outline is filled.
        Stroke stroke = gc.getStroke();
        if(stroke instanceof BasicStroke){
            Element svgShape = shapeConverter.toSVG(s);
            if(svgShape != null){
                svgShape.setAttribute(ATTR_FILL, VALUE_NONE);
                domGroupManager.addElement(svgShape);
            }
        }
        else{
            Shape strokedShape = stroke.createStrokedShape(s);
            fill(strokedShape);
        }
    }


    /**
     * Renders an image, applying a transform from image space into user space
     * before drawing.
     * The transformation from user space into device space is done with
     * the current <code>Transform</code> in the <code>Graphics2D</code>.
     * The specified transformation is applied to the image before the
     * transform attribute in the <code>Graphics2D</code> context is applied.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, and <code>Composite</code> attributes.
     * Note that no rendering is done if the specified transform is
     * noninvertible.
     * @param img the <code>Image</code> to be rendered
     * @param xform the transformation from image space into user space
     * @param obs the {@link ImageObserver}
     * to be notified as more of the <code>Image</code>
     * is converted
     * @return <code>true</code> if the <code>Image</code> is
     * fully loaded and completely rendered;
     * <code>false</code> if the <code>Image</code> is still being loaded.
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     */
    public boolean drawImage(Image img,
                             AffineTransform xform,
                             ImageObserver obs){
        boolean retVal = true;

        if(xform.getDeterminant() != 0){
            AffineTransform inverseTransform = null;
            try{
                inverseTransform = xform.createInverse();
            }   catch(NoninvertibleTransformException e){
                                // Should never happen since we checked the
                                // matrix determinant
                throw new Error();
            }

            gc.transform(xform);
            retVal = drawImage(img, 0, 0, null);
            gc.transform(inverseTransform);
        }
        else{
            AffineTransform savTransform = new AffineTransform(gc.getTransform());
            gc.transform(xform);
            retVal = drawImage(img, 0, 0, null);
            gc.setTransform(savTransform);
        }

        return retVal;

    }


    /**
     * Renders a <code>BufferedImage</code> that is
     * filtered with a
     * {@link BufferedImageOp}.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>
     * and <code>Composite</code> attributes.  This is equivalent to:
     * <pre>
     * img1 = op.filter(img, null);
     * drawImage(img1, new AffineTransform(1f,0f,0f,1f,x,y), null);
     * </pre>
     * @param op the filter to be applied to the image before rendering
     * @param img the <code>BufferedImage</code> to be rendered
     * @param x,&nbsp;y the location in user space where the upper left
     * corner of the
     * image is rendered
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     */
    public void drawImage(BufferedImage img,
                          BufferedImageOp op,
                          int x,
                          int y){
        //
        // There are two special cases: AffineTransformOp and
        // ColorConvertOp. If the input op is not one of these
        // two, then SVGBufferedImageOp is requested to map the
        // filter to an SVG equivalent.
        //
        if(op instanceof AffineTransformOp){
            AffineTransformOp transformOp = (AffineTransformOp)op;
            AffineTransform transform = transformOp.getTransform();

            if(transform.getDeterminant() != 0){
                AffineTransform inverseTransform = null;
                try{
                    inverseTransform = transform.createInverse();
                }catch(NoninvertibleTransformException e){
                    // This should never happen since we checked the
                    // matrix determinant
                    throw new Error();
                }
                gc.transform(transform);
                drawImage(img, x, y, null);
                gc.transform(inverseTransform);
            }
            else{
                AffineTransform savTransform = new AffineTransform(gc.getTransform());
                gc.transform(transform);
                drawImage(img, x, y, null);
                gc.setTransform(savTransform);
            }
        }
        else if(op instanceof ColorConvertOp){
            img = op.filter(img, null);
            drawImage(img, x, y, null);
        }
        else{
            //
            // Try and convert to an SVG filter
            //
            SVGFilterDescriptor filterDesc = domTreeManager.getFilterConverter().toSVG(op, null);
            if(filterDesc != null){
                                //
                                // Because other filters may be needed to represent the
                                // composite that applies to this image, a group is created that
                                // contains the image element.
                                //
                Element imageElement = domFactory.createElement(TAG_IMAGE);
                imageHandler.handleImage((Image)img, imageElement);
                imageElement.setAttribute(ATTR_X, Integer.toString(x));
                imageElement.setAttribute(ATTR_Y, Integer.toString(y));
                imageElement.setAttribute(ATTR_WIDTH, Integer.toString(img.getWidth(null)));
                imageElement.setAttribute(ATTR_HEIGHT, Integer.toString(img.getHeight(null)));
                imageElement.setAttribute(ATTR_FILTER, filterDesc.getFilterValue());
                Element imageGroup = domFactory.createElement(TAG_G);
                imageGroup.appendChild(imageElement);

                domGroupManager.addElement(imageGroup);
            }
            else{
                                //
                                // Could not convert to an equivalent SVG filter:
                                // filter now and draw resulting image
                                //
                img = op.filter(img, null);
                drawImage(img, x, y, null);
            }
        }
    }


    /**
     * Renders a {@link RenderedImage},
     * applying a transform from image
     * space into user space before drawing.
     * The transformation from user space into device space is done with
     * the current <code>Transform</code> in the <code>Graphics2D</code>.
     * The specified transformation is applied to the image before the
     * transform attribute in the <code>Graphics2D</code> context is applied.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, and <code>Composite</code> attributes. Note
     * that no rendering is done if the specified transform is
     * noninvertible.
     * @param img the image to be rendered
     * @param xform the transformation from image space into user space
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     */
    public void drawRenderedImage(RenderedImage img,
                                  AffineTransform xform){
        Element image = domFactory.createElement(TAG_IMAGE);
        imageHandler.handleImage(img, image);
        image.setAttribute(ATTR_X, Integer.toString(img.getMinX()));
        image.setAttribute(ATTR_Y, Integer.toString(img.getMinY()));
        image.setAttribute(ATTR_WIDTH, Integer.toString(img.getWidth()));
        image.setAttribute(ATTR_HEIGHT, Integer.toString(img.getHeight()));

        AffineTransform finalTransform = null;
        if(xform.getDeterminant() != 0){
            AffineTransform inverseTransform = null;
            try{
                inverseTransform = xform.createInverse();
            }catch(NoninvertibleTransformException e){
                                // This should never happen since we checked
                                // the matrix determinant
                throw new Error();
            }
            gc.transform(xform);
            domGroupManager.addElement(image);
            gc.transform(inverseTransform);
        }
        else{
            AffineTransform savTransform = new AffineTransform(gc.getTransform());
            gc.transform(xform);
            domGroupManager.addElement(image);
            gc.setTransform(savTransform);
        }
    }


    /**
     * Renders a
     * {@link RenderableImage},
     * applying a transform from image space into user space before drawing.
     * The transformation from user space into device space is done with
     * the current <code>Transform</code> in the <code>Graphics2D</code>.
     * The specified transformation is applied to the image before the
     * transform attribute in the <code>Graphics2D</code> context is applied.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, and <code>Composite</code> attributes. Note
     * that no rendering is done if the specified transform is
     * noninvertible.
     *<p>
     * Rendering hints set on the <code>Graphics2D</code> object might
     * be used in rendering the <code>RenderableImage</code>.
     * If explicit control is required over specific hints recognized by a
     * specific <code>RenderableImage</code>, or if knowledge of which hints
     * are used is required, then a <code>RenderedImage</code> should be
     * obtained directly from the <code>RenderableImage</code>
     * and rendered using
     *{@link #drawRenderedImage(RenderedImage, AffineTransform) drawRenderedImage}.
     * @param img the image to be rendered
     * @param xform the transformation from image space into user space
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     * @see #drawRenderedImage
     */
    public void drawRenderableImage(RenderableImage img,
                                    AffineTransform xform){
        Element image = domFactory.createElement(TAG_IMAGE);
        imageHandler.handleImage(img, image);
        image.setAttribute(ATTR_X, AbstractSVGConverter.doubleString(img.getMinX()));
        image.setAttribute(ATTR_Y, AbstractSVGConverter.doubleString(img.getMinY()));
        image.setAttribute(ATTR_WIDTH, AbstractSVGConverter.doubleString(img.getWidth()));
        image.setAttribute(ATTR_HEIGHT, AbstractSVGConverter.doubleString(img.getHeight()));

        if(xform.getDeterminant() != 0){
            AffineTransform inverseTransform = null;
            try{
                inverseTransform = xform.createInverse();
            }catch(NoninvertibleTransformException e){
                                // This should never happen because we checked the
                                // matrix determinant
                throw new Error();
            }
            gc.transform(xform);
            domGroupManager.addElement(image);
            gc.transform(inverseTransform);
        }
        else{
            AffineTransform savTransform = new AffineTransform(gc.getTransform());
            gc.transform(xform);
            domGroupManager.addElement(image);
            gc.setTransform(savTransform);
        }
    }

    /**
     * Renders the text specified by the specified <code>String</code>,
     * using the current <code>Font</code> and <code>Paint</code> attributes
     * in the <code>Graphics2D</code> context.
     * The baseline of the first character is at position
     * (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
     * <code>Composite</code> attributes. For characters in script systems
     * such as Hebrew and Arabic, the glyphs can be rendered from right to
     * left, in which case the coordinate supplied is the location of the
     * leftmost character on the baseline.
     * @param s the <code>String</code> to be rendered
     * @param x,&nbsp;y the coordinates where the <code>String</code>
     * should be rendered
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     * @see java.awt.Graphics#setFont
     * @see #setTransform
     * @see #setComposite
     * @see #setClip
     */
    public void drawString(String s, float x, float y){
        if(textAsShapes == false){
            Element text = domFactory.createElement(TAG_TEXT);
            text.setAttribute(ATTR_X, AbstractSVGConverter.doubleString(x));
            text.setAttribute(ATTR_Y, AbstractSVGConverter.doubleString(y));
            text.setAttribute(ATTR_STROKE, VALUE_NONE);
            text.appendChild(domFactory.createTextNode(s));
            domGroupManager.addElement(text);
        }
        else{
            GlyphVector gv = getFont().createGlyphVector(getFontRenderContext(), s);
            drawGlyphVector(gv, x, y);
        }
    }

    /**
     * Renders the text of the specified iterator, using the
     * <code>Graphics2D</code> context's current <code>Paint</code>. The
     * iterator must specify a font
     * for each character. The baseline of the
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the
     * User Space.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, <code>Paint</code>, and
     * <code>Composite</code> attributes.
     * For characters in script systems such as Hebrew and Arabic,
     * the glyphs can be rendered from right to left, in which case the
     * coordinate supplied is the location of the leftmost character
     * on the baseline.
     * @param iterator the iterator whose text is to be rendered
     * @param x,&nbsp;y the coordinates where the iterator's text is to be
     * rendered
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     * @see #setTransform
     * @see #setComposite
     * @see #setClip
     */
    public void drawString(AttributedCharacterIterator iterator,
                           float x, float y){
        throw new Error("AttributedCharacterIterator not supported yet");
    }


    /**
     * Renders the text of the specified
     * {@link GlyphVector} using
     * the <code>Graphics2D</code> context's rendering attributes.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, <code>Paint</code>, and
     * <code>Composite</code> attributes.  The <code>GlyphVector</code>
     * specifies individual glyphs from a {@link Font}.
     * The <code>GlyphVector</code> can also contain the glyph positions.
     * This is the fastest way to render a set of characters to the
     * screen.
     *
     * @param g the <code>GlyphVector</code> to be rendered
     * @param x,&nbsp;y the position in User Space where the glyphs should
     * be rendered
     *
     * @see java.awt.Font#createGlyphVector
     * @see java.awt.font.GlyphVector
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     * @see #setTransform
     * @see #setComposite
     * @see #setClip
     */
    public void drawGlyphVector(GlyphVector g, float x, float y){
        Shape glyphOutline = g.getOutline(x, y);
        fill(glyphOutline);
        // throw new Error("GlyphVectors not supported yet");
    }


    /**
     * Fills the interior of a <code>Shape</code> using the settings of the
     * <code>Graphics2D</code> context. The rendering attributes applied
     * include the <code>Clip</code>, <code>Transform</code>,
     * <code>Paint</code>, and <code>Composite</code>.
     * @param s the <code>Shape</code> to be filled
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     */
    public void fill(Shape s){
        Element svgShape = shapeConverter.toSVG(s);
        if(svgShape != null){
            svgShape.setAttribute(ATTR_STROKE, VALUE_NONE);
            domGroupManager.addElement(svgShape);
        }
    }

    /**
     * Checks whether or not the specified <code>Shape</code> intersects
     * the specified {@link Rectangle}, which is in device
     * space. If <code>onStroke</code> is false, this method checks
     * whether or not the interior of the specified <code>Shape</code>
     * intersects the specified <code>Rectangle</code>.  If
     * <code>onStroke</code> is <code>true</code>, this method checks
     * whether or not the <code>Stroke</code> of the specified
     * <code>Shape</code> outline intersects the specified
     * <code>Rectangle</code>.
     * The rendering attributes taken into account include the
     * <code>Clip</code>, <code>Transform</code>, and <code>Stroke</code>
     * attributes.
     * @param rect the area in device space to check for a hit
     * @param s the <code>Shape</code> to check for a hit
     * @param onStroke flag used to choose between testing the
     * stroked or the filled shape.  If the flag is <code>true</code>, the
     * <code>Stroke</code> oultine is tested.  If the flag is
     * <code>false</code>, the filled <code>Shape</code> is tested.
     * @return <code>true</code> if there is a hit; <code>false</code>
     * otherwise.
     * @see #setStroke
     * @see #fill
     * @see #draw
     * @see #transform
     * @see #setTransform
     * @see #clip
     * @see #setClip
     */
    public boolean hit(Rectangle rect,
                       Shape s,
                       boolean onStroke){
        if (onStroke) {
            s = gc.getStroke().createStrokedShape(s);
        }

        s = gc.getTransform().createTransformedShape(s);

        return s.intersects(rect);
    }


    /**
     * Returns the device configuration associated with this
     * <code>Graphics2D</code>.
     */
    public GraphicsConfiguration getDeviceConfiguration(){
        // TO BE DONE.
        return null;
    }


    /**
     * Sets the <code>Composite</code> for the <code>Graphics2D</code> context.
     * The <code>Composite</code> is used in all drawing methods such as
     * <code>drawImage</code>, <code>drawString</code>, <code>draw</code>,
     * and <code>fill</code>.  It specifies how new pixels are to be combined
     * with the existing pixels on the graphics device during the rendering
     * process.
     * <p>If this <code>Graphics2D</code> context is drawing to a
     * <code>Component</code> on the display screen and the
     * <code>Composite</code> is a custom object rather than an
     * instance of the <code>AlphaComposite</code> class, and if
     * there is a security manager, its <code>checkPermission</code>
     * method is called with an <code>AWTPermission("readDisplayPixels")</code>
     * permission.
     * @throws SecurityException
     *         if a custom <code>Composite</code> object is being
     *         used to render to the screen and a security manager
     *         is set and its <code>checkPermission</code> method
     *         does not allow the operation.
     * @param comp the <code>Composite</code> object to be used for rendering
     * @see java.awt.Graphics#setXORMode
     * @see java.awt.Graphics#setPaintMode
     * @see java.awt.AlphaComposite
     * @see java.lang.SecurityManager#checkPermission(java.awt.Permission)
     * @see java.awt.AWTPermission
     */
    public void setComposite(Composite comp){
        gc.setComposite(comp);
    }


    /**
     * Sets the <code>Paint</code> attribute for the
     * <code>Graphics2D</code> context.  Calling this method
     * with a <code>null</code> <code>Paint</code> object does
     * not have any effect on the current <code>Paint</code> attribute
     * of this <code>Graphics2D</code>.
     * @param paint the <code>Paint</code> object to be used to generate
     * color during the rendering process, or <code>null</code>
     * @see java.awt.Graphics#setColor
     * @see GradientPaint
     * @see TexturePaint
     */
    public void setPaint( Paint paint ){
        gc.setPaint(paint);
    }


    /**
     * Sets the <code>Stroke</code> for the <code>Graphics2D</code> context.
     * @param s the <code>Stroke</code> object to be used to stroke a
     * <code>Shape</code> during the rendering process
     * @see BasicStroke
     */
    public void setStroke(Stroke s){
        gc.setStroke(s);
    }


    /**
     * Sets the value of a single preference for the rendering algorithms.
     * Hint categories include controls for rendering quality and overall
     * time/quality trade-off in the rendering process.  Refer to the
     * <code>RenderingHints</code> class for definitions of some common
     * keys and values.
     * @param hintKey the key of the hint to be set.
     * @param hintValue the value indicating preferences for the specified
     * hint category.
     * @see RenderingHints
     */
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue){
        gc.setRenderingHint(hintKey, hintValue);
    }


    /**
     * Returns the value of a single preference for the rendering algorithms.
     * Hint categories include controls for rendering quality and overall
     * time/quality trade-off in the rendering process.  Refer to the
     * <code>RenderingHints</code> class for definitions of some common
     * keys and values.
     * @param hintKey the key corresponding to the hint to get.
     * @return an object representing the value for the specified hint key.
     * Some of the keys and their associated values are defined in the
     * <code>RenderingHints</code> class.
     * @see RenderingHints
     */
    public Object getRenderingHint(RenderingHints.Key hintKey){
        return gc.getRenderingHint(hintKey);
    }


    /**
     * Replaces the values of all preferences for the rendering
     * algorithms with the specified <code>hints</code>.
     * The existing values for all rendering hints are discarded and
     * the new set of known hints and values are initialized from the
     * specified {@link Map} object.
     * Hint categories include controls for rendering quality and
     * overall time/quality trade-off in the rendering process.
     * Refer to the <code>RenderingHints</code> class for definitions of
     * some common keys and values.
     * @param hints the rendering hints to be set
     * @see RenderingHints
     */
    public void setRenderingHints(Map hints){
        gc.setRenderingHints(hints);
    }


    /**
     * Sets the values of an arbitrary number of preferences for the
     * rendering algorithms.
     * Only values for the rendering hints that are present in the
     * specified <code>Map</code> object are modified.
     * All other preferences not present in the specified
     * object are left unmodified.
     * Hint categories include controls for rendering quality and
     * overall time/quality trade-off in the rendering process.
     * Refer to the <code>RenderingHints</code> class for definitions of
     * some common keys and values.
     * @param hints the rendering hints to be set
     * @see RenderingHints
     */
    public void addRenderingHints(Map hints){
        gc.addRenderingHints(hints);
    }


    /**
     * Gets the preferences for the rendering algorithms.  Hint categories
     * include controls for rendering quality and overall time/quality
     * trade-off in the rendering process.
     * Returns all of the hint key/value pairs that were ever specified in
     * one operation.  Refer to the
     * <code>RenderingHints</code> class for definitions of some common
     * keys and values.
     * @return a reference to an instance of <code>RenderingHints</code>
     * that contains the current preferences.
     * @see RenderingHints
     */
    public RenderingHints getRenderingHints(){
        return gc.getRenderingHints();
    }

    /**
     * Concatenates the current
     * <code>Graphics2D</code> <code>Transform</code>
     * with a translation transform.
     * Subsequent rendering is translated by the specified
     * distance relative to the previous position.
     * This is equivalent to calling transform(T), where T is an
     * <code>AffineTransform</code> represented by the following matrix:
     * <pre>
     *          [   1    0    tx  ]
     *          [   0    1    ty  ]
     *          [   0    0    1   ]
     * </pre>
     * @param tx the distance to translate along the x-axis
     * @param ty the distance to translate along the y-axis
     */
    public void translate(double tx, double ty){
        gc.translate(tx, ty);
    }


    /**
     * Concatenates the current <code>Graphics2D</code>
     * <code>Transform</code> with a rotation transform.
     * Subsequent rendering is rotated by the specified radians relative
     * to the previous origin.
     * This is equivalent to calling <code>transform(R)</code>, where R is an
     * <code>AffineTransform</code> represented by the following matrix:
     * <pre>
     *          [   cos(theta)    -sin(theta)    0   ]
     *          [   sin(theta)     cos(theta)    0   ]
     *          [       0              0         1   ]
     * </pre>
     * Rotating with a positive angle theta rotates points on the positive
     * x axis toward the positive y axis.
     * @param theta the angle of rotation in radians
     */
    public void rotate(double theta){
        gc.rotate(theta);
    }


    /**
     * Concatenates the current <code>Graphics2D</code>
     * <code>Transform</code> with a translated rotation
     * transform.  Subsequent rendering is transformed by a transform
     * which is constructed by translating to the specified location,
     * rotating by the specified radians, and translating back by the same
     * amount as the original translation.  This is equivalent to the
     * following sequence of calls:
     * <pre>
     *          translate(x, y);
     *          rotate(theta);
     *          translate(-x, -y);
     * </pre>
     * Rotating with a positive angle theta rotates points on the positive
     * x axis toward the positive y axis.
     * @param theta the angle of rotation in radians
     * @param x,&nbsp;y coordinates of the origin of the rotation
     */
    public void rotate(double theta, double x, double y){
        gc.rotate(theta, x, y);
    }


    /**
     * Concatenates the current <code>Graphics2D</code>
     * <code>Transform</code> with a scaling transformation
     * Subsequent rendering is resized according to the specified scaling
     * factors relative to the previous scaling.
     * This is equivalent to calling <code>transform(S)</code>, where S is an
     * <code>AffineTransform</code> represented by the following matrix:
     * <pre>
     *          [   sx   0    0   ]
     *          [   0    sy   0   ]
     *          [   0    0    1   ]
     * </pre>
     * @param sx the amount by which X coordinates in subsequent
     * rendering operations are multiplied relative to previous
     * rendering operations.
     * @param sy the amount by which Y coordinates in subsequent
     * rendering operations are multiplied relative to previous
     * rendering operations.
     */
    public void scale(double sx, double sy){
        gc.scale(sx, sy);
    }

    /**
     * Concatenates the current <code>Graphics2D</code>
     * <code>Transform</code> with a shearing transform.
     * Subsequent renderings are sheared by the specified
     * multiplier relative to the previous position.
     * This is equivalent to calling <code>transform(SH)</code>, where SH
     * is an <code>AffineTransform</code> represented by the following
     * matrix:
     * <pre>
     *          [   1   shx   0   ]
     *          [  shy   1    0   ]
     *          [   0    0    1   ]
     * </pre>
     * @param shx the multiplier by which coordinates are shifted in
     * the positive X axis direction as a function of their Y coordinate
     * @param shy the multiplier by which coordinates are shifted in
     * the positive Y axis direction as a function of their X coordinate
     */
    public void shear(double shx, double shy){
        gc.shear(shx, shy);
    }

    /**
     * Composes an <code>AffineTransform</code> object with the
     * <code>Transform</code> in this <code>Graphics2D</code> according
     * to the rule last-specified-first-applied.  If the current
     * <code>Transform</code> is Cx, the result of composition
     * with Tx is a new <code>Transform</code> Cx'.  Cx' becomes the
     * current <code>Transform</code> for this <code>Graphics2D</code>.
     * Transforming a point p by the updated <code>Transform</code> Cx' is
     * equivalent to first transforming p by Tx and then transforming
     * the result by the original <code>Transform</code> Cx.  In other
     * words, Cx'(p) = Cx(Tx(p)).  A copy of the Tx is made, if necessary,
     * so further modifications to Tx do not affect rendering.
     * @param Tx the <code>AffineTransform</code> object to be composed with
     * the current <code>Transform</code>
     * @see #setTransform
     * @see AffineTransform
     */
    public void transform(AffineTransform Tx){
        gc.transform(Tx);
    }

    /**
     * Sets the <code>Transform</code> in the <code>Graphics2D</code>
     * context.
     * @param Tx the <code>AffineTransform</code> object to be used in the
     * rendering process
     * @see #transform
     * @see AffineTransform
     */
    public void setTransform(AffineTransform Tx){
        gc.setTransform(Tx);
    }


    /**
     * Returns a copy of the current <code>Transform</code> in the
     * <code>Graphics2D</code> context.
     * @return the current <code>AffineTransform</code> in the
     *             <code>Graphics2D</code> context.
     * @see #transform
     * @see #setTransform
     */
    public AffineTransform getTransform(){
        return gc.getTransform();
    }


    /**
     * Returns the current <code>Paint</code> of the
     * <code>Graphics2D</code> context.
     * @return the current <code>Graphics2D</code> <code>Paint</code>,
     * which defines a color or pattern.
     * @see #setPaint
     * @see java.awt.Graphics#setColor
     */
    public Paint getPaint(){
        return gc.getPaint();
    }


    /**
     * Returns the current <code>Composite</code> in the
     * <code>Graphics2D</code> context.
     * @return the current <code>Graphics2D</code> <code>Composite</code>,
     *              which defines a compositing style.
     * @see #setComposite
     */
    public Composite getComposite(){
        return gc.getComposite();
    }


    /**
     * Sets the background color for the <code>Graphics2D</code> context.
     * The background color is used for clearing a region.
     * When a <code>Graphics2D</code> is constructed for a
     * <code>Component</code>, the background color is
     * inherited from the <code>Component</code>. Setting the background color
     * in the <code>Graphics2D</code> context only affects the subsequent
     * <code>clearRect</code> calls and not the background color of the
     * <code>Component</code>.  To change the background
     * of the <code>Component</code>, use appropriate methods of
     * the <code>Component</code>.
     * @param color the background color that isused in
     * subsequent calls to <code>clearRect</code>
     * @see #getBackground
     * @see java.awt.Graphics#clearRect
     */
    public void setBackground(Color color){
        gc.setBackground(color);
    }


    /**
     * Returns the background color used for clearing a region.
     * @return the current <code>Graphics2D</code> <code>Color</code>,
     * which defines the background color.
     * @see #setBackground
     */
    public Color getBackground(){
        return gc.getBackground();
    }


    /**
     * Returns the current <code>Stroke</code> in the
     * <code>Graphics2D</code> context.
     * @return the current <code>Graphics2D</code> <code>Stroke</code>,
     *                 which defines the line style.
     * @see #setStroke
     */
    public Stroke getStroke(){
        return gc.getStroke();
    }


    /**
     * Intersects the current <code>Clip</code> with the interior of the
     * specified <code>Shape</code> and sets the <code>Clip</code> to the
     * resulting intersection.  The specified <code>Shape</code> is
     * transformed with the current <code>Graphics2D</code>
     * <code>Transform</code> before being intersected with the current
     * <code>Clip</code>.  This method is used to make the current
     * <code>Clip</code> smaller.
     * To make the <code>Clip</code> larger, use <code>setClip</code>.
     * The <i>user clip</i> modified by this method is independent of the
     * clipping associated with device bounds and visibility.  If no clip has
     * previously been set, or if the clip has been cleared using
     * {@link Graphics#setClip(Shape) setClip} with a <code>null</code>
     * argument, the specified <code>Shape</code> becomes the new
     * user clip.
     * @param s the <code>Shape</code> to be intersected with the current
     *          <code>Clip</code>.  If <code>s</code> is <code>null</code>,
     *          this method clears the current <code>Clip</code>.
     */
    public void clip(Shape s){
        gc.clip(s);
    }


    /**
     * Get the rendering context of the <code>Font</code> within this
     * <code>Graphics2D</code> context.
     * The {@link FontRenderContext}
     * encapsulates application hints such as anti-aliasing and
     * fractional metrics, as well as target device specific information
     * such as dots-per-inch.  This information should be provided by the
     * application when using objects that perform typographical
     * formatting, such as <code>Font</code> and
     * <code>TextLayout</code>.  This information should also be provided
     * by applications that perform their own layout and need accurate
     * measurements of various characteristics of glyphs such as advance
     * and line height when various rendering hints have been applied to
     * the text rendering.
     *
     * @return a reference to an instance of FontRenderContext.
     * @see java.awt.font.FontRenderContext
     * @see java.awt.Font#createGlyphVector
     * @see java.awt.font.TextLayout
     * @since     JDK1.2
     */
    public FontRenderContext getFontRenderContext(){
        return gc.getFontRenderContext();
    }
}
