/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.util.Vector;

/**
 * This test validates that test properties are inherited from the class that
 * defines the "class" attribute down to each test instance that uses the 
 * same class.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ParametrizedTest extends AbstractTest {
    protected String A = "initial_A_value";
    protected String B = "initial_B_value";
    protected String expectedA = "unset";
    protected String expectedB = "unset";

    public void setA(String A) {
        this.A = A;
    }

    public void setB(String B) {
        this.B = B;
    }

    public void setExpectedA(String expectedA) {
        this.expectedA = expectedA;
    }

    public void setExpectedB(String expectedB) {
        this.expectedB = expectedB;
    }

    public String getA() {
        return A;
    }

    public String getB() {
        return B;
    }

    public String getExpectedA() {
        return expectedA;
    }

    public String getExpectedB() {
        return expectedB;
    }

    public TestReport runImpl() throws Exception {
        if (!A.equals(expectedA) || !B.equals(expectedB)) {
            TestReport r = reportError("Unexpected A or B value");
            r.addDescriptionEntry("expected.A", expectedA);
            r.addDescriptionEntry("actual.A", A);
            r.addDescriptionEntry("expected.B", expectedB);
            r.addDescriptionEntry("actual.B", B);
            return r;
        }

        return reportSuccess();
    }
}
