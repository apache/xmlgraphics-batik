/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGNumber;

/**
 * This class implements the {@link SVGNumber} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMNumber implements SVGNumber {

    /**
     * The value of this number.
     */
    protected float value;

    /**
     * The associated attribute modifier.
     */
    protected ModificationHandler modificationHandler;

    /**
     * Whether or not the current change is due to an internal change.
     */
    protected boolean internalChange;

    /**
     * Sets the associated attribute modifier.
     */
    public void setModificationHandler(ModificationHandler mh) {
        modificationHandler = mh;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumber#getValue()}.
     */
    public float getValue() {
        return value;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGNumber#setValue(float)}.
     */
    public void setValue(float val) throws DOMException {
        value = val;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this, Float.toString(val));
            internalChange = false;
        }
    }

    /**
     * Parses the given string.
     */
    public void parseValue(String val) {
        if (!internalChange) {
            try {
                value = Float.parseFloat(val);
            } catch (NumberFormatException e) {
                throw new DOMException(DOMException.SYNTAX_ERR, e.getMessage());
            }
        }
    }
}
