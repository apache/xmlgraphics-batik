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

import java.awt.EventQueue;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.w3c.dom.svg.SVGDocument;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

/**
 * Test setDocument on JSVGComponent with non-Batik SVGOMDocument.
 *
 * This test constructs a generic Document with SVG content then it
 * ensures that when this is passed to JSVGComponet.setDocument it is
 * properly imported to an SVGOMDocument and rendered from there.
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class NullSetSVGDocumentTest extends JSVGMemoryLeakTest {
    public NullSetSVGDocumentTest() {
    }

    public static final String TEST_NON_NULL_URI
        = "file:samples/anne.svg";

    /**
     * Entry describing the error
     */
    public static final String ENTRY_KEY_ERROR_DESCRIPTION 
        = "JSVGCanvasHandler.entry.key.error.description";

    /**
     * Entry describing the error
     */
    public static final String ERROR_IMAGE_NOT_CLEARED 
        = "NullSetSVGDocumentTest.message.error.image.not.cleared";

    public static final String ERROR_ON_SET 
        = "NullSetSVGDocumentTest.message.error.on.set";

    public String getName() { return getId(); }

    public JSVGCanvasHandler createHandler() {
        return new JSVGCanvasHandler(this, this) {
                public JSVGCanvas createCanvas() { 
                    return new JSVGCanvas() {
                            protected void installSVGDocument(SVGDocument doc){
                                super.installSVGDocument(doc);
                                if (doc != null) return;
                                handler.scriptDone();
                            }
                        };
                }
            };
    }

    /* JSVGCanvasHandler.Delegate Interface */
    public boolean canvasInit(JSVGCanvas canvas) {
        theCanvas = canvas;
        theFrame  = handler.getFrame();

        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        canvas.setURI(TEST_NON_NULL_URI);

        registerObjectDesc(canvas, "JSVGCanvas");
        registerObjectDesc(handler.getFrame(), "JFrame");
        return true; // We did trigger a load event.
    }

    public void canvasRendered(JSVGCanvas canvas) {
        super.canvasRendered(canvas);
        final JSVGCanvas c = canvas;
        try {
            EventQueue.invokeAndWait(new Runnable () {
                    public void run() {
                        c.setSVGDocument(null);
                    }});
        } catch (Throwable t) {
            t.printStackTrace();
            StringWriter trace = new StringWriter();
            t.printStackTrace(new PrintWriter(trace));
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(ERROR_ON_SET);
            report.setDescription(new TestReport.Entry[] { 
                new TestReport.Entry
                (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                 fmt(ERROR_ON_SET, new Object[]{ trace.toString()}))
            });
            report.setPassed(false);
            failReport = report;
        }
    }

    public boolean canvasUpdated(JSVGCanvas canvas) {
        return true;
    }


    public void canvasDone(JSVGCanvas canvas) {
        synchronized (this) {
            // Check that the original SVG
            // Document and GVT tree are cleared.
            checkObjects(new String[] { "SVGDoc", "GVT", "updateManager" });

            if (canvas.getOffScreen() == null)
                return;
            System.err.println(">>>>>>> Canvas not cleared");
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(ERROR_IMAGE_NOT_CLEARED);
            // It would be great to provide the image here
            // but it's a lot of work and this isn't _really_
            // what we are testing.  More testing that
            // everything works (no exceptions thrown).
            report.setDescription(new TestReport.Entry[] { 
                new TestReport.Entry
                (fmt(ENTRY_KEY_ERROR_DESCRIPTION, null),
                 fmt(ERROR_IMAGE_NOT_CLEARED, null))});
            report.setPassed(false);
            failReport = report;
            return;
        }
    }
}
