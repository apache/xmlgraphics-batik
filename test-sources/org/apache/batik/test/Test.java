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
     * Returns the <tt>Test</tt>'s qualified id, that is,
     * the string made of all the id's parents separated 
     * by ".". For example, if this test's id is "C", 
     * its parent id is "B" and its grand-parent id is 
     * "A", this method should return "A.B.C".
     */
    public String getQualifiedId();

    /**
     * Returns the <tt>Test</tt>'s id. The notion of 
     * identifier is left to the user of the <tt>Test</tt>
     * object, which explains why the user may set the
     * id.
     */
    public String getId();

    /**
     * Sets this <tt>Test</tt>'s id.
     */
    public void setId(String id);

    /**
     * Requests this <tt>Test</tt> to run and produce a 
     * report. It is critical for the test infrastructure
     * that implementations never throw exceptions 
     * from the run method, even if an error occurs internally
     * in the test. 
     *
     */
    public TestReport run();

    /**
     * Returns this <tt>Test</tt>'s parent, in case this 
     * <tt>Test</tt> is part of a <tt>TestSuite</tt>.
     * The returned value may be null.
     */
    public TestSuite getParent();

    /**
     * Set this <tt>Test</tt>'s parent.
     */
    public void setParent(TestSuite parent);
}
