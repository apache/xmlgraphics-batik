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
 * This class provides a factory for the 'alignment-baseline' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class WritingModeFactory extends AbstractIdentifierFactory {

    /**
     * The 'lr' string.
     */
    public final static String LR = "lr";

    /**
     * The 'lr' keyword.
     */
    public final static ImmutableValue LR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LR);

    /**
     * The 'lr-tb' string.
     */
    public final static String LR_TB = "lr-tb";

    /**
     * The 'lr-tb' keyword.
     */
    public final static ImmutableValue LR_TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LR_TB);

    /**
     * The 'rl' string.
     */
    public final static String RL = "rl";

    /**
     * The 'rl' keyword.
     */
    public final static ImmutableValue RL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, RL);

    /**
     * The 'rl-tb' string.
     */
    public final static String RL_TB = "rl-tb";

    /**
     * The 'rl-tb' keyword.
     */
    public final static ImmutableValue RL_TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, RL_TB);

    /**
     * The 'tb' string.
     */
    public final static String TB = "tb";

    /**
     * The 'tb' keyword.
     */
    public final static ImmutableValue TB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TB);

    /**
     * The 'tb-rl' string.
     */
    public final static String TB_RL = "tb-rl";

    /**
     * The 'tb-rl' keyword.
     */
    public final static ImmutableValue TB_RL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TB_RL);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(LR,        LR_VALUE);
	values.put(LR_TB,     LR_TB_VALUE);
	values.put(RL,        RL_VALUE);
	values.put(RL_TB,     RL_TB_VALUE);
	values.put(TB,        TB_VALUE);
	values.put(TB_RL,     TB_RL_VALUE);
    }

    /**
     * Creates a new WritingModeFactory object.
     */
    public WritingModeFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "writing-mode";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
