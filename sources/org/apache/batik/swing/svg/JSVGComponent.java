/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeExtension;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.bridge.UserAgent;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.gvt.event.EventDispatcher;

import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.JGVTComponent;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * This class represents a Swing component which can display SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class JSVGComponent extends JGVTComponent {

    /**
     * The document loader.
     */
    protected SVGDocumentLoader documentLoader;

    /**
     * The next document loader to run.
     */
    protected SVGDocumentLoader nextDocumentLoader;

    /**
     * The concrete bridge document loader.
     */
    protected DocumentLoader loader;

    /**
     * The GVT tree builder.
     */
    protected GVTTreeBuilder gvtTreeBuilder;

    /**
     * The next GVT tree builder to run.
     */
    protected GVTTreeBuilder nextGVTTreeBuilder;

    /**
     * The current SVG document.
     */
    protected SVGDocument svgDocument;

    /**
     * The document loader listeners.
     */
    protected List svgDocumentLoaderListeners = new LinkedList();

    /**
     * The GVT tree builder listeners.
     */
    protected List gvtTreeBuilderListeners = new LinkedList();

    /**
     * The link activation listeners.
     */
    protected List linkActivationListeners = new LinkedList();

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * The SVG user agent.
     */
    protected SVGUserAgent svgUserAgent;

    /**
     * The current bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * The current document fragment identifier.
     */
    protected String fragmentIdentifier;

    /**
     * Creates a new JSVGComponent.
     */
    public JSVGComponent() {
        this(null, false, false);
    }

    /**
     * Creates a new JSVGComponent.
     * @param ua a SVGUserAgent instance or null.
     * @param eventEnabled Whether the GVT tree should be reactive
     *        to mouse and key events.
     * @param selectableText Whether the text should be selectable.
     */
    public JSVGComponent(SVGUserAgent ua, boolean eventsEnabled,
                         boolean selectableText) {
        super(eventsEnabled, selectableText);

        svgUserAgent = ua;

        userAgent = createUserAgent();

        addSVGDocumentLoaderListener((SVGListener)listener);
        addGVTTreeBuilderListener((SVGListener)listener);
    }

    /**
     * Stops the processing of the current document.
     */
    public void stopProcessing() {
        nextDocumentLoader = null;
        nextGVTTreeBuilder = null;

        if (documentLoader != null) {
            documentLoader.interrupt();
        } else if (gvtTreeBuilder != null) {
            gvtTreeBuilder.interrupt();
        } else {
            super.stopProcessing();
        }
    }

    /**
     * Loads a SVG document from the given URL.
     * <em>Note: Because the loading is multi-threaded, the current
     * SVG document is not garanteed to be updated after this method
     * returns. The only way to be notified a document has been loaded
     * is to listen to the <tt>SVGDocumentLoaderEvent</tt>s.</em>
     */
    public void loadSVGDocument(String url) {
        stopProcessing();

        URL oldURI = null;
        if (svgDocument != null) {
            oldURI = ((SVGOMDocument)svgDocument).getURLObject();
        }
        URL newURI = null;
        try {
            newURI = new URL(oldURI, url);
        } catch (MalformedURLException e) {
            userAgent.displayError(e);
            return;
        }
        url = newURI.toString();
        fragmentIdentifier = newURI.getRef();

        loader = new DocumentLoader(userAgent);
        nextDocumentLoader = new SVGDocumentLoader(url, loader);
        nextDocumentLoader.setPriority(Thread.MIN_PRIORITY);

        Iterator it = svgDocumentLoaderListeners.iterator();
        while (it.hasNext()) {
            nextDocumentLoader.addSVGDocumentLoaderListener
                ((SVGDocumentLoaderListener)it.next());
        }

        if (documentLoader == null &&
            gvtTreeBuilder == null &&
            gvtTreeRenderer == null) {
            startDocumentLoader();
        }
    }

    /**
     * Starts a loading thread.
     */
    private void startDocumentLoader() {
        documentLoader = nextDocumentLoader;
        nextDocumentLoader = null;
        documentLoader.start();
    }

    /**
     * Sets the SVG document to display.
     */
    public void setSVGDocument(SVGDocument doc) {
        stopProcessing();
        if (!(doc.getImplementation() instanceof SVGDOMImplementation)) {
            throw new IllegalArgumentException("Invalid DOM implementation.");
        }

        if (eventsEnabled && svgDocument != null) {
            // fire the unload event
            Event evt = svgDocument.createEvent("SVGEvents");
            evt.initEvent("SVGUnload", false, false);
            ((EventTarget)(svgDocument.getRootElement())).dispatchEvent(evt);
        }

        svgDocument = doc;

        Element root = doc.getDocumentElement();
        String znp = root.getAttributeNS(null, SVGConstants.SVG_ZOOM_AND_PAN_ATTRIBUTE);
        disableInteractions = !znp.equals(SVGConstants.SVG_MAGNIFY_VALUE);

        bridgeContext = createBridgeContext();
        nextGVTTreeBuilder = new GVTTreeBuilder(doc, bridgeContext);
        nextGVTTreeBuilder.setPriority(Thread.MIN_PRIORITY);

        Iterator it = gvtTreeBuilderListeners.iterator();
        while (it.hasNext()) {
            nextGVTTreeBuilder.addGVTTreeBuilderListener
                ((GVTTreeBuilderListener)it.next());
        }

        releaseRenderingReferences();
        initializeEventHandling();

        if (gvtTreeBuilder == null &&
            documentLoader == null &&
            gvtTreeRenderer == null) {
            startGVTTreeBuilder();
        }
    }

    /**
     * Starts a tree builder.
     */
    private void startGVTTreeBuilder() {
        gvtTreeBuilder = nextGVTTreeBuilder;
        nextGVTTreeBuilder = null;
        gvtTreeBuilder.start();
    }

    /**
     * Returns the current SVG document.
     */
    public SVGDocument getSVGDocument() {
        return svgDocument;
    }

    /**
     * Returns the current's document fragment identifier.
     */
    public String getFragmentIdentifier() {
        return fragmentIdentifier;
    }

    /**
     * Sets the current fragment identifier.
     */
    public void setFragmentIdentifier(String fi) {
        fragmentIdentifier = fi;
        computeRenderingTransform();
    }

    /**
     * Creates a new bridge context.
     */
    protected BridgeContext createBridgeContext() {
        if (loader == null) {
            loader = new DocumentLoader(userAgent);
        }
        return new BridgeContext(userAgent,
                                 rendererFactory.getRenderContext(),
                                 loader);
    }

    /**
     * Computes the transform used for rendering.
     */
    protected void computeRenderingTransform() {
        try {
            if (svgDocument != null) {
                SVGSVGElement elt = svgDocument.getRootElement();
                Dimension d = getSize();
                setRenderingTransform(ViewBox.getViewTransform
                                      (fragmentIdentifier, elt, d.width, d.height));
                initialTransform = renderingTransform;
            }
        } catch (BridgeException e) {
            userAgent.displayError(e);
        }
    }

    /**
     * Updates the value of the transform used for rendering.
     */
    protected void updateRenderingTransform() {
        if (initialTransform == renderingTransform) {
            computeRenderingTransform();
        }
    }

    /**
     * Handles an exception.
     */
    protected void handleException(Exception e) {
        userAgent.displayError(e);
    }

    /**
     * Adds a SVGDocumentLoaderListener to this component.
     */
    public void addSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        svgDocumentLoaderListeners.add(l);
    }

    /**
     * Removes a SVGDocumentLoaderListener from this component.
     */
    public void removeSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        svgDocumentLoaderListeners.remove(l);
    }

    /**
     * Adds a GVTTreeBuilderListener to this component.
     */
    public void addGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        gvtTreeBuilderListeners.add(l);
    }

    /**
     * Removes a GVTTreeBuilderListener from this component.
     */
    public void removeGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        gvtTreeBuilderListeners.remove(l);
    }

    /**
     * Adds a LinkActivationListener to this component.
     */
    public void addLinkActivationListener(LinkActivationListener l) {
        linkActivationListeners.add(l);
    }

    /**
     * Removes a LinkActivationListener from this component.
     */
    public void removeLinkActivationListener(LinkActivationListener l) {
        linkActivationListeners.remove(l);
    }

    /**
     * Creates an instance of Listener.
     */
    protected Listener createListener() {
        return new SVGListener();
    }

    /**
     * To hide the listener methods.
     */
    protected class SVGListener
        extends Listener
        implements SVGDocumentLoaderListener,
                   GVTTreeBuilderListener {

        /**
         * Creates a new SVGListener.
         */
        protected SVGListener() {
        }

        // SVGDocumentLoaderListener ///////////////////////////////////////////

        /**
         * Called when the loading of a document was started.
         */
        public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
        }

        /**
         * Called when the loading of a document was completed.
         */
        public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            documentLoader = null;
            setSVGDocument(e.getSVGDocument());
        }

        /**
         * Called when the loading of a document was cancelled.
         */
        public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            documentLoader = null;

            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }
        }

        /**
         * Called when the loading of a document has failed.
         */
        public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            documentLoader = null;
            userAgent.displayError(((SVGDocumentLoader)e.getSource()).getException());

            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }
        }

        // GVTTreeBuilderListener //////////////////////////////////////////////

        /**
         * Called when a build started.
         * The data of the event is initialized to the old document.
         */
        public void gvtBuildStarted(GVTTreeBuilderEvent e) {
            computeRenderingTransform();
        }

        /**
         * Called when a build was completed.
         */
        public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }

            loader = null;
            gvtTreeBuilder = null;
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            JSVGComponent.this.setGraphicsNode(e.getGVTRoot(), false);
            Dimension2D dim = bridgeContext.getDocumentSize();
            setPreferredSize(new Dimension((int)dim.getWidth(), (int)dim.getHeight()));
            invalidate();
        }

        /**
         * Called when a build was cancelled.
         */
        public void gvtBuildCancelled(GVTTreeBuilderEvent e) {
            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }

            loader = null;
            gvtTreeBuilder = null;
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }
            JSVGComponent.this.image = null;
            repaint();
        }

        /**
         * Called when a build failed.
         */
        public void gvtBuildFailed(GVTTreeBuilderEvent e) {
            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }

            loader = null;
            gvtTreeBuilder = null;
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            GraphicsNode gn = e.getGVTRoot();
            Dimension2D dim = bridgeContext.getDocumentSize();
            if (gn == null || dim == null) {
                JSVGComponent.this.image = null;
                repaint();
            } else {
                JSVGComponent.this.setGraphicsNode(gn, false);
                setPreferredSize(new Dimension((int)dim.getWidth(),
                                               (int)dim.getHeight()));
                invalidate();
            }
            userAgent.displayError(((GVTTreeBuilder)e.getSource()).getException());
        }

        // GVTTreeRendererListener /////////////////////////////////////////////

        /**
         * Called when a rendering was completed.
         */
        public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            super.gvtRenderingCompleted(e);

            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }

            if (JSVGComponent.this.eventsEnabled) {
                Event evt = svgDocument.createEvent("SVGEvents");
                evt.initEvent("SVGLoad", false, false);
                ((EventTarget)(svgDocument.getRootElement())).dispatchEvent(evt);
                ((EventTarget)svgDocument).addEventListener("DOMAttrModified",
                                        new MutationListener(bridgeContext), false);
            }
        }

        /**
         * Called when a rendering was cancelled.
         */
        public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
            super.gvtRenderingCancelled(e);

            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }
        }

        /**
         * Called when a rendering failed.
         */
        public void gvtRenderingFailed(GVTTreeRendererEvent e) {
            super.gvtRenderingFailed(e);

            if (nextGVTTreeBuilder != null) {
                startGVTTreeBuilder();
                return;
            }
            if (nextDocumentLoader != null) {
                startDocumentLoader();
                return;
            }
        }

        /**
         * To listener to the DOM mutation events.
         */
        protected class MutationListener implements EventListener {

            BridgeContext bridgeContext;

            public MutationListener(BridgeContext bridgeContext) {
                this.bridgeContext = bridgeContext;
            }

            public void handleEvent(Event evt) {
                BridgeMutationEvent bme;
                Element target = (Element)evt.getTarget();
                bme = new BridgeMutationEvent
                    (target,
                     bridgeContext,
                     BridgeMutationEvent.PROPERTY_MUTATION_TYPE);

                MutationEvent me = (MutationEvent)evt;

                bme.setAttrName(me.getAttrName());
                bme.setAttrNewValue(me.getNewValue());

                GraphicsNodeBridge bridge;
                bridge = (GraphicsNodeBridge)bridgeContext.getBridge(target);

                bridge.update(bme);
            }
        }
    }

    /**
     * Creates a UserAgent.
     */
    protected UserAgent createUserAgent() {
        return new BridgeUserAgent();
    }

    /**
     * To hide the user-agent methods.
     */
    protected class BridgeUserAgent implements UserAgent {

        /**
         * Creates a new user agent.
         */
        protected BridgeUserAgent() {
        }

        /**
         * Returns the default size of the viewport of this user agent (0, 0).
         */
        public Dimension2D getViewportSize() {
            return getSize();
        }

        /**
         * Returns the <code>EventDispatcher</code> used by the
         * <code>UserAgent</code> to dispatch events on GVT.
         */
        public EventDispatcher getEventDispatcher() {
            return JSVGComponent.this.eventDispatcher;
        }

        /**
         * Displays an error message in the User Agent interface.
         */
        public void displayError(String message) {
            if (svgUserAgent != null) {
                svgUserAgent.displayError(message);
            }
        }

        /**
         * Displays an error resulting from the specified Exception.
         */
        public void displayError(Exception ex) {
            if (svgUserAgent != null) {
                svgUserAgent.displayError(ex);
            }
        }

        /**
         * Displays a message in the User Agent interface.
         */
        public void displayMessage(String message) {
            if (svgUserAgent != null) {
                svgUserAgent.displayMessage(message);
            }
        }

        /**
         * Returns the pixel to mm factor.
         */
        public float getPixelToMM() {
            if (svgUserAgent != null) {
                return svgUserAgent.getPixelToMM();
            }
            return 0.264583333333333333333f; // 96 dpi
        }

        /**
         * Returns the language settings.
         */
        public String getLanguages() {
            if (svgUserAgent != null) {
                return svgUserAgent.getLanguages();
            }
            return "en";
        }

        /**
         * Returns the user stylesheet uri.
         * @return null if no user style sheet was specified.
         */
        public String getUserStyleSheetURI() {
            if (svgUserAgent != null) {
                return svgUserAgent.getUserStyleSheetURI();
            }
            return null;
        }

        /**
         * Opens a link.
         * @param elt The activated link element.
         */
        public void openLink(SVGAElement elt) {
            String show = elt.getXlinkShow();
            String href = elt.getHref().getBaseVal();

            if (show.equals("new")) {
                if (svgUserAgent != null) {
                    URL oldURI = ((SVGOMDocument)svgDocument).getURLObject();
                    URL newURI = null;
                    try {
                        newURI = new URL(oldURI, href);
                    } catch (MalformedURLException e) {
                        userAgent.displayError(e);
                        return;
                    }

                    href = newURI.toString();

                    svgUserAgent.openLink(href);
                    fireLinkActivatedEvent(elt, href);
                    return;
                }
            }

            // Avoid reloading if possible.
            if (svgDocument != null) {
                URL oldURI = ((SVGOMDocument)svgDocument).getURLObject();
                URL newURI = null;
                try {
                    newURI = new URL(oldURI, href);
                } catch (MalformedURLException e) {
                    userAgent.displayError(e);
                    return;
                }
                String s = newURI.getRef();
                if (newURI.sameFile(oldURI)) {
                    if ((fragmentIdentifier == null && s != null) ||
                        (s == null && fragmentIdentifier != null) ||
                        (s != null && !s.equals(fragmentIdentifier))) {
                        fragmentIdentifier = s;
                        computeRenderingTransform();
                    }
                    fireLinkActivatedEvent(elt, newURI.toString());
                    return;
                }
            }
            fireLinkActivatedEvent(elt, href);
            JSVGComponent.this.loadSVGDocument(href);
        }

        /**
         * Fires a LinkActivatedEvent.
         */
        protected void fireLinkActivatedEvent(SVGAElement elt, String href) {
            Object[] ll = linkActivationListeners.toArray();

            if (ll.length > 0) {
                LinkActivationEvent ev;
                ev = new LinkActivationEvent(JSVGComponent.this, elt, href);

                for (int i = 0; i < ll.length; i++) {
                    LinkActivationListener l = (LinkActivationListener)ll[i];
                    l.linkActivated(ev);
                }
            }
        }

        /**
         * Informs the user agent to change the cursor.
         * @param cursor the new cursor
         */
        public void setSVGCursor(Cursor cursor) {
            JSVGComponent.this.setCursor(cursor);
        }

        /**
         * Returns the class name of the XML parser.
         */
        public String getXMLParserClassName() {
            if (svgUserAgent != null) {
                return svgUserAgent.getXMLParserClassName();
            }
            return "org.apache.crimson.parser.XMLReaderImpl";
        }

        /**
         * Returns the <code>AffineTransform</code> currently
         * applied to the drawing by the UserAgent.
         */
        public AffineTransform getTransform() {
            return JSVGComponent.this.renderingTransform;
        }

        /**
         * Returns the location on the screen of the
         * client area in the UserAgent.
         */
        public Point getClientAreaLocationOnScreen() {
            return getLocationOnScreen();
        }

        /**
         * Tells whether the given feature is supported by this
         * user agent.
         */
        public boolean hasFeature(String s) {
            return FEATURES.contains(s);
        }

        protected Map extensions = new HashMap();

        /**
         * Tells whether the given extension is supported by this
         * user agent.
         */
        public boolean supportExtension(String s) {
            boolean ret = false;
            if ((svgUserAgent != null) &&
                (svgUserAgent.supportExtension(s)))
                return true;

            return extensions.containsKey(s);
        }

        /**
         * Lets the bridge tell the user agent that the following
         * extension is supported by the bridge.
         */
        public void registerExtension(BridgeExtension ext) {
            Iterator i = ext.getImplementedExtensions();
            while (i.hasNext())
                extensions.put(i.next(), ext);
        }
    }

    protected final static Set FEATURES = new HashSet();
    static {
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_FEATURE);
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_LANG_FEATURE);
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_STATIC_FEATURE);
    }
}
