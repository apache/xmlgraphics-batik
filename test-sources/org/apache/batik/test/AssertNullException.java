/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Exception which Tests can throw when a specific <tt>assertNull</tt> fails.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class AssertNullException extends AssertException {
    public static final String ASSERTION_TYPE = "assertNull";

    /**
     * Objects which should have be equal
     */
    protected Object ref, cmp;

    public AssertNullException(){
    }

    /**
     * Requests that the exception populates the TestReport with the
     * relevant information.
     */
    public void addDescription(TestReport report){
    }

    public String getAssertionType(){
        return ASSERTION_TYPE;
    }
}
