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
 * A <code>TestSuite</code> is a composite test, that is, a test
 * made of multiple children <code>Test</code> cases. Running a
 * <code>TestSuite</code> will simply run the children test cases.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface TestSuite extends Test {
    /**
     * Adds a <code>Test</code> to the suite
     */
    void addTest(Test test);

    /**
     * Removes a <code>Test</code> from the suite
     */
    void removeTest(Test test);

    /**
     * Returns this suite's <code>Test</code>. This should
     * not return a reference to any internal structure
     * held by the <code>TestSuite</code>. For example, if
     * an internal array is used, this shoudl return
     * a copy of that array.
     */
    Test[] getChildrenTests();

    /**
     * Returns the number of child tests
     */
    int getChildrenCount();

}
