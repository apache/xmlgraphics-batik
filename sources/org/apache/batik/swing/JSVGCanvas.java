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
import java.awt.event.MouseMotionAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.List;

import javax.swing.ToolTipManager;

import org.apache.batik.bridge.UserAgent;

import org.apache.batik.swing.gvt.Interactor;
import org.apache.batik.swing.gvt.AbstractImageZoomInteractor;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.AbstractResetTransformInteractor;
import org.apache.batik.swing.gvt.AbstractRotateInteractor;
import org.apache.batik.swing.gvt.AbstractZoomInteractor;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGUserAgent;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

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
    
    /**
     * Creates a UserAgent.
     */
    protected UserAgent createUserAgent() {
        return new CanvasUserAgent();
    }

    /**
     * Helper class. Simply keeps track of the last known mouse
     * position over the canvas.
     */
    protected class LocationListener extends MouseMotionAdapter {
        protected int lastX, lastY;
        public void mouseMoved(MouseEvent evt){
            lastX = evt.getX();
            lastY = evt.getY();
        }
        
        public int getLastX(){ return lastX; }
        public int getLastY(){ return lastY; }
    }

    /**
     * Keeps track of the last known mouse position over the canvas.
     * This is used for displaying tooltips at the right location.
     */
    protected LocationListener locationListener = null;

    /**
     * The <tt>CanvasUserAgent</tt> only adds tooltips to the behavior
     * of the default <tt>BridgeUserAgent</tt>.<br />
     * A tooltip will be displayed wheneven the mouse lingers over
     * an element which has a &lt;title&gt; or a &lt;desc&gt; child
     * element.
     */
    protected class CanvasUserAgent 
        extends    BridgeUserAgent 
        implements XMLConstants {

        final String TOOLTIP_TITLE_ONLY 
            = "JSVGCanvas.CanvasUserAgent.ToolTip.titleOnly";
        final String TOOLTIP_DESC_ONLY  
            = "JSVGCanvas.CanvasUserAgent.ToolTip.descOnly";
        final String TOOLTIP_TITLE_AND_TEXT 
            = "JSVGCanvas.CanvasUserAgent.ToolTip.titleAndDesc";

        /**
         * The handleElement method builds a tool tip from the
         * content of a &lt;title&gt; element, a &lt;desc&gt;
         * element or both. <br/>
         * Because these elements can appear in any order, here 
         * is the algorithm used to build the tool tip:<br />
         * <ul>
         * <li>If a &lt;title&gt; is passed to <tt>handleElement</tt>
         *     the method checks if there is a &gt;desc&gt; peer. If
         *     there is one, nothing is done (because the desc will do
         *     it). If there in none, the tool tip is set to the value
         *     of the &lt;title&gt; element content.</li>
         * <li>If a &lt;desc&gt; is passed to <tt>handleElement</tt> 
         *     the method checks if there is a &lt;title&gt; peer. If there
         *     is one, the content of that peer is pre-pended to the
         *     content of the &lt;desc&gt; element.</li>
         * </ul> 
         */
        public void handleElement(Element elt, Object data){
            super.handleElement(elt, data);
            
            if (elt.getNamespaceURI().equals(SVGConstants.SVG_NAMESPACE_URI)) {
                if (elt.getLocalName().equals(SVGConstants.SVG_TITLE_TAG)) {
                    //
                    // If there is a <desc> peer, do nothing as the tooltip
                    // will be handled when handleElement is invoked for 
                    // the <desc> peer.
                    //
                    if (hasPeerWithTag(elt, 
                                       SVGConstants.SVG_NAMESPACE_URI, 
                                       SVGConstants.SVG_DESC_TAG)){
                        return;
                    }

                    elt.normalize();
                    String toolTip = elt.getFirstChild().getNodeValue();
                    toolTip = Messages.formatMessage(TOOLTIP_TITLE_ONLY,
                                                     new Object[]{toFormattedHTML(toolTip)});
                                            
                    setToolTip((Element)(elt.getParentNode()), toolTip);
                }
                else if (elt.getLocalName().equals(SVGConstants.SVG_DESC_TAG)) {
                    //
                    // If there is a <title> peer, prepend its content to 
                    // the content of the <desc> element.
                    //
                    elt.normalize();
                    String toolTip = elt.getFirstChild().getNodeValue();

                    Element titlePeer = getPeerWithTag(elt,
                                                       SVGConstants.SVG_NAMESPACE_URI,
                                                       SVGConstants.SVG_TITLE_TAG);

                    if (titlePeer != null) {
                        titlePeer.normalize();
                        toolTip = Messages.formatMessage
                            (TOOLTIP_TITLE_AND_TEXT,
                             new Object[]{toFormattedHTML(titlePeer.getFirstChild().getNodeValue()),
                                          toFormattedHTML(toolTip)});
                    }
                    else{
                        toolTip = Messages.formatMessage(TOOLTIP_DESC_ONLY,
                                                         new Object[]{toFormattedHTML(toolTip)});
                    }

                    setToolTip((Element)(elt.getParentNode()), toolTip);
                }
            }
        }

        /**
         * Converts line breaks to HTML breaks and encodes 
         * special entities.
         * Poor way of replacing '<', '>', '"', '&' and '''
         * in attribute values.
         */
        public String toFormattedHTML(String str){
            StringBuffer sb = new StringBuffer(str);
            replace(sb, XML_CHAR_AMP, XML_ENTITY_AMP);
            replace(sb, XML_CHAR_LT, XML_ENTITY_LT);
            replace(sb, XML_CHAR_GT, XML_ENTITY_GT);
            replace(sb, XML_CHAR_QUOT, XML_ENTITY_QUOT);
            replace(sb, XML_CHAR_APOS, XML_ENTITY_APOS);
            replace(sb, '\n', "<br>");
            return sb.toString();
        }
        
        protected void replace(StringBuffer s, 
                               char c, 
                               String r){
            String v = s.toString() + 1;
            int i = v.length();
            
            while( (i=v.lastIndexOf(c, --i)) != -1 ){
                s.deleteCharAt(i);
                s.insert(i, r);
            }
        }

        /**
         * Checks if there is a peer element of a given type.
         * This returns the first occurence of the given type
         * or null if none is found.
         */
        public Element getPeerWithTag(Element elt,
                                      String nameSpaceURI,
                                      String localName){
            Element p = (Element)elt.getParentNode();
            if (p == null) {
                return null;
            }
            
            for (Node n=p.getFirstChild(); n!=null; n=n.getNextSibling()){
                if (!nameSpaceURI.equals(n.getNamespaceURI())){
                    continue;
                }
                
                if (!localName.equals(n.getLocalName())){
                    continue;
                }
                
                if (n.getNodeType() == n.ELEMENT_NODE) {
                    return (Element)n;
                }
            }

            return null;
        }
        
        /**
         * Returns a boolean defining whether or not there is a
         * peer of <tt>elt</tt> with the given qualified tag.
         */
        public boolean hasPeerWithTag(Element elt,
                                      String nameSpaceURI,
                                      String localName){
            if (getPeerWithTag(elt, nameSpaceURI, localName) == null){
                return false;
            }
            else{
                return true;
            }
        }
        
        /**
         * Sets the tool tip on the input element.
         */
        public void setToolTip(Element elt, String toolTip){
            EventTarget target = (EventTarget)elt;
            
            elt.normalize();
            
            // On mouseover, set the tooltip to the title value
            target.addEventListener(SVGConstants.SVG_EVENT_MOUSEOVER, 
                                    new ToolTipModifier(toolTip),
                                    false);
            
            // On mouseout, remove the tooltip
            target.addEventListener(SVGConstants.SVG_EVENT_MOUSEOUT,
                                    new ToolTipModifier(null),
                                    false);
            
            if (locationListener == null){
                locationListener = new LocationListener();
                addMouseMotionListener(locationListener);
            }
        }
    }

    /**
     * Sets a specific tooltip on the JSVGCanvas when a given event
     * occurs. This listener is used in the handleElement method
     * to set, remove or modify the JSVGCanvas tooltip on mouseover
     * and on mouseout.<br/>
     * Because we are on a single <tt>JComponent</tt> we trigger an
     * artificial <tt>MouseEvent</tt> when the toolTip is set to 
     * a non-null value, so as to make sure it will show after the 
     * <tt>ToolTipManager</tt>'s default delay.
     *
     */
    protected class ToolTipModifier implements EventListener {
        /**
         * Value of the toolTip
         */
        protected String toolTip;

        /**
         * @param toolTip value to which the JSVGCanvas should be 
         *        set when the event occurs.
         */
        public ToolTipModifier(String toolTip){
            this.toolTip = toolTip;
        }

        public void handleEvent(Event evt){
            setToolTipText(toolTip);

            if(toolTip != null){
                MouseEvent e = new MouseEvent(JSVGCanvas.this,
                                              MouseEvent.MOUSE_ENTERED,
                                              System.currentTimeMillis(),
                                              0,
                                              locationListener.getLastX(),
                                              locationListener.getLastY(),
                                              0,
                                              false);
                ToolTipManager.sharedInstance().mouseEntered(e);
            }
        }
    }

}
