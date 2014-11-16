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
 * Defines the interface of a <code>Test</code> case. It is
 * highly recommended that implementations derive from the
 * <code>AbstractTest</code> class or follow the same implementation
 * approach, so that no exception is thrown from the
 * <code>run</code> method, which is critical for the operation
 * of the test infrastructure.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface Test {
    /**
     * Returns this <code>Test</code>'s name.
     */
    String getName();

    /**
     * Returns the <code>Test</code>'s qualified id, that is,
     * the string made of all the id's parents separated
     * by ".". For example, if this test's id is "C",
     * its parent id is "B" and its grand-parent id is
     * "A", this method should return "A.B.C".
     */
    String getQualifiedId();

    /**
     * Returns the <code>Test</code>'s id. The notion of
     * identifier is left to the user of the <code>Test</code>
     * object, which explains why the user may set the
     * id.
     */
    String getId();

    /**
     * Sets this <code>Test</code>'s id.
     */
    void setId(String id);

    /**
     * Requests this <code>Test</code> to run and produce a
     * report. It is critical for the test infrastructure
     * that implementations never throw exceptions
     * from the run method, even if an error occurs internally
     * in the test.
     *
     */
    TestReport run();

    /**
     * Returns this <code>Test</code>'s parent, in case this
     * <code>Test</code> is part of a <code>TestSuite</code>.
     * The returned value may be null.
     */
    TestSuite getParent();

    /**
     * Set this <code>Test</code>'s parent.
     */
    void setParent(TestSuite parent);
}
