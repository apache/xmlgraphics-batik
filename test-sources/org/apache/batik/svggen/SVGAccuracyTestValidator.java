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

package org.apache.batik.svggen;

import java.awt.Graphics2D;
import java.awt.Color;

import java.net.URL;
import java.io.File;

import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestReportValidator;
import org.apache.batik.test.TestReport;

/**
 * Validates the operation of the <tt>SVGAccuractyTest</tt> class
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGAccuracyTestValidator extends DefaultTestSuite {
    /**
     * Checks that test fails if:
     * + Rendering sequence generates an exception
     * + There is no reference image
     * + Reference SVG differs from the generated SVG
     * Checks that test works if SVG and reference SVG 
     * are identical
     */
    public SVGAccuracyTestValidator(){
        addTest(new NullPainter());
        addTest(new PainterWithException());
        addTest(new NullReferenceURL());
        addTest(new InexistantReferenceURL());
        addTest(new DiffWithReferenceImage());
        addTest(new SameAsReferenceImage());
    }

    static class NullPainter extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            Painter painter = null;
            URL refURL = new URL("http",
                                 "dummyHost",
                                 "dummyFile.svg");

            Test t 
                = new SVGAccuracyTest(painter, refURL);
            
            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_CANNOT_GENERATE_SVG);

            return super.runImpl();
        }
    }

    static class PainterWithException extends TestReportValidator 
        implements Painter {

        public void paint(Graphics2D g){
            g.setComposite(null); // Will cause the exception
            g.fillRect(0, 0, 20, 20);
        }

        public TestReport runImpl() throws Exception {
            Painter painter = this;
            URL refURL = new URL("http",
                                 "dummyHost",
                                 "dummyFile.svg");
            Test t = new SVGAccuracyTest(painter, refURL);
            
            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_CANNOT_GENERATE_SVG);

            return super.runImpl();
        }
    }

    static class ValidPainterTest extends TestReportValidator 
        implements Painter{
        
        public void paint(Graphics2D g){
            g.setPaint(Color.red);
            g.fillRect(0, 0, 40, 40);
        }
    }

    static class NullReferenceURL extends ValidPainterTest {
        public TestReport runImpl() throws Exception {
            Test t = new SVGAccuracyTest(this, null);

            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_SVG_FILE);

            return super.runImpl();
        }
    }

    static class InexistantReferenceURL extends ValidPainterTest {
        public TestReport runImpl() throws Exception {
            Test t = new SVGAccuracyTest(this,
                                         new URL("http",
                                                 "dummyHost",
                                                 "dummyFile.svg"));

            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_SVG_FILE);

            return super.runImpl();
        }
    }

    static class DiffWithReferenceImage extends ValidPainterTest {
        public TestReport runImpl() throws Exception {
            File tmpFile = File.createTempFile("EmptySVGReference",
                                               null);
            tmpFile.deleteOnExit();

            Test t = new SVGAccuracyTest(this,
                                         tmpFile.toURL());

            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_GENERATED_SVG_INACCURATE);

            return super.runImpl();
        }
    }

    static class SameAsReferenceImage extends ValidPainterTest {
        public TestReport runImpl() throws Exception {
            File tmpFile = File.createTempFile("SVGReference",
                                               null);
            tmpFile.deleteOnExit();

            SVGAccuracyTest t = new SVGAccuracyTest(this,
                                                    tmpFile.toURL());
            
            t.setSaveSVG(tmpFile);

            setConfig(t,
                      false,
                      SVGAccuracyTest.ERROR_GENERATED_SVG_INACCURATE);

            // This first run should fail but it should
            // have created the reference image in tmpFile
            super.runImpl();

            // Second run should work because the reference
            // image should match 
            setConfig(t,
                      true,
                      null);

            return super.runImpl();
        }
    }

}
