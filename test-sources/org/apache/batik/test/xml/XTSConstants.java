/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

/**
 * Contains constants for the XML Test Suite (XTS) syntax.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface XTSConstants {
    String XTS_NAMESPACE_URI 
        = "http://xml.apache.org/xml-batik/test/xts";

    
    /////////////////////////////////////////////////////////////////////////
    // XTS tags
    /////////////////////////////////////////////////////////////////////////
    String XTS_ARG_TAG        = "arg";
    String XTS_TEST_TAG       = "test";
    String XTS_TEST_SUITE_TAG = "testSuite";
    String XTS_TEST_REPORT_PROCESSOR_TAG = "testReportProcessor";
        

    
    /////////////////////////////////////////////////////////////////////////
    // XTS attributes
    /////////////////////////////////////////////////////////////////////////
    String XTS_CLASS_ATTRIBUTE = "class";
    String XTS_NAME_ATTRIBUTE  = "name";
    String XTS_VALUE_ATTRIBUTE = "value";
}
