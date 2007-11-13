/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.util.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaDefaults;

/**
 * The abstract enhanced JEditTextArea. Has cut / copy / paste, select all and
 * undo / redo shortcuts added.
 *
 * @version $Id$
 */
public class AbstractJEnhEditTextArea extends JEditTextArea {
   
    /**
     * The number of the edits to remember. 
     */
    public static final int HISTORY_SIZE = 1000;
    
    /**
     * The undo manager.
     */
    protected UndoManager undoManager;
    
    /**
     * Creates a new JEnhEditTextArea with the specified settings.
     * 
     * @param defaults
     *            The default settings
     */
    public AbstractJEnhEditTextArea(TextAreaDefaults defaults) {
        super(defaults);

        // XXX Make these key bindings use Command on OS X.

        // Cut / copy / paste
        getInputHandler().addKeyBinding("C+x", new CutAction());
        getInputHandler().addKeyBinding("C+c", new CopyAction());
        getInputHandler().addKeyBinding("C+v", new PasteAction());

        // Select all
        getInputHandler().addKeyBinding("C+a", new SelectAllAction());

        // Undo / Redo
        getInputHandler().addKeyBinding("C+z", new UndoAction());
        getInputHandler().addKeyBinding("C+y", new RedoAction());
        getDocument().addUndoableEditListener(new UndoableEditSupport());
        addFocusListener(new FocusSupport());
    }
    
    // Cut / copy / paste
    /**
     * The copy action.
     */
    protected class CopyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            copy();
        }
    }

    /**
     * The paste action.
     */
    protected class PasteAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            paste();
        }
    }

    /**
     * The cut action.
     */
    protected class CutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            cut();
        }
    }

    // Select all
    /**
     * The select all action.
     */
    protected class SelectAllAction implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            selectAll();
        }
    }

    // Undo / Redo
    /**
     * The undo action.
     */
    protected class UndoAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                getUndoManager().undo();
            } catch (CannotUndoException cue) {
            }
        }
    }

    /**
     * The redo action.
     */
    protected class RedoAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                getUndoManager().redo();
            } catch (CannotRedoException cue) {
            }
        }
    }
    
    /**
     * Listening for the undoable edits. Adds the undoable edit to undo manager.
     */
    protected class UndoableEditSupport implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            getUndoManager().addEdit(e.getEdit());
        }
    }

    /**
     * Focus listener. Creates new undo manager when focus gained. Ends the
     * current update manager when focus is lost.
     */
    protected class FocusSupport extends FocusAdapter {
        
        public void focusGained(FocusEvent e) {
            undoManager = createUndoManger();
        }

        public void focusLost(FocusEvent e) {
            undoManager.end();
        }
    }

    /**
     * Gets the UndoManager.
     * 
     * @return the UndoManager
     */
    protected UndoManager getUndoManager() {
        if (undoManager == null) {
            undoManager = createUndoManger();
        }
        return undoManager;
    }

    /**
     * Creates new UndoManager, sets its limit and returns it
     * 
     * @return the UndoManager
     */
    protected UndoManager createUndoManger() {
        UndoManager newUndoManager = new UndoManager();
        newUndoManager.setLimit(HISTORY_SIZE);
        return newUndoManager;
    }
}
