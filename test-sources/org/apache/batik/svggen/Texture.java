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
 * This test validates the convertion of Java 2D TexturePaints
 * into SVG patterns and fill and fill-opacity values
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Texture implements Painter {
    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        java.awt.geom.AffineTransform defaultTransform = g.getTransform();
        Color labelColor = Color.black;

        BufferedImage texture = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = texture.createGraphics();
        bg.setPaint(Color.red);
        bg.fillRect(0, 0, 10, 10);
        bg.setPaint(Color.yellow);
        bg.fillRect(10, 10, 10, 10);
        bg.dispose();

        Rectangle anchors[] = { new Rectangle(0, 0, texture.getWidth(), texture.getHeight()),
                                new Rectangle(texture.getWidth()/2, texture.getHeight()/2, texture.getWidth(), texture.getHeight()),
                                new Rectangle(0, 0, texture.getWidth()/2, texture.getHeight()/2) };

        String anchorDesc[] = { "Anchor matches texture image",
                                "Anchor offset to texture image center",
                                "Anchor half the size of texture" };

        // Now, fill a rectangle that is 4 times the size of the texture
        // along each axis, once for each texture.

        g.translate(0, 20);

        for(int i=0; i<anchors.length; i++){
            java.awt.TexturePaint texturePaint = new java.awt.TexturePaint(texture, anchors[i]);
            g.setPaint(texturePaint);
            g.fillRect(0, 0, texture.getWidth()*4, texture.getHeight()*4);
            java.awt.geom.AffineTransform curTxf = g.getTransform();
            g.translate(150, 0);
            g.shear(.5, 0);
            g.fillRect(0, 0, texture.getWidth()*4, texture.getHeight()*4);
            g.setTransform(curTxf);
            g.setPaint(labelColor);
            g.drawString(anchorDesc[i], 10, texture.getHeight()*4 + 20);
            g.translate(0, texture.getHeight()*4 + 40);
        }
    }
}
