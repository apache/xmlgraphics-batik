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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * This class represents a pan interactor.
 * To use it, just redefine the {@link
 * InteractorAdapter#startInteraction(InputEvent)} method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractPanInteractor extends InteractorAdapter {

    /**
     * The cursor for panning.
     */
    public final static Cursor PAN_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

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
     * To store the previous cursor.
     */
    protected Cursor previousCursor;

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

        xStart = e.getX();
        yStart = e.getY();

        JGVTComponent c = (JGVTComponent)e.getSource();

        previousCursor = c.getCursor();
        c.setCursor(PAN_CURSOR);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if (finished) {
            return;
        }
        finished = true;

        JGVTComponent c = (JGVTComponent)e.getSource();

        xCurrent = e.getX();
        yCurrent = e.getY();

        AffineTransform at =
            AffineTransform.getTranslateInstance(xCurrent - xStart,
                                                 yCurrent - yStart);
        AffineTransform rt =
            (AffineTransform)c.getRenderingTransform().clone();
        rt.preConcatenate(at);
        c.setRenderingTransform(rt);

        if (c.getCursor() == PAN_CURSOR) {
            c.setCursor(previousCursor);
        }
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        finished = true;
        
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.setPaintingTransform(null);
        if (c.getCursor() == PAN_CURSOR) {
            c.setCursor(previousCursor);
        }
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

        xCurrent = e.getX();
        yCurrent = e.getY();

        AffineTransform at =
            AffineTransform.getTranslateInstance(xCurrent - xStart,
                                                 yCurrent - yStart);
        c.setPaintingTransform(at);
    }
}
