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

/**
 * Defines the interface of a <tt>TestReport</tt> produced
 * by a <tt>Test</tt> case.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface TestReport {
    /**
     * Error code to be used when a <tt>Test</tt> fails in
     * its own operation (i.e., the <tt>Test</tt> itself
     * fails, not what it is testing. An internal failure
     * is reported when any type of exception occurs while
     * running the test.
     */
    public static final String ERROR_INTERNAL_TEST_FAILURE 
        = "TestReport.error.internal.test.failure";

    /**
     * Very generic error code which can be used to report
     * that the test failed.
     */
    public static final String ERROR_TEST_FAILED 
        = "TestReport.error.test.failed";

    /**
     * Generic error code to report test assertion failures.
     */
    public static final String ERROR_ASSERTION_FAILED
        = "TestReport.error.assertion.failed";

    /**
     * Entry describing the class of the internal exception 
     * that caused the test's internal failure
     */
    public static final String 
        ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_CLASS
        = "TestReport.entry.key.internal.test.failure.exception.class";

    /**
     * Entry describing the messages of the internal exception 
     * that caused the test's internal failure
     */
    public static final String 
        ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_MESSAGE
        = "TestReport.entry.key.internal.test.failure.exception.message";

    /**
     * Entry with the stack trace for the internal exception 
     * that caused the test's internal failure
     */
    public static final String 
        ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_STACK_TRACE
        = "TestReport.entry.key.internal.test.failure.exception.stack.trace";

    /**
     * Entry with the class of the exception that caused the test to fail.
     * Note that this is different from 
     * ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_CLASS, in
     * which case, the test itself failed unexpectedly. In this
     * case, the entry is used to describe an expected exception
     * for which the <tt>Test</tt> author probably created a
     * specific error code.
     */
    public static final String
        ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_CLASS
        = "TestReport.entry.key.reported.test.failure.exception.class";

    /**
     * Entry with the message of the exception that caused the test to fail.
     * Note that this is different from 
     * ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_MESSAGE, in
     * which case, the test itself failed unexpectedly. In this
     * case, the entry is used to describe an expected exception
     * for which the <tt>Test</tt> author probably created a
     * specific error code.
     */
    public static final String
        ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_MESSAGE
        = "TestReport.entry.key.reported.test.failure.exception.message";

    /**
     * Entry with the stack trace that caused the test to fail.
     * Note that this is different from 
     * ENTRY_KEY_INTERNAL_TEST_FAILURE_EXCEPTION_STACK_TRACE, in
     * which case, the test itself failed unexpectedly. In this
     * case, the entry is used to describe an expected exception
     * for which the <tt>Test</tt> author probably created a
     * specific error code.
     */
    public static final String
        ENTRY_KEY_REPORTED_TEST_FAILURE_EXCEPTION_STACK_TRACE
        = "TestReport.entry.key.reported.test.failure.exception.stack.trace";

    /**
     * Entry with the stack trace for a specific test error
     * condition.
     */
    public static final String
        ENTRY_KEY_ERROR_CONDITION_STACK_TRACE
        = "TestReport.entry.key.error.condition.stack.trace";

    /**
     * Inner class for describing an information element in a 
     * <tt>TestReport</tt>
     */
    public static class Entry {
        private String entryKey;
        private Object entryValue;

        public Entry(String entryKey,
                     Object entryValue){
            this.entryKey = entryKey;
            this.entryValue = entryValue;
        }

        public final String getKey(){
            return entryKey;
        }

        public final Object getValue(){
            return entryValue;
        }
    }

    /**
     * Returns the overall test result
     */
    public boolean hasPassed();

    /**
     * Returns the error code. This should never be null
     * if the test failed (i.e., if hasPassed returns false).
     */
    public String getErrorCode();

    /**
     * Returns an array of <tt>Entry</tt> objects describing the
     * test result.
     * Accepted value types are <tt>String</tt> objects, <tt>URL</tt>
     * objects, <tt>File</tt> objects and <tt>TestReport</tt> objects. 
     * <tt>File</tt> objects should be considered as temporary files
     */
    public Entry[] getDescription();

    /**
     * Appends <tt>entry</tt> to the array of description entry.
     */
    public void addDescriptionEntry(String key,
                                    Object value);

    /**
     * Returns the <tt>Test</tt> object that generated this 
     * <tt>TestReport</tt>
     */
    public Test getTest();

    /**
     * Returns the parent report in case this <tt>TestReport</tt> is
     * part of a <tt>TestSuiteReport</tt>. This may be null.
     */
    public TestSuiteReport getParentReport();

    /**
     * Set this report's parent.
     */
    public void setParentReport(TestSuiteReport parent);
}
