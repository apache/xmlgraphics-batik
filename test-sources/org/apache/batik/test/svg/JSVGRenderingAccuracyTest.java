/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.w3c.dom.Document;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.bridge.ScriptingEnvironment;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class JSVGRenderingAccuracyTest extends SamplesRenderingTest {

    public static final String REGARD_TEST_INSTANCE = "regardTestInstance";
    public static final String REGARD_START_SCRIPT = 
        "try { regardStart(); } catch (er) {}";

    /**
     * Error when canvas can't load SVG file.
     * {0} The file/url that could not be loaded.
     */
    public static final String ERROR_CANNOT_LOAD_SVG = 
        "JSVGRenderingAccuracyTest.message.error.could.not.load.svg";

    /**
     * Error when canvas can't render SVG file.
     * {0} The file/url that could not be rendered.
     */
    public static final String ERROR_SVG_RENDER_FAILED = 
        "JSVGRenderingAccuracyTest.message.error.svg.render.failed";

    /**
     * Error when canvas can't peform render update SVG file.
     * {0} The file/url that could not be updated..
     */
    public static final String ERROR_SVG_UPDATE_FAILED = 
        "JSVGRenderingAccuracyTest.message.error.svg.update.failed";

    /**
     * Error when canvas can't peform render update SVG file.
     * {0} The file/url that could not be updated..
     */
    public static final String ERROR_SAVE_FAILED = 
        "JSVGRenderingAccuracyTest.message.error.save.failed";

    public static String fmt(String key, Object []args) {
        return Messages.formatMessage(key, args);
    }

    /**
     * For subclasses
     */
    public JSVGRenderingAccuracyTest(){
    }

    JFrame     frame = null;
    JSVGCanvas canvas = null;
    UpdateManager updateManager = null;
    BufferedImage theImage = null;
    WindowListener wl = null;

    boolean renderFailed;
    boolean loadFailed;
    boolean done;
    boolean abort;
    Object loadMonitor = new Object();
    Object renderMonitor = new Object();

    public TestReport encode(URL srcURL, FileOutputStream fos) {
        DefaultTestReport report = new DefaultTestReport(this);
            loadFailed = true;
            renderFailed = true;
            
            frame = new JFrame(getName());
            canvas = new JSVGCanvas();
            frame.getContentPane().add(canvas);
            frame.setSize(new Dimension(450, 500));
            frame.setVisible(true);
            wl = new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        synchronized (loadMonitor) {
                            abort = true;
                            loadMonitor.notifyAll();
                        }
                        synchronized (renderMonitor) {
                            abort = true;
                            renderMonitor.notifyAll();
                        }
                    }
                };
            frame.addWindowListener(wl);
            
            canvas.addGVTTreeRendererListener
                (new InitialRenderListener());
            canvas.addSVGDocumentLoaderListener
                (new LoadListener());
            try {
            
            initCanvas(canvas, srcURL);
            
            synchronized (renderMonitor) {
                synchronized (loadMonitor) {
                    try { loadMonitor.wait(); }
                    catch(InterruptedException ie) { /* nothing */ }
                    if (abort || loadFailed) {
                        report.setErrorCode(ERROR_CANNOT_LOAD_SVG);
                        report.setDescription(new TestReport.Entry[] { 
                            new TestReport.Entry
                            (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                             fmt(ERROR_CANNOT_LOAD_SVG, 
                                 new Object[]{srcURL.toString()}))
                        });
                        report.setPassed(false);
                        return report;
                    }
                }

                try { renderMonitor.wait(); }
                catch(InterruptedException ie) { /* nothing */ }
                if (abort || renderFailed) {
                        report.setErrorCode(ERROR_SVG_RENDER_FAILED);
                        report.setDescription(new TestReport.Entry[] { 
                            new TestReport.Entry
                            (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                             fmt(ERROR_SVG_RENDER_FAILED, 
                                 new Object[]{srcURL.toString()}))
                        });
                    report.setPassed(false);
                    return report;
                }

                updateManager = canvas.getUpdateManager();
                if (updateManager == null) {
                    // Not dynamic?  Just use image..
                    theImage = copyImage(canvas.getOffScreen());
                } else {
                    updateManager.addUpdateManagerListener
                        (new UpdateRenderListener());
                    done = false;
                    updateManager.getUpdateRunnableQueue().invokeLater
                        (new Runnable() {
                                UpdateManager um = updateManager;
                                public void run() {
                                    ScriptingEnvironment scriptEnv;
                                    scriptEnv = um.getScriptingEnvironment();
                                    Interpreter interp;
                                    interp    = scriptEnv.getInterpreter();
                                    interp.bindObject(REGARD_TEST_INSTANCE, 
                                                      JSVGRenderingAccuracyTest.this);
                                    try {
                                        interp.evaluate(REGARD_START_SCRIPT);
                                    } catch (InterpreterException ie) {
                                        // Could not wait if no start script.
                                    }
                                }
                            });
                                                                
                    while (theImage == null) {
                        try { renderMonitor.wait(); }
                        catch (InterruptedException ie) { /* nothing */ }
                        if (abort || renderFailed) {
                            report.setErrorCode(ERROR_SVG_UPDATE_FAILED);
                            report.setDescription(new TestReport.Entry[] { 
                                new TestReport.Entry
                                (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                                 fmt(ERROR_SVG_UPDATE_FAILED, 
                                     new Object[]{srcURL.toString()}))
                            });
                            report.setPassed(false);
                            return report;
                        }
                    }
                }
                try {
                    saveImage(theImage, fos);
                } catch (IOException ioe) {
                    StringWriter trace = new StringWriter();
                    ioe.printStackTrace(new PrintWriter(trace));
                    report.setErrorCode(ERROR_SAVE_FAILED);
                    report.setDescription(new TestReport.Entry[] { 
                        new TestReport.Entry
                        (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                         fmt(ERROR_SAVE_FAILED, 
                             new Object[]{ srcURL.toString(),
                                           trace.toString()}))
                    });
                    report.setPassed(false);
                    return report;
                }
            }
        } finally {
            dispose();
        }
        report.setPassed(true);
        return report;
    }

    public void dispose() {
        frame.removeWindowListener(wl);
        wl = null;
        if (frame != null) 
            frame.setVisible(false);
        theImage = null;
        updateManager = null;
        canvas = null;
        frame = null;
    }

    /**
     * Method subclasses can implement to do more sophisticated
     * initialization of the canvas.
     */
    protected void initCanvas(JSVGCanvas canvas, URL srcURL) {
        canvas.setURI(srcURL.toString());
    }

    public void scriptDone() {
        synchronized (renderMonitor) {
            done = true;
            // Don't notify.  The Update Complete will notify - and
            // provide us with the 'up to date' image for comparison.
        }
    }

    public static BufferedImage copyImage(BufferedImage bi) {
        // Copy off the image just rendered.
        BufferedImage ret;
        ret = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        bi.copyData(ret.getRaster());
        return ret;
    }

    class UpdateRenderListener implements UpdateManagerListener {
        public void updateCompleted(UpdateManagerEvent e) {
            synchronized(renderMonitor){
                renderFailed = false;
                if (done) {
                    theImage = copyImage(e.getImage());
                }
                renderMonitor.notifyAll();
            }
        }
        public void updateFailed(UpdateManagerEvent e) {
            synchronized(renderMonitor){
                renderFailed = true;
                renderMonitor.notifyAll();
            }
        }
        public void managerStarted(UpdateManagerEvent e) { }
        public void managerSuspended(UpdateManagerEvent e) { }
        public void managerResumed(UpdateManagerEvent e) { }
        public void managerStopped(UpdateManagerEvent e) { }
        public void updateStarted(UpdateManagerEvent e) { }
    }

    class InitialRenderListener extends GVTTreeRendererAdapter {
        public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            synchronized(renderMonitor){
                renderFailed = false;
                if (done) {
                    theImage = copyImage(e.getImage());
                }
                renderMonitor.notifyAll();
            }
        }


        public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
            synchronized(renderMonitor){
                renderMonitor.notifyAll();
            }
        }

        public void gvtRenderingFailed(GVTTreeRendererEvent e) {
            synchronized(renderMonitor){
                renderMonitor.notifyAll();
            }
        }
    }

    class LoadListener extends SVGDocumentLoaderAdapter {
        public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            synchronized(loadMonitor){
                loadFailed = false;
                loadMonitor.notifyAll();
            }
        }

        public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
            synchronized(loadMonitor){
                loadMonitor.notifyAll();
            }
        }

        public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
            synchronized(loadMonitor){
                loadMonitor.notifyAll();
            }
        }
    }

};
