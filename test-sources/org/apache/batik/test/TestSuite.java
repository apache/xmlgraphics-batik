/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * A <tt>TestSuite</tt> is a composite test, that is, a test
 * made of multiple children <tt>Test</tt> cases. Running a 
 * <tt>TestSuite</tt> will simply run the children test cases.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface TestSuite extends Test {
    /**
     * Adds a <tt>Test</tt> to the suite
     */
    public void addTest(Test test);

}
