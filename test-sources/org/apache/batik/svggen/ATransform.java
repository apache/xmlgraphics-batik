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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

/**
 * This test validates the convertion of Java 2D AffineTransform into SVG
 * Shapes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ATransform implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(Color.black); // new Color(102, 102, 144));

        int legendX = 10, legendY = 12;
        g.translate(0, 30);

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();

        // Define rectangle
        Rectangle rect = new Rectangle(10, 20, 50, 30);

        // Paint with default transform
        g.drawString("Default transform", legendX, legendY);
        g.fill(rect);

        // Paint after translate
        g.translate(0, 90);
        g.drawString("Translate applied", legendX, legendY);
        g.fill(rect);

        // Rotate rectangle about its center
        g.translate(0, 90);
        g.rotate(Math.PI/2, 35, 35);
        g.drawString("Rotate about center", legendX, legendY);
        g.fill(rect);

        // Restore default transform
        g.setTransform(defaultTransform);

        // Paint after scale
        g.translate(150, 0);
        g.drawString("Scale (sx=2, sy=1)", legendX, legendY);
        g.scale(2, 1);
        g.fill(rect);

        // Paint after shear
        g.setTransform(defaultTransform);
        g.translate(150, 90);
        g.drawString("Shear", legendX, legendY);
        g.shear(.2, 1);
        g.fill(rect);

        java.awt.geom.AffineTransform txf = g.getTransform();
        g.setTransform(new java.awt.geom.AffineTransform());
        Shape shearBounds = txf.createTransformedShape(rect).getBounds();
        g.setPaint(new Color(0, 0, 0, 128));
        g.fill(shearBounds);
    }
}
