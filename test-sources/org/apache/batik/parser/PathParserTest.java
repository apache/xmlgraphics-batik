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

package org.apache.batik.parser;

import java.io.*;

import org.apache.batik.test.*;

/**
 * To test the path parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParserTest extends AbstractTest {

    protected String sourcePath;
    protected String destinationPath;

    protected StringBuffer buffer;
    protected String resultPath;

    /**
     * Creates a new PathParserTest.
     * @param spath The path to parse.
     * @param dpath The path after serialization.
     */
    public PathParserTest(String spath, String dpath) {
        sourcePath = spath;
        destinationPath = dpath;
    }

    public TestReport runImpl() throws Exception {
        PathParser pp = new PathParser();
        pp.setPathHandler(new TestHandler());

        try {
            pp.parse(new StringReader(sourcePath));
        } catch (ParseException e) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("parse.error");
            report.addDescriptionEntry("exception.text", e.getMessage());
            report.setPassed(false);
            return report;
        }

        if (!destinationPath.equals(resultPath)) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("invalid.parsing.events");
            report.addDescriptionEntry("expected.text", destinationPath);
            report.addDescriptionEntry("generated.text", resultPath);
            report.setPassed(false);
            return report;
        }

        return reportSuccess();
    }

    class TestHandler extends DefaultPathHandler {
        public TestHandler() {}

        public void startPath() throws ParseException {
            buffer = new StringBuffer();
        }
        
        public void movetoRel(float x, float y) throws ParseException {
            buffer.append('m');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void movetoAbs(float x, float y) throws ParseException {
            buffer.append('M');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void endPath() throws ParseException {
            resultPath = buffer.toString();
        }

        public void closePath() throws ParseException {
            buffer.append('Z');
        }

        public void linetoRel(float x, float y) throws ParseException {
            buffer.append('l');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void linetoAbs(float x, float y) throws ParseException {
            buffer.append('L');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void linetoHorizontalRel(float x) throws ParseException {
            buffer.append('h');
            buffer.append(x);
        }

        public void linetoHorizontalAbs(float x) throws ParseException {
            buffer.append('H');
            buffer.append(x);
        }

        public void linetoVerticalRel(float y) throws ParseException {
            buffer.append('v');
            buffer.append(y);
        }

        public void linetoVerticalAbs(float y) throws ParseException {
            buffer.append('V');
            buffer.append(y);
        }

        public void curvetoCubicRel(float x1, float y1, 
                                    float x2, float y2, 
                                    float x, float y) throws ParseException {
            buffer.append('c');
            buffer.append(x1);
            buffer.append(' ');
            buffer.append(y1);
            buffer.append(' ');
            buffer.append(x2);
            buffer.append(' ');
            buffer.append(y2);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoCubicAbs(float x1, float y1, 
                                    float x2, float y2, 
                                    float x, float y) throws ParseException {
            buffer.append('C');
            buffer.append(x1);
            buffer.append(' ');
            buffer.append(y1);
            buffer.append(' ');
            buffer.append(x2);
            buffer.append(' ');
            buffer.append(y2);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoCubicSmoothRel(float x2, float y2, 
                                          float x, float y) throws ParseException {
            buffer.append('s');
            buffer.append(x2);
            buffer.append(' ');
            buffer.append(y2);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoCubicSmoothAbs(float x2, float y2, 
                                          float x, float y) throws ParseException {
            buffer.append('S');
            buffer.append(x2);
            buffer.append(' ');
            buffer.append(y2);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoQuadraticRel(float x1, float y1, 
                                        float x, float y) throws ParseException {
            buffer.append('q');
            buffer.append(x1);
            buffer.append(' ');
            buffer.append(y1);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoQuadraticAbs(float x1, float y1, 
                                        float x, float y) throws ParseException {
            buffer.append('Q');
            buffer.append(x1);
            buffer.append(' ');
            buffer.append(y1);
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoQuadraticSmoothRel(float x, float y)
            throws ParseException {
            buffer.append('t');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void curvetoQuadraticSmoothAbs(float x, float y)
            throws ParseException {
            buffer.append('T');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void arcRel(float rx, float ry, 
                           float xAxisRotation, 
                           boolean largeArcFlag, boolean sweepFlag, 
                           float x, float y) throws ParseException {
            buffer.append('a');
            buffer.append(rx);
            buffer.append(' ');
            buffer.append(ry);
            buffer.append(' ');
            buffer.append(xAxisRotation);
            buffer.append(' ');
            buffer.append(largeArcFlag ? '1' : '0');
            buffer.append(' ');
            buffer.append(sweepFlag ? '1' : '0');
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }

        public void arcAbs(float rx, float ry, 
                           float xAxisRotation, 
                           boolean largeArcFlag, boolean sweepFlag, 
                           float x, float y) throws ParseException {
            buffer.append('A');
            buffer.append(rx);
            buffer.append(' ');
            buffer.append(ry);
            buffer.append(' ');
            buffer.append(xAxisRotation);
            buffer.append(' ');
            buffer.append(largeArcFlag ? '1' : '0');
            buffer.append(' ');
            buffer.append(sweepFlag ? '1' : '0');
            buffer.append(' ');
            buffer.append(x);
            buffer.append(' ');
            buffer.append(y);
        }
    }
}
