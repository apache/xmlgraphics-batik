/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractIdentifierFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'shape-rendering' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ShapeRenderingFactory extends AbstractIdentifierFactory {
    /**
     * The 'crispEdges' string.
     */
    public final static String CRISPEDGES = "crispedges";

    /**
     * The 'crispEdges' keyword.
     */
    public final static ImmutableValue CRISPEDGES_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, CRISPEDGES);

    /**
     * The 'geometricPrecision' string.
     */
    public final static String GEOMETRICPRECISION = "geometricprecision";

    /**
     * The 'geometricPrecision' keyword.
     */
    public final static ImmutableValue GEOMETRICPRECISION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, GEOMETRICPRECISION);

    /**
     * The 'optimizeSpeed' keyword.
     */
    public final static String OPTIMIZESPEED = "optimizespeed";

    /**
     * The 'optimizeSpeed' keyword.
     */
    public final static ImmutableValue OPTIMIZESPEED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, OPTIMIZESPEED);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,               AUTO_VALUE);
	values.put(CRISPEDGES,         CRISPEDGES_VALUE);
	values.put(GEOMETRICPRECISION, GEOMETRICPRECISION_VALUE);
	values.put(OPTIMIZESPEED,      OPTIMIZESPEED_VALUE);
    }

    /**
     * Creates a new ShapeRenderingFactory object.
     */
    public ShapeRenderingFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "shape-rendering";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
