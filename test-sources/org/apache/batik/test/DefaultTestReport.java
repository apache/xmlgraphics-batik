/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Simple, default implementation for the <tt>TestReport</tt>
 * interface.
 * 
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class DefaultTestReport implements TestReport {
    private boolean passed = true;

    protected Entry[] description = null;

    protected Test test;

    private String errorCode;
    
    /**
     * Parent report, in case this report is part of a
     * <tt>TestSuiteReport</tt>
     */
    protected TestSuiteReport parent;

    public DefaultTestReport(Test test){
        if(test == null){
            throw new IllegalArgumentException();
        }

        this.test = test;
    }

    public TestSuiteReport getParentReport(){
        return parent;
    }

    public void setParentReport(TestSuiteReport parent){
        this.parent = parent;
    }

    public Test getTest(){
        return test;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public void setErrorCode(String errorCode){
        if( !passed && errorCode == null ){
            /**
             * Error code should be set first
             */
            throw new IllegalArgumentException();
        }

        this.errorCode = errorCode;
    }

    public boolean hasPassed(){
        return passed;
    }
    
    public void setPassed(boolean passed){
        if( !passed && (errorCode == null) ){
            /**
             * Error Code should be set first
             */
            throw new IllegalArgumentException();
        }
        this.passed = passed;
    }
    
    public Entry[] getDescription(){
        return description;
    }
    
    public void setDescription(Entry[] description){
        this.description = description;
    }

    public void addDescriptionEntry(String key,
                                    Object value){
        addDescriptionEntry(new Entry(key, value));
    }

    protected void addDescriptionEntry(Entry entry){
        if(description == null){
            description = new Entry[1];
            description[0] = entry;
        }
        else{
            Entry[] oldDescription = description;
            description = new Entry[description.length + 1];
            System.arraycopy(oldDescription, 0, description, 0,
                             oldDescription.length);
            description[oldDescription.length] = entry;
        }
    }

}

