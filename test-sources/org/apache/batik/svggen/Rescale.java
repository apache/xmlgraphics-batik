/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * This test validates the convertion of Java 2D RescaleOp
 * into an SVG filer.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Rescale implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();

        //
        // Load Image
        //
        Image image = Toolkit.getDefaultToolkit().createImage("test-resources/org/apache/batik/svggen/resources/vangogh.jpg");
        MediaTracker tracker = new MediaTracker(new Button(""));
        tracker.addImage(image, 0);
        try{
            tracker.waitForAll();
        }catch(InterruptedException e){
            tracker.removeImage(image);
            image = null;
        }finally {
            if(image != null)
                tracker.removeImage(image);
            if(tracker.isErrorAny())
                image = null;
            if(image != null){
                if(image.getWidth(null)<0 ||
                   image.getHeight(null)<0)
                    image = null;
            }
        }

        if(image == null){
            throw new Error("Could not load image");
        }

        BufferedImage bi = new BufferedImage(image.getWidth(null),
                                             image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D ig = bi.createGraphics();
        ig.drawImage(image, 0, 0, null);

        java.awt.image.RescaleOp brighten = new java.awt.image.RescaleOp(1.5f, 0, null);
        java.awt.image.RescaleOp darken = new java.awt.image.RescaleOp(.6f, 0, null);

        // Simply paint the image without and with rescale filters
        g.setPaint(Color.black);
        g.drawString("Brighter / Normal / Darker", 10, 20);
        g.drawImage(bi, brighten, 10, 30);
        g.drawImage(image, 10 + bi.getWidth() + 10, 30, null);
        g.drawImage(bi, darken, 10 + 2*(bi.getWidth() + 10), 30);

        g.translate(0, bi.getHeight() + 30 + 20);
        g.drawString("Rescale Red / Green / Blue", 10, 20);
        java.awt.image.RescaleOp redStress = new java.awt.image.RescaleOp(new float[]{ 2f, 1f, 1f },
                                            new float[]{ 0, 0, 0 }, null);
        java.awt.image.RescaleOp greenStress = new java.awt.image.RescaleOp(new float[]{ 1f, 2f, 1f },
                                              new float[]{ 0, 0, 0 }, null);
        java.awt.image.RescaleOp blueStress = new java.awt.image.RescaleOp(new float[]{ 1f, 1f, 2f },
                                             new float[]{ 0, 0, 0 }, null);

        g.drawImage(bi, redStress, 10, 30);
        g.drawImage(bi, greenStress, 10 + bi.getWidth() + 10, 30);
        g.drawImage(bi, blueStress, 10 + 2*(bi.getWidth() + 10), 30);
    }
}
