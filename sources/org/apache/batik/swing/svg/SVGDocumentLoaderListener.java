/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * This interface represents a listener to the SVGDocumentLoaderEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGDocumentLoaderListener {

    /**
     * Called when the loading of a document was started.
     */
    void documentLoadingStarted(SVGDocumentLoaderEvent e);

    /**
     * Called when the loading of a document was completed.
     */
    void documentLoadingCompleted(SVGDocumentLoaderEvent e);

    /**
     * Called when the loading of a document was cancelled.
     */
    void documentLoadingCancelled(SVGDocumentLoaderEvent e);

    /**
     * Called when the loading of a document has failed.
     */
    void documentLoadingFailed(SVGDocumentLoaderEvent e);
}
