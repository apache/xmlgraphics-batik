/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.i18n;

import org.apache.batik.i18n.*;

/**
 * To test the LocalizableSupport class.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocalizableSupportTest {
    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        LocalizableSupport lc;
        lc = new LocalizableSupport("org.apache.batik.i18n.test.Messages");
        
        String s;
        s = lc.formatMessage("test.message", new Object[] { "parameter" });
        System.out.println(" ***** Test 1 *****\n" + s);

        s = lc.formatMessage("test.message2", null);
        System.out.println(" ***** Test 2 *****\n" + s);
    }
}
