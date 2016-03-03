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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Default implementation of the <code>TestSuite</code> interface.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultTestSuite extends AbstractTest implements TestSuite {
    /**
     * This Test's name
     */
    private String name = null;

    /**
     * Stores the list of child tests
     */
    protected List tests = new ArrayList();

    /**
     * Adds a <code>Test</code> to the suite
     */
    public void addTest(Test test){
        if(test == null){
            throw new IllegalArgumentException();
        }

        test.setParent(this);
        tests.add(test);
    }

    /**
     * Removes a <code>Test</code> from the suite.
     */
    public void removeTest(Test test){
        tests.remove(test);
    }

    /**
     * Runs the tests and returns a report
     */
    public TestReport runImpl(){
        Iterator iter = tests.iterator();

        DefaultTestSuiteReport report
            = new DefaultTestSuiteReport(this);

        while(iter.hasNext()){
            Test t = (Test)iter.next();
            System.err.println("Running " + t.getName());
            TestReport tr = t.run();
            if (tr == null){
                System.out.println("ERROR" + t.getId() + " returned a null report");
            }
            report.addReport(tr);
        }

        return report;
    }

    public String getName(){
        if(name != null){
            return name;
        }

        String id = getId();
        if(id != null && !"".equals(id)){
            return id;
        }

        return this.getClass().getName();
    }

    public void setName(String name){
        if(name == null && !"".equals(name)){      // ?? logic ??
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public Test[] getChildrenTests(){
        Test[] children = new Test[tests.size()];
        tests.toArray(children);
        return children;
    }

    /**
     * Returns the number of child tests
     */
    public int getChildrenCount(){
        return tests.size();
    }

}
