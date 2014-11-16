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

/**
 * To test the length parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParserFailureTestCase {

    @Test(expected=ParseException.class)
    public void testLengthParserFail1() throws Exception {
        testLengthParserFailure("123.456.7");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail2() throws Exception {
        testLengthParserFailure("1e+");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail3() throws Exception {
        testLengthParserFailure("+e3");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail4() throws Exception {
        testLengthParserFailure("1Em");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail5() throws Exception {
        testLengthParserFailure("--1");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail6() throws Exception {
        testLengthParserFailure("-1E--2");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail7() throws Exception {
        testLengthParserFailure("-.E+1");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail8() throws Exception {
        testLengthParserFailure("-.0EE+1");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail9() throws Exception {
        testLengthParserFailure("1Eem");
    }

    @Test(expected=ParseException.class)
    public void testLengthParserFail10() throws Exception {
        testLengthParserFailure("1em%");
    }

    private void testLengthParserFailure(String length) throws Exception {
        LengthParser pp = new LengthParser();
        pp.parse(new StringReader(length));
    }
}
