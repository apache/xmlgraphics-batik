/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Exception which Tests can throw when a specific <tt>assertEquals</tt> fails.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class AssertEqualsException extends AssertException {
    public static final String ENTRY_KEY_REF_OBJECT
        = "AssertEqualsException.entry.key.ref.object";

    public static final String ENTRY_KEY_CMP_OBJECT
        = "AssertEqualsException.entry.key.cmp.object";

    public static final String ASSERTION_TYPE = "assertEquals";

    /**
     * Objects which should have be equal
     */
    protected Object ref, cmp;

    public AssertEqualsException(Object ref, Object cmp){
        this.ref = ref;
        this.cmp = cmp;
    }

    /**
     * Requests that the exception populates the TestReport with the
     * relevant information.
     */
    public void addDescription(TestReport report){
        report.addDescriptionEntry(ENTRY_KEY_REF_OBJECT, ref);
        report.addDescriptionEntry(ENTRY_KEY_CMP_OBJECT, cmp);
    }

    public String getAssertionType(){
        return ASSERTION_TYPE;
    }
}
