/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception which <tt>AbstractTest</tt> extensions can throw from the 
 * <tt>rumImpl</tt> method to report an error condition.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class TestErrorConditionException extends Exception {
    /**
     * Error code. May be null.
     */
    protected String errorCode;

    /**
     * Default constructor
     */
    protected TestErrorConditionException(){
    }

    /**
     * @param errorCode describes the error condition
     */
    public TestErrorConditionException(String errorCode){
        this.errorCode = errorCode;
    }

    /**
     * Requests a report which describes the exception.
     */
    public TestReport getTestReport(Test test){
        DefaultTestReport report = new DefaultTestReport(test);
        if(errorCode != null){
            report.setErrorCode(errorCode);
        } else {
            report.setErrorCode(report.ERROR_TEST_FAILED);
        }

        report.setPassed(false);
        addStackTraceDescription(report);
        return report;
    }

    /**
     * Convenience method: adds a description entry for the stack
     * trace.
     */
    public void addStackTraceDescription(TestReport report){
        StringWriter trace = new StringWriter();
        printStackTrace(new PrintWriter(trace));
        
        report.addDescriptionEntry(report.ENTRY_KEY_ERROR_CONDITION_STACK_TRACE,
                                   trace.toString());
    }
}
