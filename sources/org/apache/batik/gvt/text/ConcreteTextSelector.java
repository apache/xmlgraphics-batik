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

package org.apache.batik.gvt.text;

import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.Selector;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.SelectionEvent;
import org.apache.batik.gvt.event.SelectionListener;

/**
 * ConcreteTextSelector.java:
 * A simple implementation of GraphicsNodeMouseListener for text selection.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public class ConcreteTextSelector implements Selector {

    private ArrayList listeners = null;
    private GraphicsNode selectionNode = null;
    private GraphicsNode currentNode = null;
    private int firstHit;
    private int lastHit;

    public ConcreteTextSelector() {
    }

    public void mouseClicked(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void mouseDragged(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void mouseEntered(GraphicsNodeMouseEvent evt) {
        currentNode = evt.getGraphicsNode();
        checkSelectGesture(evt);
    }

    public void mouseExited(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
        currentNode = null;
    }

    public void mouseMoved(GraphicsNodeMouseEvent evt) {
    }

    public void mousePressed(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void mouseReleased(GraphicsNodeMouseEvent evt) {
        checkSelectGesture(evt);
    }

    public void keyPressed(GraphicsNodeKeyEvent evt) {
        report(evt, "keyPressed");
    }

    public void keyReleased(GraphicsNodeKeyEvent evt) {
        report(evt, "keyReleased");
    }

    public void keyTyped(GraphicsNodeKeyEvent evt) {
        report(evt, "keyTyped");
    }

    public void changeStarted (GraphicsNodeChangeEvent gnce) {
    }
    public void changeCompleted (GraphicsNodeChangeEvent gnce) {
        if (selectionNode == null) return;
        Shape newShape =
            ((Selectable)selectionNode).getHighlightShape();
        dispatchSelectionEvent
            (new SelectionEvent(getSelection(),
                                SelectionEvent.SELECTION_CHANGED,
                                newShape));
    }

    public void setSelection(Mark begin, Mark end) {
        TextNode node = begin.getTextNode();
        if (node != end.getTextNode())
            throw new Error("Markers not from same TextNode");
        node.setSelection(begin, end);
        selectionNode = node;
        Object selection = getSelection();
        Shape  shape     = node.getHighlightShape();
        dispatchSelectionEvent(new SelectionEvent
            (selection, SelectionEvent.SELECTION_DONE, shape));
        copyToClipboard(selection);
    }

    public void clearSelection() {
        if (selectionNode == null) 
            return;
        dispatchSelectionEvent(new SelectionEvent
            (null, SelectionEvent.SELECTION_CLEARED, null));
        // copyToClipboard(null);
        selectionNode = null;
    }

    /*
     * Checks the event to see if it is a selection gesture and processes it
     * accordingly.
     * @param evt the GraphicsNodeEvent, which may be a "select gesture"
     * Param evt is a GraphicsNodeEvent rather than a GraphicsNodeMouseEvent
     * for future extension, so we can use Shift-arrow, etc.
     */
    protected void checkSelectGesture(GraphicsNodeEvent evt) {

        GraphicsNodeMouseEvent mevt = null;
        if (evt instanceof GraphicsNodeMouseEvent) {
            mevt = (GraphicsNodeMouseEvent) evt;
        }

        GraphicsNode source = evt.getGraphicsNode();

        if (isDeselectGesture(evt)) {
            if (selectionNode != null)
                selectionNode.getRoot()
                    .removeTreeGraphicsNodeChangeListener(this);

            clearSelection();
        } else if ((source instanceof Selectable) && (mevt != null)) {

            Point2D p = new Point2D.Double(mevt.getX(), mevt.getY());
            AffineTransform t = source.getGlobalTransform();
            if (t == null) {
                t = new AffineTransform();
            }
            else {
                 try {
                     t = t.createInverse();
                 } catch (NoninvertibleTransformException ni) {
                 }
            }
            p = t.transform(p, null);

            if (isSelectStartGesture(evt)) {
                if (selectionNode != source) {
                    if (selectionNode != null)
                        selectionNode.getRoot()
                            .removeTreeGraphicsNodeChangeListener(this);
                    if (source != null)
                        source.getRoot()
                            .addTreeGraphicsNodeChangeListener(this);
                }

                selectionNode = source;
                ((Selectable) source).selectAt(p.getX(), p.getY());
                dispatchSelectionEvent(
                        new SelectionEvent(null,
                                SelectionEvent.SELECTION_STARTED,
                                null));

            } else if (isSelectEndGesture(evt)) {
                if (selectionNode != source) {
                    if (selectionNode != null)
                        selectionNode.getRoot()
                            .removeTreeGraphicsNodeChangeListener(this);
                    if (source != null)
                        source.getRoot()
                            .addTreeGraphicsNodeChangeListener(this);
                }
                selectionNode = source;

                ((Selectable) source).selectTo(p.getX(), p.getY());

                Object oldSelection = getSelection();
                Shape newShape =
                    ((Selectable) source).getHighlightShape();
                dispatchSelectionEvent(
                        new SelectionEvent(oldSelection,
                                SelectionEvent.SELECTION_DONE,
                                newShape));
                copyToClipboard(oldSelection);
            } else

            if (isSelectContinueGesture(evt)) {

                if (selectionNode == source) {
                    boolean result = ((Selectable) source).selectTo(p.getX(), 
                                                                    p.getY());
                    if (result) {
                        Shape newShape =
                        ((Selectable) source).getHighlightShape();

                        dispatchSelectionEvent(
                            new SelectionEvent(null,
                                SelectionEvent.SELECTION_CHANGED,
                                newShape));
                    }
                }
            } else if (isSelectAllGesture(evt)) {
                if (selectionNode != source) {
                    if (selectionNode != null)
                        selectionNode.getRoot()
                            .removeTreeGraphicsNodeChangeListener(this);
                    if (source != null)
                        source.getRoot()
                            .addTreeGraphicsNodeChangeListener(this);
                }
                selectionNode = source;
                
                ((Selectable) source).selectAll(p.getX(), p.getY());
                Object oldSelection = getSelection();
                Shape newShape =
                    ((Selectable) source).getHighlightShape();
                dispatchSelectionEvent(
                        new SelectionEvent(oldSelection,
                                SelectionEvent.SELECTION_DONE,
                                newShape));
                copyToClipboard(oldSelection);
            }
        }
    }

    private boolean isDeselectGesture(GraphicsNodeEvent evt) {
        return ((evt.getID() == GraphicsNodeMouseEvent.MOUSE_CLICKED)
            && (((GraphicsNodeMouseEvent) evt).getClickCount() == 1));
    }

    private boolean isSelectStartGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_PRESSED);
    }

    private boolean isSelectEndGesture(GraphicsNodeEvent evt) {
        return ((evt.getID() == GraphicsNodeMouseEvent.MOUSE_RELEASED));
    }

    private boolean isSelectContinueGesture(GraphicsNodeEvent evt) {
        return (evt.getID() == GraphicsNodeMouseEvent.MOUSE_DRAGGED);
    }

    private boolean isSelectAllGesture(GraphicsNodeEvent evt) {
        return ((evt.getID() == GraphicsNodeMouseEvent.MOUSE_CLICKED)
            && (((GraphicsNodeMouseEvent) evt).getClickCount() == 2));
    }

    /*
     * Get the contents of the current selection.
     */
    public Object getSelection() {
        Object value = null;
        if (selectionNode instanceof Selectable) {
            value =  ((Selectable) selectionNode).getSelection();
        }
        return value;
    }

    /**
     * Reports whether the current selection contains any objects.
     */
    public boolean isEmpty() {
        return (getSelection() == null);
    }

    /**
     * Reports whether the current selection contains any objects.
     */
    public void dispatchSelectionEvent(SelectionEvent e) {
        if (listeners != null) {
            Iterator iter = listeners.iterator();
            switch(e.getID()) {
            case SelectionEvent.SELECTION_DONE:
                while (iter.hasNext()) {
                    ((SelectionListener)iter.next()).selectionDone(e);
                }
                break;
            case SelectionEvent.SELECTION_CHANGED:
                while (iter.hasNext()) {
                    ((SelectionListener)iter.next()).selectionChanged(e);
                }
                break;
            case SelectionEvent.SELECTION_CLEARED:
                while (iter.hasNext()) {
                    ((SelectionListener)iter.next()).selectionCleared(e);
                }
                break;
            case SelectionEvent.SELECTION_STARTED:
                while (iter.hasNext()) {
                    ((SelectionListener)iter.next()).selectionStarted(e);
                }
                break;
            }
        }
    }

    /**
     * Add a SelectionListener to this Selector's notification list.
     * @param l the SelectionListener to add.
     */
    public void addSelectionListener(SelectionListener l) {
        if (listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(l);
    }

    /**
     * Remove a SelectionListener from this Selector's notification list.
     * @param l the SelectionListener to be removed.
     */
    public void removeSelectionListener(SelectionListener l) {
        if (listeners != null) {
            listeners.remove(l);
        }
    }

    private void copyToClipboard(final Object o) {
	//
	// HACK: getSystemClipboard sometimes deadlocks on linux when called
	// from the AWT Thread. The Thread creation prevents that.
	//
	new Thread() {
	    public void run() {
		// first see if we can access the clipboard
		SecurityManager securityManager = System.getSecurityManager();
		boolean canAccessClipboard = true;
		if (securityManager != null) {
		    try {
			securityManager.checkSystemClipboardAccess();
		    } catch (SecurityException e) {
			canAccessClipboard = false;
		    }
		}
		if (canAccessClipboard) {
		    String label = "";
		    if (o instanceof CharacterIterator) {
			CharacterIterator iter = (CharacterIterator) o;
			char[] cbuff = new char[iter.getEndIndex()-iter.getBeginIndex()];
			if (cbuff.length > 0) {
			    cbuff[0] = iter.first();
			}
			for (int i=1; i<cbuff.length;++i) {
			    cbuff[i] = iter.next();
			}
			label = new String(cbuff);
		    }
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    StringSelection selection = new StringSelection(label);
		    clipboard.setContents(selection, selection);
		}
	    }
	}.start();
    }

    private void report(GraphicsNodeEvent evt, String message) {
        GraphicsNode source = evt.getGraphicsNode();
        String label = "(non-text node)";
        if (source instanceof TextNode) {
            char[] cbuff;
            java.text.CharacterIterator iter =
                ((TextNode) source).getAttributedCharacterIterator();
            cbuff = new char[iter.getEndIndex()];
            if (cbuff.length > 0) cbuff[0] = iter.first();
            for (int i=1; i<cbuff.length;++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
        }
        System.out.println("Mouse "+message+" in "+label);
    }
}


