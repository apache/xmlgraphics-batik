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
