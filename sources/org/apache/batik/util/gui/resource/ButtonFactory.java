/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import java.net.URL;
import java.util.*;
import javax.swing.*;

/**
 * This class represents a button factory which builds
 * buttons from the content of a resource bundle.<br>
 *
 * The resource entries format is (for a button named 'Button'):<br>
 * <pre>
 *   Button.text      = text
 *   Button.icon      = icon_name 
 *   Button.mnemonic  = mnemonic 
 *   Button.action    = action_name
 *   Button.selected  = true | false
 *   Button.tooltip   = tool tip text
 * where
 *   text, icon_name and action_name are strings
 *   mnemonic is a character
 * </pre>
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ButtonFactory extends ResourceManager {
    // Constants
    //
    private final static String ICON_SUFFIX        = ".icon";
    private final static String TEXT_SUFFIX        = ".text";
    private final static String MNEMONIC_SUFFIX    = ".mnemonic";
    private final static String ACTION_SUFFIX      = ".action";
    private final static String SELECTED_SUFFIX    = ".selected";
    private final static String TOOLTIP_SUFFIX     = ".tooltip";

    /** The table which contains the actions */
    private ActionMap actions;

    /**
     * Creates a new button factory
     * @param rb the resource bundle that contains the buttons
     *           description.
     * @param am the actions to bind to the button
     */
    public ButtonFactory(ResourceBundle rb, ActionMap am) {
        super(rb);
        actions = am;
    }

    /**
     * Creates and returns a new swing button
     * @param name the name of the button in the resource bundle
     * @throws MissingResourceException if key is not the name of a button.
     *         It is not thrown if the mnemonic and the action keys are missing
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character
     * @throws MissingListenerException if the button action is not found in
     *         the action map
     */
    public JButton createJButton(String name)
	throws MissingResourceException,
	       ResourceFormatException,
	       MissingListenerException {
        JButton result;
	try {
	    result = new JButton(getString(name+TEXT_SUFFIX));
	} catch (MissingResourceException e) {
	    result = new JButton();
	}
	initializeButton(result, name);
        return result;
    }

    /**
     * Creates and returns a new swing button initialised
     * to be used as a toolbar button
     * @param name the name of the button in the resource bundle
     * @throws MissingResourceException if key is not the name of a button.
     *         It is not thrown if the mnemonic and the action keys are missing
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character
     * @throws MissingListenerException if the button action is not found in
     *         the action map
     */
    public JButton createJToolbarButton(String name)
	throws MissingResourceException,
	       ResourceFormatException,
	       MissingListenerException {
        JButton result;
	try {
	    result = new JToolbarButton(getString(name+TEXT_SUFFIX));
	} catch (MissingResourceException e) {
	    result = new JToolbarButton();
	}
	initializeButton(result, name);
        return result;
    }

    /**
     * Creates and returns a new swing radio button
     * @param name the name of the button in the resource bundle
     * @throws MissingResourceException if key is not the name of a button.
     *         It is not thrown if the mnemonic and the action keys are
     *         missing.
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character.
     * @throws MissingListenerException if the button action is not found in
     *         the action map.
     */
    public JRadioButton createJRadioButton(String name)
	throws MissingResourceException,
	       ResourceFormatException,
	       MissingListenerException {
        JRadioButton result = new JRadioButton(getString(name+TEXT_SUFFIX));
	initializeButton(result, name);

        // is the button selected?
	try {
	    result.setSelected(getBoolean(name+SELECTED_SUFFIX));
	} catch (MissingResourceException e) {
	}
	
        return result;
    }

    /**
     * Creates and returns a new swing check box
     * @param name the name of the button in the resource bundle
     * @throws MissingResourceException if key is not the name of a button.
     *         It is not thrown if the mnemonic and the action keys are missing
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character.
     * @throws MissingListenerException if the button action is not found in
     *         the action map.
     */
    public JCheckBox createJCheckBox(String name)
	throws MissingResourceException,
	       ResourceFormatException,
	       MissingListenerException {
        JCheckBox result = new JCheckBox(getString(name+TEXT_SUFFIX));
	initializeButton(result, name);

        // is the button selected?
	try {
	    result.setSelected(getBoolean(name+SELECTED_SUFFIX));
	} catch (MissingResourceException e) {
	}
	
        return result;
    }

    /**
     * Initializes a button
     * @param b    the button to initialize
     * @param name the button's name
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character.
     * @throws MissingListenerException if the button action is not found
     *         in the action map.
     */
    private void initializeButton(AbstractButton b, String name)
	throws ResourceFormatException, MissingListenerException {
	// Icon
	try {
	    String s = getString(name+ICON_SUFFIX);
	    URL url  = actions.getClass().getResource(s);
	    if (url != null) {
		b.setIcon(new ImageIcon(url));
	    }
	} catch (MissingResourceException e) {
	}

        // Mnemonic
	try {
	    String str = getString(name+MNEMONIC_SUFFIX);
	    if (str.length() == 1) {
		b.setMnemonic(str.charAt(0));
	    } else {
		throw new ResourceFormatException("Malformed mnemonic",
						  bundle.getClass().getName(),
						  name+MNEMONIC_SUFFIX);
	    }
	} catch (MissingResourceException e) {
	}

        // Action
	try {
	    Action a = actions.getAction(getString(name+ACTION_SUFFIX));
	    if (a == null) {
		throw new MissingListenerException("", "Action",
                                                   name+ACTION_SUFFIX);
	    }
	    b.addActionListener(a);
	    if (a instanceof JComponentModifier) {
		((JComponentModifier)a).addJComponent(b);
	    }
	} catch (MissingResourceException e) {
	}

	// ToolTip
	try {
	    String s = getString(name+TOOLTIP_SUFFIX);
	    if (s != null) {
		b.setToolTipText(s);
	    }
	} catch (MissingResourceException e) {
	}
    }
}
