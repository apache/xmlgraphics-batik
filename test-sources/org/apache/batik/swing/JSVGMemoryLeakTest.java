/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.MemoryLeakTest;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import java.io.File;
import java.net.MalformedURLException;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class JSVGMemoryLeakTest extends MemoryLeakTest
    implements JSVGCanvasHandler.Delegate {
    public JSVGMemoryLeakTest() {
    }

    public String getName() { return getId(); }

    TestReport failReport = null;
    boolean done;
    JSVGCanvasHandler handler;
    JFrame theFrame;
    JSVGCanvas theCanvas;

    public TestReport doSomething() throws Exception {
        handler = new JSVGCanvasHandler(this, this);
        registerObjectDesc(handler, "Handler");
        done = false;
        handler.runCanvas(getId());

        SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    // System.out.println("In Invoke");
                    theCanvas.stopProcessing();
                    theFrame.remove(theCanvas);
                    theFrame.removeNotify();
                    theCanvas.removeNotify();
                    theFrame=null;
                    theCanvas=null;
                }
            });

        {
            // Create a new Frame to take focus for Swing so old one
            // can be GC'd.
            javax.swing.JFrame jframe = new javax.swing.JFrame("FocusFrame"); 
            // registerObjectDesc(jframe, "FocusFrame");
            jframe.setSize(new java.awt.Dimension(40, 50));
            jframe.setVisible(true);
            jframe.setVisible(false);
            jframe.removeNotify();
        }

        handler = null;
        if (failReport != null) return failReport;
        DefaultTestReport report = new DefaultTestReport(this);
        report.setPassed(true);
        return report;
    }

    public void scriptDone() {
        synchronized (this) {
            done = true;
            // The canvasUpdate will notify the handler that the
            // canvas can be shut down.
        }
    }

    /* JSVGCanvasHandler.Delegate Interface */
    public void canvasInit(JSVGCanvas canvas) {
        // System.err.println("In Init");
        theCanvas = canvas;
        theFrame  = handler.getFrame();

        File f = new File(getId());
        try {
            canvas.setURI(f.toURL().toString());
        } catch (MalformedURLException mue) {
        }
        registerObjectDesc(canvas, "JSVGCanvas");
        registerObjectDesc(handler.getFrame(), "JFrame");
    }

    public void canvasLoaded(JSVGCanvas canvas) {
        // System.err.println("Loaded");
        registerObjectDesc(canvas.getSVGDocument(), "SVG Document");
    }

    public void canvasRendered(JSVGCanvas canvas) {
        // System.err.println("Rendered");
        registerObjectDesc(canvas.getGraphicsNode(), "Graphics Node Tree");
        registerObjectDesc(canvas.getUpdateManager(), "Update Manager");
    }

    public boolean canvasUpdated(JSVGCanvas canvas) {
        // System.err.println("Updated");
        synchronized (this) {
            return done;
        }
    }
    public void canvasDone(final JSVGCanvas canvas) {
        // System.err.println("Done");
    }

    public void failure(TestReport report) {
        synchronized (this) {
            done = true;
            failReport = report;
        }
    }
};
