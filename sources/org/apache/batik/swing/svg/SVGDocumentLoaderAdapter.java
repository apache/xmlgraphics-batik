/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * An adapter class that represents a listener to the
 * <tt>SVGDocumentLoaderEvent</tt> events.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class SVGDocumentLoaderAdapter
    implements SVGDocumentLoaderListener {

    /**
     * Called when the loading of a document was started.
     */
    public void documentLoadingStarted(SVGDocumentLoaderEvent e) {}

    /**
     * Called when the loading of a document was completed.
     */
    public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {}

    /**
     * Called when the loading of a document was cancelled.
     */
    public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {}

    /**
     * Called when the loading of a document has failed.
     */
    public void documentLoadingFailed(SVGDocumentLoaderEvent e) {}
}
