/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.util.Iterator;
import java.util.Vector;

/**
 * Default implementation of the <tt>TestSuite</tt> interface.
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
    protected Vector tests = new Vector();

    /**
     * Adds a <tt>Test</tt> to the suite
     */
    public void addTest(Test test){
        if(test == null){
            throw new IllegalArgumentException();
        }

        test.setParent(this);
        tests.addElement(test);
    }

    /**
     * Removes a <tt>Test</tt> from the suite.
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
        if(name == null && !"".equals(name)){
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public Test[] getChildrenTests(){
        Test[] children = new Test[tests.size()];
        tests.copyInto(children);
        return children;
    }

    /**
     * Returns the number of child tests
     */
    public int getChildrenCount(){
        return tests.size();
    }

}
