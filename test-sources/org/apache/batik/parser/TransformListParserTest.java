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
 * To test the transform list parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TransformListParserTest extends AbstractTest {

    protected String sourceTransform;
    protected String destinationTransform;

    protected StringBuffer buffer;
    protected String resultTransform;

    /**
     * Creates a new TransformListParserTest.
     * @param stransform The transform to parse.
     * @param dtransform The transform after serialization.
     */
    public TransformListParserTest(String stransform, String dtransform) {
        sourceTransform = stransform;
        destinationTransform = dtransform;
    }

    public TestReport runImpl() throws Exception {
        TransformListParser pp = new TransformListParser();
        pp.setTransformListHandler(new TestHandler());

        try {
            pp.parse(new StringReader(sourceTransform));
        } catch (ParseException e) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("parse.error");
            report.addDescriptionEntry("exception.text", e.getMessage());
            report.setPassed(false);
            return report;
        }

        if (!destinationTransform.equals(resultTransform)) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("invalid.parsing.events");
            report.addDescriptionEntry("expected.text", destinationTransform);
            report.addDescriptionEntry("generated.text", resultTransform);
            report.setPassed(false);
            return report;
        }

        return reportSuccess();
    }

    class TestHandler extends DefaultTransformListHandler {
        boolean first;
        public TestHandler() {}
        public void startTransformList() throws ParseException {
            buffer = new StringBuffer();
            first = true;
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
            resultTransform = buffer.toString();
        }
    }
}
