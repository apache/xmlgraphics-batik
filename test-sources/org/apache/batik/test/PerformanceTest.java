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

import java.util.Vector;

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
        int iter = 50;

        double refUnit = 0;
        long refStart = 0;
        long refEnd = 0;
        long opEnd = 0;
        long opStart = 0;
        double opLength = 0;

        // Run once to remove class load time from timing.
        runRef();
        runOp();
        // System.gc();

        double[] scores = new double[iter];

        for (int i=0; i<iter; i++) {
            if ( i%2 == 0) {
                refStart = System.currentTimeMillis();
                runRef();
                refEnd = System.currentTimeMillis();
                runOp();
                opEnd = System.currentTimeMillis();
                refUnit = refEnd - refStart;
                opLength = opEnd - refEnd;
            } else {
                opStart = System.currentTimeMillis();
                runOp();
                opEnd = System.currentTimeMillis();
                runRef();
                refEnd = System.currentTimeMillis();
                refUnit = refEnd - opEnd;
                opLength = opEnd - opStart;
            }

            scores[i] = opLength / refUnit;
            System.err.println(".");
            // System.err.println(">>>>>>>> scores[" + i + "] = " + scores[i] + " (" + refUnit + " / " + opLength + ")");
            System.gc();
        }

        System.err.println();

        // Now, sort the scores
        sort(scores);

        // Compute the mean score based on the scores, not accounting
        // for the lowest and highest scores
        double score = 0;
        int trim = 5;
        for (int i=trim; i<scores.length-trim; i++) {
            score += scores[i];
        }

        score /= (iter - 2*trim);

        // Compute the score
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

    protected void sort(double a[]) throws Exception {
        for (int i = a.length - 1; i>=0; i--) {
            boolean swapped = false;
            for (int j = 0; j<i; j++) {
                if (a[j] > a[j+1]) {
                    double d = a[j];
                    a[j] = a[j+1];
                    a[j+1] = d;
                    swapped = true;
                }
            }
            if (!swapped)
                return;
        }
    }

    /**
     * Runs the reference operation.
     * By default, this runs the same BufferedImage drawing 
     * operation 10000 times
     */
    protected void runRef() {
        Vector v = new Vector();
        for (int i=0; i<10000; i++) {
            v.addElement("" + i);
        }
        
        for (int i=0; i<10000; i++) {
            if (v.contains("" + i)) {
                v.remove("" + i);
            }
        }
    }

    /**
     * Runs the tested operation
     */
    protected abstract void runOp() throws Exception;
}
