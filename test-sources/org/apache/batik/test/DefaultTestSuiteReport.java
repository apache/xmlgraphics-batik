/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.util.Iterator;
import java.util.Vector;

/**
 * Simple implementation of the <tt>TestReport</tt> interface
 * for <tt>TestSuite</tt>
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultTestSuiteReport implements TestReport {
    /**
     * Error code for a failed TestSuite
     */
    public static final String ERROR_CHILD_TEST_FAILED 
        = "DefaultTestSuiteReport.error.child.test.failed";

    /**
     * Entry for a failed child test report
     */
    public static final String ENTRY_KEY_FAILED_CHILD_TEST_REPORT
        = "DefaultTestSuiteReport.entry.key.failed.child.test.report";

    /**
     * Entry for a passed child test report
     */
    public static final String ENTRY_KEY_PASSED_CHILD_TEST_REPORT
        = "DefaultTestSuiteReport.entry.key.passed.child.test.report";

    /**
     * Set of <tt>TestReport</tt> coming from the <tt>TestSuite</tt>
     */
    protected Vector reports = new Vector();

    /**
     * TestSuite that created this report
     */
    protected TestSuite testSuite;

    /**
     * Status of the TestSuite
     */
    private boolean passed = true;
    
    public DefaultTestSuiteReport(TestSuite testSuite){
        if(testSuite == null){
            throw new IllegalArgumentException();
        }

        this.testSuite = testSuite;
    }

    public Test getTest(){
        return testSuite;
    }

    public String getErrorCode(){
        if(hasPassed()){
            return null;
        }
        else{
            return ERROR_CHILD_TEST_FAILED;
        }
    }

    public boolean hasPassed(){
        Iterator iter = reports.iterator();

        boolean passed = true;

        while(iter.hasNext()){
            TestReport childReport = (TestReport)iter.next();
            passed = passed && childReport.hasPassed();
        }
        
        return passed;
    }
    
    public Entry[] getDescription(){
        Iterator iter = reports.iterator();
        Vector descs = new Vector();

        while(iter.hasNext()){
            TestReport childReport = (TestReport)iter.next();
            if(!childReport.hasPassed()){
                TestReport.Entry entry 
                    = new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_FAILED_CHILD_TEST_REPORT, null),
                                           childReport);
                descs.addElement(entry);
            }
        }
        
        iter = reports.iterator();
        while(iter.hasNext()){
            TestReport childReport = (TestReport)iter.next();
            if(childReport.hasPassed()){
                TestReport.Entry entry 
                    = new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_PASSED_CHILD_TEST_REPORT, null),
                                           childReport);
                descs.addElement(entry);
            }
        }
        
        TestReport.Entry[] entries = null;
        if(descs.size() > 0){
            entries = new TestReport.Entry[descs.size()];
            descs.copyInto(entries);
        }

        return entries;
    }

    public void addReport(TestReport report){
        if(report == null){
            throw new IllegalArgumentException();
        }

        reports.addElement(report);
    }
    
}
