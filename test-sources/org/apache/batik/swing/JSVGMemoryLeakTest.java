/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.MemoryLeakTest;

import java.io.File;
import java.net.MalformedURLException;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

import org.w3c.dom.Element;


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

    public static String fmt(String key, Object []args) {
        return TestMessages.formatMessage(key, args);
    }

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
            handler.scriptDone();
        }
    }

    public void registerElement(Element e, String desc) {
        registerObjectDesc(e, desc);
        UpdateManager um = theCanvas.getUpdateManager();
        BridgeContext bc = um.getBridgeContext();
        GraphicsNode gn = bc.getGraphicsNode(e);
        if (gn != null)
            registerObjectDesc(gn, desc+"_GN");
        if (e instanceof SVGOMElement) {
            SVGOMElement svge = (SVGOMElement)e;
            SVGContext svgctx = svge.getSVGContext();
            if (svgctx != null) 
                registerObjectDesc(svgctx, desc+"_CTX");
        }
    }

    /* JSVGCanvasHandler.Delegate Interface */
    public boolean canvasInit(JSVGCanvas canvas) {
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

        return true;
    }

    public void canvasLoaded(JSVGCanvas canvas) {
        // System.err.println("Loaded");
        registerObjectDesc(canvas.getSVGDocument(), "SVGDoc");
    }

    public void canvasRendered(JSVGCanvas canvas) {
        // System.err.println("Rendered");
        registerObjectDesc(canvas.getGraphicsNode(), "GVT");
        registerObjectDesc(canvas.getUpdateManager(), "updateManager");
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
