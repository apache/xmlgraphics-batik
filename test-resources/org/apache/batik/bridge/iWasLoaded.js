/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
    
/**
 * If this script is loaded by ecmaCheckNoLoad.svg, it will mark
 * the test result as failed.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

    var result = document.getElementById("testResult");
    result.setAttributeNS(null, "result", "failed");
    result.setAttributeNS(null, "errorCode", "iWasLoaded.js should not have been loaded");
