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

import java.util.Iterator;
import java.util.Vector;

/**
 * Simple implementation of the <tt>TestReport</tt> interface
 * for <tt>TestSuite</tt>
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultTestSuiteReport implements TestSuiteReport {
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
     * Descriptions in addition to that coming from children.
     */
    protected Entry[] description = null;

    /**
     * Parent report in case this report is part of a bigger one.
     */
    protected TestSuiteReport parent;

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

    public TestSuiteReport getParentReport(){
        return parent;
    }

    public void setParentReport(TestSuiteReport parent){
        this.parent = parent;
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
    
    public void addDescriptionEntry(String key,
                                    Object value){
        addDescriptionEntry(new Entry(key, value));
    }

    protected void addDescriptionEntry(Entry entry){
        if(description == null){
            description = new Entry[1];
            description[0] = entry;
        }
        else{
            Entry[] oldDescription = description;
            description = new Entry[description.length + 1];
            System.arraycopy(oldDescription, 0, description, 0,
                             oldDescription.length);
            description[oldDescription.length] = entry;
        }
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

        if(description != null){
            TestReport.Entry[] e = entries;
            entries = new TestReport.Entry[e.length + description.length];
            System.arraycopy(e, 0, entries, 0, e.length);
            System.arraycopy(description, 0, entries, e.length, description.length);
        }

        return entries;
    }

    public void addReport(TestReport report){
        if(report == null){
            throw new IllegalArgumentException();
        }

        report.setParentReport(this);
        reports.addElement(report);
    }
    

    public TestReport[] getChildrenReports(){
        int nReports = reports.size();
        TestReport[] r = new TestReport[nReports];
        reports.copyInto(r);
        return r;
    }
}
