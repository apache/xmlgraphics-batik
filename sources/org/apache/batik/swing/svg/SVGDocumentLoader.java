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

import java.io.InterruptedIOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.w3c.dom.svg.SVGDocument;

/**
 * This class represents an object which loads asynchroneaously a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGDocumentLoader extends Thread {

    /**
     * The URL of the document,
     */
    protected String url;

    /**
     * The document loader.
     */
    protected DocumentLoader loader;

    /**
     * The exception thrown.
     */
    protected Exception exception;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

    /**
     * Creates a new SVGDocumentLoader.
     * @param u The URL of the document.
     * @param l The document loader to use
     */
    public SVGDocumentLoader(String u, DocumentLoader l) {
        url = u;
        loader = l;
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs this loader.
     */
    public void run() {
        SVGDocumentLoaderEvent evt;
        evt = new SVGDocumentLoaderEvent(this, null);
        try {
            fireEvent(startedDispatcher, evt);
            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, evt);
                return;
            }

            SVGDocument svgDocument = (SVGDocument)loader.loadDocument(url);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, evt);
                return;
            }

            evt = new SVGDocumentLoaderEvent(this, svgDocument);
            fireEvent(completedDispatcher, evt);
        } catch (InterruptedIOException e) {
            fireEvent(cancelledDispatcher, evt);
        } catch (Exception e) {
            exception = e;
            fireEvent(failedDispatcher, evt);
        } catch (ThreadDeath td) {
            exception = new Exception(td.getMessage());
            fireEvent(failedDispatcher, evt);
            throw td;
        } catch (Throwable t) {
            t.printStackTrace();
            exception = new Exception(t.getMessage());
            fireEvent(failedDispatcher, evt);
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
     * Adds a SVGDocumentLoaderListener to this SVGDocumentLoader.
     */
    public void addSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        listeners.add(l);
    }

    /**
     * Removes a SVGDocumentLoaderListener from this SVGDocumentLoader.
     */
    public void removeSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        listeners.remove(l);
    }

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingStarted
                    ((SVGDocumentLoaderEvent)event);
            }
        };
            
            static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingCompleted
                 ((SVGDocumentLoaderEvent)event);
            }
        };

    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingCancelled
                 ((SVGDocumentLoaderEvent)event);
            }
        };

    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingFailed
                 ((SVGDocumentLoaderEvent)event);
            }
        };
}
