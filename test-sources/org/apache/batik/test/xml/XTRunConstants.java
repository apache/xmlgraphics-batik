/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

/**
 * Contains constants for the XML Test Run (XTRun) syntax.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface XTRunConstants {
    String XTRun_NAMESPACE_URI 
        = "http://xml.apache.org/xml-batik/test/xtrun";

    /////////////////////////////////////////////////////////////////////////
    // XTRun tags
    /////////////////////////////////////////////////////////////////////////
    String XTRun_TEST_RUN_TAG = "testRun";
    String XTRun_TEST_SUITE_TAG = "testSuite";
    String XTRun_TEST_REPORT_PROCESSOR_TAG = "testReportProcessor";

    /////////////////////////////////////////////////////////////////////////
    // XTRun attributes
    /////////////////////////////////////////////////////////////////////////
    String XTRun_HREF_ATTRIBUTE = "href";
    String XTRun_NAME_ATTRIBUTE  = "name";
}
