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
public class DisplayFactory extends AbstractIdentifierFactory {
    /**
     * The 'block' string.
     */
    public final static String BLOCK = "block";

    /**
     * The 'block' identifier value.
     */
    public final static ImmutableValue BLOCK_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BLOCK);

    /**
     * The 'compact' string.
     */
    public final static String COMPACT = "compact";

    /**
     * The 'compact' identifier value.
     */
    public final static ImmutableValue COMPACT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, COMPACT);

    /**
     * The 'inline' string.
     */
    public final static String INLINE = "inline";

    /**
     * The 'inline' identifier value.
     */
    public final static ImmutableValue INLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INLINE);

    /**
     * The 'inline-table' string.
     */
    public final static String INLINE_TABLE = "inline-table";

    /**
     * The 'inline-table' identifier value.
     */
    public final static ImmutableValue INLINE_TABLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, INLINE_TABLE);

    /**
     * The 'list-item' string.
     */
    public final static String LIST_ITEM = "list-item";

    /**
     * The 'list-item' identifier value.
     */
    public final static ImmutableValue LIST_ITEM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LIST_ITEM);

    /**
     * The 'marker' string.
     */
    public final static String MARKER = "marker";

    /**
     * The 'marker' identifier value.
     */
    public final static ImmutableValue MARKER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MARKER);

    /**
     * The 'run-in' string.
     */
    public final static String RUN_IN = "run-in";

    /**
     * The 'run-in' identifier value.
     */
    public final static ImmutableValue RUN_IN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, RUN_IN);

    /**
     * The 'table' string.
     */
    public final static String TABLE = "table";

    /**
     * The 'table' identifier value.
     */
    public final static ImmutableValue TABLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE);

    /**
     * The 'table-caption' string.
     */
    public final static String TABLE_CAPTION = "table-caption";

    /**
     * The 'table-caption' identifier value.
     */
    public final static ImmutableValue TABLE_CAPTION_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_CAPTION);

    /**
     * The 'table-cell' string.
     */
    public final static String TABLE_CELL = "table-cell";

    /**
     * The 'table-cell' identifier value.
     */
    public final static ImmutableValue TABLE_CELL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_CELL);

    /**
     * The 'table-column' string.
     */
    public final static String TABLE_COLUMN = "table-column";

    /**
     * The 'table-column' identifier value.
     */
    public final static ImmutableValue TABLE_COLUMN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_COLUMN);

    /**
     * The 'table-column-group' string.
     */
    public final static String TABLE_COLUMN_GROUP = "table-column-group";

    /**
     * The 'table-column-group' identifier value.
     */
    public final static ImmutableValue TABLE_COLUMN_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_COLUMN_GROUP);

    /**
     * The 'table-footer-group' string.
     */
    public final static String TABLE_FOOTER_GROUP = "table-footer-group";

    /**
     * The 'table-footer-group' identifier value.
     */
    public final static ImmutableValue TABLE_FOOTER_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_FOOTER_GROUP);

    /**
     * The 'table-header-group' string.
     */
    public final static String TABLE_HEADER_GROUP = "table-header-group";

    /**
     * The 'table-header-group' identifier value.
     */
    public final static ImmutableValue TABLE_HEADER_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_HEADER_GROUP);

    /**
     * The 'table-row' string.
     */
    public final static String TABLE_ROW = "table-row";

    /**
     * The 'table-row' identifier value.
     */
    public final static ImmutableValue TABLE_ROW_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_ROW);

    /**
     * The 'table-row-group' string.
     */
    public final static String TABLE_ROW_GROUP = "table-row-group";

    /**
     * The 'table-row-group' identifier value.
     */
    public final static ImmutableValue TABLE_ROW_GROUP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TABLE_ROW_GROUP);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BLOCK,                BLOCK_VALUE);
	values.put(COMPACT,              COMPACT_VALUE);
	values.put(INLINE,               INLINE_VALUE);
	values.put(INLINE_TABLE,         INLINE_TABLE_VALUE);
	values.put(LIST_ITEM,            LIST_ITEM_VALUE);
	values.put(MARKER,               MARKER_VALUE);
	values.put(NONE,                 NONE_VALUE);
	values.put(RUN_IN,               RUN_IN_VALUE);
	values.put(TABLE,                TABLE_VALUE);
	values.put(TABLE_CAPTION,        TABLE_CAPTION_VALUE);
	values.put(TABLE_CELL,           TABLE_CELL_VALUE);
	values.put(TABLE_COLUMN,         TABLE_COLUMN_VALUE);
	values.put(TABLE_COLUMN_GROUP,   TABLE_COLUMN_GROUP_VALUE);
	values.put(TABLE_FOOTER_GROUP,   TABLE_FOOTER_GROUP_VALUE);
	values.put(TABLE_HEADER_GROUP,   TABLE_HEADER_GROUP_VALUE);
	values.put(TABLE_ROW,            TABLE_ROW_VALUE);
	values.put(TABLE_ROW_GROUP,      TABLE_ROW_GROUP_VALUE);
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
