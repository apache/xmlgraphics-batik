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

/**
 * This test validates the convertion of Java 2D GradientPaints
 * into SVG linearGradient definition and reference.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Gradient implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();
        Color labelColor = Color.black;

        //
        // First, define cross hair marker
        //
        GeneralPath crossHair = new GeneralPath();
        crossHair.moveTo(-5, 0);
        crossHair.lineTo(5, 0);
        crossHair.moveTo(0, -5);
        crossHair.lineTo(0, 5);

        //
        // Simple test checking color values and start
        // and end points
        //
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(30, 40, Color.red,
                                                   30, 120, Color.yellow);
        g.setPaint(labelColor);
        g.drawString("Simple vertical gradient", 10, 20);
        g.setPaint(gradient);
        g.fillRect(10, 30, 100, 100);
        g.setPaint(labelColor);
        g.translate(30, 40);
        g.draw(crossHair);
        g.setTransform(defaultTransform);
        g.translate(30, 120);
        g.draw(crossHair);

        g.setTransform(defaultTransform);
        g.translate(0, 140);

        //
        // Now, test cycling behavior
        //
        java.awt.GradientPaint nonCyclicGradient = new java.awt.GradientPaint(0, 0, Color.red,
                                                            20, 0, Color.yellow);
        java.awt.GradientPaint cyclicGradient = new java.awt.GradientPaint(0, 0, Color.red,
                                                         20, 0, Color.yellow, true);

        g.setPaint(labelColor);
        g.drawString("Non Cyclic / Cyclic Gradients", 10, 20);

        g.translate(10, 30);

        g.setPaint(nonCyclicGradient);
        g.fillRect(0, 0, 100, 30);

        g.translate(0, 30);
        g.setPaint(cyclicGradient);
        g.fillRect(0, 0, 100, 30);

        g.setPaint(labelColor);
        g.drawLine(0, 0, 100, 0);

        g.setTransform(defaultTransform);
        g.translate(0, 240);

        //
        // Now, test transformations
        //
        g.setPaint(labelColor);
        g.drawString("Sheared GradientPaint", 10, 20);
        g.translate(10, 25);

        java.awt.GradientPaint shearedGradient = new java.awt.GradientPaint(0, 0, Color.red,
                                                          100, 0, Color.yellow);
        g.setPaint(shearedGradient);
        g.shear(0.5, 0);

        g.fillRect(0, 0, 100, 40);

        g.setTransform(defaultTransform);
        g.translate(0, 320);

        g.setPaint(labelColor);
        g.drawString("Opacity in stop color", 10, 20);

        java.awt.GradientPaint transparentGradient = new java.awt.GradientPaint(10, 30, new Color(255, 0, 0, 0),
                                                                                110, 30, Color.yellow);

        g.setPaint(transparentGradient);
        g.fillRect(10, 30, 100, 30);
    }
}
