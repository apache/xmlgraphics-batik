/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
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
    String getName();

    /**
     * Returns the <tt>Test</tt>'s qualified id, that is,
     * the string made of all the id's parents separated
     * by ".". For example, if this test's id is "C",
     * its parent id is "B" and its grand-parent id is
     * "A", this method should return "A.B.C".
     */
    String getQualifiedId();

    /**
     * Returns the <tt>Test</tt>'s id. The notion of
     * identifier is left to the user of the <tt>Test</tt>
     * object, which explains why the user may set the
     * id.
     */
    String getId();

    /**
     * Sets this <tt>Test</tt>'s id.
     */
    void setId(String id);

    /**
     * Requests this <tt>Test</tt> to run and produce a
     * report. It is critical for the test infrastructure
     * that implementations never throw exceptions
     * from the run method, even if an error occurs internally
     * in the test.
     *
     */
    TestReport run();

    /**
     * Returns this <tt>Test</tt>'s parent, in case this
     * <tt>Test</tt> is part of a <tt>TestSuite</tt>.
     * The returned value may be null.
     */
    TestSuite getParent();

    /**
     * Set this <tt>Test</tt>'s parent.
     */
    void setParent(TestSuite parent);
}
