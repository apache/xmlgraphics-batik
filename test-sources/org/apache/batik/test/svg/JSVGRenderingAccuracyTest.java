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

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.swing.JSVGCanvasHandler;

import org.apache.batik.swing.JSVGCanvas;

import java.awt.image.BufferedImage;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class JSVGRenderingAccuracyTest extends SamplesRenderingTest 
       implements JSVGCanvasHandler.Delegate {

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

    URL srcURL;
    FileOutputStream fos;
    TestReport failReport = null;
    boolean done;

    public TestReport encode(URL srcURL, FileOutputStream fos) {
        this.srcURL = srcURL;
        this.fos    = fos;

        JSVGCanvasHandler handler = new JSVGCanvasHandler(this, this);
        done = false;
        handler.runCanvas(srcURL.toString());
        
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
        canvas.setURI(srcURL.toString());
    }

    public void canvasLoaded(JSVGCanvas canvas) {
    }

    public void canvasRendered(JSVGCanvas canvas) {
    }

    public boolean canvasUpdated(JSVGCanvas canvas) {
        synchronized (this) {
            return done;
        }
    }

    public void canvasDone(JSVGCanvas canvas) {
        synchronized (this) {
            done = true;
            if (failReport != null)
                return;

            try {
                BufferedImage theImage = copyImage(canvas.getOffScreen());
                saveImage(theImage, fos);
            } catch (IOException ioe) {
                StringWriter trace = new StringWriter();
                ioe.printStackTrace(new PrintWriter(trace));
                DefaultTestReport report = new DefaultTestReport(this);
                report.setErrorCode(ERROR_SAVE_FAILED);
                report.setDescription(new TestReport.Entry[] { 
                    new TestReport.Entry
                    (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     fmt(ERROR_SAVE_FAILED, 
                         new Object[]{ srcURL.toString(),
                                       trace.toString()}))
                });
                report.setPassed(false);
                failReport = report;
            }
        }
    }

    public void failure(TestReport report) {
        synchronized (this) {
            done = true;
            failReport = report;
        }
    }

    public static BufferedImage copyImage(BufferedImage bi) {
        // Copy off the image just rendered.
        BufferedImage ret;
        ret = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        bi.copyData(ret.getRaster());
        return ret;
    }
};

