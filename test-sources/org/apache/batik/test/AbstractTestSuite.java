/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.util.List;
import java.util.LinkedList;

/**
 * This class provides an implementation for the <tt>addTest</tt>
 * method and a protected member to store the children <tt>Test</tt>
 * instances.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class AbstractTestSuite implements TestSuite {
    /**
     * Stores the list of children <tt>Test</tt> instances.
     */
    protected List children = new LinkedList();

    /**
     * Adds a <tt>Test</tt> to the suite
     */
    public void addTest(Test test){
        if(test != null){
            children.add(test);
        }
    }

}
