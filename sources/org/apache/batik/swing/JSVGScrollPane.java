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

package org.apache.batik.swing;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.BorderLayout;

import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.ComponentAdapter;
/*
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
*/

import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;

import java.awt.event.ComponentEvent; 

import javax.swing.JScrollBar;
import javax.swing.Box;
import javax.swing.JPanel;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.apache.batik.bridge.ViewBox;
import org.apache.batik.bridge.BridgeException;

import org.apache.batik.gvt.CanvasGraphicsNode;

import org.apache.batik.swing.JSVGCanvas;

import org.apache.batik.swing.svg.SVGUserAgent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

import org.apache.batik.swing.gvt.JGVTComponentListener;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGDocument;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;

/**
*	This is implements a 2D scroller that will scroll an JSVGCanvas.
*	<p>
*	Reimplimentation, rather than imlementing the Scrollable interface,
*	provides several advantages. The main advantage is the ability to 
*	more precisely control ScrollBar events; fewer JSVGCanvas updates 
*	are required when scrolling. This creates a significant performance
*	(reflected by an increase in scroll speed) advantage compared to
*	implementating the Scrollable interface.
*	<p>
*	NOTE: this has not been tested with using a JSVGCanvas instead of an
*	XJSVGCanvas.
*	<p>
*	@author Zach DelProposto
*	
*
*/
public class JSVGScrollPane extends JPanel
{
    private final JSVGCanvas canvas;
	
    private JPanel horizontalPanel;
    private JScrollBar vertical;
    private JScrollBar horizontal;
    private Component cornerBox;
    private SBListener hsbListener;
    private SBListener vsbListener;
	
    private double matrix[] = new double[6];
	
    private Rectangle2D.Float viewBox = null; // SVG Root element viewbox 
    private boolean ignoreScrollChange = false;
	

    /**
     *	Creates a JSVGScrollPane, which will scroll an JSVGCanvas.
     *
     */
    public JSVGScrollPane(JSVGCanvas canvas)
    {
        super();
        this.canvas = canvas;
        canvas.setRecenterOnResize(false);

        // create components
        vertical = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
        horizontal = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
		
        // create a spacer next to the horizontal bar
        horizontalPanel = new JPanel(new BorderLayout());
        horizontalPanel.add(horizontal, BorderLayout.CENTER);
        cornerBox = Box.createRigidArea
            (new Dimension(vertical.getPreferredSize().width, 
                           horizontal.getPreferredSize().height));
        horizontalPanel.add(cornerBox, BorderLayout.EAST);
		
        // listeners
        hsbListener = new SBListener(false);
        horizontal.getModel().addChangeListener(hsbListener);
        horizontal.addMouseListener(hsbListener);
        horizontal.addMouseMotionListener(hsbListener);
		
        vsbListener = new SBListener(true);
        vertical.getModel().addChangeListener(vsbListener);
        vertical.addMouseListener(vsbListener);
        vertical.addMouseMotionListener(vsbListener);
		
        // by default, scrollbars are not visible
        horizontalPanel.setVisible(false);
        vertical.setVisible(false);
		
        // addMouseWheelListener(new WheelListener());
		
        // layout
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(vertical, BorderLayout.EAST);
        add(horizontalPanel, BorderLayout.SOUTH);
		
        // inform of ZOOM events (to print sizes, such as in a status bar)
        canvas.addSVGDocumentLoaderListener
            (new SVGScrollDocumentLoaderListener());
		
        // canvas listeners
        ScrollListener xlistener = new ScrollListener();
        canvas.addJGVTComponentListener(xlistener);
        this.addComponentListener(xlistener);
        canvas.addGVTTreeRendererListener(xlistener);
    }// JSVGScrollPane()


    public JSVGCanvas getCanvas() {
        return canvas;
    }


