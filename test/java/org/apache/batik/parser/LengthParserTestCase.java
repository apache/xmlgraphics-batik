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
 * To test the length parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParserTestCase {

    @Test
    public void testLengthParser1() throws Exception {
        testLength("123.456", "123.456");
    }

    @Test
    public void testLengthParser2() throws Exception {
        testLength("123em", "123.0em");
    }

    @Test
    public void testLengthParser3() throws Exception {
        testLength(".456ex", "0.456ex");
    }

    @Test
    public void testLengthParser4() throws Exception {
        testLength("-.456789in", "-0.456789in");
    }

    @Test
    public void testLengthParser5() throws Exception {
        testLength("-456789.cm", "-456789.0cm");
    }

    @Test
    public void testLengthParser6() throws Exception {
        testLength("-4567890.mm", "-4567890.0mm");
    }

    @Test
    public void testLengthParser7() throws Exception {
        testLength("-000456789.pc", "-456789.0pc");
    }

    @Test
    public void testLengthParser8() throws Exception {
        testLength("-0.00456789pt", "-0.00456789pt");
    }

    @Test
    public void testLengthParser9() throws Exception {
        testLength("-0px", "0.0px");
    }

    @Test
    public void testLengthParser10() throws Exception {
        testLength("0000%", "0.0%");
    }

    private void testLength(String length, String expected) throws Exception {
        LengthParser pp = new LengthParser();
        StringBuffer results = new StringBuffer();
        pp.setLengthHandler(new TestHandler(results));
        pp.parse(new StringReader(length));
        assertEquals(null, expected, results.toString());
    }

    private static class TestHandler extends DefaultLengthHandler {
        
        private StringBuffer buffer;

        public TestHandler(StringBuffer buffer) {
            this.buffer = buffer;
        }

        public void startLength() throws ParseException {
            buffer.setLength(0);
        }
        
        public void lengthValue(float v) throws ParseException {
            buffer.append(v);
        }

        public void em() throws ParseException {
            buffer.append("em");
        }

        public void ex() throws ParseException {
            buffer.append("ex");
        }

        public void in() throws ParseException {
            buffer.append("in");
        }

        public void cm() throws ParseException {
            buffer.append("cm");
        }

        public void mm() throws ParseException {
            buffer.append("mm");
        }

        public void pc() throws ParseException {
            buffer.append("pc");
        }

        public void pt() throws ParseException {
            buffer.append("pt");
        }

        public void px() throws ParseException {
            buffer.append("px");
        }

        public void percentage() throws ParseException {
            buffer.append("%");
        }

        public void endLength() throws ParseException {
        }
    }
}
