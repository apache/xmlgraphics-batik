/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

/**
 * Default implementation of SVGLength.
 *
 * This implementation is not linked to any
 * attribute in the Document. It is used
 * by the root element to return a default SVGLength.
 *
 * @see org.w3c.dom.svg.SVGSVGElement#createSVGLength()
 *
 * @author nicolas.socheleau@bitflash.com
 * @version $Id$
 */
public class SVGOMLength extends AbstractSVGLength {

    /**
     * Element associated to this length.
     */
    protected AbstractElement element;

    /**
     * Default constructor.
     *
     * The direction of this length is undefined
     * and this length is not associated to any
     * attribute.
     */
    public SVGOMLength(AbstractElement elt){
        super(OTHER_LENGTH);
        element = elt;
    }

    /**
     */
    protected SVGOMElement getAssociatedElement(){
        return (SVGOMElement)element;
    }

}
