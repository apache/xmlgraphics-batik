/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
