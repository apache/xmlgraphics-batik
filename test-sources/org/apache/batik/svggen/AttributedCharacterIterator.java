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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.text.AttributedString;


/**
 * This test validates the convertion of Java 2D AffineTransform into SVG
 * Shapes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class AttributedCharacterIterator implements Painter {
    public void paint(Graphics2D g) {
        String fontName = "Arial";
        int fontSize = 15;

        String text = "Attributed Strings are fun !";
        AttributedString styledText = new AttributedString(text);

        //
        // Set font family for the whole string
        //
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        styledText.addAttribute(TextAttribute.FAMILY, font.getFamily());
        styledText.addAttribute(TextAttribute.SIZE, new Float(font.getSize()));
        styledText.addAttribute(TextAttribute.FOREGROUND, Color.black);

        //
        // Set font style attributes for different part of the string
        //

        // "Attributed" is in Bold
        styledText.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 10);

        // "String" is italic
        // styledText.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, 11, 18);

        // fun is Bold and underlined
        styledText.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 23, 28);
        // styledText.addAttribute(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
        // styledText.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 23, 28);

        /*TextLayout aLayout = new TextLayout("A", font, frc);
          Shape aShape = aLayout.getOutline(null);

          ShapeGraphicAttribute aReplacement = new ShapeGraphicAttribute(aShape, GraphicAttribute.ROMAN_BASELINE, true);
          styledText.addAttribute(TextAttribute.CHAR_REPLACEMENT, aReplacement, 0, 1);


          // Create a BufferedImage to decorate the Shape
          {
          TextLayout aLayout = new TextLayout("A", font, frc);
          Shape aShape = aLayout.getOutline(null);
          Rectangle bounds = aShape.getBounds();

          int blurWidth = 6;
          BufferedImage image = new BufferedImage(bounds.width + blurWidth*4, bounds.height + blurWidth*4,
          BufferedImage.TYPE_INT_ARGB);
          Graphics2D g2 = image.createGraphics();
          int w = image.getWidth(), h = image.getHeight();
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g2.setPaint(Color.black);
          g2.translate(-bounds.x + (w - bounds.width)/2, -bounds.y + (h - bounds.height)/2);
          g2.fill(aShape);
          g2.setStroke(new BasicStroke(blurWidth/2));
          g2.draw(aShape);
          g2.dispose();

          float k[] = new float[blurWidth*blurWidth];
          for(int i=0; i<k.length; i++) k[i] = 1/(float)k.length;
          Kernel kernel = new Kernel(blurWidth, blurWidth, k);
          ConvolveOp blur = new ConvolveOp(kernel);
          image = blur.filter(image, null);
          g2 = image.createGraphics();
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g2.translate(-bounds.x + (w - bounds.width)/2, -bounds.y + (h - bounds.height)/2);
          g2.setComposite(AlphaComposite.Clear);
          g2.fill(aShape);

          image = image.getSubimage(blurWidth, blurWidth, image.getWidth() - 2*blurWidth, image.getHeight() - 2*blurWidth);

          ImageGraphicAttribute aImageReplacement = new ImageGraphicAttribute(image, GraphicAttribute.ROMAN_BASELINE, blurWidth,
          blurWidth + bounds.height);
          styledText.addAttribute(TextAttribute.CHAR_REPLACEMENT, aImageReplacement, 0, 1);
          }
        */

        //
        // Set text color
        //

        // "Attributed" is in dard red
        styledText.addAttribute(TextAttribute.FOREGROUND, new Color(128, 0, 0), 0, 10);

        // "String" is blue
        styledText.addAttribute(TextAttribute.FOREGROUND, new Color(70, 107, 132), 11, 18);

        // "fun" is yellow on blue background
        styledText.addAttribute(TextAttribute.FOREGROUND, new Color(236, 214, 70), 23, 28);
        styledText.addAttribute(TextAttribute.BACKGROUND, new Color(70, 107, 132), 23, 28);

        java.text.AttributedCharacterIterator iter = styledText.getIterator();
        /*TextLayout layout = new TextLayout(iter, frc);

        Rectangle bounds = layout.getBounds().getBounds();
        bounds.width += 50;
        bounds.height += 50;

        layout.draw(g, 25, layout.getAscent() + 25);*/
        g.drawString(iter, 10, 100);
    }
}
