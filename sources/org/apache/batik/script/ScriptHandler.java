/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import org.w3c.dom.Document;

/**
 * This interface must be implemented in order to call Java code from
 * an SVG document.
 *
 * A ScriptHandler instance is called when a 'script' element's 'type'
 * attribute value is 'application/x-java-jar-file' and when the
 * manifest of the jar file referenced by the 'xlink:href' attribute
 * contains a 'Script-Handler' entry.  The value of this entry must be
 * the classname of the ScriptHandler to call.
 *
 * This classes implementing this interface must have a default
 * constructor.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ScriptHandler {
    
    /**
     * Runs this handler.  This method is called by the SVG viewer
     * when the scripts are loaded.
     * @param doc The current document.
     * @param win An object which represents the current viewer.
     */
    void run(Document doc, Window win);
}
