/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import javax.swing.Action;

/**
 * This interface represents a SVG viewer application.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Application {

    /**
     * Creates and shows a new viewer frame.
     */
    JSVGViewerFrame createAndShowJSVGViewerFrame();

    /**
     * Closes the given viewer frame.
     */
    void closeJSVGViewerFrame(JSVGViewerFrame f);

    /**
     * Creates an action to exit the application.
     */
    Action createExitAction(JSVGViewerFrame vf);

    /**
     * Opens the given link in a new window.
     */
    void openLink(String url);

    /**
     * Returns the XML parser class name.
     */
    String getXMLParserClassName();

}
