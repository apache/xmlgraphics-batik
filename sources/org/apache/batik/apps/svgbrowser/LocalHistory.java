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

package org.apache.batik.apps.svgbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * This class represents an history of the files visited by a single
 * browser frame.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LocalHistory {
    /**
     * The frame to manage.
     */
    protected JSVGViewerFrame svgFrame;    

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
     * The current state.
     */
    protected int state;

    // States
    protected final static int STABLE_STATE = 0;
    protected final static int BACK_PENDING_STATE = 1;
    protected final static int FORWARD_PENDING_STATE = 2;
    protected final static int RELOAD_PENDING_STATE = 3;

    /**
     * Creates a new local history.
     * @param mb The menubar used to display the history. It must
     *        contains one '@@@' item used as marker to place the
     *        history items.
     * @param svgFrame The frame to manage.
     */
    public LocalHistory(JMenuBar mb, JSVGViewerFrame svgFrame) {
        this.svgFrame = svgFrame;

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
        update();
        state = BACK_PENDING_STATE;
        currentURI -= 2;
        svgFrame.showSVGDocument((String)visitedURIs.get(currentURI + 1));
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
        update();
        state = FORWARD_PENDING_STATE;
        svgFrame.showSVGDocument((String)visitedURIs.get(currentURI + 1));
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
        update();
        state = RELOAD_PENDING_STATE;
        currentURI--;
        svgFrame.showSVGDocument((String)visitedURIs.get(currentURI + 1));
    }

    /**
     * Updates the history.
     * @param uri The URI of the document just loaded.
     */
    public void update(String uri) {
        if (currentURI < -1) {
            throw new InternalError();
        }
        state = STABLE_STATE;
        if (++currentURI < visitedURIs.size()) {
            if (!visitedURIs.get(currentURI).equals(uri)) {
                int len = menu.getItemCount();
                for (int i = len - 1; i >= index + currentURI + 1; i--) {
                    JMenuItem mi = menu.getItem(i);
                    group.remove(mi);
                    menu.remove(i);
                }
                visitedURIs = visitedURIs.subList(0, currentURI + 1);
            }
            JMenuItem mi = menu.getItem(index + currentURI);
            group.remove(mi);
            menu.remove(index + currentURI);
            visitedURIs.set(currentURI, uri);
        } else {
            if (visitedURIs.size() >= 15) {
                visitedURIs.remove(0);
                JMenuItem mi = menu.getItem(index);
                group.remove(mi);
                menu.remove(index);
                currentURI--;
            }
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
     * Updates the state of this history.
     */
    protected void update() {
        switch (state) {
        case BACK_PENDING_STATE:
            currentURI += 2;
            break;
        case RELOAD_PENDING_STATE:
            currentURI++;
        case FORWARD_PENDING_STATE:
        case STABLE_STATE:
        }
    }

    /**
     * To listen to the radio buttons.
     */
    protected class RadioListener implements ActionListener {
        public RadioListener() {}
	public void actionPerformed(ActionEvent e) {
	    String uri = e.getActionCommand();
            currentURI = getItemIndex((JMenuItem)e.getSource()) - 1;
	    svgFrame.showSVGDocument(uri);
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
