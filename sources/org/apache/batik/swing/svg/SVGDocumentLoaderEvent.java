/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.util.EventObject;

import org.w3c.dom.svg.SVGDocument;

/**
 * This class represents an event which indicate an event originated
 * from a SVGDocumentLoader instance.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGDocumentLoaderEvent extends EventObject {
    
    /**
     * The associated SVG document.
     */
    protected SVGDocument svgDocument;

    /**
     * Creates a new SVGDocumentLoaderEvent.
     * @param source the object that originated the event, ie. the
     *               SVGDocumentLoader.
     * @param doc The associated document.
     */
    public SVGDocumentLoaderEvent(Object source, SVGDocument doc) {
        super(source);
        svgDocument = doc;
    }

    /**
     * Returns the associated SVG document, or null if the loading
     * was just started or an error occured.
     */
    public SVGDocument getSVGDocument() {
        return svgDocument;
    }
}
