/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.List;

import org.apache.batik.swing.gvt.Interactor;
import org.apache.batik.swing.gvt.AbstractImageZoomInteractor;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.AbstractResetTransformInteractor;
import org.apache.batik.swing.gvt.AbstractRotateInteractor;
import org.apache.batik.swing.gvt.AbstractZoomInteractor;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGUserAgent;

/**
 * This class represents a general-purpose swing SVG component. The
 * <tt>JSVGCanvas</tt> does not provided additional functionalities compared to
 * the <tt>JSVGComponent</tt> but simply provides an API conformed to the
 * JavaBean specification. The only major change between the
 * <tt>JSVGComponent</tt> and this component is that interactors and text
 * selection are activated by default.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGCanvas extends JSVGComponent {

    /**
     * An interactor to perform a zoom.
     * <p>Binding: BUTTON1 + CTRL Key</p>
     */
    protected Interactor zoomInteractor = new AbstractZoomInteractor() {
        public boolean startInteraction(InputEvent ie) {
            int mods = ie.getModifiers();
            return
                ie.getID() == MouseEvent.MOUSE_PRESSED &&
                (mods & ie.BUTTON1_MASK) != 0 &&
                (mods & ie.CTRL_MASK) != 0;
        }
    };

    /**
     * An interactor to perform a realtime zoom.
     * <p>Binding: BUTTON3 + SHIFT Key</p>
     */
    protected Interactor imageZoomInteractor
        = new AbstractImageZoomInteractor() {
        public boolean startInteraction(InputEvent ie) {
            int mods = ie.getModifiers();
            return
                ie.getID() == MouseEvent.MOUSE_PRESSED &&
                (mods & ie.BUTTON3_MASK) != 0 &&
                (mods & ie.SHIFT_MASK) != 0;
        }
    };

    /**
     * An interactor to perform a translation.
     * <p>Binding: BUTTON1 + SHIFT Key</p>
     */
    protected Interactor panInteractor = new AbstractPanInteractor() {
        public boolean startInteraction(InputEvent ie) {
            int mods = ie.getModifiers();
            return
                ie.getID() == MouseEvent.MOUSE_PRESSED &&
                (mods & ie.BUTTON1_MASK) != 0 &&
                (mods & ie.SHIFT_MASK) != 0;
        }
    };

    /**
     * An interactor to perform a rotation.
     * <p>Binding: BUTTON3 + CTRL Key</p>
     */
    protected Interactor rotateInteractor = new AbstractRotateInteractor() {
        public boolean startInteraction(InputEvent ie) {
            int mods = ie.getModifiers();
            return
                ie.getID() == MouseEvent.MOUSE_PRESSED &&
                (mods & ie.BUTTON3_MASK) != 0 &&
                (mods & ie.CTRL_MASK) != 0;
        }
    };

    /**
     * An interactor to reset the rendering transform.
     * <p>Binding: CTRL+SHIFT+BUTTON3</p>
     */
    protected Interactor resetTransformInteractor =
        new AbstractResetTransformInteractor() {
        public boolean startInteraction(InputEvent ie) {
            int mods = ie.getModifiers();
            return
                ie.getID() == MouseEvent.MOUSE_CLICKED &&
                (mods & ie.BUTTON3_MASK) != 0 &&
                (mods & ie.SHIFT_MASK) != 0 &&
                (mods & ie.CTRL_MASK) != 0;
        }
    };

    /**
     * This flag bit indicates whether or not the zoom interactor is
     * enabled. True means the zoom interactor is functional.
     */
    private boolean isZoomInteractorEnabled = true;

    /**
     * This flag bit indicates whether or not the image zoom interactor is
     * enabled. True means the image zoom interactor is functional.
     */
    private boolean isImageZoomInteractorEnabled = true;

    /**
     * This flag bit indicates whether or not the pan interactor is
     * enabled. True means the pan interactor is functional.
     */
    private boolean isPanInteractorEnabled = true;

    /**
     * This flag bit indicates whether or not the rotate interactor is
     * enabled. True means the rotate interactor is functional.
     */
    private boolean isRotateInteractorEnabled = true;

    /**
     * This flag bit indicates whether or not the reset transform interactor is
     * enabled. True means the reset transform interactor is functional.
     */
    private boolean isResetTransformInteractorEnabled = true;

    /**
     * The <tt>PropertyChangeSupport</tt> used to fire
     * <tt>PropertyChangeEvent</tt>.
     */
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * The URI of the current document being displayed.
     */
    protected String uri;

    /**
     * Creates a new JSVGCanvas.
     */
    public JSVGCanvas() {
        this(null, true, true);
    }

    /**
     * Creates a new JSVGCanvas.
     * @param ua a SVGUserAgent instance or null.
     * @param eventEnabled Whether the GVT tree should be reactive
     *        to mouse and key events.
     * @param selectableText Whether the text should be selectable.
     */
    public JSVGCanvas(SVGUserAgent ua, boolean eventsEnabled,
                      boolean selectableText) {
        super(ua, eventsEnabled, selectableText);
        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(100, 100));

        List intl = getInteractors();
        intl.add(zoomInteractor);
        intl.add(imageZoomInteractor);
        intl.add(panInteractor);
        intl.add(rotateInteractor);
        intl.add(resetTransformInteractor);
    }

    /**
     * Adds the specified <tt>PropertyChangeListener</tt>.
     *
     * @param pcl the property change listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    /**
     * Removes the specified <tt>PropertyChangeListener</tt>.
     *
     * @param pcl the property change listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

    /**
     * Adds the specified <tt>PropertyChangeListener</tt> for the
     * specified property.
     *
     * @param propertyName the name of the property to listen on
     * @param pcl the property change listener to add
     */
    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(propertyName, pcl);
    }

    /**
     * Removes the specified <tt>PropertyChangeListener</tt> for the
     * specified property.
     *
     * @param propertyName the name of the property that was listened on
     * @param pcl the property change listener to remove
     */
    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(propertyName, pcl);
    }

    /**
     * Determines whether the zoom interactor is enabled or not.
     */
    public void setEnableZoomInteractor(boolean b) {
        if (isZoomInteractorEnabled != b) {
            boolean oldValue = isZoomInteractorEnabled;
            isZoomInteractorEnabled = b;
            if (isZoomInteractorEnabled) {
                getInteractors().add(zoomInteractor);
            } else {
                getInteractors().remove(zoomInteractor);
            }
            pcs.firePropertyChange("enableZoomInteractor", oldValue, b);
        }
    }

    /**
     * Returns true if the zoom interactor is enabled, false otherwise.
     */
    public boolean getEnableZoomInteractor() {
        return isZoomInteractorEnabled;
    }

    /**
     * Determines whether the image zoom interactor is enabled or not.
     */
    public void setEnableImageZoomInteractor(boolean b) {
        if (isImageZoomInteractorEnabled != b) {
            boolean oldValue = isImageZoomInteractorEnabled;
            isImageZoomInteractorEnabled = b;
            if (isImageZoomInteractorEnabled) {
                getInteractors().add(imageZoomInteractor);
            } else {
                getInteractors().remove(imageZoomInteractor);
            }
            pcs.firePropertyChange("enableImageZoomInteractor", oldValue, b);
        }
    }

    /**
     * Returns true if the image zoom interactor is enabled, false otherwise.
     */
    public boolean getEnableImageZoomInteractor() {
        return isImageZoomInteractorEnabled;
    }

    /**
     * Determines whether the pan interactor is enabled or not.
     */
    public void setEnablePanInteractor(boolean b) {
        if (isPanInteractorEnabled != b) {
            boolean oldValue = isPanInteractorEnabled;
            isPanInteractorEnabled = b;
            if (isPanInteractorEnabled) {
                getInteractors().add(panInteractor);
            } else {
                getInteractors().remove(panInteractor);
            }
            pcs.firePropertyChange("enablePanInteractor", oldValue, b);
        }
    }

    /**
     * Returns true if the pan interactor is enabled, false otherwise.
     */
    public boolean getEnablePanInteractor() {
        return isPanInteractorEnabled;
    }

    /**
     * Determines whether the rotate interactor is enabled or not.
     */
    public void setEnableRotateInteractor(boolean b) {
        if (isRotateInteractorEnabled != b) {
            boolean oldValue = isRotateInteractorEnabled;
            isRotateInteractorEnabled = b;
            if (isRotateInteractorEnabled) {
                getInteractors().add(rotateInteractor);
            } else {
                getInteractors().remove(rotateInteractor);
            }
            pcs.firePropertyChange("enableRotateInteractor", oldValue, b);
        }
    }

    /**
     * Returns true if the rotate interactor is enabled, false otherwise.
     */
    public boolean getEnableRotateInteractor() {
        return isRotateInteractorEnabled;
    }

    /**
     * Determines whether the reset transform interactor is enabled or not.
     */
    public void setEnableResetTransformInteractor(boolean b) {
        if (isResetTransformInteractorEnabled != b) {
            boolean oldValue = isResetTransformInteractorEnabled;
            isResetTransformInteractorEnabled = b;
            if (isResetTransformInteractorEnabled) {
                getInteractors().add(resetTransformInteractor);
            } else {
                getInteractors().remove(resetTransformInteractor);
            }
            pcs.firePropertyChange("enableResetTransformInteractor", oldValue, b);
        }
    }

    /**
     * Returns true if the reset transform interactor is enabled, false otherwise.
     */
    public boolean getEnableResetTransformInteractor() {
        return isResetTransformInteractorEnabled;
    }

    /**
     * Returns the URI of the current document.
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets the URI to the specified uri.
     *
     * @param newURI the new uri of the document to display
     */
    public void setURI(String newURI) {
        String oldValue = uri;
        this.uri = newURI;
        loadSVGDocument(uri);
        pcs.firePropertyChange("URI", oldValue, uri);
    }
}
