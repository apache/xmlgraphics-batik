/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Vector;

/**
 * This class allows for the return of a proxy object quickly, while a
 * heavy weight object is constrcuted in a background Thread.  This
 * proxy object will then block if any methods are called on it that
 * require talking to the source object.
 *
 * This is actually a particular instance of a very general pattern
 * this is probably best represented using the Proxy class in the
 * Reflection APIs.
 */

public class DeferRable implements Filter {
    Filter src;

    /**
     * Constructor takes nothing
     */
    public DeferRable() { 
    }

    /**
     * Key method that blocks if the src has not yet been provided.
     */
    public synchronized Filter getSource() {
        while (src == null) {
            try {
                // Wait for someone to set src.
                wait();
            }
            catch(InterruptedException ie) { 
                // Loop around again see if src is set now...
            }
        }
        return src;
    }

    /**
     * Key method that sets the src.  The source can only
     * be set once (this makes sense given the intent of the
     * class is to stand in for a real object, so swaping that
     * object isn't a good idea.
     *
     * This will wake all the threads that might be waiting for
     * the source to be set.
     */
    public synchronized void setSource(Filter src) {
        // Only let them set Source once.
        if (this.src != null) return;

        this.src = src;
        notifyAll();
    }

    public long getTimeStamp() { 
        return getSource().getTimeStamp();
    }

    public Vector getSources() {
        return getSource().getSources();
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public boolean isDynamic() { 
        return getSource().isDynamic();
    }

    /**
     * Implement the baseclass method to call getSource() so
     * it will block until we have a real source.
     */
    public Rectangle2D getBounds2D() {
        return getSource().getBounds2D();
    }

    public float getMinX() {
        return (float)getBounds2D().getX();
    }
    public float getMinY() {
        return (float)getBounds2D().getY();
    }
    public float getWidth() {
        return (float)getBounds2D().getWidth();
    }
    public float getHeight() {
        return (float)getBounds2D().getHeight();
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public Object getProperty(String name) {
        return getSource().getProperty(name);
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public String [] getPropertyNames() {
        return getSource().getPropertyNames();
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public RenderedImage createDefaultRendering() {
        return getSource().createDefaultRendering();
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public RenderedImage createScaledRendering(int w, int h, 
                                               RenderingHints hints) {
        return getSource().createScaledRendering(w, h, hints);
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public RenderedImage createRendering(RenderContext rc) {
        return getSource().createRendering(rc);
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public Shape getDependencyRegion(int srcIndex, 
                                     Rectangle2D outputRgn) {
        return getSource().getDependencyRegion(srcIndex, outputRgn);
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public Shape getDirtyRegion(int srcIndex, 
                                Rectangle2D inputRgn) {
        return getSource().getDirtyRegion(srcIndex, inputRgn);
    }
}
