/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.batik.swing.JSVGCanvas;

/**
 * This class represents an history of the files visited by a single
 * browser frame.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocalHistory {

    /**
     * The canvas to manage.
     */
    protected JSVGCanvas svgCanvas;

    /**
     * The menu which contains the history.
     */
    protected JMenu menu;

    /**
     * The index of the first history item in this menu.
     */
    protected int index;

    /**
     * The visited URIs.
     */
    protected List visitedURIs = new ArrayList();

    /**
     * The index of the current URI.
     */
    protected int currentURI = -1;

    /**
     * The button group for the menu items.
     */
    protected ButtonGroup group = new ButtonGroup();

    /**
     * The action listener.
     */
    protected ActionListener actionListener = new RadioListener();

    /**
     * Creates a new local history.
     * @param mb The menubar used to display the history. It must
     *        contains one '@@@' item used as marker to place the
     *        history items.
     * @param canvas The canvas to manage.
     */
    public LocalHistory(JMenuBar mb, JSVGCanvas canvas) {
        svgCanvas = canvas;

        // Find the marker.
        int mc = mb.getMenuCount();
        for (int i = 0; i < mc; i++) {
            JMenu m = mb.getMenu(i);
            int ic = m.getItemCount();
            for (int j = 0; j < ic; j++) {
                JMenuItem mi = m.getItem(j);
                if (mi != null) {
                    String s = mi.getText();
                    if ("@@@".equals(s)) {
                        menu = m;
                        index = j;
                        m.remove(j);
                        return;
                    }
                }
            }
        }
        throw new IllegalArgumentException("No '@@@' marker found");
    }

    /**
     * Goes back of one position in the history.
     * Assumes that <tt>canGoBack()</tt> is true.
     */
    public void back() {
        currentURI -= 2;
        svgCanvas.loadSVGDocument((String)visitedURIs.get(currentURI + 1));
    }

    /**
     * Whether it is possible to go back.
     */
    public boolean canGoBack() {
        return currentURI > 0;
    }

    /**
     * Goes forward of one position in the history.
     * Assumes that <tt>canGoForward()</tt> is true.
     */
    public void forward() {
        svgCanvas.loadSVGDocument((String)visitedURIs.get(currentURI + 1));
    }

    /**
     * Whether it is possible to go forward.
     */
    public boolean canGoForward() {
        return currentURI < visitedURIs.size() - 1;
    }

    /**
     * Reloads the current document.
     */
    public void reload() {
        currentURI--;
        svgCanvas.loadSVGDocument((String)visitedURIs.get(currentURI + 1));
    }

    /**
     * Updates the history.
     * @param uri The URI of the document just loaded.
     */
    public void update(String uri) {
        if (++currentURI < visitedURIs.size()) {
            if (!visitedURIs.get(currentURI).equals(uri)) {
                for (int i = currentURI + 1; i < visitedURIs.size(); i++) {
                    JMenuItem mi = menu.getItem(index + i - 1);
                    group.remove(mi);
                    menu.remove(index + i - 1);
                }
                visitedURIs = visitedURIs.subList(0, currentURI + 1);
            }
            JMenuItem mi = menu.getItem(index + currentURI);
            group.remove(mi);
            menu.remove(index + currentURI);
            visitedURIs.set(currentURI, uri);
        } else {
            visitedURIs.add(uri);
        }

        // Computes the button text.
        String text = uri;
        int i = uri.lastIndexOf("/");
        if (i == -1) {
            i = uri.lastIndexOf("\\");
            if (i != -1) {
                text = uri.substring(i + 1);
            }
        } else {
            text = uri.substring(i + 1);
        }

        JMenuItem mi = new JRadioButtonMenuItem(text);
        mi.setActionCommand(uri);
        mi.addActionListener(actionListener);
        group.add(mi);
        mi.setSelected(true);
        menu.insert(mi, index + currentURI);
    }

    /**
     * To listen to the radio buttons.
     */
    protected class RadioListener implements ActionListener {
        public RadioListener() {}
	public void actionPerformed(ActionEvent e) {
	    String uri = e.getActionCommand();
            currentURI = getItemIndex((JMenuItem)e.getSource()) - 1;
	    svgCanvas.loadSVGDocument(uri);
	}
        public int getItemIndex(JMenuItem item) {
            int ic = menu.getItemCount();
            for (int i = index; i < ic; i++) {
                if (menu.getItem(i) == item) {
                    return i - index;
                }
            }
            throw new InternalError();
        }
    }
}
