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
 * This test validates the convertion of Java 2D shapes into SVG
 * Shapes.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class BasicShapes implements Painter {
    public void paint(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(Color.black);

    // Rectangle
        g.drawString("Rectangle", 10, 20);
        Rectangle rect = new Rectangle(10, 30, 50, 40);
        g.draw(rect);
    
        g.translate(0, 90);

    // Round Rectangle
        g.drawString("RoundRectangle", 10, 20);
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(10, 30, 50, 40, 10, 10);
        g.draw(roundRect);

        g.translate(0, 90);

    // Circle
        g.drawString("Circle", 10, 20);
        Ellipse2D circle = new Ellipse2D.Float(10, 30, 50, 50);
        g.draw(circle);

        g.translate(0, 90);

    // CubicCurve2D
        g.drawString("CubicCurve2D", 10, 20);
        CubicCurve2D curve = new CubicCurve2D.Float(10, 55, 22.5f, 00, 38.5f, 110, 60, 55);
        g.draw(curve);

        g.translate(150, -270);

    // Polygon
        g.drawString("Polygon", 10, 20);
        Polygon polygon = new Polygon(new int[] { 30, 50, 10 },
                                      new int[] { 30, 60, 60 },
                                      3);
        g.draw(polygon);

        g.translate(0, 90);

        // General Path
        g.drawString("GeneralPath", 10, 20);
        GeneralPath path = new GeneralPath();
        path.moveTo(30, 30);
        path.quadTo(30, 50, 50, 60);
        path.quadTo(30, 50, 10, 60);
        path.quadTo(30, 50, 30, 30);
        path.closePath();
        g.draw(path);

        g.translate(0, 90);
    
        // Area
        g.drawString("Area", 10, 20);
        Area area = new Area(new Rectangle(10, 30, 50, 50));
        area.subtract(new Area(new Ellipse2D.Double(12, 32, 46, 46)));
        g.fill(area);

        g.translate(0, 90);
    
        // QuadCurve 2D
        g.drawString("QuadCurve2D", 10, 20);
        QuadCurve2D quad = new QuadCurve2D.Float(10, 55, 35, 105, 60, 55);
        g.draw(quad);

        g.translate(-75, 70);
  
    // Line
        g.drawString("Line2D", 10, 20);
        g.draw(new Line2D.Float(10, 30, 60, 30));
    }
}
