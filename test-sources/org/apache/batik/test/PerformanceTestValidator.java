/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.Vector;

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
