/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * This abstract <code>Test</code> implementation instruments performance
 * testing.
 *
 * Derived classes need only implement the <code>runOp</code> and, 
 * optionally, the <code>runRef</code> methods.
 *
 * The <code>setReferenceScore</code> method is used to specify 
 * the last recorded score for the performance test and the 
 * <code>setAllowedScoreDeviation</code> method is used to specify
 * the allowed deviation from the reference score.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class PerformanceTest extends AbstractTest {
    /**
     * Reference score. -1 means there is no reference score
     */
    protected double referenceScore = -1;

    /**
     * Allowed deviation from the reference score. 10% by default
     */
    protected double allowedScoreDeviation = 0.1;

    /**
     * Score during last run
     */
    protected double lastScore = -1;

    public double getLastScore() {
        return lastScore;
    }

    public double getReferenceScore() {
        return referenceScore;
    }

    public void setReferenceScore(double referenceScore) {
        this.referenceScore = referenceScore;
    }

    public double getAllowedScoreDeviation() {
        return allowedScoreDeviation;
    }

    public void setAllowedScoreDeviation(double allowedScoreDeviation) {
        this.allowedScoreDeviation = allowedScoreDeviation;
    }

    /**
     * Force implementations to only implement <code>runOp</code>
     * and other performance specific methods.
     */
    public final TestReport run() {
        return super.run();
    }

    /**
     * Force implementations to only implement <code>runOp</code>
     * and other performance specific methods.
     */
    public final boolean runImplBasic() throws Exception {
        // Should never be called for a PerformanceTest
        return false;
    }

    /**
     * This implementation of runImpl runs the reference 
     * operation (with <code>runRef</code>), then runs
     * the operation (with <code>runOp</code>) and checks whether
     * or not the score is within the allowed deviation of the 
     * reference score.
     *
     * @see #runRef
     * @see #runOp
     */
    public final TestReport runImpl() throws Exception {
        // Run the reference and time it the second time it
        // is run
        runRef();

        long refStart = System.currentTimeMillis();
        runRef();
        long refEnd = System.currentTimeMillis();
        
        double refUnit = refEnd - refStart;

        System.err.println(">>>>>>>>>>>>>> refUnit : " + refUnit);

        // Now, run the test's operation and time it the second
        // time it is run
        runOp();

        long opStart = System.currentTimeMillis();
        runOp();
        long opEnd = System.currentTimeMillis();

        double opLength = opEnd - opStart;

        // Compute the score
        double score = opLength / refUnit;
        this.lastScore = score;

        // Compare to the reference score
        if (referenceScore == -1) {
            TestReport report = reportError("no.reference.score.set");
            report.addDescriptionEntry("computed.score", "" + score);
            return report;
        } else {
            double scoreMin = referenceScore*(1-allowedScoreDeviation);
            double scoreMax = referenceScore*(1+allowedScoreDeviation);
            if (score > scoreMax) {
                TestReport report = reportError("performance.regression");
                report.addDescriptionEntry("reference.score", "" + referenceScore);
                report.addDescriptionEntry("computed.score", "" + score);
                report.addDescriptionEntry("score.deviation", "" + 100*((score-referenceScore)/referenceScore));
                return report;
            } else if (score < scoreMin) {
                TestReport report = reportError("unexpected.performance.improvement");
                report.addDescriptionEntry("reference.score", "" + referenceScore);
                report.addDescriptionEntry("computed.score", "" + score);
                report.addDescriptionEntry("score.deviation", "" + 100*((score-referenceScore)/referenceScore));
                return report;
            } else {
                return reportSuccess();
            }
        }
    }

    /**
     * Runs the reference operation.
     * By default, this runs the same BufferedImage drawing 
     * operation 10000 times
     */
    protected void runRef() {
        BufferedImage buf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buf.createGraphics();
        AffineTransform txf = new AffineTransform();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.setPaint(new Color(30, 100, 200));

        for (int i=0; i<50; i++) {
            for (int j=0; j<20; j++) {
                txf.setToIdentity();
                txf.translate(-100, -100);
                txf.rotate(j*Math.PI/100);
                txf.translate(100, 100);
                g.setTransform(txf);
                g.drawRect(30, 30, 140, 140);
            }
        }
    }

    /**
     * Runs the tested operation
     */
    protected abstract void runOp() throws Exception;
}
