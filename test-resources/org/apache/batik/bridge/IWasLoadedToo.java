/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.bridge;

import org.w3c.dom.*;
import org.apache.batik.script.ScriptHandler;
import org.apache.batik.script.Window;

/**
 * If this script is loaded by jarCheckLoad.svg, it will mark
 * the test result as passed.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class IWasLoadedToo implements ScriptHandler {
    public void run(final Document document, final Window win){
        Element result = document.getElementById("testResult");
        result.setAttributeNS(null, "result", "passed");
    }
}
