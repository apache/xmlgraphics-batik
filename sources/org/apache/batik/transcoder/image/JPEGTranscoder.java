/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.resources.Messages;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.batik.transcoder.TranscoderInput;

/**
 * This class is an <tt>ImageTranscoder</tt> that produces a JPEG image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class JPEGTranscoder extends ImageTranscoder {

    /**
     * The JPEG encoder quality key.
     */
    public static final TranscodingHints.Key KEY_QUALITY = new QualityKey(0);

    /**
     * Constructs a new transcoder that produces jpeg images.
     */
    public JPEGTranscoder() {
        hints.put(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
    }

    /**
     * Creates a new ARGB image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @param TranscoderException if an error occured while storing the image
     */
    public void writeImage(BufferedImage img, TranscoderOutput output)
        throws TranscoderException {
        OutputStream ostream = output.getOutputStream();
        if (ostream == null) {
            throw new TranscoderException("jpeg.badoutput");
        }
        float quality;
        if (hints.containsKey(KEY_QUALITY)) {
            quality = ((Float)hints.get(KEY_QUALITY)).floatValue();
        } else {
            handler.error(new TranscoderException("jpeg.unspecifiedQuality"));
            quality = 1f;
        }
        try {
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(ostream);
            JPEGEncodeParam params = JPEGCodec.getDefaultJPEGEncodeParam(img);
            params.setQuality(quality, true);
            jpegEncoder.encode(img, params);
        } catch (IOException ex) {
            throw new TranscoderException(ex);
        }
    }

    /**
     * A transcoding Key represented the JPEG image quality.
     */
    private static class QualityKey extends TranscodingHints.Key {
        public QualityKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            if (v instanceof Float) {
                float q = ((Float)v).floatValue();
                return (q > 0 && q <= 1f);
            } else {
                return false;
            }
        }
    }
/*
    static void save(String inputFilename, String outputFilename,
                     int w, int h, Rectangle aoi) throws Exception {
        System.out.println("Saving: "+inputFilename+" to "+outputFilename);
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(KEY_XML_PARSER_CLASSNAME,
                             "org.apache.crimson.parser.XMLReaderImpl");
        t.addTranscodingHint(KEY_QUALITY, new Float(.8f));
        if (w > 0) {
            t.addTranscodingHint(KEY_WIDTH, new Integer(w));
        }
        if (h > 0) {
            t.addTranscodingHint(KEY_HEIGHT, new Integer(h));
        }
        if (aoi != null) {
            t.addTranscodingHint(KEY_AOI, aoi);
        }
        String uri = new File(inputFilename).toURL().toString();
        TranscoderInput input = new TranscoderInput(uri);
        OutputStream ostream = new FileOutputStream(outputFilename);
        TranscoderOutput output = new TranscoderOutput(ostream);
        t.transcode(input, output);
        ostream.flush();
        ostream.close();
        System.out.println(outputFilename+" saved\n");
    }

    public static void main(String [] args) throws Exception {

        // document with a viewport

        if (args.length > 0) {
            String s = args[0];
            s = s.substring(s.lastIndexOf(File.separatorChar));
            s = s.substring(1, s.lastIndexOf('.'));;
            save(args[0], s+".jpg",
                 -1, -1, null);
            save(args[0], s+"-thumbnail.jpg",
                 200, -1, null);
        } else {
            save("samples/anne.svg", "anne-identity.jpg",
                 -1, -1, null);

            save("samples/anne.svg", "anne-x2.jpg",
                 900, 1000, null);

            save("samples/anne.svg", "anne-thumbnail.jpg",
                 225, -1, null);

            save("samples/anne.svg", "anne-tile1.jpg",
                 225, -1, new Rectangle(0, 0, 225, 250));
            save("samples/anne.svg", "anne-tile2.jpg",
                 225, -1, new Rectangle(225, 0, 225, 250));
            save("samples/anne.svg", "anne-tile3.jpg",
                 225, -1, new Rectangle(0, 250, 225, 250));
            save("samples/anne.svg", "anne-tile4.jpg",
                 225, -1, new Rectangle(225, 250, 225, 250));

            save("samples/anne.svg", "anne-tile1x2.jpg",
                 450, 500, new Rectangle(0, 0, 225, 250));
            save("samples/anne.svg", "anne-tile2x2.jpg",
                 450, 500, new Rectangle(225, 0, 225, 250));
            save("samples/anne.svg", "anne-tile3x2.jpg",
                 450, 500, new Rectangle(0, 250, 225, 250));
            save("samples/anne.svg", "anne-tile4x2.jpg",
                 450, 500, new Rectangle(225, 250, 225, 250));

            // document without a viewport

            save("local/tests/fish.svg", "fish-identity.jpg",
                 -1, -1, null);

            save("local/tests/fish.svg", "fish-x2.jpg",
                 1200, 800, null);
            save("local/tests/fish.svg", "fish-thumbnail.jpg",
                 300, -1, null);

            save("local/tests/fish.svg", "fish-tile1.jpg",
                 300, -1, new Rectangle(0, 0, 300, 200));
            save("local/tests/fish.svg", "fish-tile2.jpg",
                 300, -1, new Rectangle(300, 0, 300, 200));
            save("local/tests/fish.svg", "fish-tile3.jpg",
                 300, -1, new Rectangle(0, 200, 300, 200));
            save("local/tests/fish.svg", "fish-tile4.jpg",
                 300, -1, new Rectangle(300, 200, 300, 200));

            save("local/tests/fish.svg", "fish-tile1x2.jpg",
                 600, 400, new Rectangle(0, 0, 300, 200));
            save("local/tests/fish.svg", "fish-tile2x2.jpg",
                 600, 400, new Rectangle(300, 0, 300, 200));
            save("local/tests/fish.svg", "fish-tile3x2.jpg",
                 600, 400, new Rectangle(0, 200, 300, 200));
            save("local/tests/fish.svg", "fish-tile4x2.jpg",
                 600, 400, new Rectangle(300, 200, 300, 200));
        }

        System.exit(0);
    }
*/
}
