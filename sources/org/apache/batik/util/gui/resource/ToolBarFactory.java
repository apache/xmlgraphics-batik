/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * This class represents a tool bar factory which builds
 * tool bars from the content of a resource file.<br>
 *
 * The resource entries format is (for a tool bar named 'ToolBar'):<br>
 * <pre>
 *   ToolBar           = Item1 Item2 - Item3 ...
 *   See ButtonFactory.java for details about the items
 *   ...
 * '-' represents a separator
 * </pre>
 * All entries are optional.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ToolBarFactory extends ResourceManager {
    // Constants
    //
    private final static String SEPARATOR = "-";

    /**
     * The table which contains the actions
     */
    private ActionMap actions;

    /**
     * The button factory
     */
    private ButtonFactory buttonFactory;

    /**
     * Creates a new tool bar factory
     * @param rb the resource bundle that contains the menu bar
     *           description.
     * @param am the actions to add to menu items
     */
    public ToolBarFactory(ResourceBundle rb, ActionMap am) {
	super(rb);
	actions = am;
	buttonFactory = new ButtonFactory(rb, am);
    }

    /**
     * Creates a tool bar
     * @param name the name of the menu bar in the resource bundle
     * @throws MissingResourceException if one of the keys that compose the
     *         tool bar is missing.
     *         It is not thrown if the action key is missing.
     * @throws ResourceFormatException  if a boolean is malformed
     * @throws MissingListenerException if an item action is not found in the
     * action map.
     */
    public JToolBar createJToolBar(String name)
	throws MissingResourceException,
               ResourceFormatException,
	       MissingListenerException {
	JToolBar result  = new JToolBar();
        List     buttons = getStringList(name);
        Iterator it      = buttons.iterator();

        while (it.hasNext()) {
	    String s = (String)it.next();
	    if (s.equals(SEPARATOR)) {
		result.add(new JToolbarSeparator());
	    } else {
		result.add(createJButton(s));
	    }
        }
	return result;
    }

    /**
     * Creates and returns a new swing button
     * @param name the name of the button in the resource bundle
     * @throws MissingResourceException if key is not the name of a button.
     *         It is not thrown if the mnemonic and the action keys are missing
     * @throws ResourceFormatException if the mnemonic is not a single
     *         character
     * @throws MissingListenerException if the button action is not found in
     *         the action map.
     */
    public JButton createJButton(String name)
	throws MissingResourceException,
	       ResourceFormatException,
	       MissingListenerException {
	return buttonFactory.createJToolbarButton(name);
    }
}
