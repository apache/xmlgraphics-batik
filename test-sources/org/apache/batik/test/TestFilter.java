/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Interace to accept or reject a test or testSuite.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface TestFilter {
    /**
     * The filter will return null or the input
     * <tt>Test</tt>. The filter may modify the test content
     * for example <tt>TestSuites</tt> may have some of their
     * children tests removed.
     */
    public Test filter(Test t);
}

