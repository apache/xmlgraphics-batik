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

package org.apache.batik.swing;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.w3c.dom.Element;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.MemoryLeakTest;
import org.apache.batik.test.TestReport;

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

    public String getName() { return "JSVGMemoryLeakTest."+getId(); }

    TestReport failReport = null;
    boolean done;
    JSVGCanvasHandler handler;
    JFrame theFrame;
    JSVGCanvas theCanvas;

    public static String fmt(String key, Object []args) {
        return TestMessages.formatMessage(key, args);
    }

    public JSVGCanvasHandler createHandler() {
        return new JSVGCanvasHandler(this, this);
    }

    public TestReport doSomething() throws Exception {
        handler = createHandler();
        registerObjectDesc(handler, "Handler");
        done = false;
        handler.runCanvas(getId());

        SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    // System.out.println("In Invoke");
                    theFrame.remove(theCanvas);
                    theCanvas.dispose();

                    theFrame.dispose();
                    theFrame=null;
                    theCanvas=null;
                }
            });

        try  { Thread.sleep(100); } catch (InterruptedException ie) { }

        SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    // Create a new Frame to take focus for Swing so old one
                    // can be GC'd.
                    theFrame = new JFrame("FocusFrame"); 
                    // registerObjectDesc(jframe, "FocusFrame");
                    theFrame.setSize(new java.awt.Dimension(40, 50));
                    theFrame.setVisible(true);
                }});

        try  { Thread.sleep(100); } catch (InterruptedException ie) { }
        
        SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    theFrame.setVisible(false);
                    theFrame.dispose();
                }});
        
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
}
