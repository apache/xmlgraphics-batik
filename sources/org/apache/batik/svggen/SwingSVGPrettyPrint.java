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

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.w3c.dom.Element;
/**
 * This class offers a way to create an SVG document with grouping
 * that reflects the Swing composite structure (container/components).
 *
 * @author             Vincent Hardy
 * @version            1.1, May 2nd, 2000. Added
 */
public abstract class SwingSVGPrettyPrint implements SVGSyntax {
        /**
         * @param cmp Swing component to be converted to SVG
         * @param svgGen SVGraphics2D to use to paint Swing components
         * @return an SVG fragment containing an SVG equivalent of the Swing
         *         component tree.
         */
    public static void print(JComponent cmp, SVGGraphics2D svgGen) {
        if ((cmp instanceof JComboBox) || (cmp instanceof JScrollBar)) {
            // This is a work around unresolved issue with JComboBox
            // and JScrollBar
            printHack(cmp, svgGen);
            return;
        }

        // Spawn a new Graphics2D for this component
        SVGGraphics2D g = (SVGGraphics2D)svgGen.create();
        g.setColor(cmp.getForeground());
        g.setFont(cmp.getFont());
        Element topLevelGroup = g.getTopLevelGroup();

        // If there is no area to be painted, return here
        if ((cmp.getWidth() <= 0) || (cmp.getHeight() <= 0))
            return;

        Rectangle clipRect = g.getClipBounds();
        if (clipRect == null)
            g.setClip(0, 0, cmp.getWidth(), cmp.getHeight());

        paintComponent(cmp, g);
        paintBorder(cmp, g);
        paintChildren(cmp, g);

        // Now, structure DOM tree to reflect this component's structure
        Element cmpGroup = g.getTopLevelGroup();
        cmpGroup.setAttributeNS(null, "id",
                                svgGen.getGeneratorContext().idGenerator.
                                generateID(cmp.getClass().getName()));

        topLevelGroup.appendChild(cmpGroup);
        svgGen.setTopLevelGroup(topLevelGroup);
    }

    /**
     * @param cmp Swing component to be converted to SVG
     * @param svgGen SVGraphics2D to use to paint Swing components
     * @return an SVG fragment containing an SVG equivalent of the Swing
     *         component tree.
     */
    private static void printHack(JComponent cmp, SVGGraphics2D svgGen) {
        // Spawn a new Graphics2D for this component
        SVGGraphics2D g = (SVGGraphics2D)svgGen.create();
        g.setColor(cmp.getForeground());
        g.setFont(cmp.getFont());
        Element topLevelGroup = g.getTopLevelGroup();

        // If there is no area to be painted, return here
        if ((cmp.getWidth() <= 0) || (cmp.getHeight() <= 0))
            return;

        Rectangle clipRect = g.getClipBounds();
        if (clipRect == null) {
            g.setClip(0, 0, cmp.getWidth(), cmp.getHeight());
        }

        cmp.paint(g);

        // Now, structure DOM tree to reflect this component's structure
        Element cmpGroup = g.getTopLevelGroup();
        cmpGroup.setAttributeNS(null, "id",
                                svgGen.getGeneratorContext().idGenerator.
                                generateID(cmp.getClass().getName()));

        topLevelGroup.appendChild(cmpGroup);
        svgGen.setTopLevelGroup(topLevelGroup);
    }


    private static void paintComponent(JComponent cmp, SVGGraphics2D svgGen){
        ComponentUI ui = UIManager.getUI(cmp);
        if(ui != null){
            ui.installUI(cmp);
            ui.update(svgGen, cmp);
        }
    }

    /**
     * WARNING: The following code does some special case processing
     * depending on the class of the input JComponent. This is needed
     * because there is no generic way I could find to determine whether
     * a component should be painted or not.
     */
    private static void paintBorder(JComponent cmp, SVGGraphics2D svgGen){
        Border border = cmp.getBorder();
        if(border != null){
            if( (cmp instanceof AbstractButton)
                ||
                (cmp instanceof JPopupMenu)
                ||
                (cmp instanceof JToolBar)
                ||
                (cmp instanceof JMenuBar)
                ||
                (cmp instanceof JProgressBar) ){
                if( ((cmp instanceof AbstractButton) && ((AbstractButton)cmp).isBorderPainted())
                    ||
                    ((cmp instanceof JPopupMenu) && ((JPopupMenu)cmp).isBorderPainted())
                    ||
                    ((cmp instanceof JToolBar) && ((JToolBar)cmp).isBorderPainted())
                    ||
                    ((cmp instanceof JMenuBar) && ((JMenuBar)cmp).isBorderPainted())
                    ||
                    ((cmp instanceof JProgressBar) && ((JProgressBar)cmp).isBorderPainted() ))
                    border.paintBorder(cmp, svgGen, 0, 0, cmp.getWidth(), cmp.getHeight());
            } else {
                border.paintBorder(cmp, svgGen, 0, 0, cmp.getWidth(), cmp.getHeight());
            }
        }
    }

    private static void paintChildren(JComponent cmp, SVGGraphics2D svgGen){
        int i = cmp.getComponentCount() - 1;
        boolean isJComponent = false;
        Rectangle tmpRect = new Rectangle();

        for(; i>=0; i--){
            Component comp = cmp.getComponent(i);

            if(comp != null && JComponent.isLightweightComponent(comp) &&
               (comp.isVisible() == true)) {
                Rectangle cr = null;
                isJComponent = (comp instanceof JComponent);

                if(isJComponent) {
                    cr = tmpRect;
                    ((JComponent)comp).getBounds(cr);
                } else {
                    cr = comp.getBounds();
                }

                boolean hitClip =
                    svgGen.hitClip(cr.x, cr.y, cr.width, cr.height);

                if (hitClip) {
                    SVGGraphics2D cg = (SVGGraphics2D)svgGen.create(cr.x, cr.y, cr.width, cr.height);
                    cg.setColor(comp.getForeground());
                    cg.setFont(comp.getFont());
                    if(comp instanceof JComponent)
                        print((JComponent)comp, cg);
                    else{
                        comp.paint(cg);
                    }
                }
            }
        }
    }
}
