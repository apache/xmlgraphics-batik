/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'display' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DisplayFactory
    extends    AbstractIdentifierFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_BLOCK_VALUE,                BLOCK_VALUE);
	values.put(CSS_COMPACT_VALUE,              COMPACT_VALUE);
	values.put(CSS_INLINE_VALUE,               INLINE_VALUE);
	values.put(CSS_INLINE_TABLE_VALUE,         INLINE_TABLE_VALUE);
	values.put(CSS_LIST_ITEM_VALUE,            LIST_ITEM_VALUE);
	values.put(CSS_MARKER_VALUE,               MARKER_VALUE);
	values.put(CSS_NONE_VALUE,                 NONE_VALUE);
	values.put(CSS_RUN_IN_VALUE,               RUN_IN_VALUE);
	values.put(CSS_TABLE_VALUE,                TABLE_VALUE);
	values.put(CSS_TABLE_CAPTION_VALUE,        TABLE_CAPTION_VALUE);
	values.put(CSS_TABLE_CELL_VALUE,           TABLE_CELL_VALUE);
	values.put(CSS_TABLE_COLUMN_VALUE,         TABLE_COLUMN_VALUE);
	values.put(CSS_TABLE_COLUMN_GROUP_VALUE,   TABLE_COLUMN_GROUP_VALUE);
	values.put(CSS_TABLE_FOOTER_GROUP_VALUE,   TABLE_FOOTER_GROUP_VALUE);
	values.put(CSS_TABLE_HEADER_GROUP_VALUE,   TABLE_HEADER_GROUP_VALUE);
	values.put(CSS_TABLE_ROW_VALUE,            TABLE_ROW_VALUE);
	values.put(CSS_TABLE_ROW_GROUP_VALUE,      TABLE_ROW_GROUP_VALUE);
    }

    /**
     * Creates a new DisplayFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public DisplayFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "display";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
