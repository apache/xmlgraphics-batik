/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;

import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.ValueManager;

import org.apache.batik.util.CSSConstants;

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class represents an object which provide support for the
 * 'marker' shorthand properties.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class MarkerShorthandManager
    extends AbstractValueFactory
    implements ShorthandManager {
    
    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_MARKER_PROPERTY;
    }
    
    /**
     * Implements {@link ShorthandManager#setValues(CSSEngine,ShorthandManager.PropertyHandler,LexicalUnit,boolean)}.
     */
    public void setValues(CSSEngine eng,
                          ShorthandManager.PropertyHandler ph,
                          LexicalUnit lu,
                          boolean imp)
        throws DOMException {
        ph.property(CSSConstants.CSS_MARKER_END_PROPERTY, lu, imp);
        ph.property(CSSConstants.CSS_MARKER_MID_PROPERTY, lu, imp);
        ph.property(CSSConstants.CSS_MARKER_START_PROPERTY, lu, imp);
    }
}
