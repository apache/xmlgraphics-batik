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

package org.apache.batik.util.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class is a dialog used to enter an URI or to choose a local file
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class URIChooser extends JDialog implements ActionMap {

    /**
     * The return value if 'OK' is chosen.
     */
    public final static int OK_OPTION = 0;

    /**
     * The return value if 'Cancel' is chosen.
     */
    public final static int CANCEL_OPTION = 1;

    /**
     * The resource file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.util.gui.resources.URIChooserMessages";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager resources;
    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        resources = new ResourceManager(bundle);
    }
    
    /**
     * The button factory
     */
    protected ButtonFactory buttonFactory;

    /**
     * The text field
     */
    protected JTextField textField;

    /**
     * The OK button
     */
    protected JButton okButton;
    
    /**
     * The Clear button
     */
    protected JButton clearButton;

    /**
     * The current path.
     */
    protected String currentPath = ".";

    /**
     * The file filter.
     */
    protected FileFilter fileFilter;

    /**
     * The last return code.
     */
    protected int returnCode;

    /**
     * The last chosen path.
     */
    protected String chosenPath;

    /**
     * Creates a new URIChooser
     * @param d the parent dialog
     */
    public URIChooser(JDialog d) {
        super(d);
        initialize();
    }

    /**
     * Creates a new URIChooser
     * @param f the parent frame
     * @param okAction the action to associate to the ok button
     */
    public URIChooser(JFrame f) {
        super(f);
        initialize();
    }

    /**
     * Shows the dialog.
     * @return OK_OPTION or CANCEL_OPTION.
     */
    public int showDialog() {
        pack();
        show();
        return returnCode;
    }

    /**
     * Returns the text entered by the user.
     */
    public String getText() {
        return chosenPath;
    }

    /**
     * Sets the file filter to use with the file selector.
     */
    public void setFileFilter(FileFilter ff) {
        fileFilter = ff;
    }

    /**
     * Initializes the dialog
     */
    protected void initialize() {
        setModal(true);

        listeners.put("BrowseButtonAction", new BrowseButtonAction());
        listeners.put("OKButtonAction",     new OKButtonAction());
        listeners.put("CancelButtonAction", new CancelButtonAction());
        listeners.put("ClearButtonAction",  new ClearButtonAction());

        setTitle(resources.getString("Dialog.title"));
        buttonFactory = new ButtonFactory(bundle, this);
        
        getContentPane().add("North",  createURISelectionPanel());
        getContentPane().add("South",  createButtonsPanel());
    }

    /**
     * Creates the URI selection panel
     */
    protected JPanel createURISelectionPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
        ExtendedGridBagConstraints constraints;
        constraints = new ExtendedGridBagConstraints();

        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.setGridBounds(0, 0, 2, 1);
        p.add(new JLabel(resources.getString("Dialog.label")), constraints);

        textField = new JTextField(30);
        textField.getDocument().addDocumentListener(new DocumentAdapter());
        constraints.weightx = 1.0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.setGridBounds(0, 1, 1, 1);
        p.add(textField, constraints);
        
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.setGridBounds(1, 1, 1, 1);
        p.add(buttonFactory.createJButton("BrowseButton"), constraints);
        
        return p;
    }

    /**
     * Creates the buttons panel
     */
    protected JPanel createButtonsPanel() {
        JPanel  p = new JPanel(new FlowLayout());

        p.add(okButton = buttonFactory.createJButton("OKButton"));
        p.add(buttonFactory.createJButton("CancelButton"));
        p.add(clearButton = buttonFactory.createJButton("ClearButton"));
            
        okButton.setEnabled(false);
        clearButton.setEnabled(false);
        
        return p;
    }

    /**
     * To update the state of the OK button
     */
    protected void updateOKButtonAction() {
        okButton.setEnabled(!textField.getText().equals(""));
    }

    /**
     * To update the state of the Clear button
     */
    protected void updateClearButtonAction() {
        clearButton.setEnabled(!textField.getText().equals(""));
    }

    /**
     * To listen to the document changes
     */
    protected class DocumentAdapter implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            updateOKButtonAction();
            updateClearButtonAction();
        }
            
        public void insertUpdate(DocumentEvent e) {
            updateOKButtonAction();
            updateClearButtonAction();
        }

        public void removeUpdate(DocumentEvent e) {
            updateOKButtonAction();
            updateClearButtonAction();
        }       
    }

    /**
     * The action associated with the 'browse' button
     */
    protected class BrowseButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(currentPath);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode
                (JFileChooser.FILES_AND_DIRECTORIES);
            if (fileFilter != null) {
                fileChooser.setFileFilter(fileFilter);
            }
            
            int choice = fileChooser.showOpenDialog(URIChooser.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                try {
                    textField.setText(currentPath = f.getCanonicalPath());
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * The action associated with the 'OK' button of the URI chooser
     */
    protected class OKButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            returnCode = OK_OPTION;
            chosenPath = textField.getText();
            dispose();
        }
    }

    /**
     * The action associated with the 'Cancel' button of the URI chooser
     */
    protected class CancelButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            returnCode = CANCEL_OPTION;
            dispose();
            textField.setText(chosenPath);
        }
    }

    /**
     * The action associated with the 'Clear' button of the URI chooser
     */
    protected class ClearButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            textField.setText("");
        }
    }

    // ActionMap implementation

    /**
     * The map that contains the listeners
     */
    protected Map listeners = new HashMap(10);

    /**
     * Returns the action associated with the given string
     * or null on error
     * @param key the key mapped with the action to get
     * @throws MissingListenerException if the action is not found
     */
    public Action getAction(String key) throws MissingListenerException {
        return (Action)listeners.get(key);
    }
}
