/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Exception which Tests can throw when a specific assertion fails.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class AssertException extends TestErrorConditionException {
    public static final String ENTRY_KEY_ASSERTION_TYPE 
        = "AssertException.entry.key.assertion.type";

    /**
     * <tt>TestErrorConditionException</tt> implementation.
     */
    public TestReport getTestReport(Test test){
        DefaultTestReport report = new DefaultTestReport(test);
        report.setErrorCode(report.ERROR_ASSERTION_FAILED);
        report.addDescriptionEntry(ENTRY_KEY_ASSERTION_TYPE,
                                   getAssertionType());
        addDescription(report);
        addStackTraceDescription(report);        
        return report;
    }

    /**
     * Requests that the exception populates the TestReport with the
     * relevant information.
     */
    public abstract void addDescription(TestReport report);

    /**
     * Returns the type of assertion which failed. e.g., "assertEquals"
     */
    public abstract String getAssertionType();
}
