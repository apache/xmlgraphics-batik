/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GVTTreeWalker;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;

import org.apache.batik.swing.JSVGCanvas;

import org.apache.batik.util.gui.ExtendedGridBagConstraints;
import org.apache.batik.util.gui.resource.ActionMap;
import org.apache.batik.util.gui.resource.ButtonFactory;
import org.apache.batik.util.gui.resource.MissingListenerException;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a Dialog that lets the user searching for text inside
 * an SVG document.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class FindDialog extends JDialog implements ActionMap {

    /**
     * The resource file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.FindDialog";

    // action names
    public final static String NEXT_ACTION = "NextButtonAction";
    public final static String PREVIOUS_ACTION = "PreviousButtonAction";
    public final static String CLOSE_ACTION = "CloseButtonAction";

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

    /** The button factory */
    protected ButtonFactory buttonFactory;

    /** The GVT root into which text is searched. */
    protected GraphicsNode gvtRoot;

    /** The GVTTreeWalker used to scan the GVT Tree. */
    protected GVTTreeWalker walker;

    /** The TextField that owns the text to search. */
    protected JTextField search;

    /** The next button. */
    protected JButton next;

    /** The next button. */
    protected JButton previous;

    /** The cancel button. */
    protected JButton close;

    /** The case sensitive button. */
    protected JCheckBox caseSensitive;

    /** The canvas. */
    protected JSVGCanvas svgCanvas;

    /**
     * Constructs a new <tt>FindDialog</tt>.
     */
    public FindDialog(JSVGCanvas svgCanvas) {
        this(null, svgCanvas);
    }

    /**
     * Constructs a new <tt>FindDialog</tt>.
     */
    public FindDialog(Frame owner, JSVGCanvas svgCanvas) {
        super(owner, resources.getString("Dialog.title"));
        this.svgCanvas = svgCanvas;

        buttonFactory = new ButtonFactory(bundle, this);

        listeners.put(NEXT_ACTION, new NextButtonAction());
        listeners.put(PREVIOUS_ACTION, new PreviousButtonAction());
        listeners.put(CLOSE_ACTION, new CloseButtonAction());

        getContentPane().add(createFindPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the Find panel.
     */
    protected JPanel createFindPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBorder(BorderFactory.createTitledBorder
                        (BorderFactory.createEtchedBorder(),
                         resources.getString("Panel.title")));

        ExtendedGridBagConstraints gbc = new ExtendedGridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.anchor = ExtendedGridBagConstraints.EAST;
        gbc.fill = ExtendedGridBagConstraints.NONE;
        gbc.setWeight(0, 0);
        gbc.setGridBounds(0, 0, 1, 1);
        panel.add(new JLabel(resources.getString("FindLabel.text")), gbc);

        gbc.fill = ExtendedGridBagConstraints.HORIZONTAL;
        gbc.setWeight(1.0, 0);
        gbc.setGridBounds(1, 0, 1, 1);
        panel.add(search = new JTextField(20), gbc);
        search.getDocument().addDocumentListener(new TextTracker());

        gbc.fill = ExtendedGridBagConstraints.NONE;
        gbc.anchor = ExtendedGridBagConstraints.WEST;
        gbc.setWeight(0, 0);
        gbc.setGridBounds(1, 1, 1, 1);
        caseSensitive = buttonFactory.createJCheckBox("CaseSensitiveCheckBox");
        panel.add(caseSensitive, gbc);

        return panel;
    }

    /**
     * Creates the buttons panel
     */
    protected JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(next = buttonFactory.createJButton("NextButton"));
        panel.add(previous = buttonFactory.createJButton("PreviousButton"));
        panel.add(close = buttonFactory.createJButton("CloseButton"));

        updateButtonState();

        return panel;
    }


    /**
     * Sets the graphics node into which text is searched.
     * @param gvtRoot the GVT root node
     */
    public void setGraphicsNode(GraphicsNode gvtRoot) {
        this.gvtRoot = gvtRoot;
        if (gvtRoot != null) {
            this.walker = new GVTTreeWalker(gvtRoot);
        } else {
            this.walker = null;
        }
        updateButtonState();
    }

    /**
     * Returns the next GraphicsNode that matches the specified string or null
     * if any.
     *
     * @param text the text to match
     */
    protected GraphicsNode getNext(String text) {
        GraphicsNode gn = walker.nextGraphicsNode();
        while (gn != null && !match(gn, text)) {
            gn = walker.nextGraphicsNode();
        }
        return gn;
    }

    /**
     * Returns the previous GraphicsNode that matches the specified string or
     * null if any.
     *
     * @param text the text to match
     */
    protected GraphicsNode getPrevious(String text) {
        GraphicsNode gn = walker.previousGraphicsNode();
        while (gn != null && !match(gn, text)) {
            gn = walker.previousGraphicsNode();
        }
        return gn;
    }

    /**
     * Returns true if the specified node matches the following text, false
     * otherwise.
     *
     * @param node the graphics node to check
     * @param text the text use to match
     */
    protected boolean match(GraphicsNode node, String text) {
        if (!(node instanceof TextNode)
            || !node.isVisible()
            || text == null || text.length() == 0) {
            return false;
        }

        String s = ((TextNode)node).getText();
        if (!caseSensitive.isSelected()) {
            s = s.toLowerCase();
            text = text.toLowerCase();
        }
        return s.startsWith(text);
    }

    /**
     * Updates the state of the 'next', 'previous' buttons and 'search' text
     * field.
     */
    private void updateButtonState() {
        boolean b1 = (gvtRoot != null);
        String text = search.getText();
        boolean b2 = (text != null && text.length() > 0);
        next.setEnabled(b1 && b2);
        previous.setEnabled(b1 && b2);
        search.setEnabled(b1);
    }


    /**
     * Shows the current selected <tt>TextNode</tt>.
     */
    protected void showSelectedGraphicsNode() {
        GraphicsNode gn = walker.getCurrentGraphicsNode();
        if (!(gn instanceof TextNode)) {
            return;
        }
        Rectangle2D bounds = gn.getBounds(svgCanvas.getRenderContext());
        bounds = gn.getGlobalTransform().createTransformedShape
            (bounds).getBounds();
        Dimension dim = svgCanvas.getSize();
        AffineTransform Tx = new AffineTransform();
        double s = Math.min(dim.width/bounds.getWidth(),
                            dim.height/bounds.getHeight());
        Tx.scale(s*.8, s*.8);
        Tx.translate(-bounds.getX(), -bounds.getY());
        svgCanvas.setRenderingTransform(Tx);
        //System.out.println(gn+" "+((TextNode)gn).getText()+" "+bounds);
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

    //////////////////////////////////////////////////////////////////////////
    // Action implementation
    //////////////////////////////////////////////////////////////////////////

    /**
     * The action associated to the 'next' button.
     */
    protected class NextButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            GraphicsNode gn = getNext(search.getText());
            if (gn != null) {
                previous.setEnabled(true);
                showSelectedGraphicsNode();
            } else {
                next.setEnabled(false);
            }
        }
    }

    /**
     * The action associated to the 'previous' button.
     */
    protected class PreviousButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            GraphicsNode gn = getPrevious(search.getText());
            if (gn != null) {
                next.setEnabled(true);
                showSelectedGraphicsNode();
            } else {
                previous.setEnabled(false);
            }
        }
    }

    /**
     * The action associated to the 'close' button.
     */
    protected class CloseButtonAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    /**
     * This class is used to track all modifications of the search TextField.
     */
    protected class TextTracker implements DocumentListener {

        public void changedUpdate(DocumentEvent e) { }

        public void insertUpdate(DocumentEvent e) {
            updateButtonState();
            String text = search.getText();
            // go to next only if the current node does not match the text
            if (!match(walker.getCurrentGraphicsNode(), text)) {
                GraphicsNode gn = getNext(text);
                if (gn != null) {
                    previous.setEnabled(true);
                    showSelectedGraphicsNode();
                } else {
                    next.setEnabled(false);
                }
            }
        }

        public void removeUpdate(DocumentEvent e) {
            updateButtonState();
            GraphicsNode gn = getPrevious(search.getText());
            if (gn != null) {
                next.setEnabled(true);
                showSelectedGraphicsNode();
            } else {
                previous.setEnabled(false);
            }
        }
    }
}


