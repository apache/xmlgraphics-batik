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
     * This test's id.
     */
    protected String id = "";

    /**
     * This test's parent, in case this test is part of 
     * a suite.
     */
    protected TestSuite parent;

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
     * Return this <tt>Test</tt>'s id.
     */
    public String getId(){
        return id;
    }

    /**
     * Return this <tt>Test</tt>'s qualified id.
     */
    public String getQualifiedId(){
        if(parent == null){
            return getId();
        }
        return getParent().getQualifiedId() + "." + getId();
    }

    /**
     * Set this <tt>Test</tt>'s id. Null is not allowed.
     */
    public void setId(String id){
        if(id == null){
            throw new IllegalArgumentException();
        }

        this.id = id;
    }

    public TestSuite getParent(){
        return parent;
    }

    public void setParent(TestSuite parent){
        this.parent = parent;
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
    public TestReport run(){
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

            }catch(Exception ex){
                ex.printStackTrace();
            }finally {
                //
                // In case we are in severe trouble, even filling in the 
                // TestReport may fail. Because the TestReport instance
                // was created up-front, this ensures we can return 
                // the report, even though it may be incomplete.
                System.out.println("SERIOUS ERROR");
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
    public TestReport runImpl() throws Exception {
        boolean passed = runImplBasic();

        // No exception was thrown if we get to this 
        // portion of rumImpl. The test result is 
        // given by passed.
        DefaultTestReport report = new DefaultTestReport(this);
        if(!passed){
            report.setErrorCode(TestReport.ERROR_TEST_FAILED);
        }
        report.setPassed(passed);
        return report;
    }

    /**
     * In the simplest test implementation, developers can 
     * simply implement the following method.
     */
    public boolean runImplBasic() throws Exception {
        return true;
    }

    /**
     * Convenience method.
     */
    public TestReport reportSuccess() {
        DefaultTestReport report = new DefaultTestReport(this);
        report.setPassed(true);
        return report;
    }

    /**
     * Convenience method to report a simple error code.
     */
    public TestReport reportError(String errorCode){
        DefaultTestReport report = new DefaultTestReport(this);
        report.setErrorCode(errorCode);
        report.setPassed(false);
        return report;
    }

    /**
     * Convenience method to help implementations report errors.
     * An <tt>AbstractTest</tt> extension will typically catch 
     * exceptions for specific error conditions it wants to point 
     * out. For example:<tt>
     * public TestReport runImpl() throws Exception { <br />
     *   try{ <br />
     *      .... something .... <br />
     *   catch(MySpecialException e){ <br />
     *      return reportException(MY_SPECIAL_ERROR_CODE, e); <br />
     *   } <br />
     * <br />
     * public static final String MY_SPECIAL_ERROR_CODE = "myNonQualifiedClassName.my.error.code" <br />
     * <br />
     * </tt> <br />
     * Note that the implementor will also need to add an entry
     * in its Messages.properties file. That file is expected to be 
     * in a resource file called <tt>Messages</tt> having the same package 
     * name as the <tt>Test</tt> class, appended with "<tt>.resources</tt>".
     */
    public TestReport reportException(String errorCode,
                                      Exception e){
        DefaultTestReport report 
            = new DefaultTestReport(this);

        StringWriter trace = new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        report.setErrorCode(errorCode);

                
        TestReport.Entry[] entries = new TestReport.Entry[]{
            new TestReport.Entry
                (Messages.formatMessage(report.ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_CLASS, null),
                 e.getClass().getName()),
            new TestReport.Entry
                (Messages.formatMessage(report.ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_MESSAGE, null),
                 e.getMessage()),
            new TestReport.Entry
                (Messages.formatMessage(report.ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_STACK_TRACE, null),
                 trace.toString())
                };
        report.setDescription(entries);
        report.setPassed(false);
        return report;
    }
            

}
