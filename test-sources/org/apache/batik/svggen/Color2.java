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
 * This test color opacity on fill and strokes, because this
 * is handled differently in the Java 2D API than in SVG.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Color2 implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Define Colors
        Color blue = Color.blue;
        Color green = Color.green;
        Color transparentBlue = new Color(0, 0, 255, 128);
        Color transparentGreen = new Color(0, 255, 0, 128);

        // Define AlphaComposites
        AlphaComposite srcOver = AlphaComposite.SrcOver;
        AlphaComposite srcOverTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

        // Define rectangle
        Rectangle rect = new Rectangle(10, 40, 100, 50);

        // Define thick stroke
        BasicStroke thickStroke = new BasicStroke(5);

        // First test: Opaque Colors with AlphaComposite
        g.setPaint(Color.black);
        g.drawString("Opaque Colors, Half Transparent AlphaComposite", 10, 30);

        g.setComposite(srcOverTransparent);
        g.setStroke(thickStroke);
        g.setPaint(blue);
        g.fill(rect);
        g.setPaint(green);
        g.draw(rect);
        g.setPaint(Color.black);
        g.fill(rect);

        g.translate(0, 90);

        // Second test: transparent color, opaque Source Over
        g.setPaint(Color.black);
        g.setComposite(srcOver);
        g.drawString("Transparent Colors, Opaque AlphaComposite SrcOver", 10, 30);

        g.setPaint(transparentBlue);
        g.fill(rect);
        g.setPaint(transparentGreen);
        g.draw(rect);
    }
}
