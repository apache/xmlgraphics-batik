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

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.text.Mark;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.util.Base64Test;


/**
 * This test validates that the text selection API's work properly.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class TextSelectionTest extends AbstractTest {

    /**
     * Directory for reference files
     */
    public static final String REFERENCE_DIR
        = "test-references/org/apache/batik/gvt/";

    public static final String VARIATION_DIR
        = "variation/";

    public static final String CANDIDATE_DIR
        = "candidate/";


    /**
     * Error when unable to load requested SVG file
     * {0} = file
     * {1} = exception
     */
    public static final String ERROR_READING_SVG
        = "TextSelectionTest.error.reading.svg";

    /**
     * Error id doesn't reference an element
     * {0} = id
     */
    public static final String ERROR_BAD_ID
        = "TextSelectionTest.error.bad.id";

    /**
     * Error id doesn't reference a text element
     * {0} = id
     * {1} = element referenced
     */
    public static final String ERROR_ID_NOT_TEXT
        = "TextSelectionTest.error.id.not.text";

    /**
     * Error couldn't get selection highlight specified.
     * {0} = id
     * {1} = start index
     * {2} = end index
     * {3} = exception
     */
    public static final String ERROR_GETTING_SELECTION
        = "TextSelectionTest.error.getting.selection";

    /**
     * Error when unable to read/open ref URL
     * {0} = URL
     * {1} = exception stack trace.
     */
    public static final String ERROR_CANNOT_READ_REF_URL
        = "TextSelectionTest.error.cannot.read.ref.url";

    /**
     * Result didn't match reference result.
     * {0} = first byte of mismatch
     */
    public static final String ERROR_WRONG_RESULT
        = "TextSelectionTest.error.wrong.result";

    /**
     * No Reference or Variation file to compaire with.
     * {0} = reference url
     */
    public static final String ERROR_NO_REFERENCE
        = "TextSelectionTest.error.no.reference";


    public static final String ENTRY_KEY_ERROR_DESCRIPTION
        = "TextSelectionTest.entry.key.error.description";

    protected URL    svg   = null;
    protected String id    = null;
    protected int    start;
    protected int    end;
    protected URL    ref   = null;
    protected URL    var   = null;
    protected File   can   = null;

    /**
     * Constructor. ref is ignored if action == ROUND.
     * @param svg    The svg file to load
     * @param id     The element to select text from (must be a <text> element)
     * @param start  The first character to select
     * @param end    The last character to select
     * @param ref    The reference file.
     */
    public TextSelectionTest(String svg, String id, 
                             Integer start, Integer end, 
                             String ref) {
        this.svg   = resolveURL(svg);
        this.id    = id;
        this.start = start.intValue();
        this.end   = end.intValue();
        this.ref   = resolveURL(REFERENCE_DIR+ref);
        this.var   = resolveURL(REFERENCE_DIR+VARIATION_DIR+ref);
        this.can   = new File(REFERENCE_DIR+CANDIDATE_DIR+ref);
    }

    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    protected URL resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if(f.getParentFile().exists()){
            try{
                return f.toURL();
            }catch(MalformedURLException e){
                throw new IllegalArgumentException();
            }
        }
        
        // url is not a file. It must be a regular URL...
        try{
            return new URL(url);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(url);
        }
    }

    /**
     * Returns this Test's name
     */
    public String getName() {
        return svg + "#" + id + "(" + start + "," + end + ")";
    }


    /**
     * This method will only throw exceptions if some aspect
     * of the test's internal operation fails.
     */
    public TestReport runImpl() throws Exception {
        DefaultTestReport report = new DefaultTestReport(this);

        SVGDocument  svgDoc;
        BridgeContext  ctx;
        try {
            UserAgent      userAgent = new UserAgentAdapter();
            DocumentLoader loader    = new DocumentLoader(userAgent);
            GVTBuilder     builder   = new GVTBuilder();

            ctx     = new BridgeContext(userAgent, loader);
            ctx.setDynamic(true);
            svgDoc  = (SVGDocument)loader.loadDocument(svg.toString());
            builder.build(ctx, svgDoc);
        } catch(Exception e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            report.setErrorCode(ERROR_READING_SVG);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage
                     (ERROR_READING_SVG,
                      new String[]{svg.toString(), trace.toString()}))
                    });
            report.setPassed(false);
            return report;
        }

        Shape highlight = null;
        try {
            Element e = svgDoc.getElementById(id);
            /// System.out.println("Element: " + e + " CTX: " + ctx );
            GraphicsNode gn = ctx.getGraphicsNode(e);
            if (gn == null) {
                report.setErrorCode(ERROR_BAD_ID);
                report.setDescription(new TestReport.Entry[] {
                    new TestReport.Entry
                        (Messages.formatMessage
                         (ENTRY_KEY_ERROR_DESCRIPTION, null),
                         Messages.formatMessage
                         (ERROR_BAD_ID, new String[]{ id }))
                        });
                report.setPassed(false);
                return report;
            }

            if (!(gn instanceof TextNode)) {
                report.setErrorCode(ERROR_ID_NOT_TEXT);
                report.setDescription(new TestReport.Entry[] {
                    new TestReport.Entry
                        (Messages.formatMessage
                         (ENTRY_KEY_ERROR_DESCRIPTION, null),
                         Messages.formatMessage
                         (ERROR_ID_NOT_TEXT, new String[]{id, gn.toString()}))
                        });
                report.setPassed(false);
                return report;
            }


            TextNode tn = (TextNode)gn;
            Mark f = tn.getMarkerForChar(start,true);
            Mark l = tn.getMarkerForChar(end,false);
            tn.setSelection(f, l);
            highlight = tn.getHighlightShape();
        } catch(Exception e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            report.setErrorCode(ERROR_GETTING_SELECTION);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage
                     (ERROR_GETTING_SELECTION,
                      new String[]{id, ""+start, ""+end, trace.toString()}))
                    });
            report.setPassed(false);
            return report;
        }

        InputStream refIS = null;
        try {
            refIS = var.openStream();
        } catch(Exception e) { try {
            refIS = ref.openStream();
        } catch(Exception ex) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            report.setErrorCode(ERROR_CANNOT_READ_REF_URL);
            report.setDescription
                (new TestReport.Entry[] {
                    new TestReport.Entry
                        (Messages.formatMessage
                         (ENTRY_KEY_ERROR_DESCRIPTION, null),
                         Messages.formatMessage
                         (ERROR_CANNOT_READ_REF_URL,
                          new String[]{ref.toString(), trace.toString()}))
                        });
            report.setPassed(false);
        }
        }

        int mismatch = -2;
        if (refIS != null) {
            PipedOutputStream pos  = new PipedOutputStream();
            InputStream       inIS = new PipedInputStream(pos);
            Checker check = new Checker(inIS, refIS);
            check.start();
            PrintStream pw = new PrintStream(pos);
            printShape(highlight, pw);
            pw.flush();
            pw.close();
            pos.close();
            mismatch = check.getMismatch();

        }

        if (mismatch == -1) {
          report.setPassed(true);
          return report;
        }

        if (mismatch == -2) {
            report.setErrorCode(ERROR_NO_REFERENCE);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage(ERROR_NO_REFERENCE, 
                                            new String[]{ref.toString()}))
                    });
        } else {
            report.setErrorCode(ERROR_WRONG_RESULT);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage(ERROR_WRONG_RESULT, 
                                            new String[]{""+mismatch}))
                    });
        }
        report.setPassed(false);

        // Now write a canidate reference/variation file...
        if (can.exists())
            can.delete();

        printShape(highlight, new PrintStream(new FileOutputStream(can)));

        return report;
    }

    public static class Checker extends Thread {
        int mismatch = -2;
        InputStream is1, is2;
        public Checker(InputStream is1, InputStream is2) {
            this.is1 = is1;
            this.is2 = is2;
        }
        public int getMismatch() {
            while (true) {
                try {
                    this.join();
                    break;
                } catch (InterruptedException ie) { }
            }

            return mismatch;
        }
        public void run() {
            mismatch = Base64Test.compareStreams (is1, is2, false);
        }
    }

    public static void printShape(Shape s, PrintStream ps) {
        PathIterator pi = s.getPathIterator(null);
        float pts [] = new float[6];
        int type;
        while (!pi.isDone()) {
            type = pi.currentSegment(pts);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                ps.println(" MoveTo: [" + 
                                   pts[0] + ", " + pts[1] + "]");
                break;
            case PathIterator.SEG_LINETO:
                ps.println(" LineTo: [" + 
                                   pts[0] + ", " + pts[1] + "]");
                break;

            case PathIterator.SEG_QUADTO:
                ps.println(" QuadTo: [" + 
                                   pts[0] + ", " + pts[1] + "] [" +
                                   pts[2] + ", " + pts[3] + "]");
                break;

            case PathIterator.SEG_CUBICTO:
                ps.println("CurveTo: [" + 
                                   pts[0] + ", " + pts[1] + "] [" +
                                   pts[2] + ", " + pts[3] + "] [" +
                                   pts[4] + ", " + pts[5] + "]");
                break;

            case PathIterator.SEG_CLOSE:
                ps.println("Close");
                break;
            }
            pi.next();
        }
    }


}

