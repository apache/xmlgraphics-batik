/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGExternalResourcesRequired;

/**
 * Provides support for the SVGExternalResourcesRequired interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGExternalResourcesRequiredSupport {
    /**
     * The externalResourcesRequired attribute name.
     */
    public final static String EXTERNAL_RESOURCE_REQUIRED =
	"externalResourcesRequired";

    /**
     * The externalResourcesRequired attribute reference.
     */
    protected WeakReference externalResourcesRequired;

    /**
     * To implement {@link
     * SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired(Element elt) {
	SVGAnimatedBoolean ab;
	if (externalResourcesRequired == null ||
            (ab = (SVGAnimatedBoolean)externalResourcesRequired.get()) ==
             null) {
	    ab = new SVGOMAnimatedBoolean(elt, null,
                                          EXTERNAL_RESOURCE_REQUIRED);
	    externalResourcesRequired = new WeakReference(ab);
	}
	return ab;
    }
}
