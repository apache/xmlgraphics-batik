/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.refimpl.bridge.DefaultUserAgent;
import org.apache.batik.refimpl.bridge.SVGBridgeContext;

import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;

import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;
import org.apache.batik.refimpl.gvt.renderer.StaticRendererFactory;

import org.apache.batik.refimpl.parser.ParserFactory;

import org.apache.batik.util.SVGUtilities;

import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class represents a JComponent which is able to represents
 * a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGCanvas extends JComponent {
    /**
     * The global offscreen buffer.
     */
    protected BufferedImage globalBuffer;

    /**
     * The current offscreen buffer.
     */
    protected BufferedImage buffer;

    /**
     * Root of the GVT tree displayed by this viewer
     */
    protected GraphicsNode gvtRoot;

    /**
     * The current renderer.
     */
    protected Renderer renderer;

    /**
     * The renderer factory.
     */
    protected RendererFactory rendererFactory;

    /**
     * The GVT builder.
     */
    protected GVTBuilder builder;

    /**
     * The bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * The SVG document to render.
     */
    protected SVGDocument document;

    /**
     * Must the buffer be updated?
     */
    protected boolean repaint;

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * The tranform to apply to the graphics object.
     */
    protected AffineTransform transform = new AffineTransform();

    /**
     * Creates a new SVG canvas.
     */
    public JSVGCanvas() {
        this(new DefaultUserAgent());
    }

    /**
     * Creates a new SVG canvas.
     */
    public JSVGCanvas(UserAgent ua) {
        userAgent = ua;
        rendererFactory = new StaticRendererFactory();
 
        builder = new ConcreteGVTBuilder();
        bridgeContext = new SVGBridgeContext();
        bridgeContext.setGVTFactory
            (ConcreteGVTFactory.getGVTFactoryImplementation());
        bridgeContext.setParserFactory(new ParserFactory());
        bridgeContext.setUserAgent(userAgent);
        bridgeContext.setGraphicsNodeRableFactory
            (new ConcreteGraphicsNodeRableFactory());

        addComponentListener(new CanvasListener());
    }

    /**
     * Sets the renderer factory to use to create the renderer.
     */
    public void setRendererFactory(RendererFactory rf) {
        rendererFactory = rf;
        repaint();
    }

    /**
     * Returns the current renderer factory.
     */
    public RendererFactory getRendererFactory() {
        return rendererFactory;
    }

    /**
     * Sets the SVG document to display.
     */
    public void setSVGDocument(SVGDocument doc) {
        document = doc;
        bridgeContext.setViewCSS((ViewCSS)doc.getDocumentElement());
        gvtRoot = builder.build(bridgeContext, document);

        computeTransform();

        repaint = true;
        repaint();
    }

    /**
     * Clears the offscreen buffer.
     */
    protected void clearBuffer(int w, int h) {
        Graphics2D g = buffer.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.setClip(0, 0, w, h);
        g.setPaint(Color.white);
        g.fillRect(0, 0, w, h);
    }

    /**
     * Paints this component.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();
        int w = size.width;
        int h = size.height;

        if (w < 1 || h < 1) {
            return;
        }
        
        updateBuffer(w, h);
        if (repaint) {
            renderer = rendererFactory.createRenderer(buffer);
            renderer.setTransform(transform);
        }
        if (renderer != null && gvtRoot != null &&
            renderer.getTree() != gvtRoot) {
            renderer.setTree(gvtRoot);
            repaint = true;
        }
        if (repaint) {
            clearBuffer(w, h);
            renderer.repaint(new Rectangle2D.Float(0, 0, w, h));
        }
        repaint = false;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(buffer, null, 0, 0);
    }

    /**
     * Updates the offscreen buffer.
     * @param w&nbsp;h The size of the component.
     */
    protected void updateBuffer(int w, int h) {
        // Create a new buffer if needed.
        if (globalBuffer == null ||
            globalBuffer.getWidth() < w ||
            globalBuffer.getHeight() < h) {
            globalBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            buffer = globalBuffer;
            repaint = true;
        } else if (buffer.getWidth() != w ||
                   buffer.getHeight() != h) {
            buffer = globalBuffer.getSubimage(0, 0, w, h);
            repaint = true;
        }
    }

    /**
     * Computes the value of the transform attribute.
     */
    protected void computeTransform() {
        SVGSVGElement elt = document.getRootElement();
        Dimension size = getSize();
        int w = size.width;
        int h = size.height;
        
        transform = SVGUtilities.getPreserveAspectRatioTransform
            (elt, w, h, bridgeContext.getParserFactory());
    }

    /**
     * To correctly resize the view.
     */
    protected class CanvasListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            if (gvtRoot == null) {
                return;
            }
            computeTransform();
        }
    }
}
