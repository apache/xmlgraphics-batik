/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.net.*;
import java.io.*;

import javax.swing.*;
import javax.swing.plaf.*;

import com.sun.image.codec.jpeg.*;

public class ImageLoader {
    /*
     * Image loading utility. 
     */
    protected final static Component component = new Component() {};
    protected final static MediaTracker tracker = new MediaTracker(component);

    /**
     * loadImage loads the image located at path.
     *
     * @param path location of image file in local file system.
     * @return loaded image at path or url
     */
    public static synchronized Image loadImage(String path){
        File file = new File(path);
        Image image = null;
        try{
            URL url = file.toURL();
            image = loadImage(url);
        }catch(MalformedURLException e){
        }

        return image;
    }

    /**
     * loadImage loads the image located at URL. 
     *
     * @param url URL where the image file is located.
     * @return loaded image at path or url
     */
    public static synchronized Image loadImage(URL url){
        Image image = null;
        image = Toolkit.getDefaultToolkit().getImage(url);

        if(image != null){
            tracker.addImage(image, 0);
            try{
                tracker.waitForAll();
            } catch (InterruptedException e) {
                tracker.removeImage(image);
                image = null;
            } finally {
                if(image!=null)
                    tracker.removeImage(image);
	
                if(tracker.isErrorAny())
                    image = null;

                if(image!=null){
                    if(image.getWidth(null)<0 || 
                       image.getHeight(null)<0)
                        image = null;
                }
            }
        }

        return image;
    }


    /**
     * loadImage loads an image from a given file into a BufferedImage.
     * The image is returned in the format defined by the imageType parameter.
     * Note that this is special cased for JPEG images where loading is performed
     * outside the standard media tracker, for efficiency reasons.
     *
     * @param file File where the image file is located.
     * @param imageType one of the image type defined in the BufferedImage class.
     * @return loaded image at path or url
     * @see java.awt.image.BufferedImage
     */
    public static synchronized BufferedImage loadImage(File file, int imageType){
        BufferedImage image = null;
        try{
            URL url = file.toURL();
            image = loadImage(url, imageType);
        }catch(MalformedURLException e){
        }

        return image;
    }

    /**
     * loadImage loads an image from a given path into a BufferedImage.
     * The image is returned in the format defined by the imageType parameter.
     * Note that this is special cased for JPEG images where loading is performed
     * outside the standard media tracker, for efficiency reasons.
     *
     * @param path Name of file where the image file is located.
     * @param imageType one of the image type defined in the BufferedImage class.
     * @return loaded image at path or url
     * @see java.awt.image.BufferedImage
     */
    public static synchronized BufferedImage loadImage(String path, int imageType){
        File file = new File(path);
        BufferedImage image = null;
        try{
            URL url = file.toURL();
            image = loadImage(url, imageType);
        }catch(MalformedURLException e){
        }

        return image;
    }

    /**
     * loadImage loads an image from a given URL into a BufferedImage.
     * The image is returned in the format defined by the imageType parameter.
     * Note that this is special cased for JPEG images where loading is performed
     * outside the standard media tracker, for efficiency reasons.
     *
     * @param url URL where the image file is located.
     * @param imageType one of the image type defined in the BufferedImage class.
     * @return loaded image at path or url
     * @see java.awt.image.BufferedImage
     */
    public static synchronized BufferedImage loadImage(URL url, int imageType){
        BufferedImage image = null; // return value

        // Special handling for JPEG images to avoid extra processing if possible.
        if(url==null || !url.toString().toLowerCase().endsWith(".jpg")){
            Image tmpImage = loadImage(url);
            if(tmpImage!=null){
                image = new BufferedImage(tmpImage.getWidth(null),
                                          tmpImage.getHeight(null),
                                          imageType);
                Graphics2D g = image.createGraphics();
                g.drawImage(tmpImage, 0, 0, null);
                g.dispose();
            }
        }
        else{
            BufferedImage tmpImage = loadJPEGImage(url);
            if(tmpImage != null){
                if(tmpImage.getType() != imageType){
                    // System.out.println("Incompatible JPEG image type: creating new buffer image");
                    image = new BufferedImage(tmpImage.getWidth(null),
                                              tmpImage.getHeight(null),
                                              imageType);
                    Graphics2D g = image.createGraphics();
                    g.drawImage(tmpImage, 0, 0, null);
                    g.dispose();
                }
                else
                    image = tmpImage;
            }
        }
        return image;
    }

    /**
     * loads a JPEG image from a given location.
     *
     * @param url URL where the image file is located.
     * @return loaded image at path or url
     */
    public static synchronized BufferedImage loadJPEGImage(URL url){
        BufferedImage image = null;

        if(url != null){
            InputStream in = null;
            try{
                in = url.openStream();
                JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
                image = decoder.decodeAsBufferedImage();
            }catch(IOException e){
                image = null;
            }finally{
                try{
                    if(in != null)
                        in.close();
                }catch(IOException ioe){}
            }

            if(image!=null){
                // System.out.println("Image type : " + image.getType());
                if(image.getWidth()<=0 || 
                   image.getHeight()<=0)
                    image = null;
            }
        }

        return image;
    }


}
