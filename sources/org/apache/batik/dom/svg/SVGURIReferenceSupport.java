/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides support for the SVGURIReference interface methods.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGURIReferenceSupport implements SVGConstants {
    /**
     * The reference to the 'href' animated string.
     */
    protected WeakReference hrefReference;

    /**
     * To implement {@link org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref(Element elt) {
        SVGAnimatedString result;
        if (hrefReference == null ||
            (result = (SVGAnimatedString)hrefReference.get()) == null) {
            result = new SVGOMAnimatedString
                (elt, XLinkSupport.XLINK_NAMESPACE_URI, SVG_HREF_ATTRIBUTE);
            hrefReference = new WeakReference(result);
        }
        return result;
    }
}
