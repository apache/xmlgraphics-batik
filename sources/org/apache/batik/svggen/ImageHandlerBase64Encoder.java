/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.*;

import org.apache.batik.util.svg.Base64Encoder;
import org.apache.batik.ext.awt.image.codec.ImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;

import org.w3c.dom.*;

/**
 * This implementation of ImageHandler encodes the input image as
 * a PNG image first, then encodes the PNG image using Base64 
 * encoding and uses the result to encoder the image url using
 * the data protocol.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandler
 */
public class ImageHandlerBase64Encoder extends DefaultImageHandler{
    private static final String DATA_PROTOCOL_PNG_PREFIX = "data:image/png;base64,";

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    protected void handleHREF(Image image, Element imageElement){
        if(image == null){
            throw new IllegalArgumentException();
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if(width>0 && height>0){
            handleEmptyImage(imageElement);
        }
        else{
            BufferedImage buf = new BufferedImage(width, height,
                                                  BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = buf.createGraphics();
            g.drawImage(image, 0, 0, null);
            handleHREF((RenderedImage)buf, imageElement);
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    protected void handleHREF(RenderableImage image, Element imageElement){
        if(image == null){
            throw new IllegalArgumentException();
        }
        
        RenderedImage r = image.createDefaultRendering();
        if(r == null){
            handleEmptyImage(imageElement);
        }
        else{
            handleHREF(r, imageElement);
        }
    }

    protected void handleEmptyImage(Element imageElement){
        imageElement.setAttribute(ATTR_HREF, DATA_PROTOCOL_PNG_PREFIX);
        imageElement.setAttribute(SVG_WIDTH_ATTRIBUTE, "0");
        imageElement.setAttribute(SVG_HEIGHT_ATTRIBUTE, "0");
    }

    /**
     * This version of handleHREF encodes the input image into a
     * PNG image whose bytes are then encoded with Base64. The
     * resulting encoded data is used to set the url on the 
     * input imageElement, using the data: protocol.
     */
    protected void handleHREF(RenderedImage image, Element imageElement){
        //
        // First, encode the input image in PNG
        //
        byte[] pngBytes = encodeImage(image);

        //
        // Now, convert PNG data to Base64
        //
        Base64Encoder b64Encoder = new Base64Encoder();
        ByteArrayInputStream is = new ByteArrayInputStream(pngBytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            b64Encoder.encodeBuffer(new ByteArrayInputStream(pngBytes),
                                    os);
        }catch(IOException e){
            // Should not happen because we are doing in-memory processing
            throw new Error();
        }

        //
        // Finally, write out url
        //
        imageElement.setAttribute(ATTR_HREF,
                                  DATA_PROTOCOL_PNG_PREFIX + 
                                  os.toString());
        
    }

    public byte[] encodeImage(RenderedImage buf){
        try{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageEncoder encoder = new PNGImageEncoder(os, null);
            encoder.encode(buf);
            os.flush();
            os.close();
            return os.toByteArray();
        }catch(IOException e){
            // We are doing in-memory processing. This should not happen.
            throw new Error();
        }
    }

    /**
     * This method creates a BufferedImage with an alpha channel, as this is
     * supported by Base64.
     */
    public BufferedImage buildBufferedImage(Dimension size){
        return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) {
        BufferedImage buf = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        g.setPaint(Color.red);
        g.fillRect(0, 0, 50, 50);
        g.fillRect(50, 50, 50, 50);
        g.dispose();

        ImageHandler imageHandler = new ImageHandlerBase64Encoder();
        Document domFactory = TestUtil.getDocumentPrototype();
        Element imageElement = domFactory.createElement(SVGSyntax.SVG_IMAGE_TAG);

        imageHandler.handleImage((RenderedImage)buf, imageElement);

        System.out.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        System.out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20000802//EN\"");
        System.out.println("\"http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd\">");
        System.out.println();
        System.out.println("<svg width=\"450\" height=\"500\">");
        System.out.println("    <rect width=\"100%\" height=\"100%\" fill=\"yellow\" />");
        System.out.println("    <image x=\"30\" y=\"30\" xlink:href=\"" + imageElement.getAttribute(SVGSyntax.ATTR_HREF) + "\" width=\"100\" height=\"100\" />");        
        System.out.println("</svg>");
        System.exit(0);
    }
}
