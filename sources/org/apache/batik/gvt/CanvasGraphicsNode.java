/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
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
