/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.io.File;

import org.apache.batik.util.ParsedURL;

/**
 * This implementation of the <tt>SquiggleInputHandler</tt> class
 * simply displays an SVG file into the JSVGCanvas.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGInputHandler implements SquiggleInputHandler {
    public static final String[] SVG_MIME_TYPES = 
    { "image/svg+xml" };

    public static final String[] SVG_FILE_EXTENSIONS =
    { ".svg", ".svgz" };

    /**
     * Returns the list of mime types handled by this handler.
     */
    public String[] getHandledMimeTypes() {
        return SVG_MIME_TYPES;
    }
    
    /**
     * Returns the list of file extensions handled by this handler
     */
    public String[] getHandledExtensions() {
        return SVG_FILE_EXTENSIONS;
    }

    /**
     * Returns a description for this handler.
     */
    public String getDescription() {
        return "";
    }

    /**
     * Handles the given input for the given JSVGViewerFrame
     */
    public void handle(ParsedURL purl, JSVGViewerFrame svgViewerFrame) {
        svgViewerFrame.getJSVGCanvas().loadSVGDocument(purl.toString());
    }

    /**
     * Returns true if the input file can be handled.
     */
    public boolean accept(File f) {
        return f != null && f.isFile() && accept(f.getPath());
    }

    /**
     * Returns true if the input URI can be handled by the handler
     */
    public boolean accept(ParsedURL purl) {
        // <!> Note: this should be improved to rely on Mime Type 
        //     when the http protocol is used. This will use the 
        //     ParsedURL.getContentType method.
        if (purl == null) {
            return false;
        }

        String path = purl.getPath();
        if (path == null) return false;

        return accept(path);
    }

    /**
     * Returns true if the resource at the given path can be handled
     */
    public boolean accept(String path) {
        if (path == null) return false;
        for (int i=0; i<SVG_FILE_EXTENSIONS.length; i++) {
            if (path.endsWith(SVG_FILE_EXTENSIONS[i])) {
                return true;
            }
        }

        return false;
    }
}
