/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.Test;
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

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class JSVGCanvasHandler {

    public interface Delegate {
        public String getName();
        public void canvasInit(JSVGCanvas canvas);
        public void canvasLoaded(JSVGCanvas canvas);
        public void canvasRendered(JSVGCanvas canvas);
        public boolean canvasUpdated(JSVGCanvas canvas);
        public void canvasDone(JSVGCanvas canvas);
        public void failure(TestReport report);
    }
    
    public static final String REGARD_TEST_INSTANCE = "regardTestInstance";
    public static final String REGARD_START_SCRIPT = 
        "try { regardStart(); } catch (er) {}";

    /**
     * Error when canvas can't load SVG file.
     * {0} The file/url that could not be loaded.
     */
    public static final String ERROR_CANNOT_LOAD_SVG = 
        "JSVGCanvasHandler.message.error.could.not.load.svg";

    /**
     * Error when canvas can't render SVG file.
     * {0} The file/url that could not be rendered.
     */
    public static final String ERROR_SVG_RENDER_FAILED = 
        "JSVGCanvasHandler.message.error.svg.render.failed";

    /**
     * Error when canvas can't peform render update SVG file.
     * {0} The file/url that could not be updated..
     */
    public static final String ERROR_SVG_UPDATE_FAILED = 
        "JSVGCanvasHandler.message.error.svg.update.failed";

    /**
     * Entry describing the error
     */
    public static final String ENTRY_KEY_ERROR_DESCRIPTION 
        = "JSVGCanvasHandler.entry.key.error.description";

    public static String fmt(String key, Object []args) {
        return TestMessages.formatMessage(key, args);
    }

    JFrame     frame = null;
    JSVGCanvas canvas = null;
    UpdateManager updateManager = null;
    WindowListener wl = null;
    InitialRenderListener irl = null;
    LoadListener ll = null;
    UpdateRenderListener url = null;
    
    boolean renderFailed;
    boolean loadFailed;
    boolean abort;
    Object loadMonitor = new Object();
    Object renderMonitor = new Object();
    
    Delegate delegate;
    Test host;

    public JSVGCanvasHandler(Test host, Delegate delegate) {
        this.host     = host;
        this.delegate = delegate;
    }

    public JFrame getFrame()     { return frame; }
    public JSVGCanvas getCanvas() { return canvas; }

    public void runCanvas(String desc) {
        DefaultTestReport report = new DefaultTestReport(host);
        loadFailed = true;
        renderFailed = true;
        
        frame = new JFrame(delegate.getName());
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

        irl = new InitialRenderListener();
        canvas.addGVTTreeRendererListener(irl);
        ll = new LoadListener();
        canvas.addSVGDocumentLoaderListener(ll);
        try {
            
            delegate.canvasInit(canvas);
            
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
                                 new Object[]{desc}))
                        });
                        report.setPassed(false);
                        delegate.failure(report);
                        return;
                    }
                    delegate.canvasLoaded(canvas);
                }

                try { renderMonitor.wait(); }
                catch(InterruptedException ie) { /* nothing */ }
                if (abort || renderFailed) {
                    report.setErrorCode(ERROR_SVG_RENDER_FAILED);
                    report.setDescription(new TestReport.Entry[] { 
                        new TestReport.Entry
                        (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                         fmt(ERROR_SVG_RENDER_FAILED, 
                             new Object[]{desc}))
                    });
                    report.setPassed(false);
                    delegate.failure(report);
                    return;
                }
                delegate.canvasRendered(canvas);

                updateManager = canvas.getUpdateManager();
                if (updateManager != null) {
                    url = new UpdateRenderListener();
                    updateManager.addUpdateManagerListener(url);
                    updateManager.getUpdateRunnableQueue().invokeLater
                        (new Runnable() {
                                UpdateManager um = updateManager;
                                public void run() {
                                    ScriptingEnvironment scriptEnv;
                                    scriptEnv = um.getScriptingEnvironment();
                                    Interpreter interp;
                                    interp    = scriptEnv.getInterpreter();
                                    interp.bindObject(REGARD_TEST_INSTANCE, 
                                                      host);
                                    try {
                                        interp.evaluate(REGARD_START_SCRIPT);
                                    } catch (InterpreterException ie) {
                                        // Could not wait if no start script.
                                    }
                                }
                            });

                    boolean done = false;
                    while (!done) {
                        try { renderMonitor.wait(); }
                        catch (InterruptedException ie) { /* nothing */ }
                        if (abort || renderFailed) {
                            report.setErrorCode(ERROR_SVG_UPDATE_FAILED);
                            report.setDescription(new TestReport.Entry[] { 
                                new TestReport.Entry
                                (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                                 fmt(ERROR_SVG_UPDATE_FAILED, 
                                     new Object[]{desc}))
                            });
                            report.setPassed(false);
                            delegate.failure(report);
                            return;
                        }
                        done = delegate.canvasUpdated(canvas);
                    }
                }
            }
        } finally {
            delegate.canvasDone(canvas);
            dispose();
        }
    }
    
    public void dispose() {
        if (frame != null) {
            frame.removeWindowListener(wl);
            frame.setVisible(false);
        }
        wl = null;
        if (canvas != null) {
            canvas.removeGVTTreeRendererListener(irl);  irl=null;
            canvas.removeSVGDocumentLoaderListener(ll); ll=null;
            canvas.removeUpdateManagerListener(url);    url=null;
        }
        updateManager = null;
        canvas = null;
        frame = null;
    }
    
    class UpdateRenderListener implements UpdateManagerListener {
        public void updateCompleted(UpdateManagerEvent e) {
            synchronized(renderMonitor){
                renderFailed = false;
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
}
