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
 * This test validates the convertion of the three elementary
 * thypes of Java 2D API graphic objects: shapes, text and
 * images
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class GraphicObjects implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Text
        g.setPaint(Color.black);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        g.drawString("Hello SVG drawString(...)", 20, 40);

        g.translate(0, 70);

        // Shapes
        Ellipse2D ellipse = new Ellipse2D.Float(20, 0, 60, 60);
        g.setPaint(new Color(176, 22, 40));
        g.fill(ellipse);
        g.translate(60, 0);
        g.setPaint(new Color(208, 170, 119));
        g.fill(ellipse);
        g.translate(60, 0);
        g.setPaint(new Color(221, 229, 111));
        g.fill(ellipse);
        g.translate(60, 0);
        g.setPaint(new Color(240, 165, 0));
        g.fill(ellipse);

        g.translate(-180, 60);

        // Draw background pattern
        BufferedImage pattern = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D ig = pattern.createGraphics();
        ig.setPaint(Color.white);
        ig.fillRect(0, 0, 10, 10);
        ig.setPaint(new Color(0xaaaaaa));
        ig.fillRect(0, 0, 5, 5);
        ig.fillRect(5, 5, 5, 50);
        TexturePaint texture = new TexturePaint(pattern, new Rectangle(0, 0, 10, 10));

        // Image
        BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
        ig = image.createGraphics();
        ig.setPaint(texture);
        ig.fillRect(0, 0, 200, 150);
        g.drawImage(image, 40, 40, null);

        image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
        ig = image.createGraphics();
        GradientPaint paint = new GradientPaint(0, 0, new Color(103, 103, 152),
                                                        200, 150, new Color(103, 103, 152, 0));
        ig.setPaint(paint);
        ig.fillRect(0, 0, 200, 150);
        ig.setPaint(Color.black);
        ig.setFont(new Font("Arial", Font.PLAIN, 10));
        ig.drawString("This is an image with alpha", 10, 30);
        ig.dispose();

        g.drawImage(image, 40, 40, null);
    }
}
