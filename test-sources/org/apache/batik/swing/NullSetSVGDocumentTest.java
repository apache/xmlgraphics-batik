/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import java.awt.EventQueue;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.svg.JSVGRenderingAccuracyTest;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

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
};
