/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
