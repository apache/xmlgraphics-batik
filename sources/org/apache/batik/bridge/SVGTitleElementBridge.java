/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;title&gt; element.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGTitleElementBridge extends AbstractSVGBridge implements GenericBridge {

    /**
     * Constructs a new bridge for the &lt;title&gt; element.
     */
    public SVGTitleElementBridge() {}

    /**
     * Returns 'title'.
     */
    public String getLocalName() {
        return SVG_TITLE_TAG;
    }

    /**
     * Invoked to handle an <tt>Element</tt> for a given <tt>BridgeContext</tt>.
     * For example, see the <tt>SVGTitleElementBridge</tt>.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     */
    public void handleElement(BridgeContext ctx, Element e){
        UserAgent ua = ctx.getUserAgent();
        ua.handleElement(e, null);
    }

}

