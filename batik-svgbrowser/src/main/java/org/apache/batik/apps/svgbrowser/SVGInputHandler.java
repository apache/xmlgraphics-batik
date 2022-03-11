/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.apps.svgbrowser;

import java.io.File;

import org.apache.batik.util.ParsedURL;

/**
 * This implementation of the <code>SquiggleInputHandler</code> class
 * simply displays an SVG file into the JSVGCanvas.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
//@ServiceProvider(value=SquiggleInputHandler.class,attribute ={
//		"mimeTypes:List<String>='"+SVGInputHandler.MIME_TYPE_IMAGE_SVG_XML+"'",
//		"extensions:List<String>='"+SVGInputHandler.EXTENSION_SVG+","+SVGInputHandler.EXTENSION_SVGZ+"'"})
public class SVGInputHandler implements SquiggleInputHandler {

	public static final String MIME_TYPE_IMAGE_SVG_XML = "image/svg+xml";

	public static final String EXTENSION_SVGZ = ".svgz";
	
	public static final String EXTENSION_SVG = ".svg";

	public static final String[] SVG_MIME_TYPES = 
    { MIME_TYPE_IMAGE_SVG_XML };

    public static final String[] SVG_FILE_EXTENSIONS =
    { EXTENSION_SVG, EXTENSION_SVGZ };

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
        for (String SVG_FILE_EXTENSION : SVG_FILE_EXTENSIONS) {
            if (path.endsWith(SVG_FILE_EXTENSION)) {
                return true;
            }
        }

        return false;
    }
}
