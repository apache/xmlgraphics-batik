/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.test.svg.SelfContainedSVGOnLoadTest;

/**
 * Helper class to simplify writing the unitTesting.xml file for 
 * CSS DOM Tests.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

public class EcmaScriptSVGDOMTest extends SelfContainedSVGOnLoadTest {
    public void setId(String id){
        super.setId(id);
        svgURL = resolveURL("test-resources/org/apache/batik/dom/svg/" + id + ".svg");
    }
}
