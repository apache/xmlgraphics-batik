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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.refimpl.bridge.DefaultBridgeContext;
import org.apache.batik.refimpl.gvt.renderer.StaticRendererFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.util.SVGUtilities;
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
        BridgeContext ctx =
            new DefaultBridgeContext(getParserClassName(), svgDocument);
        ctx.setGVTBuilder(getGVTBuilder());

        DefaultSVGContext svgCtx = new DefaultSVGContext();
        svgCtx.setUserAgent(ctx.getUserAgent());
        //svgCtx.setUserStyleSheetURI(null);
        ((SVGOMDocument) svgDocument).setSVGContext(svgCtx);

        ParserFactory parserFactory = ctx.getParserFactory();

        SVGSVGElement elt = svgDocument.getRootElement();
        int w = (int) elt.getWidth().getBaseVal().getValue();
        int h = (int) elt.getHeight().getBaseVal().getValue();

        // build the GVT tree
        GraphicsNode gvtRoot = getGVTBuilder().build(ctx, document);

        // create the renderer and paint the offscreen image
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setClip(0, 0, w, h);
        g2d.setComposite(AlphaComposite.Src);
        g2d.setPaint(getBackgroundPaint());
        g2d.fillRect(0, 0, w, h);
        g2d.setComposite(AlphaComposite.SrcOver);

        Renderer renderer = new StaticRendererFactory().createRenderer(img);
        AffineTransform t = SVGUtilities.getPreserveAspectRatioTransform
            (elt, w, h, parserFactory);
        renderer.setTransform(t);
        renderer.setTree(gvtRoot);
        renderer.repaint(new Rectangle(0, 0, w, h));
        // save the offscreen image
        try {
            writeImage(img, ostream);
        } catch (IOException ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        }
    }

    /**
     * Writes the specified image to the specified output stream.
     * @param img the image to write
     * @param ostream the output stream where to write the image
     * @param IOException if an IO error occured
     */
    protected abstract void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException;

}
