/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Defines the interface of a <tt>Test</tt> case. It is
 * highly recommended that implementations derive from the
 * <tt>AbstractTest</tt> class or follow the same implementation
 * approach, so that no exception is thrown from the 
 * <tt>run</tt> method, which is critical for the operation
 * of the test infrastructure.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface Test {
    /**
     * Returns this <tt>Test</tt>'s name. 
     */
    public String getName();

    /**
     * Requests this <tt>Test</tt> to run and produce a 
     * report. It is critical for the test infrastructure
     * that implementations never throw exceptions 
     * from the run method, even if an error occurs internally
     * in the test. 
     *
     */
    public TestReport run();
}
