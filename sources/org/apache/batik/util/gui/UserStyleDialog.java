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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a dialog to select the user style sheet.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UserStyleDialog extends JDialog implements ActionMap {

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
        "org.apache.batik.util.gui.resources.UserStyleDialog";

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
     * The main panel.
     */
    protected Panel panel;

    /**
     * The chosen path.
     */
    protected String chosenPath;

    /**
     * The last return code.
     */
    protected int returnCode;

    /**
     * Creates a new user style dialog.
     */
    public UserStyleDialog(JFrame f) {
        super(f);
        setModal(true);
        setTitle(resources.getString("Dialog.title"));

        listeners.put("OKButtonAction",        new OKButtonAction());
        listeners.put("CancelButtonAction",    new CancelButtonAction());

        getContentPane().add(panel = new Panel());
        getContentPane().add("South", createButtonsPanel());
        pack();
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
     * Returns the chosen path or null.
     */
    public String getPath() {
        return chosenPath;
    }

    /**
     * Sets the current dialog path.
     */
    public void setPath(String s) {
        chosenPath = s;
        panel.fileTextField.setText(s);
        panel.fileCheckBox.setSelected(true);
    }

    /**
     * Creates the OK/Cancel buttons panel
     */
    protected JPanel createButtonsPanel() {
        JPanel  p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonFactory bf = new ButtonFactory(bundle, this);
        p.add(bf.createJButton("OKButton"));
        p.add(bf.createJButton("CancelButton"));

        return p;
    }

    /**
     * The action associated with the 'OK' button
     */
    protected class OKButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (panel.fileCheckBox.isSelected()) {
                String path = panel.fileTextField.getText();
                if (path.equals("")) {
                    JOptionPane.showMessageDialog
                        (UserStyleDialog.this,
                         resources.getString("StyleDialogError.text"),
                         resources.getString("StyleDialogError.title"),
                         JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    File f = new File(path);
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            path = null;
                        } else {
                            path = "file:" + path;
                        }
                    }
                    chosenPath = path;
                }
            } else {
                chosenPath = null;
            }
            returnCode = OK_OPTION;
            dispose();
        }
    }

    /**
     * The action associated with the 'Cancel' button
     */
    protected class CancelButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            returnCode = CANCEL_OPTION;
            dispose();
        }
    }

    /**
     * The map that contains the listeners
     */
    protected Map listeners = new HashMap();

    /**
     * Returns the action associated with the given string
     * or null on error
     * @param key the key mapped with the action to get
     * @throws MissingListenerException if the action is not found
     */
    public Action getAction(String key) throws MissingListenerException {
        return (Action)listeners.get(key);
    }

    /**
     * This class represents the main panel of the dialog.
     */
    public static class Panel extends JPanel {
        /**
         * The file check box
         */
        protected JCheckBox fileCheckBox;

        /**
         * The file label
         */
        protected JLabel fileLabel;

        /**
         * The file text field
         */
        protected JTextField fileTextField;

        /**
         * The browse button
         */
        protected JButton browseButton;

        /**
         * Creates a new Panel object.
         */
        public Panel() {
            super(new GridBagLayout());
            setBorder(BorderFactory.createTitledBorder
                      (BorderFactory.createEtchedBorder(),
                       resources.getString("Panel.title")));

            ExtendedGridBagConstraints constraints =
                new ExtendedGridBagConstraints();
            constraints.insets = new Insets(5, 5, 5, 5);
            
            fileCheckBox =
                new JCheckBox(resources.getString("PanelFileCheckBox.text"));
            fileCheckBox.addChangeListener(new FileCheckBoxChangeListener());
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.setGridBounds(0, 2, 3, 1);
            this.add(fileCheckBox, constraints);

            fileLabel = new JLabel(resources.getString("PanelFileLabel.text"));
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.setGridBounds(0, 3, 3, 1);
            this.add(fileLabel, constraints);

            fileTextField = new JTextField(30);
            constraints.weightx = 1.0;
            constraints.weighty = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.setGridBounds(0, 4, 2, 1);
            this.add(fileTextField, constraints);

            ButtonFactory bf = new ButtonFactory(bundle, null);
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.setGridBounds(2, 4, 1, 1);
            browseButton = bf.createJButton("PanelFileBrowseButton");
            this.add(browseButton, constraints);
            browseButton.addActionListener(new FileBrowseButtonAction());
        
            fileLabel.setEnabled(false);
            fileTextField.setEnabled(false);
            browseButton.setEnabled(false);
        }

        /**
         * Returns the chosen path or null.
         */
        public String getPath() {
            if(fileCheckBox.isSelected()){
                return fileTextField.getText();
            }
            else{
                return null;
            }
        }
        
        /**
         * Sets the current dialog path.
         */
        public void setPath(String s) {
            if(s == null){
                fileTextField.setEnabled(false);
                fileCheckBox.setSelected(false);
            }
            else{
                fileTextField.setEnabled(true);
                fileTextField.setText(s);
                fileCheckBox.setSelected(true);
            }
        }

        /**
         * To listen to the file checkbox
         */
        protected class FileCheckBoxChangeListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                boolean selected = fileCheckBox.isSelected();
                fileLabel.setEnabled(selected);
                fileTextField.setEnabled(selected);
                browseButton.setEnabled(selected);
            }
        }

        /**
         * The action associated with the 'browse' button
         */
        protected class FileBrowseButtonAction extends AbstractAction {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setFileHidingEnabled(false);

                int choice = fileChooser.showOpenDialog(Panel.this);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    try {
                        fileTextField.setText(f.getCanonicalPath());
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
}
