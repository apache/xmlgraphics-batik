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

package org.apache.batik.test.svg;

import java.awt.Graphics2D;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;

import java.util.List;
import java.util.Iterator;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.swing.JSVGCanvasHandler;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.Overlay;

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
    JSVGCanvasHandler handler = null;

    public TestReport encode(URL srcURL, FileOutputStream fos) {
        this.srcURL = srcURL;
        this.fos    = fos;

        handler = new JSVGCanvasHandler(this, this);
        done = false;
        handler.runCanvas(srcURL.toString());

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

    /* JSVGCanvasHandler.Delegate Interface */
    public boolean canvasInit(JSVGCanvas canvas) {
        canvas.setURI(srcURL.toString());
        return true;
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
                // Get the base image
                BufferedImage theImage = copyImage(canvas.getOffScreen());

                // Capture the overlays
                List overlays = canvas.getOverlays();

                // Paint the overlays
                Graphics2D g = theImage.createGraphics();
                Iterator it = overlays.iterator();
                while (it.hasNext()) {
                    ((Overlay)it.next()).paint(g);
                }

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
}

