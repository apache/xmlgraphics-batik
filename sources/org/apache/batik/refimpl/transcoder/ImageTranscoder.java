/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.refimpl.bridge.DefaultBridgeContext;
import org.apache.batik.refimpl.bridge.SVGUtilities;
import org.apache.batik.refimpl.gvt.renderer.StaticRendererFactory;
import org.apache.batik.transcoder.TranscoderException;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;


/**
 * A generic <tt>Transcoder</tt> to transcode document to an image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class ImageTranscoder extends AbstractTranscoder {

    public void transcodeToStream(Document document, OutputStream ostream)
            throws TranscoderException {
        SVGDocument svgDocument = (SVGDocument) document;
        RendererFactory rendererFactory = new StaticRendererFactory();
        BridgeContext ctx =
            new DefaultBridgeContext(getParserClassName(), svgDocument);
        ctx.setGVTBuilder(getGVTBuilder());
        ctx.setCurrentViewport(getDefaultViewport());
        ctx.setGraphicsNodeRenderContext(rendererFactory.getRenderContext());

        UserAgent ua = ctx.getUserAgent();
        DefaultSVGContext svgCtx = new DefaultSVGContext();
        svgCtx.setPixelToMM(ua.getPixelToMM());
        //Dimension2D dim = ua.getViewportSize();
        //svgCtx.setViewportWidth((float)dim.getWidth());
        //svgCtx.setViewportHeight((float)dim.getHeight());
        //svgCtx.setUserStyleSheetURI(null);
        ((SVGOMDocument) svgDocument).setSVGContext(svgCtx);

        ParserFactory parserFactory = ctx.getParserFactory();

        SVGSVGElement elt = svgDocument.getRootElement();
        int w = (int) elt.getWidth().getBaseVal().getValue();
        int h = (int) elt.getHeight().getBaseVal().getValue();

        // build the GVT tree
        GraphicsNode gvtRoot = getGVTBuilder().build(ctx, document);

        // create the renderer and paint the offscreen image
        BufferedImage img = createImage(w, h);
        Graphics2D g2d = img.createGraphics();
        g2d.setClip(0, 0, w, h);
        g2d.setComposite(AlphaComposite.Src);
        if(getBackgroundPaint() != null){
            g2d.setPaint(getBackgroundPaint());
            g2d.fillRect(0, 0, w, h);
        }
        g2d.setComposite(AlphaComposite.SrcOver);

        Renderer renderer = rendererFactory.createRenderer(img);

        AffineTransform t = SVGUtilities.getPreserveAspectRatioTransform
            (elt, w, h);
        renderer.setTransform(t);
        renderer.setTree(gvtRoot);
        try {
            Shape s = t.createInverse().createTransformedShape(new Rectangle(0, 0, w, h));
            renderer.repaint(s);
            // save the offscreen image
            writeImage(img, ostream);
        } catch (IOException ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        }
    }

    /**
     * Creates a new image of the specified dimension.
     * @param w the width of the image
     * @param h the height of the image
     */
    public abstract BufferedImage createImage(int w, int h);

    /**
     * Writes the specified image to the specified output stream.
     * @param img the image to write
     * @param ostream the output stream where to write the image
     * @param IOException if an IO error occured
     */
    public abstract void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException;

}
