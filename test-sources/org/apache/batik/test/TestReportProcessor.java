/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Interface for classes that can process <tt>TestReport</tt> instances
 * This allows different applications to use the same <tt>TestReport</tt>
 * for different purposes, such as generating an XML output or 
 * emailing a test result summary.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface TestReportProcessor {
    /**
     * Generic error code. Takes no parameter.
     */
    public static final String INTERNAL_ERROR = 
        "TestReportProcessor.error.code.internal.error";

    /**
     * Requests the processor to process the input 
     * <tt>TestReport</tt> instances. Note that a processor
     * should make its own copy of any resource described 
     * by a <tt>TestReport</tt> such as files, as these
     * may be transient resources. In particular, a 
     * processor should not keep a reference to the 
     * input <tt>TestReport</tt>
     */
    public void processReport(TestReport report) 
        throws TestException;
}


