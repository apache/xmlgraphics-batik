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

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * The graphics node container with a background color.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CanvasGraphicsNode extends CompositeGraphicsNode {

    /**
     * This is the position transform for this graphics node.
     * This is needed because getCTM returns the transform
     * to the viewport coordinate system which is after viewing but
     * before positioning.
     */
    protected AffineTransform positionTransform;
    /**
     * This is the viewing transform for this graphics node.
     * This is needed because getCTM returns the transform
     * to the viewport coordinate system which is after viewing but
     * before positioning.
     */
    protected AffineTransform viewingTransform;

    /** 
     * The background of this canvas graphics node.
     */
    protected Paint backgroundPaint;

    /**
     * Constructs a new empty <tt>CanvasGraphicsNode</tt>.
     */
    public CanvasGraphicsNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the background paint of this canvas graphics node.
     *
     * @param newBackgroundPaint the new background paint
     */
    public void setBackgroundPaint(Paint newBackgroundPaint) {
        this.backgroundPaint = newBackgroundPaint;
    }

    /**
     * Returns the background paint of this canvas graphics node.
     */
    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setPositionTransform(AffineTransform at) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.positionTransform = at;
        if (positionTransform != null) {
            transform = new AffineTransform(positionTransform);
            if (viewingTransform != null)
                transform.concatenate(viewingTransform);
        } else if (viewingTransform != null)
            transform = new AffineTransform(viewingTransform);
        else
            transform = new AffineTransform();
        
        if (transform.getDeterminant() != 0){
            try{
                inverseTransform = transform.createInverse();
            }catch(NoninvertibleTransformException e){
                // Should never happen.
                throw new Error();
            }
        }
        else{
            // The transform is not invertible. Use the same
            // transform.
            inverseTransform = transform;
        }
        fireGraphicsNodeChangeCompleted();
    }

    public AffineTransform getPositionTransform() {
        return positionTransform;
    }

    public void setViewingTransform(AffineTransform at) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.viewingTransform = at;
        if (positionTransform != null) {
            transform = new AffineTransform(positionTransform);
            if (viewingTransform != null)
                transform.concatenate(viewingTransform);
        } else if (viewingTransform != null)
            transform = new AffineTransform(viewingTransform);
        else
            transform = new AffineTransform();

        if(transform.getDeterminant() != 0){
            try{
                inverseTransform = transform.createInverse();
            }catch(NoninvertibleTransformException e){
                // Should never happen.
                throw new Error();
            }
        }
        else{
            // The transform is not invertible. Use the same
            // transform.
            inverseTransform = transform;
        }
        fireGraphicsNodeChangeCompleted();
    }

    public AffineTransform getViewingTransform() {
        return viewingTransform;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (backgroundPaint != null) {
            g2d.setPaint(backgroundPaint);
            g2d.fill(g2d.getClip()); // Fast paint for huge background area
        }
        super.primitivePaint(g2d);
    }
}
