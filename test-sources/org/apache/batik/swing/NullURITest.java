/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import javax.swing.*;
import java.awt.event.*;

import org.apache.batik.swing.svg.*;
import org.apache.batik.test.*;

/**
 * This test makes sure that setting the canvas's document uri to 
 * null does not cause a NullPointerException
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class NullURITest extends AbstractTest {
    /**
     * Code used if error happens while setting null URI on JSVGCanvas
     */
    public static final String ERROR_WITH_NULL_URI
        = "error.with.null.uri";

    public TestReport runImpl() throws Exception {
        final JSVGCanvas canvas = new JSVGCanvas();
        final String monitor = "monitor";

        class LoadListener extends SVGDocumentLoaderAdapter {
            public boolean failed = false;
            public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    System.out.println(">>>>>>>>>>>>>>> in documentLoadingFailed");
                    failed = true;
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }
        }

        LoadListener l = new LoadListener();
        canvas.addSVGDocumentLoaderListener(l);

        canvas.setURI(null);

        // Give chance to the DocumentLoader thread to kick in
        // This is not 
        synchronized(monitor){
            // Document loading should not take more than 10 sec.
            // Put a maximum to avoid deadlocks in case something
            // else goes wrong.
            monitor.wait(10000);
        }

        if (!l.failed){
            return reportSuccess();
        } else {
            return reportError(ERROR_WITH_NULL_URI);
        }
    }
}
