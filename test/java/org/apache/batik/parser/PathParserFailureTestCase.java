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
 * To test the path parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParserFailureTestCase {

    @Test(expected=ParseException.class)
    public void testPathParserFail1() throws Exception {
        testPathParserFailure("m 1ee2 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail2() throws Exception {
        testPathParserFailure("m 1e4e2 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail3() throws Exception {
        testPathParserFailure("m 1e+ 2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail4() throws Exception {
        testPathParserFailure("m 1 l 3 4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail5() throws Exception {
        testPathParserFailure("m 1.5.6.7 l 3 4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail6() throws Exception {
        testPathParserFailure("m 1.5,6.7,l 3 4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail7() throws Exception {
        testPathParserFailure("m 1.5,6.7,L 3 4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail8() throws Exception {
        testPathParserFailure("m 1.5,6.7,h 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail9() throws Exception {
        testPathParserFailure("m 1.5,6.7,H 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail10() throws Exception {
        testPathParserFailure("m 1.5,6.7,v 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail11() throws Exception {
        testPathParserFailure("m 1.5,6.7,V 3");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail12() throws Exception {
        testPathParserFailure("m 1.5,6.7,c 1,2 3,4 5,6");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail13() throws Exception {
        testPathParserFailure("m 1.5,6.7,C 1,2 3,4 5,6");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail14() throws Exception {
        testPathParserFailure("m 1.5,6.7,s 1,2 3,4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail15() throws Exception {
        testPathParserFailure("m 1.5,6.7,S 1,2 3,4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail16() throws Exception {
        testPathParserFailure("m 1.5,6.7,q 1,2 3,4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail17() throws Exception {
        testPathParserFailure("m 1.5,6.7,Q 1,2 3,4");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail18() throws Exception {
        testPathParserFailure("m 1.5,6.7,t 1,2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail19() throws Exception {
        testPathParserFailure("m 1.5,6.7,T 1,2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail20() throws Exception {
        testPathParserFailure("m 1.5,6.7,a 2,2 0 1 1 2 2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail21() throws Exception {
        testPathParserFailure("m 1.5,6.7,A 4,4 0 1 1 2 2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail22() throws Exception {
        testPathParserFailure("m m 1,2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail23() throws Exception {
        testPathParserFailure("M M 1,2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail24() throws Exception {
        testPathParserFailure("m 1,2 l l 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail25() throws Exception {
        testPathParserFailure("m 1,2 L L 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail26() throws Exception {
        testPathParserFailure("m 1,2 h h 3 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail27() throws Exception {
        testPathParserFailure("m 1,2 H H 3 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail28() throws Exception {
        testPathParserFailure("m 1,2 v v 3 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail29() throws Exception {
        testPathParserFailure("m 1,2 V V 3 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail30() throws Exception {
        testPathParserFailure("m 1,2 c c 1,2 3,4 5,6z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail31() throws Exception {
        testPathParserFailure("m 1,2 C C 1,2 3,4 5,6 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail32() throws Exception {
        testPathParserFailure("m 1,2 s s 1,2 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail33() throws Exception {
        testPathParserFailure("m 1,2 S S 1,2 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail34() throws Exception {
        testPathParserFailure("m 1,2 q q 1,2 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail35() throws Exception {
        testPathParserFailure("m 1,2 Q Q 1,2 3,4 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail36() throws Exception {
        testPathParserFailure("m 1,2 t t 1,2 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail37() throws Exception {
        testPathParserFailure("m 1,2 T T 1,2 z");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail38() throws Exception {
        testPathParserFailure("m 1.5,6.7 a a 2,2 0 1 1 2 2");
    }

    @Test(expected=ParseException.class)
    public void testPathParserFail39() throws Exception {
        testPathParserFailure("m 1.5,6.7 A A 4,4 0 1 1 2 2");
    }

    private void testPathParserFailure(String path) throws Exception {
        PathParser pp = new PathParser();
        pp.parse(new StringReader(path));
    }

}
