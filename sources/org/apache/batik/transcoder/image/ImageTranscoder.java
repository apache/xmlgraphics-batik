/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

// <!> FIXME : Those import clauses will changed with new design
import org.apache.batik.refimpl.gvt.renderer.StaticRenderer;
import org.apache.batik.refimpl.bridge.SVGUtilities;

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
     * The image background color key. Used by opaque image formats
     * and could be used by image formats that support alpha channel.
     */
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR = new ColorKey(0);

    /**
     * Constructs a new <tt>ImageTranscoder</tt>.
     */
    protected ImageTranscoder() {}

    /**
     * Transcodes the specified Document as an image in the specified output.
     * @param document the document to transcode
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    protected void transcode(Document document, TranscoderOutput output)
            throws TranscoderException {
    }

    /**
     * Returns the specified length in pixels.
     * @param length the length to parse and convert in pixels
     */
    private int getLengthInPixels(String length) throws TranscoderException {
        try {
            return Integer.parseInt(length);
        } catch (NumberFormatException ex) {
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
            // don't parse the length now !
            return (v instanceof String);
        }
    }

    /**
     * A transcoding Key represented as a color.
     */
    private static class ColorKey extends TranscodingHints.Key {
        public ColorKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Color);
        }
    }
}
