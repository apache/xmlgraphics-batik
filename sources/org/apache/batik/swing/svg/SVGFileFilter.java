/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * This implementation of FileFilter will allows SVG files
 * with extention '.svg' or '.svgz'.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class SVGFileFilter extends FileFilter {
    /**
     * Returns true if <tt>f</tt> is an SVG file
     */
    public boolean accept(File f) {
        boolean accept = false;
        String fileName = null;
        if (f != null) {
            if (f.isDirectory()) {
                accept = true;
            } else {
                fileName = f.getPath().toLowerCase();
                if (fileName.endsWith(".svg") || fileName.endsWith(".svgz"))
                    accept = true;
            }
        }
        return accept;
    }

    /**
     * Returns the file description
     */
    public String getDescription() {
        return ".svg, .svgz";
    }
}
