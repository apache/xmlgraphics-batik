/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

// <!> FIXME : Those import clauses will change with new design
import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.refimpl.bridge.DefaultBridgeContext;
import org.apache.batik.refimpl.bridge.SVGUtilities;
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;

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
 * <p>Three additional transcoding hints that act on the SVG
 * processor can be specified:
 *
 * <p><tt>KEY_LANGUAGE</tt> to set the default language to use (may be
 * used by a &lt;switch> SVG element for example),
 * <tt>KEY_USER_STYLESHEET_URI</tt> to fix the URI of a user
 * stylesheet, and <tt>KEY_PIXEL_TO_MM</tt> to specify the pixel to
 * millimeter conversion factor.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class ImageTranscoder extends XMLAbstractTranscoder {

    /** The user agent dedicated to an <tt>ImageTranscoder</tt>. */
    protected UserAgent userAgent = new ImageTranscoderUserAgent();

    /**
     * Constructs a new <tt>ImageTranscoder</tt>.
     */
    protected ImageTranscoder() {
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

    // --------------------------------------------------------------------
    // UserAgent implementation
    // --------------------------------------------------------------------

    /**
     * A user agent implementation for <tt>ImageTranscoder</tt>.
     */
    protected class ImageTranscoderUserAgent implements UserAgent {

        /**
         * Returns the default size of this user agent (400x400).
         */
        public Dimension2D getViewportSize() {
            return new Dimension(400, 400);
        }

        /**
         * Displays the specified error message using the <tt>ErrorHandler</tt>.
         */
        public void displayError(String message) {
            try {
                handler.error(new TranscoderException(message));
            } catch (TranscoderException ex) {
                throw new RuntimeException();
            }
        }

        /**
         * Displays the specified error using the <tt>ErrorHandler</tt>.
         */
        public void displayError(Exception e) {
            try {
                handler.error(new TranscoderException(e));
            } catch (TranscoderException ex) {
                throw new RuntimeException();
            }
        }

        /**
         * Displays the specified message using the <tt>ErrorHandler</tt>.
         */
        public void displayMessage(String message) {
            try {
                handler.warning(new TranscoderException(message));
            } catch (TranscoderException ex) {
                throw new RuntimeException();
            }
        }

        /**
         * Returns the pixel to millimeter conversion factor specified in the
         * <tt>TranscodingHints</tt> or 0.3528 if any.
         */
        public float getPixelToMM() {
            if (hints.containsKey(KEY_PIXEL_TO_MM)) {
                return ((Float)hints.get(KEY_PIXEL_TO_MM)).floatValue();
            } else {
                return 0.3528f;
            }
        }

        /**
         * Returns the user language specified in the
         * <tt>TranscodingHints</tt> or "en" (english) if any.
         */
        public String getLanguages() {
            if (hints.containsKey(KEY_LANGUAGE)) {
                return (String)hints.get(KEY_LANGUAGE);
            } else {
                return "en";
            }
        }

        /**
         * Returns the user stylesheet specified in the
         * <tt>TranscodingHints</tt> or null if any.
         */
        public String getUserStyleSheetURI() {
            return (String)hints.get(KEY_USER_STYLESHEET_URI);
        }

        /**
         * Returns the XML parser to use from the TranscodingHints.
         */
        public String getXMLParserClassName() {
            return (String)hints.get(KEY_XML_PARSER_CLASSNAME);
        }

        /**
         * Unsupported operation.
         */
        public EventDispatcher getEventDispatcher() {
            return null;
        }

        /**
         * Unsupported operation.
         */
        public void openLink(SVGAElement elt) { }

        /**
         * Unsupported operation.
         */
        public void setSVGCursor(Cursor cursor) { }

        /**
         * Unsupported operation.
         */
        public void runThread(Thread t) { }

        /**
         * Unsupported operation.
         */
        public AffineTransform getTransform() {
            return null;
        }

        /**
         * Unsupported operation.
         */
        public Point getClientAreaLocationOnScreen() {
            return new Point();
        }
    }

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * The image width key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_WIDTH</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The width of the top most svg element</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the width of the image to create.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_WIDTH
        = new LengthKey(0);

    /**
     * The image height key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_HEIGHT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Integer</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The height of the top most svg element</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the height of the image to create.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_HEIGHT
        = new LengthKey(1);

    /**
     * The image background paint key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_BACKGROUND_COLOR</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Paint</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the background color to use.
     * The color is required by opaque image formats and is used by
     * image formats that support alpha channel.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR
        = new PaintKey(0);

    /**
     * The area of interest key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_AOI</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Rectangle</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The document's size</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the area of interest to render. The
     * rectangle coordinates must be specified in pixels and in the
     * document coordinates system.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_AOI
        = new RectangleKey(0);

    /**
     * The language key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_LANGUAGE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">"en"</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the preferred language of the document.
     * </TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_LANGUAGE
        = new StringKey(0);

    /**
     * The user stylesheet URI key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_USER_STYLESHEET_URI</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the user style sheet.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_USER_STYLESHEET_URI
        = new StringKey(1);

    /**
     * The pixel to millimeter conversion factor key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_PIXEL_TO_MM</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">0.33</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the pixel to millimeter conversion factor.
     * </TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_PIXEL_TO_MM
        = new FloatKey(0);

    /**
     * A transcoding Key represented as a string.
     */
    private static class StringKey extends TranscodingHints.Key {
        public StringKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof String);
        }
    }

    /**
     * A transcoding Key represented as a float.
     */
    private static class FloatKey extends TranscodingHints.Key {
        public FloatKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Float);
        }
    }

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
