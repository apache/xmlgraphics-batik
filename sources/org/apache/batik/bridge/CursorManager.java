/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;

import java.util.Map;
import java.util.Hashtable;

import org.apache.batik.util.SVGConstants;

/**
 * The CursorManager class is a helper class which preloads the cursors 
 * corresponding to the SVG built in cursors.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class CursorManager implements SVGConstants {
    /**
     * Maps SVG Cursor Values to Java Cursors
     */
    protected static Map cursorMap;

    /**
     * Default cursor when value is not found
     */
    public static final Cursor DEFAULT_CURSOR 
        = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /**
     * Cursor used over anchors
     */
    public static final Cursor ANCHOR_CURSOR
        = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /**
     * Cursor used over text
     */
    public static final Cursor TEXT_CURSOR
        = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

    /**
     * Static initialization of the cursorMap
     */
    static {
        cursorMap = new Hashtable();
        cursorMap.put(SVG_CROSSHAIR_VALUE,
                      Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        cursorMap.put(SVG_DEFAULT_VALUE,
                      Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        cursorMap.put(SVG_POINTER_VALUE,
                      Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cursorMap.put(SVG_MOVE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        cursorMap.put(SVG_E_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        cursorMap.put(SVG_NE_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
        cursorMap.put(SVG_NW_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        cursorMap.put(SVG_N_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        cursorMap.put(SVG_SE_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        cursorMap.put(SVG_SW_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
        cursorMap.put(SVG_S_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        cursorMap.put(SVG_W_RESIZE_VALUE,
                      Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        cursorMap.put(SVG_TEXT_VALUE,
                      Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        cursorMap.put(SVG_WAIT_VALUE,
                      Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cursorMap.put(SVG_HELP_VALUE, 
                      Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
        
    }

    /**
     * Returns a Cursor object for a given cursor value. This initial implementation
     * does not handle user-defined cursors, so it always uses the cursor at the 
     * end of the list
     */
    public static Cursor getCursor(String cursorName){
        Cursor c = (Cursor)cursorMap.get(cursorName);
        return c != null ? c : DEFAULT_CURSOR;
    }
    
}
