/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.util;

import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;


/**
 * This is a helper class to set the desired default resources
 * in Swing UIs
 *
 * @version $Id$
 */
public class SwingInitializer {
    /**
     * Initializes Swing resources
     */
    public static void swingDefaultsFontInit(Font font){
        FontUIResource fontRes = new FontUIResource(font);
        UIManager.put("CheckBox.font", fontRes);
        UIManager.put("PopupMenu.font", fontRes);
        UIManager.put("TextPane.font", fontRes);
        UIManager.put("MenuItem.font", fontRes);
        UIManager.put("ComboBox.font", fontRes);
        UIManager.put("Button.font", fontRes);
        UIManager.put("Tree.font", fontRes);
        UIManager.put("ScrollPane.font", fontRes);
        UIManager.put("TabbedPane.font", fontRes);
        UIManager.put("EditorPane.font", fontRes);
        UIManager.put("TitledBorder.font", fontRes);
        UIManager.put("Menu.font", fontRes);
        UIManager.put("TextArea.font", fontRes);
        UIManager.put("OptionPane.font", fontRes);
        UIManager.put("DesktopIcon.font", fontRes);
        UIManager.put("MenuBar.font", fontRes);
        UIManager.put("ToolBar.font", fontRes);
        UIManager.put("RadioButton.font", fontRes);
        UIManager.put("ToggleButton.font", fontRes);
        UIManager.put("ToolTip.font", fontRes);
        UIManager.put("ProgressBar.font", fontRes);
        UIManager.put("TableHeader.font", fontRes);
        UIManager.put("Panel.font", fontRes);
        UIManager.put("List.font", fontRes);
        UIManager.put("ColorChooser.font", fontRes);
        UIManager.put("PasswordField.font", fontRes);
        UIManager.put("TextField.font", fontRes);
        UIManager.put("Table.font", fontRes);
        UIManager.put("Label.font", fontRes);
        UIManager.put("InternalFrameTitlePane.font", fontRes);
        UIManager.put("CheckBoxMenuItem.font", fontRes);
    }
}
