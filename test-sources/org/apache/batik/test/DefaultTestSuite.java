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
    private String name = this.getClass().getName();

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

        tests.addElement(test);
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

            report.addReport(t.run());
        }

        return report;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if(name == null){
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

}
