/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGClipPathElement;

/**
 * This class implements {@link SVGClipPathElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMClipPathElement
    extends    SVGGraphicsElement
    implements SVGClipPathElement {

    /**
     * The clipPathUnits values.
     */
    protected final static String[] CLIP_PATH_UNITS_VALUES = {
        "",
        SVG_USER_SPACE_ON_USE_VALUE,
        SVG_OBJECT_BOUNDING_BOX_VALUE
    };

    /**
     * Creates a new SVGOMClipPathElement object.
     */
    protected SVGOMClipPathElement() {
    }

    /**
     * Creates a new SVGOMClipPathElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMClipPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CLIP_PATH_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGClipPathElement#getClipPathUnits()}.
     */
    public SVGAnimatedEnumeration getClipPathUnits() {
        return getAnimatedEnumerationAttribute
            (null, SVG_CLIP_PATH_UNITS_ATTRIBUTE, CLIP_PATH_UNITS_VALUES,
             (short)1);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMClipPathElement();
    }
}
