/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.util.EventObject;

import org.w3c.dom.svg.SVGAElement;

/**
 * This class represents an event which indicate an event originated
 * from a GVTTreeBuilder instance.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LinkActivationEvent extends EventObject {
    
    /**
     * The URI the link references.
     */
    protected String referencedURI;

    /**
     * Creates a new LinkActivationEvent.
     * @param source the object that originated the event, ie. the
     *               GVTTreeBuilder.
     * @param link   the link element.
     * @param uri    the URI of the document loaded.
     */
    public LinkActivationEvent(Object source, SVGAElement link, String uri) {
        super(source);
        referencedURI = uri;
    }

    /**
     * Returns the referenced URI.
     */
    public String getReferencedURI() {
        return referencedURI;
    }
}
