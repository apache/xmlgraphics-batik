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

/**
 * Provides a default implementation for the <tt>getName</tt>
 * method.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class AbstractTest implements Test {
    /**
     * TestReport
     */
    private DefaultTestReport report 
        = new DefaultTestReport(this) {
                {
                    setErrorCode(ERROR_INTERNAL_TEST_FAILURE);
                    setPassed(false);
                }
            };

    /**
     * Returns this <tt>Test</tt>'s name. 
     */
    public String getName(){
        return getClass().getName();
    }

    /**
     * This default implementation of the run method
     * catches any Exception or Error throw from the 
     * runImpl method and creates a <tt>TestReport</tt>
     * indicating an internal <tt>Test</tt> failure
     * when that happens. Otherwise, this method
     * simply returns the <tt>TestReport</tt> generated
     * by the <tt>runImpl</tt> method.
     */
    public final TestReport run(){
        try{
            return runImpl();
        }catch(Exception e){
            try {
                
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                
                TestReport.Entry[] entries = new TestReport.Entry[]{
                    new TestReport.Entry
                        (Messages.formatMessage(report.ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_CLASS, null),
                         e.getClass().getName()),
                    new TestReport.Entry
                        (Messages.formatMessage(report.ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_MESSAGE, null),
                         e.getMessage()),
                    new TestReport.Entry
                        (Messages.formatMessage(report.ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_STACK_TRACE, null),
                         trace.toString())
                        };

                report.setDescription(entries);

            }finally {
                //
                // In case we are in severe trouble, even filling in the 
                // TestReport may fail. Because the TestReport instance
                // was created up-front, this ensures we can return 
                // the report, even though it may be incomplete.
                return report;
            }
                
        }
    }

    /**
     * Subclasses should implement this method with the content of 
     * the test case. Typically, implementations will choose to 
     * catch and process all exceptions and error conditions they
     * are looking for in the code they exercise but will let 
     * exceptions due to their own processing propagate. 
     */
    public abstract TestReport runImpl() throws Exception;
}
