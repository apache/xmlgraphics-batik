/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.batik.parser;

import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To test the transform list parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TransformListParserTestCase {

    @Test
    public void testTransformParser1() throws Exception {
        testTransformList("matrix(1 2 3 4 5 6)", "matrix(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)");
    }

    @Test
    public void testTransformParser2() throws Exception {
        testTransformList("translate(1)", "translate(1.0)");
    }

    @Test
    public void testTransformParser3() throws Exception {
        testTransformList("translate(1e2 3e4)", "translate(100.0, 30000.0)");
    }

    @Test
    public void testTransformParser4() throws Exception {
        testTransformList("scale(1e-2)", "scale(0.01)");
    }

    @Test
    public void testTransformParser5() throws Exception {
        testTransformList("scale(-1e-2 -3e-4)", "scale(-0.01, -3.0E-4)");
    }

    @Test
    public void testTransformParser6() throws Exception {
        testTransformList("skewX(1.234)", "skewX(1.234)");
    }

    @Test
    public void testTransformParser7() throws Exception {
        testTransformList("skewY(.1)", "skewY(0.1)");
    }

    @Test
    public void testTransformParser8() throws Exception {
        testTransformList("translate(1,2) skewY(.1)", "translate(1.0, 2.0) skewY(0.1)");
    }

    @Test
    public void testTransformParser9() throws Exception {
        testTransformList("scale(1,2),skewX(.1e1)", "scale(1.0, 2.0) skewX(1.0)");
    }

    @Test
    public void testTransformParser10() throws Exception {
        testTransformList("scale(1) , skewX(2) translate(3,4)", "scale(1.0) skewX(2.0) translate(3.0, 4.0)");
    }

    private void testTransformList(String path, String expected) throws Exception {
        TransformListParser pp = new TransformListParser();
        StringBuffer results = new StringBuffer();
        pp.setTransformListHandler(new TestHandler(results));
        pp.parse(new StringReader(path));
        assertEquals(null, expected, results.toString());
    }

    private static class TestHandler extends DefaultTransformListHandler {

        private StringBuffer buffer;
        private boolean first;

        public TestHandler(StringBuffer buffer) {
            this.buffer = buffer;
            this.first = true;
        }

        public void startTransformList() throws ParseException {
            buffer.setLength(0);
        }

        public void matrix(float a, float b, float c, float d, float e, float f)
            throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("matrix(");
            buffer.append(a);
            buffer.append(", ");
            buffer.append(b);
            buffer.append(", ");
            buffer.append(c);
            buffer.append(", ");
            buffer.append(d);
            buffer.append(", ");
            buffer.append(e);
            buffer.append(", ");
            buffer.append(f);
            buffer.append(")");
        }

        public void rotate(float theta) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
        }

        public void rotate(float theta, float cx, float cy) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
        }

        public void translate(float tx) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("translate(");
            buffer.append(tx);
            buffer.append(")");
        }

        public void translate(float tx, float ty) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("translate(");
            buffer.append(tx);
            buffer.append(", ");
            buffer.append(ty);
            buffer.append(")");
        }

        public void scale(float sx) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("scale(");
            buffer.append(sx);
            buffer.append(")");
        }

        public void scale(float sx, float sy) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("scale(");
            buffer.append(sx);
            buffer.append(", ");
            buffer.append(sy);
            buffer.append(")");
        }

        public void skewX(float skx) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("skewX(");
            buffer.append(skx);
            buffer.append(")");
        }

        public void skewY(float sky) throws ParseException {
            if (!first) {
                buffer.append(' ');
            }
            first = false;
            buffer.append("skewY(");
            buffer.append(sky);
            buffer.append(")");
        }

        public void endTransformList() throws ParseException {
        }

    }
}
