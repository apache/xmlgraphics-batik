/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;


/**
 * This class provides an implementation of the {@link
 * SVGAnimatedLength} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMAnimatedLength extends AbstractSVGAnimatedLength {

    /**
     * The default value if the attribute is not specified.
     */
    protected String defaultValue;

    /**
     * Creates a new SVGAnimatedLength.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param def The default value if the attribute is not specified.
     * @param dir The length's direction.
     */
    public SVGOMAnimatedLength(AbstractElement elt,
                               String ns,
                               String ln,
                               String def,
                               short dir) {
        super(elt, ns, ln, dir);
        defaultValue = def;
    }

    /**
     * Returns the default value to use when the associated attribute
     * was not specified.
     */
    protected String getDefaultValue() {
        return defaultValue;
    }

}
