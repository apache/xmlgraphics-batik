/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.batik.transcoder.image.resources.Messages;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.svg.DefaultSVGContext;

// <!> FIXME : Those import clauses will changed with new design
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.refimpl.bridge.DefaultBridgeContext;
import org.apache.batik.refimpl.bridge.SVGUtilities;
import org.apache.batik.refimpl.bridge.DefaultUserAgent;

/**
 * This class enables to transcode an input to an image of any format.
 *
 * <p>Two transcoding hints (<tt>KEY_WIDTH</tt> and
 * <tt>KEY_HEIGHT</tt>) can be used to respectively specify the image
 * width and the image height. If only one of these keys is specified,
 * the transcoder preserves the aspect ratio of the original image.
 *
 * <p>The <tt>KEY_BACKGROUND_COLOR</tt> defines the background color
 * to use for opaque image formats, or the background color that may
 * be used for image formats that support alpha channel.
 *
 * <p>The <tt>KEY_AOI</tt> represents the area of interest to paint
 * in device space.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class ImageTranscoder extends XMLAbstractTranscoder {

    /**
     * The image width key.
     */
    public static final TranscodingHints.Key KEY_WIDTH = new LengthKey(0);

    /**
     * The image height key.
     */
    public static final TranscodingHints.Key KEY_HEIGHT = new LengthKey(1);

    /**
     * The image background paint key. Used by opaque image formats
     * and could be used by image formats that support alpha channel.
     */
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR = new PaintKey(0);

    /**
     * The area of interest key.
     */
    public static final TranscodingHints.Key KEY_AOI = new RectangleKey(0);

    /**
     * Constructs a new <tt>ImageTranscoder</tt>.
     */
    protected ImageTranscoder() {
        CSSDocumentHandler.setParserClassName(
            "org.apache.batik.css.parser.Parser");
        hints.put(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                  SVGConstants.SVG_NAMESPACE_URI);
        hints.put(KEY_DOCUMENT_ELEMENT,
                  SVGConstants.TAG_SVG);
        hints.put(KEY_DOM_IMPLEMENTATION,
                  SVGDOMImplementation.getDOMImplementation());
    }

    /**
     * Transcodes the specified Document as an image in the specified output.
     * @param document the document to transcode
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    protected void transcode(Document document, TranscoderOutput output)
            throws TranscoderException {
        if (!(document instanceof SVGOMDocument)) {
            throw new TranscoderException(
                Messages.formatMessage("notsvg", null));
        }
        SVGDocument svgDoc = (SVGDocument)document;
        SVGSVGElement root = svgDoc.getRootElement();
        // initialize the SVG document with the appropriate context
        String parserClassname = (String)hints.get(KEY_XML_PARSER_CLASSNAME);
        UserAgent userAgent = new DefaultUserAgent(parserClassname);
        DefaultSVGContext svgCtx = new DefaultSVGContext();
        svgCtx.setPixelToMM(userAgent.getPixelToMM());
        ((SVGOMDocument)document).setSVGContext(svgCtx);

        // get the width and height attributes of the SVG document
        int docWidth = (int)root.getWidth().getBaseVal().getValue();
        int docHeight = (int)root.getHeight().getBaseVal().getValue();
        // get transcoding hints
        Paint bgcolor = null;
        if (hints.containsKey(KEY_BACKGROUND_COLOR)) {
            bgcolor = (Paint)hints.get(KEY_BACKGROUND_COLOR);
        }
        int imgWidth = -1;
        if (hints.containsKey(KEY_WIDTH)) {
            imgWidth = ((Integer)hints.get(KEY_WIDTH)).intValue();
        }
        int imgHeight = -1;
        if (hints.containsKey(KEY_HEIGHT)) {
            imgHeight = ((Integer)hints.get(KEY_HEIGHT)).intValue();
        }
        Rectangle aoi;
        if (hints.containsKey(KEY_AOI)) {
            aoi = (Rectangle)hints.get(KEY_AOI);
        } else {
            aoi = new Rectangle(0, 0, docWidth, docHeight);
        }
        // compute the image's width and height according the hints
        int width, height;
        if (imgWidth > 0 && imgHeight > 0) {
            width = imgWidth;
            height = imgHeight;
        } else if (imgHeight > 0) {
            width = (docWidth * imgHeight) / docHeight;
            height = imgHeight;
        } else if (imgWidth > 0) {
            width = imgWidth;
            height = (docHeight * imgWidth) / docWidth;
        } else {
            width = docWidth;
            height = docHeight;
        }

        //
        // Compute the zoom factor and position considering the image size
        // and the aoi coordinates and dimension
        //
        //      [preserveAspectRatio]
        //      [scale]
        // Px = [translate]
        //
        // With:
        //
        // [preserveAspectRatio] : initial scale factor to fit image size
        // [scale] : take into account the size of the aoi
        // [translate] : take into account the (x, y) coordinates of the aoi
        //

        // preserve aspect ratio matrix
        AffineTransform Px;
        Px = SVGUtilities.getPreserveAspectRatioTransform(root, width, height);
        if (Px.isIdentity() && (width != docWidth || height != docHeight)) {
            // The document has no viewBox, we need to resize it by hand.
            // we want to keep the document size ratio
            float d = Math.max(docWidth, docHeight);
            float dd = Math.max(width, height);
            float scale = dd/d;
            Px = AffineTransform.getScaleInstance(scale, scale);
        }
        // aoi translation matrix
        AffineTransform Mx =
            AffineTransform.getTranslateInstance(-aoi.x, -aoi.y);
        // aoi scale factor
        float sx = (float)docWidth / aoi.width;
        float sy = (float)docHeight / aoi.height;
        Mx.preConcatenate(AffineTransform.getScaleInstance(sx, sy));

        Px.concatenate(Mx);

        // build the GVT tree
        GVTBuilder builder = new ConcreteGVTBuilder();
        BridgeContext ctx = new DefaultBridgeContext(userAgent, svgDoc);
        GraphicsNode gvtRoot = builder.build(ctx, svgDoc);
        // prepare the image to be painted
        BufferedImage img = createImage(width, height);
        Graphics2D g2d = img.createGraphics();
        g2d.setClip(0, 0, width, height);
        if (bgcolor != null) {
            g2d.setComposite(AlphaComposite.Src);
            g2d.setPaint(bgcolor);
            g2d.fillRect(0, 0, width, height);
        }
        g2d.setComposite(AlphaComposite.SrcOver);
        // paint the SVG document using the bridge package
        Renderer renderer = new StaticRenderer(img);
        renderer.setTransform(Px);
        renderer.setTree(gvtRoot);
        try {
            renderer.repaint(aoi);
            writeImage(img, output);
        } catch (Exception ex) {
            throw new TranscoderException(ex);
        }
    }

    /**
     * Creates a <tt>DocumentFactory</tt> that is used to create an SVG DOM
     * tree. The specified DOM Implementation is ignored and the Batik
     * SVG DOM Implementation is automatically used.
     *
     * @param domImpl the DOM Implementation (not used)
     * @param parserClassname the XML parser classname
     */
    protected DocumentFactory createDocumentFactory(DOMImplementation domImpl,
                                                    String parserClassname) {
        return new SAXSVGDocumentFactory(parserClassname);
    }

    /**
     * Creates a new image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public abstract BufferedImage createImage(int width, int height);

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @param TranscoderException if an error occured while storing the image
     */
    public abstract void writeImage(BufferedImage img, TranscoderOutput output)
        throws TranscoderException;


    /**
     * A transcoding Key represented as a length.
     */
    private static class LengthKey extends TranscodingHints.Key {
        public LengthKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Integer);
        }
    }

    /**
     * A transcoding Key represented as a background paint.
     */
    private static class PaintKey extends TranscodingHints.Key {
        public PaintKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Paint);
        }
    }

    /**
     * A transcoding Key represented as a rectangle.
     */
    private static class RectangleKey extends TranscodingHints.Key {
        public RectangleKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Rectangle);
        }
    }
}
