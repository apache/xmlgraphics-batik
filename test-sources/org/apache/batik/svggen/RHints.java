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
 * This test validates the convertion of Java 2D RenderingHints
 * into an SVG attributes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class RHints implements Painter {
    public void paint(Graphics2D g) {
        java.awt.RenderingHints.Key antialiasKey = java.awt.RenderingHints.KEY_ANTIALIASING;
        Object antialiasOn= java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
        Object antialiasOff= java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
        java.awt.RenderingHints.Key textAntialiasKey = java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
        Object textAntialiasOn = java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        Object textAntialiasOff = java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        java.awt.RenderingHints.Key interpolationKey = java.awt.RenderingHints.KEY_INTERPOLATION;
        Object interpolationBicubic = java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        Object interpolationNeighbor = java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

        Font defaultFont = g.getFont();
        java.awt.geom.AffineTransform defaultTransform = g.getTransform();
        Font textFont = new Font("Impact", Font.PLAIN, 25);

        //
        // First, test text antialiasing
        //
        g.setPaint(Color.black);
        g.setRenderingHint(antialiasKey, antialiasOn);
        g.drawString("Text antialiasing", 10, 20);

        g.setRenderingHint(antialiasKey, antialiasOff);
        g.setRenderingHint(textAntialiasKey, textAntialiasOn);
        g.setFont(textFont);
        g.drawString("HELLO antialiased", 30, 60);
        g.setRenderingHint(textAntialiasKey, textAntialiasOff);
        g.drawString("HELLO aliased", 30, 90);

        //
        // Now, test shape antialiasing
        //
        g.translate(0, 100);
        g.setRenderingHint(antialiasKey, antialiasOn);
				g.setRenderingHint(textAntialiasKey, textAntialiasOn);
        g.setFont(defaultFont);
        g.drawString("Shape antialiasing", 10, 20);

        g.translate(30, 0);
        g.setRenderingHint(antialiasKey, antialiasOff);
        Ellipse2D ellipse = new Ellipse2D.Float(10, 30, 100, 30);
        g.fill(ellipse);
        g.translate(0, 40);
        g.setRenderingHint(antialiasKey, antialiasOn);
        g.fill(ellipse);

        g.setTransform(defaultTransform);
        g.translate(0, 200);

        //
        // Now, test interpolation hint
        //
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        Graphics2D ig = image.createGraphics();
        ig.setPaint(Color.red);
        ig.fillRect(0, 0, 2, 2);
        ig.setPaint(Color.yellow);
        ig.fillRect(0, 0, 1, 1);
        ig.fillRect(1, 1, 2, 2);
        ig.dispose();

        g.setRenderingHint(interpolationKey, interpolationNeighbor);
        g.drawString("Interpolation Nearest Neighbor / Bicubic", 10, 30);
        g.drawImage(image, 10, 50, 40, 40, null);

        g.setRenderingHint(interpolationKey, interpolationBicubic);
        g.drawImage(image, 60, 50, 40, 40, null);

    }
}
