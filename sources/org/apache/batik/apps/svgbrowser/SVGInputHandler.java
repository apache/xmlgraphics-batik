/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
