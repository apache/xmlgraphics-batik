/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

/**
 * Contains constants for the XML Test Report (XTR) syntax.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface XTRConstants extends XMLReflectConstants{
    String XTR_NAMESPACE_URI 
        = "http://xml.apache.org/xml-batik/test/xtr";

    
    /////////////////////////////////////////////////////////////////////////
    // XTR tags
    /////////////////////////////////////////////////////////////////////////
    String XTR_DESCRIPTION_TAG = "description";
    String XTR_FILE_ENTRY_TAG = "fileEntry";
    String XTR_GENERIC_ENTRY_TAG = "genericEntry";
    String XTR_TEST_REPORT_TAG = "testReport";
    String XTR_TEST_SUITE_REPORT_TAG = "testSuiteReport";
    String XTR_URI_ENTRY_TAG = "uriEntry";
    
    /////////////////////////////////////////////////////////////////////////
    // XTR attributes
    /////////////////////////////////////////////////////////////////////////
    String XTR_DATE_ATTRIBUTE = "date";
    String XTR_KEY_ATTRIBUTE = "key";
    String XTR_ERROR_CODE_ATTRIBUTE = "errorCode";
    String XTR_ID_ATTRIBUTE = "id";
    String XTR_STATUS_ATTRIBUTE = "status";
    String XTR_TEST_NAME_ATTRIBUTE = "testName";
    String XTR_VALUE_ATTRIBUTE     = "value";

    /////////////////////////////////////////////////////////////////////////
    // XTR values
    /////////////////////////////////////////////////////////////////////////
    String XTR_PASSED_VALUE = "passed";
    String XTR_FAILED_VALUE = "failed";
}
