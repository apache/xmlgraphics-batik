/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2004 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.util.gui.resource;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

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
        // Action
	try {
	    Action a = actions.getAction(getString(name+ACTION_SUFFIX));
	    if (a == null) {
		throw new MissingListenerException("", "Action",
                                                   name+ACTION_SUFFIX);
	    }
	    b.setAction(a);
            try {
                b.setText(getString(name+TEXT_SUFFIX));
            } catch (MissingResourceException mre) {
                // not all buttons have text defined so just
                // ignore this exception.
            }
	    if (a instanceof JComponentModifier) {
		((JComponentModifier)a).addJComponent(b);
	    }
	} catch (MissingResourceException e) {
	}

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
