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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGTextContentElement;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGCanvasHandler;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.svg.JSVGRenderingAccuracyTest;


/**
 * This test validates that the text selection API's work properly.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class TextSelectionTest extends JSVGRenderingAccuracyTest {

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

    protected String textID    = null;
    protected int    start;
    protected int    end;

    public void setId(String id) { this.id = id; }

    /**
     * Constructor. ref is ignored if action == ROUND.
     * @param svg    The svg file to load
     * @param id     The element to select text from (must be a <text> element)
     * @param start  The first character to select
     * @param end    The last character to select
     * @param ref    The reference file.
     */
    public TextSelectionTest(String file,   String textID, 
                             Integer start, Integer end) {
        this.textID    = textID;
        this.start = start.intValue();
        this.end   = end.intValue();
        super.setFile(file);
    }

    protected String buildRefImgURL(String svgDir, String svgFile){
        return getRefImagePrefix() + svgDir + getRefImageSuffix() + 
            svgFile + "-" +textID+ "-" + start + "-" + end +PNG_EXTENSION;
    }

    public String buildVariationURL(String svgDir, String svgFile){
        return getVariationPrefix() + svgDir + getVariationSuffix() + 
            svgFile + "-" +textID+ "-" + start + "-" + end +PNG_EXTENSION;

    }

    public String  buildSaveVariationFile(String svgDir, String svgFile){
        return getSaveVariationPrefix() + svgDir + getSaveVariationSuffix() + 
            svgFile + "-" +textID+ "-" + start + "-" + end +PNG_EXTENSION;
    }

    public String  buildCandidateReferenceFile(String svgDir, String svgFile){
        return getCandidateReferencePrefix() + svgDir + getCandidateReferenceSuffix() + 
            svgFile + "-" +textID+ "-" + start + "-" + end +PNG_EXTENSION;
    }
    /**
     * Returns this Test's name
     */
    public String getName() {
        return super.getName() + "#" +textID+ "(" + start + "," + end + ")";
    }
    
    public JSVGCanvasHandler createCanvasHandler() {
        return new JSVGCanvasHandler(this, this) {
                public JSVGCanvas createCanvas() { 
                    JSVGCanvas ret = new JSVGCanvas(); 
                    ret.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
                    return ret;
                }
            };
    }

    public void canvasRendered(JSVGCanvas canvas) {
        DefaultTestReport report = new DefaultTestReport(this);
        try {
            Element e = canvas.getSVGDocument().getElementById(textID);
            if (e == null) {
                report.setErrorCode(ERROR_BAD_ID);
                report.setDescription(new TestReport.Entry[] {
                    new TestReport.Entry
                        (Messages.formatMessage
                         (ENTRY_KEY_ERROR_DESCRIPTION, null),
                         Messages.formatMessage
                         (ERROR_BAD_ID, new String[]{textID}))
                        });
                report.setPassed(false);
                failReport = report;
                return;
            }
            if (!(e instanceof SVGTextContentElement)) {
                report.setErrorCode(ERROR_ID_NOT_TEXT);
                report.setDescription(new TestReport.Entry[] {
                    new TestReport.Entry
                        (Messages.formatMessage
                         (ENTRY_KEY_ERROR_DESCRIPTION, null),
                         Messages.formatMessage
                         (ERROR_ID_NOT_TEXT, new String[]{id, e.toString()}))
                        });
                report.setPassed(false);
                failReport = report;
                return;
            }
            SVGTextContentElement tce = (SVGTextContentElement)e;
            tce.selectSubString(start, end);
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
            failReport = report;
        }
        finally {
            scriptDone();
        }
    }
}

