/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.parser;

import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To test the path parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParserTestCase {

    @Test
    public void testPathParser1() throws Exception {
        testPath("M1 2", "M1.0 2.0");
    }

    @Test
    public void testPathParser2() throws Exception {
        testPath("m1.1 2.0", "m1.1 2.0");
    }

    @Test
    public void testPathParser3() throws Exception {
        testPath("M1 2z", "M1.0 2.0Z");
    }

    @Test
    public void testPathParser4() throws Exception {
        testPath("M1 2e3Z", "M1.0 2000.0Z");
    }

    @Test
    public void testPathParser5() throws Exception {
        testPath("M1 2L 3,4", "M1.0 2.0L3.0 4.0");
    }

    @Test
    public void testPathParser5_1() throws Exception {
        testPath("M1 2 3,4", "M1.0 2.0L3.0 4.0");
    }

    @Test
    public void testPathParser5_2() throws Exception {
        testPath("M1, 2, 3,4", "M1.0 2.0L3.0 4.0");
    }

    @Test
    public void testPathParser5_3() throws Exception {
        testPath("m1, 2, 3,4", "m1.0 2.0l3.0 4.0");
    }

    @Test
    public void testPathParser6() throws Exception {
        testPath("M1 2H3.1", "M1.0 2.0H3.1");
    }

    @Test
    public void testPathParser6_1() throws Exception {
        testPath("M1 2H3.1 4", "M1.0 2.0H3.1H4.0");
    }

    @Test
    public void testPathParser6_2() throws Exception {
        testPath("M1 2H3.1,4", "M1.0 2.0H3.1H4.0");
    }

    @Test
    public void testPathParser7() throws Exception {
        testPath("M1 2h 3.1", "M1.0 2.0h3.1");
    }

    @Test
    public void testPathParser7_1() throws Exception {
        testPath("M1 2h 3.1 4", "M1.0 2.0h3.1h4.0");
    }

    @Test
    public void testPathParser7_2() throws Exception {
        testPath("M1 2h 3.1,4", "M1.0 2.0h3.1h4.0");
    }

    @Test
    public void testPathParser8() throws Exception {
        testPath("M1 2H 3.1,4", "M1.0 2.0H3.1H4.0");
    }

    @Test
    public void testPathParser9() throws Exception {
        testPath("M1 2h 3.1-4", "M1.0 2.0h3.1h-4.0");
    }

    @Test
    public void testPathParser10() throws Exception {
        testPath("M1 2V3.1e-3", "M1.0 2.0V0.0031");
    }

    @Test
    public void testPathParser11() throws Exception {
        testPath("M1 2V3.1", "M1.0 2.0V3.1");
    }

    @Test
    public void testPathParser12() throws Exception {
        testPath("M1 2v3.1,.4", "M1.0 2.0v3.1v0.4");
    }

    @Test
    public void testPathParser13() throws Exception {
        testPath("M1 2v3.1-.4", "M1.0 2.0v3.1v-0.4");
    }

    @Test
    public void testPathParser14() throws Exception {
        testPath("M1 2C3 4 5 6 7 8", "M1.0 2.0C3.0 4.0 5.0 6.0 7.0 8.0");
    }

    @Test
    public void testPathParser15() throws Exception {
        testPath("M1 2c.3.4.5.6.7.8", "M1.0 2.0c0.3 0.4 0.5 0.6 0.7 0.8");
    }

    @Test
    public void testPathParser16() throws Exception {
        testPath("M1 2S3+4+5+6", "M1.0 2.0S3.0 4.0 5.0 6.0");
    }

    @Test
    public void testPathParser17() throws Exception {
        testPath("M1 2s.3+.4+.5-.6", "M1.0 2.0s0.3 0.4 0.5 -0.6");
    }

    @Test
    public void testPathParser18() throws Exception {
        testPath("M1 2q3. 4.+5 6", "M1.0 2.0q3.0 4.0 5.0 6.0");
    }

    @Test
    public void testPathParser19() throws Exception {
        testPath("M1 2Q.3e0.4.5.6", "M1.0 2.0Q0.3 0.4 0.5 0.6");
    }

    @Test
    public void testPathParser20() throws Exception {
        testPath("M1 2t+.3-.4", "M1.0 2.0t0.3 -0.4");
    }

    @Test
    public void testPathParser21() throws Exception {
        testPath("M1 2T -.3+4", "M1.0 2.0T-0.3 4.0");
    }

    @Test
    public void testPathParser22() throws Exception {
        testPath("M1 2a3 4 5 0,1 6 7", "M1.0 2.0a3.0 4.0 5.0 0 1 6.0 7.0");
    }

    @Test
    public void testPathParser23() throws Exception {
        testPath("M1 2A3 4 5 0,1 6 7", "M1.0 2.0A3.0 4.0 5.0 0 1 6.0 7.0");
    }

    @Test
    public void testPathParser24() throws Exception {
        testPath("M1 2t+.3-.4,5,6", "M1.0 2.0t0.3 -0.4t5.0 6.0");
    }

    @Test
    public void testPathParser25() throws Exception {
        testPath("M1 2T -.3+4 5-6", "M1.0 2.0T-0.3 4.0T5.0 -6.0");
    }

    private void testPath(String path, String expected) throws Exception {
        PathParser pp = new PathParser();
        StringBuffer results = new StringBuffer();
        pp.setPathHandler(new TestHandler(results));
        pp.parse(new StringReader(path));
        assertEquals(null, expected, results.toString());
    }

    private static class TestHandler extends DefaultPathHandler {

        private StringBuffer buffer;

        public TestHandler(StringBuffer buffer) {
            this.buffer = buffer;
        }

        public void startPath() throws ParseException {
            buffer.setLength(0);
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
