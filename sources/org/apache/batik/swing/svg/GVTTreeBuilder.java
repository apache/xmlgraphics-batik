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

package org.apache.batik.swing.svg;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.DynamicGVTBuilder;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.w3c.dom.svg.SVGDocument;


/**
 * This class represents an object which builds asynchroneaously
 * a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GVTTreeBuilder extends Thread {

    /**
     * The SVG document to give to the bridge.
     */
    protected SVGDocument svgDocument;

    /**
     * The bridge context to use.
     */
    protected BridgeContext bridgeContext;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The exception thrown.
     */
    protected Exception exception;

    /**
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

    /**
     * Creates a new GVTTreeBuilder.
     */
    public GVTTreeBuilder(SVGDocument   doc,
                          BridgeContext bc) {
        svgDocument = doc;
        bridgeContext = bc;
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs this builder.
     */
    public void run() {
        GVTTreeBuilderEvent ev;
        ev = new GVTTreeBuilderEvent(this, null);
        try {
            fireEvent(startedDispatcher, ev);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }
            GVTBuilder builder = null;

            if (bridgeContext.isDynamic()) {
                builder = new DynamicGVTBuilder();
            } else {
                builder = new GVTBuilder();
            }
            GraphicsNode gvtRoot = builder.build(bridgeContext, svgDocument);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            ev = new GVTTreeBuilderEvent(this, gvtRoot);
            fireEvent(completedDispatcher, ev);
        } catch (InterruptedBridgeException e) {
            fireEvent(cancelledDispatcher, ev);
        } catch (BridgeException e) {
            exception = e;
            ev = new GVTTreeBuilderEvent(this, e.getGraphicsNode());
            fireEvent(failedDispatcher, ev);
        } catch (Exception e) {
            exception = e;
            fireEvent(failedDispatcher, ev);
        } catch (Throwable t) {
            t.printStackTrace();
            exception = new Exception(t.getMessage());
            fireEvent(failedDispatcher, ev);
        } finally {
            // bridgeContext.getDocumentLoader().dispose();
        }
    }

    public void interrupt() {
        super.interrupt();
        synchronized (this) {
            beenInterrupted = true;
        }
    }

    /**
     * Returns the exception, if any occured.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Adds a GVTTreeBuilderListener to this GVTTreeBuilder.
     */
    public void addGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        listeners.add(l);
    }

    /**
     * Removes a GVTTreeBuilderListener from this GVTTreeBuilder.
     */
    public void removeGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        listeners.remove(l);
    }

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildStarted
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildCompleted
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildCancelled
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildFailed
                 ((GVTTreeBuilderEvent)event);
            }
        };
}
