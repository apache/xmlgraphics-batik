/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer.util.resources;

import javax.swing.JPanel;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

/**
 * Defines localized messages for the parent package.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$ 
 */
public final class Messages {
    /**
     * The error messages bundle class name.
     */
    protected static String RESOURCES =
        "org.apache.batik.refimpl.gvt.renderer.util.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected static LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public static String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    /*
     * Constant values for constant messages, and keys which
     * are used with formatMessage.
     */

    // Error Messages
    public static final String KEY_ERROR_COULD_NOT_LOAD_ENTITY = "error.loading.general";
    public static final String ERROR_INTERNAL_ERROR = formatMessage("error.internalError", null);

    // Menu Bar Menu Titles
    public static final String MENU_ITEM_FILE_OPEN = formatMessage("menuItem.file.open", null);
    public static final String MENU_ITEM_FILE_EXIT = formatMessage("menuItem.file.exit", null);

    // Document Menu Items
    public static final String MENU_ITEM_DOCUMENT_ZOOM_IN = formatMessage("menuItem.document.zoomIn", null);
    public static final String MENU_ITEM_DOCUMENT_ZOOM_OUT = formatMessage("menuItem.document.zoomOut", null);
    public static final String MENU_ITEM_DOCUMENT_PAN = formatMessage("menuItem.document.pan", null);
    public static final String MENU_ITEM_DOCUMENT_PREVIOUS = formatMessage("menuItem.document.previous", null);
    public static final String MENU_ITEM_DOCUMENT_NEXT = formatMessage("menuItem.document.next", null);
    public static final String MENU_ITEM_DOCUMENT_SEARCH = formatMessage("menuItem.document.search", null);
    public static final String MENU_ITEM_DOCUMENT_RELOAD = formatMessage("menuItem.document.reload", null);

    // File Menu Items
    public static final String MENU_TITLE_FILE = formatMessage("menuTitle.file", null);
    public static final String MENU_TITLE_DOCUMENT = formatMessage("menuTitle.document", null);
    public static final String DIALOG_FILE_OPEN_TITLE = formatMessage("dialog.file.open.title", null);
    public static final String DIALOG_EXIT_CONFIRM = formatMessage("dialog.exit.confirm", null);

    // Labels
    public static final String LABEL_LOCATION = formatMessage("label.location", null);
    public static final String LABEL_STATUS = formatMessage("label.status", null);
    public static final String LABEL_VIEWER_TITLE = formatMessage("label.viewerTitle", null);

    // Tooltips
    public static final String TOOL_TIP_PREVIOUS = formatMessage("tooltip.previous", null);
    public static final String TOOL_TIP_NEXT = formatMessage("tooltip.next", null);
    public static final String TOOL_TIP_RELOAD = formatMessage("tooltip.reload", null);
    public static final String TOOL_TIP_SEARCH = formatMessage("tooltip.search", null);
    public static final String TOOL_TIP_ZOOM_IN = formatMessage("tooltip.zoomIn", null);
    public static final String TOOL_TIP_ZOOM_OUT = formatMessage("tooltip.zoomOut", null);
    public static final String TOOL_TIP_PAN = formatMessage("tooltip.pan", null);

    /**
     * Prevent creation of instances of this class
     */
    private Messages(){
    }
}
