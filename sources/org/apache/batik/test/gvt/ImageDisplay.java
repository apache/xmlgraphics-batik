/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.gvt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * This is a small collection of display functions for images.
 * I have found these invaluable for debugging problems in the rendering
 * chain.
 */
public class ImageDisplay {
    
      /**
       * A function to print a sub rectangle of an image to System.out.
       * 
       * @param text   Text to print before image data.
       * @param ri     The image to take data from.
       * @param bounds The bounds of the rect to print.
       */
    public static void printImage(String text, 
                                  RenderedImage ri,
                                  Rectangle bounds) {
        java.awt.image.Raster ras = ri.getData();

        int minX = Math.max(bounds.x, ri.getMinX());
        int minY = Math.max(bounds.y, ri.getMinY());
        int maxX = Math.min(bounds.x + bounds.width,  
                            ri.getMinX() + ri.getWidth());
        int maxY = Math.min(bounds.y + bounds.height, 
                            ri.getMinY() + ri.getHeight());

        int [] pixel=null;
        if (text != null)
            System.out.println("\n" + text + "\n");
        for (int y=minY; y < maxY-1; y++) {
            for (int x=minX; x < maxX-1; x++) {
                pixel = ras.getPixel(x,y, pixel);
                System.out.print(Integer.toHexString(pixel[0]) + "," + 
                                 Integer.toHexString(pixel[1]) + "," +
                                 Integer.toHexString(pixel[2]) + "," +
                                 Integer.toHexString(pixel[3]) + " ");
            }
            System.out.println("");
        }

    }

      /**
       * A function to print an entire image to System.out.
       * 
       * @param text   Text to print before image data.
       * @param ri     The image to take data from.
       */
    public static void printImage(String text, 
                                  RenderedImage ri) {
        printImage(text, ri, new Rectangle(ri.getMinX(),
                                           ri.getMinY(),
                                           ri.getWidth(),
                                           ri.getHeight()));
    }
                                  

      /**
       * A function to display an image.
       * The image data is copied before this function returns ensuring that
       * the image displayed is the image when the function was called.
       * However this may use lots of memory so be careful.
       * 
       * @param text   Label for image.
       * @param ri     The image to display
       */
    public static void showImage(String text,
                                 final RenderedImage ri) {

        ColorModel cm = ri.getColorModel();
        Raster     r  = ri.getData();
        WritableRaster wr;
        wr = Raster.createWritableRaster(r.getSampleModel(),
                                         new java.awt.Point(r.getMinX(), 
                                                            r.getMinY()));
        wr.setRect(r);
        wr = wr.createWritableTranslatedChild(0,0);
        final BufferedImage bi
            = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);

        JFrame               f = new JFrame(text);
        JPanel               p = new JPanel();
        GridBagLayout      gb  = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        p.setLayout(gb);
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER; //end row
        
        f.getContentPane().add("North", new javax.swing.JLabel(text));

        JComponent comp = new JComponent() {
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.drawImage(bi, null, 0, 0);
                }
            };
        comp.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
        f.getContentPane().add("Center", comp);

        f.pack();
        f.show();
    }
}

