/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.batik.swing.svg.*;
import org.apache.batik.swing.gvt.*;
import org.apache.batik.test.*;

/**
 * This test makes sure that setting the canvas's document uri to 
 * null does not cause a NullPointerException
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class NullURITest extends AbstractTest {
    public static final String TEST_NON_NULL_URI
        = "file:samples/anne.svg";

    public static final String ERROR_COULD_NOT_RENDER_TEST_URI
        = "error.could.not.render.test.uri";

    public static final String ERROR_COULD_NOT_RENDER_NULL_URI
        = "error.could.not.render.null.uri";

    public TestReport runImpl() throws Exception {
        final JFrame f = new JFrame();
        final JSVGCanvas canvas = new JSVGCanvas();

        final String monitor = "monitor";

        f.getContentPane().add(canvas);
        canvas.setPreferredSize(new Dimension(450, 500));
        f.pack();
        f.setVisible(true);

        // This class is not fool-proof: it assumes that the
        // non-null uri will render properly
        class InitialRenderListener extends GVTTreeRendererAdapter {
            public boolean failed = true;

            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                failed = false;
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }


            public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }

            public void gvtRenderingFailed(GVTTreeRendererEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }
        }

        class LoadListener extends SVGDocumentLoaderAdapter {
            public boolean failed = false;
            public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    failed = true;
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    failed = true;
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }
        }

        InitialRenderListener l = new InitialRenderListener();
        LoadListener ll = new LoadListener();
        canvas.addGVTTreeRendererListener(l);

        canvas.setURI(TEST_NON_NULL_URI);
        canvas.setEnabled(false);

        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_TEST_URI);
        }

        // Now, wait on new rendering
        l.failed = true;
        canvas.setURI(null);
        canvas.setEnabled(false);

        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_NULL_URI);
        }

        canvas.setURI(TEST_NON_NULL_URI);
        canvas.setEnabled(false);
        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_TEST_URI);
        }

        
        f.dispose();

        synchronized(monitor){
            monitor.wait(10000);
        }

        return reportSuccess();
    
    }
}
