/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Proxy to an <code>SVGDocument</code> using a <code>WeakReference</code> to
 * allow the document to be discared when not used outside of the intepreter.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class SVGDocumentProxy extends DocumentProxy
    implements SVGDocument {

    public SVGDocumentProxy(SVGDocument document)
    {
        super(document);
    }

    private SVGDocument svg()
    {
        return (SVGDocument)ref.get();
    }

    // DocumentEvent implementation

    /**
     *
     * @param eventTypeThe <code>eventType</code> parameter specifies the
     *   type of <code>Event</code> interface to be created. If the
     *   <code>Event</code> interface specified is supported by the
     *   implementation this method will return a new <code>Event</code> of
     *   the interface type requested. If the <code>Event</code> is to be
     *   dispatched via the <code>dispatchEvent</code> method the
     *   appropriate event init method must be called after creation in
     *   order to initialize the <code>Event</code>'s values. As an example,
     *   a user wishing to synthesize some kind of <code>UIEvent</code>
     *   would call <code>createEvent</code> with the parameter "UIEvents".
     *   The <code>initUIEvent</code> method could then be called on the
     *   newly created <code>UIEvent</code> to set the specific type of
     *   UIEvent to be dispatched and set its context information.The
     *   <code>createEvent</code> method is used in creating
     *   <code>Event</code>s when it is either inconvenient or unnecessary
     *   for the user to create an <code>Event</code> themselves. In cases
     *   where the implementation provided <code>Event</code> is
     *   insufficient, users may supply their own <code>Event</code>
     *   implementations for use with the <code>dispatchEvent</code> method.
     * @return The newly created <code>Event</code>
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support the
     *   type of <code>Event</code> interface requested
     */
    public Event createEvent(String eventType)
        throws DOMException
    {
        return svg().createEvent(eventType);
    }

    // SVGDocument implementation

    public String getTitle()
    {
        return svg().getTitle();
    }

    public String getReferrer()
    {
        return svg().getReferrer();
    }

    public String getDomain()
    {
        return svg().getDomain();
    }

    public String getURL()
    {
        return svg().getURL();
    }

    public SVGSVGElement getRootElement()
    {
        return svg().getRootElement();
    }
}

