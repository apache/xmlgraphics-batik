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

package org.apache.batik.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Validates the operation of the <code>PerformanceTest</code> class.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PerformanceTestValidator extends AbstractTest {
    public TestReport runImpl() throws Exception {
        // First, work with SimplePerformanceTest to check the life
        // cycle of using a performance test.
        // ===========================================================
        SimplePerformanceTest p = new SimplePerformanceTest();
        TestReport r = p.run();
        assertTrue(!r.hasPassed());
        assertTrue(r.getErrorCode().equals("no.reference.score.set"));
        p.setReferenceScore(p.getLastScore());
        p.run();
        p.setReferenceScore(p.getLastScore());
        p.run();

        double score = p.getLastScore();        
        p.setReferenceScore(score);
        r = p.run();

        if (!r.hasPassed()) {
            TestReport result = reportError("unexpected.performance.test.failure");
            result.addDescriptionEntry("error.code", r.getErrorCode());
            result.addDescriptionEntry("expected.score", "" + score);
            result.addDescriptionEntry("actual.score", "" + p.getLastScore());
            result.addDescriptionEntry("regression.percentage", "" + 100*(score - p.getLastScore())/p.getLastScore());
            return result;
        }

        // Now, check that performance changes are detected
        // ===========================================================
        p.setReferenceScore(score*0.5);
        r = p.run();
        assertTrue(!r.hasPassed());
        if (!r.getErrorCode().equals("performance.regression")) {
            TestReport result = reportError("unexpected.performance.test.error.code");
            result.addDescriptionEntry("expected.code", "performance.regression");
            result.addDescriptionEntry("actual.code", r.getErrorCode());
            result.addDescriptionEntry("expected.score", "" + score);
            result.addDescriptionEntry("actual.score", "" + p.getLastScore());
            result.addDescriptionEntry("regression.percentage", "" + 100*(score - p.getLastScore())/p.getLastScore());
            return result;
        }

        p.setReferenceScore(score*2);
        r = p.run();
        assertTrue(!r.hasPassed());
        if (!r.getErrorCode().equals("unexpected.performance.improvement")) {
            TestReport result = reportError("unexpected.performance.test.error.code");
            result.addDescriptionEntry("expected.code", "unexpected.performance.improvement");
            result.addDescriptionEntry("actual.code", r.getErrorCode());
            result.addDescriptionEntry("expected.score", "" + score);
            result.addDescriptionEntry("actual.score", "" + p.getLastScore());
            result.addDescriptionEntry("regression.percentage", "" + 100*(score - p.getLastScore())/p.getLastScore());
            return result;
        }

        return reportSuccess();
    }

    static class SimplePerformanceTest extends PerformanceTest {
        public void runOp() {
            // runRef();
            BufferedImage buf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buf.createGraphics();
            AffineTransform txf = new AffineTransform();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setPaint(new Color(30, 100, 200));
            
            for (int j=0; j<20; j++) {
                txf.setToIdentity();
                txf.translate(-100, -100);
                txf.rotate(j*Math.PI/100);
                txf.translate(100, 100);
                g.setTransform(txf);
                g.drawRect(30, 30, 140, 140);
            }
            /*
            Vector v = new Vector();
            for (int i=0; i<5000; i++) {
                v.addElement("" + i);
            }
            
            for (int i=0; i<5000; i++) {
                if (v.contains("" + i)) {
                    v.remove("" + i);
                }
                }*/
        }
    }
}
