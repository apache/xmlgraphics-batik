/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
