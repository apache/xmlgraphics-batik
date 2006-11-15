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
