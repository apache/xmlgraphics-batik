/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import javax.swing.filechooser.FileFilter;

import java.io.File;
import java.net.MalformedURLException;

/**
 * This class filters file for a given <tt>SquiggleInputHandler</tt>
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SquiggleInputHandlerFilter extends FileFilter {
    protected SquiggleInputHandler handler;

    public SquiggleInputHandlerFilter(SquiggleInputHandler handler) {
        this.handler = handler;
    }

    public boolean accept(File f) {
        return f.isDirectory() || handler.accept(f);
    }

    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        String extensions[] = handler.getHandledExtensions();
        int n = extensions != null ? extensions.length : 0;
        for (int i=0; i<n; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(extensions[i]);
        }

        if (n > 0) {
            sb.append(" ");
        }

        sb.append(handler.getDescription());
        return sb.toString();
    }
}
