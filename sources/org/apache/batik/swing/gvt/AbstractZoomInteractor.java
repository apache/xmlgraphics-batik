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

package org.apache.batik.swing.gvt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

/**
 * This class represents a zoom interactor.
 * To use it, just redefine the {@link
 * InteractorAdapter#startInteraction(InputEvent)} method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AbstractZoomInteractor extends InteractorAdapter {

    /**
     * Whether the interactor has finished.
     */
    protected boolean finished = true;

    /**
     * The mouse x start position.
     */
    protected int xStart;

    /**
     * The mouse y start position.
     */
    protected int yStart;

    /**
     * The mouse x current position.
     */
    protected int xCurrent;

    /**
     * The mouse y current position.
     */
    protected int yCurrent;

    /**
     * The zoom marker top line.
     */
    protected Line2D markerTop;

    /**
     * The zoom marker left line.
     */
    protected Line2D markerLeft;

    /**
     * The zoom marker bottom line.
     */
    protected Line2D markerBottom;

    /**
     * The zoom marker right line.
     */
    protected Line2D markerRight;

    /**
     * The overlay.
     */
    protected Overlay overlay = new ZoomOverlay();

    /**
     * Used to draw marker
     */
    protected BasicStroke markerStroke = new BasicStroke(1,
                                                         BasicStroke.CAP_SQUARE,
                                                         BasicStroke.JOIN_MITER,
                                                         10,
                                                         new float[] { 4, 4 }, 0);

    /**
     * Tells whether the interactor has finished.
     */
    public boolean endInteraction() {
        return finished;
    }

    // MouseListener ///////////////////////////////////////////////////////
        
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        if (!finished) {
            mouseExited(e);
            return;
        }
        
        finished = false;
        markerTop = null;
        markerLeft = null;
        markerBottom = null;
        markerRight = null;

        xStart = e.getX();
        yStart = e.getY();
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.getOverlays().add(overlay);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.getOverlays().remove(overlay);
        overlay.paint(c.getGraphics());

        xCurrent = e.getX();
        yCurrent = e.getY();

        if ((xCurrent - xStart) != 0 &&
            (yCurrent - yStart) != 0) {

            int dx = xCurrent - xStart;
            int dy = yCurrent - yStart;
            
            if (dx < 0) {
                dx = -dx;
                xStart = xCurrent;
            }
            if (dy < 0) {
                dy = -dy;
                yStart = yCurrent;
            }

            Dimension size = c.getSize();

            // Zoom factor
            float scaleX = size.width / (float)dx;
            float scaleY = size.height / (float)dy;
            float scale = (scaleX < scaleY) ? scaleX : scaleY;
        
            // Zoom translate
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            at.translate(-xStart, -yStart);

            at.concatenate(c.getRenderingTransform());
            c.setRenderingTransform(at);
        }
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.getOverlays().remove(overlay);
        overlay.paint(c.getGraphics());
    }

    // MouseMotionListener /////////////////////////////////////////////////

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {
        JGVTComponent c = (JGVTComponent)e.getSource();

        overlay.paint(c.getGraphics());

        xCurrent = e.getX();
        yCurrent = e.getY();

        // Constrain rectangle to window's Aspect Ratio.
        float xMin, yMin, width, height;
        if (xStart < xCurrent) {
            xMin = xStart;
            width = xCurrent - xStart;
        } else {
            xMin = xCurrent;
            width = xStart - xCurrent;
        }
        if (yStart < yCurrent) {
            yMin = yStart;
            height = yCurrent - yStart;
        } else {
            yMin = yCurrent;
            height = yStart - yCurrent;
        }
        Dimension d = c.getSize();
        float compAR = d.width/(float)d.height;
        if (compAR > width/height) {
            width = compAR*height;
        } else {
            height = width/compAR;
        }

        markerTop    = new Line2D.Float(xMin, yMin, xMin+width,  yMin);
        markerLeft   = new Line2D.Float(xMin, yMin, xMin, yMin+height);
        markerBottom = new Line2D.Float(xMin, yMin+height,  
                                        xMin+width,  yMin+height);
        markerRight  = new Line2D.Float(xMin+width,  yMin,  
                                        xMin+width,  yMin+height);

        overlay.paint(c.getGraphics());
    }

    /**
     * To paint the interactor.
     */
    protected class ZoomOverlay implements Overlay {
        
        /**
         * Paints this overlay.
         */
        public void paint(Graphics g) {
            if (markerTop != null) {
                Graphics2D g2d = (Graphics2D)g;

                g2d.setXORMode(Color.white);
                g2d.setColor(Color.black);
                g2d.setStroke(markerStroke);

                g2d.draw(markerTop);
                g2d.draw(markerLeft);
                g2d.draw(markerBottom);
                g2d.draw(markerRight);
            }
        }
    }
}
