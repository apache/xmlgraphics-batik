/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.rasterizer;

/**
 * Describes an error condition in <tt>SVGConverter</tt>
 *
 * @author <a href="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGConverterException extends Exception {
    /**
     * Error code
     */
    protected String errorCode;

    /**
     * Additional information about the error condition
     */
    protected Object[] errorInfo;

    /**
     * Defines whether or not this is a fatal error condition
     */
    protected boolean isFatal;

    public SVGConverterException(String errorCode){
        this(errorCode, null, false);
    }

    public SVGConverterException(String errorCode, 
                                  Object[] errorInfo){
        this(errorCode, errorInfo, false);
    }

    public SVGConverterException(String errorCode,
                                  Object[] errorInfo,
                                  boolean isFatal){
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
        this.isFatal = isFatal;
    }

    public SVGConverterException(String errorCode,
                                  boolean isFatal){
        this(errorCode, null, isFatal);
    }

    public boolean isFatal(){
        return isFatal;
    }

    public String getMessage(){
        return Messages.formatMessage(errorCode, errorInfo);
    }

    public String getErrorCode(){
        return errorCode;
    }
}
