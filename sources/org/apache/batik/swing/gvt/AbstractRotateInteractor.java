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

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * This class represents a rotate interactor.
 * To use it, just redefine the {@link
 * InteractorAdapter#startInteraction(InputEvent)} method.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AbstractRotateInteractor extends InteractorAdapter {

    /**
     * Whether the interactor has finished.
     */
    protected boolean finished;

    /**
     * The initial rotation angle.
     */
    protected double initialRotation;

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
        finished = false;
        JGVTComponent c = (JGVTComponent)e.getSource();

        Dimension d = c.getSize();
        double dx = e.getX() - d.width / 2;
        double dy = e.getY() - d.height / 2;
        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
        initialRotation = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();

        AffineTransform at = rotateTransform(c.getSize(), e.getX(), e.getY());
        at.concatenate(c.getRenderingTransform());
        c.setRenderingTransform(at);
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        finished = true;
        JGVTComponent c = (JGVTComponent)e.getSource();
        c.setPaintingTransform(null);
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

        c.setPaintingTransform(rotateTransform(c.getSize(), e.getX(), e.getY()));
    }

    /**
     * Returns the rotate transform.
     */
    protected AffineTransform rotateTransform(Dimension d, int x, int y) {
        double dx = x - d.width / 2;
        double dy = y - d.height / 2;
        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
        double angle = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);

        angle -= initialRotation;

        return AffineTransform.getRotateInstance(angle, d.width / 2, d.height / 2);
    }
}
