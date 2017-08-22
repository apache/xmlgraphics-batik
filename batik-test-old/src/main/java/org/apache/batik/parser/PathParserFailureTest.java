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

import java.io.*;

import org.apache.batik.test.*;

/**
 * To test the path parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParserFailureTest extends AbstractTest {

    protected String sourcePath;

    /**
     * Creates a new PathParserFailureTest.
     * @param spath The path to parse.
     */
    public PathParserFailureTest(String spath) {
        sourcePath = spath;
    }

    public TestReport runImpl() throws Exception {
        PathParser pp = new PathParser();
        try {
            pp.parse(new StringReader(sourcePath));
        } catch (Exception e) {
            return reportSuccess();
        }
        DefaultTestReport report = new DefaultTestReport(this);
        report.setErrorCode("parse.without.error");
        report.addDescriptionEntry("input.text", sourcePath);
        report.setPassed(false);
        return report;
    }
}
