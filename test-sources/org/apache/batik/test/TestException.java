/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

/**
 * Classes in the test package and subpackages should throw 
 * <tt>TestException</tt> to reflect internal failures in their
 * operation.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class TestException extends Exception {
    /**
     * Error code
     */
    protected String errorCode;

    /**
     * Parameters for the error message
     */
    protected Object[] errorParams;

    /**
     * Exception, if any, that caused the error
     */
    protected Exception sourceError;

    public TestException(String errorCode,
                         Object[] errorParams,
                         Exception e){
        this.errorCode = errorCode;
        this.errorParams = errorParams;
        this.sourceError = e;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public Object[] getErrorParams(){
        return errorParams;
    }

    public Exception getSourceError(){
        return sourceError;
    }

    public String getMessage(){
        return Messages.formatMessage(errorCode,
                                      errorParams);
    }
}
