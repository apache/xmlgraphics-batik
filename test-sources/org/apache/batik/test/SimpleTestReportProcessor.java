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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

/**
 * A simple implementation of the <tt>TestReportProcessor</tt> interface
 * that prints out the <tt>TestReport</tt> to the standard output.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SimpleTestReportProcessor implements TestReportProcessor {
    /**
     * Message keys
     */
    public static final String MESSAGES_TEST_SUITE_STATUS_TEST_PASSED
        = "SimpleTestReportProcessor.messages.test.suite.status.testPassed";

    public static final String MESSAGES_TEST_SUITE_STATUS_TEST_FAILED
        = "SimpleTestReportProcessor.messages.test.suite.status.testFailed";

    public static final String MESSAGES_TEST_SUITE_STATUS
        = "SimpleTestReportProcessor.messages.test.suite.status";

    public static final String MESSAGES_TEST_SUITE_ERROR_CODE
        = "SimpleTestReportProcessor.messages.test.suite.error.code";

    /**
     * Default output writer
     */
    private PrintWriter printWriter;

    /**
     * Sets the <tt>PrintWriter</tt> this processor should use
     */
    public void setPrintWriter(PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    /**
     * Recursively prints out the entries of the input 
     * report and its children reports, if any.
     */
    public void processReport(TestReport report)
        throws TestException{
        try{
            PrintWriter out = printWriter;
            if(printWriter == null){
                out = new PrintWriter(new OutputStreamWriter(System.out));
            }
            processReport(report, "", out);
            out.flush();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(INTERNAL_ERROR,
                                    new Object[] { e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);
        }
    }

    /**
     * Prints out the input report, prefixing all output
     * with the input string
     */
    public void processReport(TestReport report, String prefix, PrintWriter out){
        String status = report.hasPassed() 
            ? Messages.formatMessage(MESSAGES_TEST_SUITE_STATUS_TEST_PASSED, null)
            : Messages.formatMessage(MESSAGES_TEST_SUITE_STATUS_TEST_FAILED, null);

        out.println(Messages.formatMessage(MESSAGES_TEST_SUITE_STATUS,
                                                  new Object[]{ report.getTest().getName(),
                                                                status }));

        if(!report.hasPassed()){
            out.println(Messages.formatMessage(MESSAGES_TEST_SUITE_ERROR_CODE, 
                                                      new Object[]{report.getErrorCode()}));
        }
        
        TestReport.Entry[] entries = report.getDescription();
        int n = entries != null ? entries.length : 0;
        for(int i=0; i<n; i++){
            out.print(prefix + entries[i].getKey() + " : " );
            printValue(entries[i].getValue(), prefix + "    ", out);
        }
    }

    /**
     * Prints out the input value depending on its
     * type.
     */
    protected void printValue(Object value, String prefix, PrintWriter out){
        if(!(value instanceof TestReport)){
            out.println(value);
        }
        else{
            out.println();
            processReport((TestReport)value, prefix, out);
        }
    }
}


