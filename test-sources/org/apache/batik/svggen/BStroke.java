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
 * This test validates convertion of BasicStroke
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class BStroke implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        /*
         * Strokes of varying width
         */
        java.awt.BasicStroke strokesWidth[] = {
            new java.awt.BasicStroke(2.f),
            new java.awt.BasicStroke(4.f),
            new java.awt.BasicStroke(8.f),
            new java.awt.BasicStroke(16.f)
                };


        /*
         * Strokes of varying termination styles
         */
        java.awt.BasicStroke strokesCap[] = {
            new java.awt.BasicStroke(15.f, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_BEVEL), // No decoration
            new java.awt.BasicStroke(15.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_BEVEL), // Square end
            new java.awt.BasicStroke(15.f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_BEVEL), // Rounded end
        };

        /*
         * Strokes of varying segment connection styles
         */
        java.awt.BasicStroke strokesJoin[] = {
            new java.awt.BasicStroke(10.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_BEVEL), // Connected with a straight segment
            new java.awt.BasicStroke(10.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_MITER), // Extend outlines until they meet
            new java.awt.BasicStroke(10.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_ROUND), // Round of corner.
        };
        /*
         * Strokes of varying miterlimits
         */
        java.awt.BasicStroke strokesMiter[] = {
            new java.awt.BasicStroke(6.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_MITER, 1),   // Actually cuts of all angles
            new java.awt.BasicStroke(6.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_MITER, 2f),  // Cuts off angles less than 60degrees
            new java.awt.BasicStroke(6.f, java.awt.BasicStroke.CAP_SQUARE, java.awt.BasicStroke.JOIN_MITER, 10f), // Cuts off angles less than 11 degrees
        };

        /*
         * Srokes with varying dash styles
         */
        java.awt.BasicStroke strokesDash[] = {
            new java.awt.BasicStroke(8.f,
                            java.awt.BasicStroke.CAP_BUTT,
                            java.awt.BasicStroke.JOIN_BEVEL,
                            8.f,
                            new float[]{ 6.f, 6.f },
                            0.f),

            new java.awt.BasicStroke(8.f,
                            java.awt.BasicStroke.CAP_BUTT,
                            java.awt.BasicStroke.JOIN_BEVEL,
                            8.f,
                            new float[]{ 10.f, 4.f },
                            0.f),

            new java.awt.BasicStroke(8.f,
                            java.awt.BasicStroke.CAP_BUTT,
                            java.awt.BasicStroke.JOIN_BEVEL,
                            8.f,
                            new float[]{ 4.f, 4.f, 10.f, 4.f },
                            0.f),

            new java.awt.BasicStroke(8.f,
                            java.awt.BasicStroke.CAP_BUTT,
                            java.awt.BasicStroke.JOIN_BEVEL,
                            8.f,
                            new float[]{ 4.f, 4.f, 10.f, 4.f },
                            4.f)
                };

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();

        // Varying width
        g.setPaint(Color.black);
        g.drawString("Varying width", 10, 10);
        for(int i=0; i<strokesWidth.length; i++){
            g.setStroke(strokesWidth[i]);
            g.drawLine(10, 30, 10, 80);
            g.translate(20, 0);
        }

        // Varying end caps
        g.setTransform(defaultTransform);
        g.translate(0, 120);
        g.drawString("Varying end caps", 10, 10);
        for(int i=0; i<strokesCap.length; i++){
            g.setStroke(strokesCap[i]);
            g.drawLine(15, 30, 15, 80);
            g.translate(30, 0);
        }

        // Varying line joins
        GeneralPath needle = new GeneralPath();
        needle.moveTo(0, 60);
        needle.lineTo(10, 20);
        needle.lineTo(20, 60);
        g.setTransform(defaultTransform);
        g.translate(0, 240);
        g.drawString("Varying line joins", 10, 10);
        g.translate(20, 20);
        for(int i=0; i<strokesJoin.length; i++){
            g.setStroke(strokesJoin[i]);
            g.draw(needle);
            g.translate(35, 0);
        }

        // Varying miter limit
        g.setTransform(defaultTransform);
        g.translate(150, 120);
        GeneralPath miterShape = new GeneralPath();
        miterShape.moveTo(0, 0);
        miterShape.lineTo(30, 0);
        miterShape.lineTo(30, 60); // 90 degree elbow
        miterShape.lineTo(0, 30); // 45 degree elbow.
        g.drawString("Varying miter limit", 10, 10);
        g.translate(10, 30);
        for(int i=0; i<strokesMiter.length; i++){
            g.setStroke(strokesMiter[i]);
            g.draw(miterShape);
            g.translate(40, 0);
        }

        // Varing dashing patterns
        g.setTransform(defaultTransform);
        g.translate(150, 0);
        g.drawString("Varying dash patterns", 10, 10);
        g.translate(20, 0);
        for(int i=0; i<strokesDash.length; i++){
            g.setStroke(strokesDash[i]);
            g.drawLine(10, 20, 10, 80);
            g.translate(20, 0);
        }

    }
}
