/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.util.awt.svg;

import org.w3c.dom.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.*;
/**
 * This class offers a way to create an SVG document with grouping
 * that reflects the Swing composite structure (container/components).
 * 
 * @author             Vincent Hardy
 * @version            1.1, May 2nd, 2000. Added 
 */
public abstract class SwingSVGPrettyPrint{
	/**
	 * @param cmp Swing component to be converted to SVG
	 * @param svgGen SVGraphics2D to use to paint Swing components
	 * @return an SVG fragment containing an SVG equivalent of the Swing 
	 *         component tree.
	 */
	public static void print(JComponent cmp, SVGGraphics2D svgGen){
		if((cmp instanceof JComboBox) || (cmp instanceof JScrollBar)){
			// This is a work around unresolved issue with JComboBox
			// and JScrollBar
			printHack((JComponent)cmp, svgGen);
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
		cmpGroup.setAttribute("id", SVGIDGenerator.generateID(cmp.getClass().getName()));

		topLevelGroup.appendChild(cmpGroup);
		svgGen.setTopLevelGroup(topLevelGroup);
	}

	/**
	 * @param cmp Swing component to be converted to SVG
	 * @param svgGen SVGraphics2D to use to paint Swing components
	 * @return an SVG fragment containing an SVG equivalent of the Swing 
	 *         component tree.
	 */
	private static void printHack(JComponent cmp, SVGGraphics2D svgGen){
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
		cmpGroup.setAttribute("id", SVGIDGenerator.generateID(cmp.getClass().getName()));

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
			}
			else{
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
				
				Rectangle clip = svgGen.getClipBounds();
				Rectangle bounds = comp.getBounds(); // new Rectangle(cr);

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
