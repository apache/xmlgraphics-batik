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
 * This test validates convertion of Java 2D Color into SVG fill,
 * stroke and opacity attributes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Color1 implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();

        // Colors used for labels and test output
        java.awt.Color labelColor = java.awt.Color.black;

        java.awt.Color colorConstants[] = { java.awt.Color.black,
                                   java.awt.Color.blue,
                                   java.awt.Color.cyan,
                                   java.awt.Color.darkGray,
                                   java.awt.Color.gray,
                                   java.awt.Color.green,
                                   java.awt.Color.lightGray,
                                   java.awt.Color.magenta,
                                   java.awt.Color.orange,
                                   java.awt.Color.pink,
                                   java.awt.Color.red,
                                   java.awt.Color.white,
                                   java.awt.Color.yellow };

        String colorConstantStrings[] =  { "black",
                                           "blue",
                                           "cyan",
                                           "darkGray",
                                           "gray",
                                           "green",
                                           "lightGray",
                                           "magenta",
                                           "orange",
                                           "pink",
                                           "red",
                                           "white",
                                           "yellow" };


        g.translate(20, 20);
        g.setPaint(labelColor);
        g.drawString("Color Constants", -5, 0);
        g.translate(0, 20);

        for(int i=0; i<colorConstants.length; i++){
            g.setPaint(labelColor);
            g.drawString(colorConstantStrings[i], 10, 3);
            g.setPaint(colorConstants[i]);
            g.fillRect(-5, -5, 10, 10);
            g.setPaint(labelColor);
            g.drawRect(-5, -5, 10, 10);
            g.translate(0, 20);
        }

        g.setTransform(defaultTransform);
        g.translate(150, 20);
        g.setColor(labelColor);
        g.drawString("Various opacities", 0, 0);
        g.translate(0, 10);

        //
        // Now, test opacities
        //
        int opacitySteps = 20;
        g.setPaint(new java.awt.Color(80, 255, 80));
        g.fillRect(0, 0, 40, 260);
        int stepHeight = 260/opacitySteps;
        Font defaultFont = g.getFont();
        Font opacityFont = new Font(defaultFont.getFamily(),
                                    defaultFont.getStyle(),
                                    (int)(defaultFont.getSize()*0.8));
        g.setFont(opacityFont);

        for(int i=0; i<opacitySteps; i++){
            int opacity = ((i + 1)*255)/opacitySteps;
            java.awt.Color color = new java.awt.Color(0, 0, 0, opacity);
            g.setPaint(color);
            g.fillRect(0, 0, 40, stepHeight);

            g.setPaint(labelColor);
            g.drawString("" + opacity, 50, stepHeight/2);
            g.translate(0, stepHeight);
        }
    }
}
