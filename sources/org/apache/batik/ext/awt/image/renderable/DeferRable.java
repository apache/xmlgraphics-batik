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

package org.apache.batik.ext.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
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
    Filter      src;
    Rectangle2D bounds;
    Map         props;
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
        this.src    = src;
        this.bounds = src.getBounds2D();
        notifyAll();
    }

    public synchronized void setBounds(Rectangle2D bounds) {
        if (this.bounds != null) return;
        this.bounds = bounds;
        notifyAll();
    }

    public synchronized void setProperties(Map props) {
        this.props = props;
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
        synchronized(this) {
            while ((src == null) && (bounds == null))  {
                try {
                    // Wait for someone to set bounds.
                    wait();
                }
                catch(InterruptedException ie) { 
                    // Loop around again see if src is set now...
                }
            }
        }
        if (src != null)
            return src.getBounds2D();
        return bounds;
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
        synchronized (this) {
            while ((src == null) && (props == null)) {
                try {
                    // Wait for someone to set src | props
                    wait();
                } catch(InterruptedException ie) { }
            }
        }
        if (src != null)
            return src.getProperty(name);
        return props.get(name);
    }

    /**
     * Forward the call (blocking until source is set if need be).
     */
    public String [] getPropertyNames() {
        synchronized (this) {
            while ((src == null) && (props == null)) {
                try {
                    // Wait for someone to set src | props
                    wait();
                } catch(InterruptedException ie) { }
            }
        }
        if (src != null)
            return src.getPropertyNames();

        String [] ret = new String[props.size()];
        props.keySet().toArray(ret);
        return ret;
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
