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