    class SVGScrollDocumentLoaderListener extends SVGDocumentLoaderAdapter {
        public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            SVGSVGElement root = e.getSVGDocument().getRootElement();
            root.addEventListener
                (SVGConstants.SVG_SVGZOOM_EVENT_TYPE, 
                 new EventListener() {
                     public void handleEvent(Event evt) {
                         if (!(evt.getTarget() instanceof SVGSVGElement))
                             return;
                         // assert(evt.getType() == SVGConstants.SVG_SVGZOOM_EVENT_TYPE);
                         SVGSVGElement svg = (SVGSVGElement) evt.getTarget();
                         scaleChange(svg.getCurrentScale());
                     } // handleEvent()
                 }, false);
        }// documentLoadingCompleted()			
    };
	
	
    /**
     *	Resets this object (for reloads),
     *	releasing any cached data and recomputing
     *	scroll extents.
     */
    public void reset()
    {
        viewBox = null;
        horizontalPanel.setVisible(false);
        vertical.setVisible(false);
        revalidate();
    }// reset()
	
	
    /**
     *	Sets the translation portion of the transform based upon the
     *	current scroll bar position
     */
    private void setScrollPosition() {
        checkAndSetViewBoxRect();
        if (viewBox == null) return;

        AffineTransform crt = canvas.getRenderingTransform();
        AffineTransform vbt = canvas.getViewBoxTransform();
        if (crt == null) crt = new AffineTransform();
        if (vbt == null) vbt = new AffineTransform();

        Rectangle r2d = vbt.createTransformedShape(viewBox).getBounds();
        // System.err.println("Pre : " + r2d);
        int tx = 0, ty = 0;
        if (r2d.x < 0) tx -= r2d.x;
        if (r2d.y < 0) ty -= r2d.y;

        int deltaX = horizontal.getValue()-tx;
        int deltaY = vertical.getValue()  -ty;

        // System.err.println("tx = "+tx+"; ty = "+ty);
        // System.err.println("dx = "+deltaX+"; dy = "+deltaY);
        // System.err.println("Pre CRT: " + crt);

        crt = (AffineTransform)crt.clone();
        crt.preConcatenate
            (AffineTransform.getTranslateInstance(-deltaX, -deltaY));
        canvas.setRenderingTransform(crt);

        updateScrollbarVisibility();
    }// setScrollPosition()
	
	
	
    /**
     *	MouseWheel Listener
     *	<p>
     *	Provides mouse wheel support. The mouse wheel will scroll the currently
     *	displayed scroll bar, if only one is displayed. If two scrollbars are 
     *	displayed, the mouse wheel will only scroll the vertical scrollbar.
     */
    /*
    private class WheelListener implements MouseWheelListener
    {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            final JScrollBar sb = (vertical.isVisible()) ? 
                vertical : horizontal;	// vertical is preferred
			
            if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                final int amt = e.getUnitsToScroll() * sb.getUnitIncrement();
                sb.setValue(sb.getValue() + amt);
            } else if(e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL){
                final int amt = e.getWheelRotation() * sb.getBlockIncrement();
                sb.setValue(sb.getValue() + amt);
            }
			
        }// mouseWheelMoved()
    }// inner class WheelListener
    */
	
	
    /**
     *	Advanced JScrollBar listener. 
     *	<p>
     *	<b>A separate listener must be attached to each scrollbar,
     *	since we keep track of mouse state for each scrollbar
     *	separately!</b> 
     *  <p> 
     *  This coalesces drag events so we don't track them, and
     *  'passes through' click events. It doesn't coalesce as many
     *  events as it should, but it helps * considerably.
     */
    private class SBListener extends MouseAdapter 
        implements ChangeListener, MouseMotionListener
    {
        // 'true' if we are in a drag (versus a click)
        private boolean inDrag = false; 
        // true if we are in a click
        private boolean inClick = false;		

        private boolean isVertical;
        int startValue;

        public SBListener(boolean vertical)
        {
            isVertical = vertical;
        }// SBListener()
			
			
        public synchronized void mouseDragged(MouseEvent e)
        {
            inDrag = true;
            AffineTransform at;
            if (isVertical) {
                int newValue = vertical.getValue();
                at = AffineTransform.getTranslateInstance
                    (0, startValue-newValue);
            } else {
                int newValue = horizontal.getValue();
                at = AffineTransform.getTranslateInstance
                    (startValue-newValue, 0);
            }

            canvas.setPaintingTransform(at);
        }// mouseDragged()
			
			
        public synchronized void mousePressed(MouseEvent e)
        {
            // we've pressed the mouse
            inClick = true;
            if (isVertical)
                startValue = vertical.getValue();
            else
                startValue = horizontal.getValue();
       }// mousePressed()
			
			
        public synchronized void mouseReleased(MouseEvent e)
        {
            if(inDrag) {
                // This is the 'end' of a drag
                setScrollPosition();
            }
				
            // reset drag indicator
            inDrag = false;
            inClick = false;
        }// mouseReleased()
			
        public void mouseMoved(MouseEvent e)
        {
            // do nothing
        }// mouseMoved()
			
        public synchronized void stateChanged(ChangeEvent e)
        {
            // only respond to changes if we are NOT being dragged
            // and ignoreScrollChange is not set
            if(!inDrag && !inClick && !ignoreScrollChange) {
                //System.out.println(e);
                //System.out.println(vertical.getModel());
                //System.out.println(horizontal.getModel());
                setScrollPosition();
            }
        }// stateChanged()
    }// inner class SBListener
	
	
	
    /** Handle scroll, zoom, and resize events */
    private class ScrollListener extends ComponentAdapter 
        implements JGVTComponentListener, GVTTreeRendererListener
    {
        private boolean isReady = false;
		
        public void componentTransformChanged(ComponentEvent evt)
        {
            if(isReady) {
                resizeScrollBars(canvas.getRenderingTransform(), 
                                 canvas.getViewBoxTransform(), 
                                 canvas.getSize());
            }
        }// componentTransformChanged()
		
		
        public void componentResized(ComponentEvent evt)
        {
            if(isReady) {
                resizeScrollBars(canvas.getRenderingTransform(), 
                                 canvas.getViewBoxTransform(), 
                                 canvas.getSize());
            }
        }// componentResized()
		
		
        public void gvtRenderingCompleted(GVTTreeRendererEvent e)
        {
            isReady = true;
        }// gvtRenderingCompleted()
		
		
        public void gvtRenderingCancelled(GVTTreeRendererEvent e)
        {
            // do nothing
        }// gvtRenderingCancelled()
		
		
        public void gvtRenderingFailed(GVTTreeRendererEvent e)
        {
            // do nothing
        }// gvtRenderingFailed()
		
        public void gvtRenderingPrepare(GVTTreeRendererEvent e)
        {
            // do nothing
        }// gvtRenderingPrepare()
		
        public void gvtRenderingStarted(GVTTreeRendererEvent e)
        {
            // do nothing
        }// gvtRenderingStarted()
 		
    }// inner class ScrollListener
	
	
    /**
     *	Compute the scrollbar extents, and determine if 
     *	scrollbars should be visible.
     *
     */
    private void resizeScrollBars(AffineTransform crt, 
                                  AffineTransform vbt, 
                                  Dimension vpSize)
    {
        ignoreScrollChange = true;

        /*
          System.out.println("** resizeScrollBars()");
          System.out.println("   crt: "+crt);
          System.out.println("   vbt: "+vbt);
          System.out.println("   vpSize: "+vpSize);
        */
        checkAndSetViewBoxRect();
        if (viewBox == null) return;

        if (vbt == null) vbt = new AffineTransform();
        if (crt == null) crt = new AffineTransform();

        Rectangle r2d = vbt.createTransformedShape(viewBox).getBounds();

        // System.out.println("VB: " + r2d);
        int maxW = r2d.width;
        int maxH = r2d.height;
        int tx = 0, ty = 0;
        if (r2d.x > 0) maxW += r2d.x;
        else           tx   -= r2d.x;
        if (r2d.y > 0) maxH += r2d.y;
        else           ty   -= r2d.y;

        // compute translation
        // final int tx = (int) ((crt.getTranslateX() > 0) ? 
        //                       0 : -crt.getTranslateX());
        // final int ty = (int) ((crt.getTranslateY() > 0) ? 
        //                       0 : -crt.getTranslateY());
		
        // System.err.println("   maxW = "+maxW+"; maxH = "+maxH + 
        //                    " tx = "+tx+"; ty = "+ty);
        vertical.setValue(ty);
        horizontal.setValue(tx);
        vpSize = updateScrollbarVisibility();

        // set scroll params
        vertical.  setValues(ty, vpSize.height, 0, maxH);
        horizontal.setValues(tx, vpSize.width,  0, maxW);
		
        // set block scroll; this should be equal to a full 'page', 
        // minus a small amount to keep a portion in view
        // that small amount is 10%.
        vertical.  setBlockIncrement( (int) (0.9f * vpSize.height) );
        horizontal.setBlockIncrement( (int) (0.9f * vpSize.width) );
		
        // set unit scroll. This is arbitrary, but we define
        // it to be 20% of the current viewport. 
        vertical.  setUnitIncrement( (int) (0.2f * vpSize.height) );
        horizontal.setUnitIncrement( (int) (0.2f * vpSize.width) );
		
        ignoreScrollChange = false;
        //System.out.println("  -- end resizeScrollBars()");
    }// resizeScrollBars()

    protected Dimension updateScrollbarVisibility() {
        AffineTransform vbt = canvas.getViewBoxTransform();
        Rectangle r2d = vbt.createTransformedShape(viewBox).getBounds();
        int maxW = r2d.width;
        int maxH = r2d.height;
        int tx = 0, ty = 0;
        if (r2d.x > 0) maxW += r2d.x;
        else           tx   -= r2d.x;
        if (r2d.y > 0) maxH += r2d.y;
        else           ty   -= r2d.y;
        // display scrollbars, if appropriate
        // (if scaled document size is larger than viewport size)
        validate();
        Dimension vpSize = canvas.getSize();
        int maxVPW = vpSize.width;  int minVPW = vpSize.width;
        int maxVPH = vpSize.height; int minVPH = vpSize.height;
        if (vertical.isVisible()) {
            maxVPW += vertical.getPreferredSize().width;
        } else {
            minVPW -= vertical.getPreferredSize().width;
        }
        if (horizontalPanel.isVisible()) {
            maxVPH += horizontal.getPreferredSize().height;
        } else {
            minVPH -= horizontal.getPreferredSize().height;
        }
        // System.err.println("W: [" + minVPW + "," + maxVPW + "] " +
        //                    "H: [" + minVPH + "," + maxVPH + "]");
        // System.err.println("MAX: [" + maxW + "," + maxH + "]");
        boolean vVis = (maxH > maxVPH) || (vertical.getValue() != 0);
        boolean hVis = (maxW > maxVPW) || (horizontal.getValue() != 0);
        Dimension ret = new Dimension();
        if (vVis) {
            if (hVis) {
                horizontalPanel.setVisible(true);
                vertical.setVisible(true);
                cornerBox.setVisible(true);
                ret.width  = minVPW;
                ret.height = minVPH;
            } else {
                vertical.setVisible(true);
                ret.width = minVPW;
                if (maxW > minVPW) {
                    horizontalPanel.setVisible(true);
                    cornerBox.setVisible(true);
                    ret.height = minVPH;
                } else {
                    horizontalPanel.setVisible(false);
                    cornerBox.setVisible(false);
                    ret.height = maxVPH;
                }
            }
        } else {
            if (hVis) {
                horizontalPanel.setVisible(true);
                ret.height = minVPH;
                if (maxH > minVPH) {
                    vertical.setVisible(true);
                    cornerBox.setVisible(true);
                    ret.width  = minVPW;
                } else {
                    vertical.setVisible(false);
                    cornerBox.setVisible(false);
                    ret.width  = maxVPW;
                }
            } else {
                vertical       .setVisible(false);
                horizontalPanel.setVisible(false);
                cornerBox      .setVisible(false);
                ret.width  = maxVPW;
                ret.height = maxVPH;
            }
        }
        
        return ret;
    }
	
    /** 
     *	Derives the SVG Viewbox from the SVG root element. 
     *	Caches it. Assumes that it will not change.
     *
     */
    private void checkAndSetViewBoxRect() {
        if (viewBox != null) return;
        SVGDocument doc = canvas.getSVGDocument();
        if (doc == null) return;
        SVGSVGElement el = doc.getRootElement();
        if (el == null) return;

        String viewBoxStr = el.getAttributeNS
            (null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        float[] rect = ViewBox.parseViewBoxAttribute(el, viewBoxStr);
        viewBox = new Rectangle2D.Float(rect[0], rect[1], rect[2], rect[3]); 
        
        System.out.println("  ** viewBox rect set: "+viewBox);
        // System.out.println("  ** doc size: "+
        //                    canvas.getSVGDocumentSize());
    }// checkAndSetViewBoxRect()
	
	
    /** 
     *	Called when the scale size changes. The scale factor
     *	(1.0 == original size). By default, this method does
     *	nothing, but may be overidden to display a scale
     *	(zoom) factor in a status bar, for example.
     */
    public void scaleChange(float scale)
    {
        // do nothing
    }


    static class JSVGScrollCanvas extends JSVGCanvas {
        /**
         * Implements our new resizing behavior. This
         * prevents scaling from changing all the time
         * when the window size changes.  <p>
         * Updates the value of the transform used for rendering.
         * Return true if a repaint is required, otherwise false.
         */
        protected boolean updateRenderingTransform()  {
            if((svgDocument == null) || (gvtRoot == null))
                return false;
                    
            // Code provided by Mark Claassen
            try {
                SVGSVGElement elt = svgDocument.getRootElement();
                Dimension d;
                Dimension2D d2 = getSVGDocumentSize();
                d = new Dimension((int)d2.getWidth(),
                                  (int)d2.getHeight());

                Dimension oldD = prevComponentSize;
                if (oldD == null) { oldD = d; }
                prevComponentSize = d;
			
                if (d.width  < 1) { d.width  = 1; }
                if (d.height < 1) { d.height = 1; }
                        
                AffineTransform at = ViewBox.getViewTransform
                    (fragmentIdentifier, elt, d.width, d.height);
                CanvasGraphicsNode cgn = getCanvasGraphicsNode();
                AffineTransform vt = cgn.getViewingTransform();
                if(at.equals(vt))
                    // No new transform
                    // Only repaint if size really changed.
                    return ((oldD.width != d.width) || 
                            (oldD.height != d.height));
            } catch (BridgeException e) {
                userAgent.displayError(e);
            }
            return true;
        }// updateRenderingTransform()
    }
	
}// class JSVGScrollPane


