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
 * This is the interface expected from classes which can handle specific 
 * types of input for the Squiggle SVG browser. The simplest implementation
 * will simply handle SVG documents. Other, more sophisticated implementations
 * will handle other types of documents and convert them into SVG before
 * displaying them in an SVG canvas.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface SquiggleInputHandler {
    /**
     * Returns the list of mime types handled by this handler.
     */
    String[] getHandledMimeTypes();

    /**
     * Returns the list of file extensions handled by this handler
     */
    String[] getHandledExtensions();

    /**
     * Returns a description for this handler
     */
    String getDescription();

    /**
     * Returns true if the input file can be handled by the handler
     */
    boolean accept(File f);

    /**
     * Returns true if the input URI can be handled by the handler
     * @param purl URL describing the candidate input
     */
    boolean accept(ParsedURL purl);

    /**
     * Handles the given input for the given JSVGViewerFrame
     */
    void handle(ParsedURL purl, JSVGViewerFrame svgFrame) throws Exception ;
}
