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
 * This test validates convertion of Java 2D clip inot SVG clipPath
 * definition and attributes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Clip implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Save original clip
        Shape clipShape = g.getClip();
        java.awt.geom.AffineTransform transform = g.getTransform();

        g.setPaint(Color.black);

        Dimension size = new Dimension(300, 400);
        int w=100, h=50;
        int vOffset = h + 20;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = image.createGraphics();
        gi.setPaint(Color.white);
        gi.fillRect(0, 0, 100, 50);
        gi.setPaint(Color.green);
        gi.fillRect(0, 0, 50, 25);
        gi.setPaint(Color.black);
        gi.fillRect(50, 0, 50, 25);
        gi.setPaint(Color.red);
        gi.fillRect(50, 25, 50, 25);
        gi.dispose();

        // Set simple clip : does not modify the output
        g.clipRect(0, 0, size.width, size.height);
        g.drawImage(image, 0, 0, null);
        g.setClip(clipShape);

        g.drawString("Clip set to device bounds", 110, 25);

        g.translate(0, vOffset);

        // Intersect current clip with a smaller clip : show only
        // the top right corner of the image
        g.drawString("Clip set to upper right quarter", 110, 25);

        g.clipRect(w/2, 0, w/2, h/2);
        g.drawImage(image, 0, 0, null);

        // Restore
        g.setTransform(transform);
        g.setClip(clipShape);
        g.translate(0, 2*vOffset);

        // Scale before setting the same clip
        g.drawString("Clip set to upper right quarter", 110, 15);
        g.drawString("after .5 scale", 110, 30);
        g.scale(.5, .5);
        g.clipRect(w/2, 0, w/2, h/2);
        g.drawImage(image, 0, 0, null);

        // Restore
        g.setTransform(transform);
        g.setClip(clipShape);

        g.translate(0, 3*vOffset);

        // Use a non-rectangle clipping area
        g.drawString("Non-Rectagular clip", 110, 25);
        Shape circle = new Ellipse2D.Float(0, 0, w, h);
        g.clip(circle);
        g.drawImage(image, 0, 0, null);

        // Restore
        g.setTransform(transform);
        g.setClip(clipShape);

        g.translate(0, 4*vOffset);

        // Use a non-rectangle clipping area again,
        // after setting a scale transform
        g.drawString("Non-Rectagular clip after", 110, 15);
        g.drawString(".5 scale", 110, 30);
        g.scale(.5, .5);
        g.clip(circle);
        g.drawImage(image, 0, 0, null);

        // Restore
        g.setTransform(transform);
        g.setClip(clipShape);
        g.translate(0, 5*vOffset);

        // Use a non-rectangle clipping area again,
        // before setting a scale transform
        g.drawString("Non-Rectagular clip before", 110, 15);
        g.drawString(".5 scale", 110, 30);
        g.clip(circle);
        g.scale(.5, .5);
        g.drawImage(image, 0, 0, null);
    }
}
