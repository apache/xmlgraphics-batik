/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import org.apache.batik.test.xml.XMLTestReportProcessor;

/**
 * This implementation of the <tt>TestReportProcessor</tt> interface
 * converts the <tt>TestReports</tt> it processes into an 
 * XML document that it outputs in a directory. The directory
 * used by the object can be configured at creation time.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGAccuracyTestProcessor extends XMLTestReportProcessor{
}
