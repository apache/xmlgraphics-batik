/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.codec;

import org.apache.batik.test.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

/**
 * This test validates the PNGEncoder operation. It creates a
 * BufferedImage, then encodes it with the PNGEncoder, then
 * decodes it and compares the decoded image with the original one.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PNGEncoderTest extends AbstractTest {
    /**
     * Error when image cannot be encoded
     * {0} = trace for the exception which was reported
     */
    public static final String ERROR_CANNOT_ENCODE_IMAGE 
        = "PNGEncoderTest.error.cannot.encode.image";

    /**
     * Error when image cannot be decoded
     * {0} = trace for the exception which was reported
     */
    public static final String ERROR_CANNOT_DECODE_IMAGE
        = "PNGEncoderTest.error.cannot.decode.image";

    /**
     * Decoded image differs from encoded image
     */
    public static final String ERROR_DECODED_DOES_NOT_MATCH_ENCODED
        = "PNGEncoderTest.error.decoded.does.not.match.encoded";

    public TestReport runImpl() throws Exception {
        // Create a BufferedImage to be encoded
        BufferedImage image = new BufferedImage(100, 75, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = image.createGraphics();
        ig.scale(.5, .5);
        ig.setPaint(new Color(128,0,0));
        ig.fillRect(0, 0, 100, 50);
        ig.setPaint(Color.orange);
        ig.fillRect(100, 0, 100, 50);
        ig.setPaint(Color.yellow);
        ig.fillRect(0, 50, 100, 50);
        ig.setPaint(Color.red);
        ig.fillRect(100, 50, 100, 50);
        ig.setPaint(new Color(255, 127, 127));
        ig.fillRect(0, 100, 100, 50);
        ig.setPaint(Color.black);
        ig.draw(new Rectangle2D.Double(0.5, 0.5, 199, 149));
        ig.dispose();

        image = image.getSubimage(50, 0, 50, 25); 

        // Create an output stream where the PNG data
        // will be stored.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream os = buildOutputStream(bos);

        // Now, try to encode image
        PNGEncodeParam params =
            PNGEncodeParam.getDefaultEncodeParam(image);
        PNGImageEncoder pngImageEncoder = new PNGImageEncoder(os, params);

        try{
            pngImageEncoder.encode(image);
            os.close();
        }catch(Exception e){
            return reportException(ERROR_CANNOT_ENCODE_IMAGE, e);
        }

        // Now, try to decode image
        InputStream is 
            = buildInputStream(bos);

        PNGImageDecoder pngImageDecoder 
            = new PNGImageDecoder(is, new PNGDecodeParam());

        RenderedImage decodedRenderedImage = null;
        try{
            decodedRenderedImage = pngImageDecoder.decodeAsRenderedImage(0);
        }catch(Exception e){
            return reportException(ERROR_CANNOT_DECODE_IMAGE,
                            e);
        }

        BufferedImage decodedImage = null;
        if(decodedRenderedImage instanceof BufferedImage){
            decodedImage = (BufferedImage)decodedRenderedImage;
        }
        else{
            decodedImage = new BufferedImage(decodedRenderedImage.getWidth(),
                                             decodedRenderedImage.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB);
            ig = decodedImage.createGraphics();
            ig.drawRenderedImage(decodedRenderedImage, 
                                 new AffineTransform());
            ig.dispose();
        }

        // Compare images
        if(checkIdentical(image, decodedImage) != true){
            return reportError(ERROR_DECODED_DOES_NOT_MATCH_ENCODED);
        }

        return reportSuccess();
    }

    /**
     * Template method for building the PNG output stream. This gives a
     * chance to sub-classes (e.g., Base64PNGEncoderTest) to add an
     * additional encoding.
     */
    public OutputStream buildOutputStream(ByteArrayOutputStream bos){
        return bos;
    }

    /**
     * Template method for building the PNG input stream. This gives a
     * chance to sub-classes (e.g., Base64PNGEncoderTest) to add an
     * additional decoding.
     */
    public InputStream buildInputStream(ByteArrayOutputStream bos){
        return new ByteArrayInputStream(bos.toByteArray());
    }

    /**
     * Compares the data for the two images
     */
    public static boolean checkIdentical(BufferedImage imgA,
                                         BufferedImage imgB){
        boolean identical = true;
        if(imgA.getWidth() == imgB.getWidth() 
           &&
           imgA.getHeight() == imgB.getHeight()){
            int w = imgA.getWidth();
            int h = imgA.getHeight();
            for(int i=0; i<h; i++){
                for(int j=0; j<w; j++){
                    if(imgA.getRGB(j,i) != imgB.getRGB(j,i)){
                        identical = false;
                        break;
                    }
                }
                if( !identical ){
                    break;
                }
            }
        }

        return identical;
    }

}
