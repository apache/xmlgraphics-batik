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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;

/**
 * This class represents an object which renders asynchroneaously
 * a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GVTTreeRenderer extends Thread {

    /**
     * The renderer used to paint.
     */
    protected ImageRenderer renderer;

    /**
     * The area of interest.
     */
    protected Shape areaOfInterest;

    /**
     * The buffer width.
     */
    protected int width;

    /**
     * The buffer height.
     */
    protected int height;

    /**
     * The user to device transform.
     */
    protected AffineTransform user2DeviceTransform;

    /**
     * Whether to enable the double buffering.
     */
    protected boolean doubleBuffering;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

    /**
     * Creates a new GVTTreeRenderer.
     * @param r The renderer to use to paint.
     * @param usr2dev The user to device transform.
     * @param dbuffer Whether the double buffering should be enabled.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     */
    public GVTTreeRenderer(ImageRenderer r, AffineTransform usr2dev,
                           boolean dbuffer,
                           Shape aoi, int width, int height) {
        renderer = r;
        areaOfInterest = aoi;
        user2DeviceTransform = usr2dev;
        doubleBuffering = dbuffer;
        this.width = width;
        this.height = height;
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs this renderer.
     */
    public void run() {
        GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, null);
        try {
            fireEvent(prepareDispatcher, ev);

            renderer.setTransform(user2DeviceTransform);
            renderer.setDoubleBuffered(doubleBuffering);
            renderer.updateOffScreen(width, height);
            renderer.clearOffScreen();

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            ev = new GVTTreeRendererEvent(this, renderer.getOffScreen());
            fireEvent(startedDispatcher, ev);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            renderer.repaint(areaOfInterest);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            ev = new GVTTreeRendererEvent(this, renderer.getOffScreen());
            fireEvent(completedDispatcher, ev);
        } catch (NoClassDefFoundError e) {
            // This error was reported to happen when the rendering
            // is interrupted with JDK1.3.0rc1 Solaris.
        } catch (InterruptedBridgeException e) {
            // this sometimes happens with SVG Fonts since the glyphs are
            // not built till the rendering stage
            fireEvent(cancelledDispatcher, ev);
        } catch (Throwable t) {
            t.printStackTrace();
            fireEvent(failedDispatcher, ev);
        }
    }

    public void interrupt() {
        super.interrupt();
        synchronized (this) {
            beenInterrupted = true;
        }
    }

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    /**
     * Adds a GVTTreeRendererListener to this GVTTreeRenderer.
     */
    public void addGVTTreeRendererListener(GVTTreeRendererListener l) {
        listeners.add(l);
    }

    /**
     * Removes a GVTTreeRendererListener from this GVTTreeRenderer.
     */
    public void removeGVTTreeRendererListener(GVTTreeRendererListener l) {
        listeners.remove(l);
    }

    static Dispatcher prepareDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeRendererListener)listener).gvtRenderingPrepare
                    ((GVTTreeRendererEvent)event);
            }
        };
            
    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeRendererListener)listener).gvtRenderingStarted
                    ((GVTTreeRendererEvent)event);
            }
        };
            
    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeRendererListener)listener).gvtRenderingCancelled
                    ((GVTTreeRendererEvent)event);
            }
        };
            
    static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeRendererListener)listener).gvtRenderingCompleted
                    ((GVTTreeRendererEvent)event);
            }
        };
            
    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeRendererListener)listener).gvtRenderingFailed
                    ((GVTTreeRendererEvent)event);
            }
        };
            

}
