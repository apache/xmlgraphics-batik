/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import java.net.URL;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGAElement;

/**
 * An abstract user agent adaptor implementation.  It exists to simply
 * the creation of UserAgent instances.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class UserAgentAdapter implements UserAgent {
    protected Set FEATURES = new HashSet();

    public UserAgentAdapter() {
    };

    public void addStdFeatures() {
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_FEATURE);
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_LANG_FEATURE);
        FEATURES.add(SVGConstants.SVG_ORG_W3C_SVG_STATIC_FEATURE);
    }


    /**
     * Returns the default size of this user agent (400x400).
     */
    public Dimension2D getViewportSize() {
        return new Dimension(1, 1);
    }

    /**
     * Display the specified message.
     */
    public void displayMessage(String message) {
    }

    /**
     * Display the specified error message (forwards call to displayMessage).
     */
    public void displayError(String message) {
        displayMessage(message);
    }

    /**
     * Display the specified error (forwards call to displayError(String))
     */
    public void displayError(Exception e) {
        displayError(e.getMessage());
    }

    /**
     * Shows an alert dialog box.
     */
    public void showAlert(String message) {
    }

    /**
     * Shows a prompt dialog box.
     */
    public String showPrompt(String message) {
        return null;
    }

    /**
     * Shows a prompt dialog box.
     */
    public String showPrompt(String message, String defaultValue) {
        return null;
    }

    /**
     * Shows a confirm dialog box.
     */
    public boolean showConfirm(String message) {
        return false;
    }

    /**
     * Returns the pixel to millimeter conversion factor 0.26458333 (96dpi)
     */
    public float getPixelToMM() {
        return 0.26458333333333333333333333333333f; // 96dpi
    }

    /**
     * Returns the user language "en" (english).
     */
    public String getLanguages() {
        return "en";
    }

    /**
     * Returns this user agent's CSS media.
     */
    public String getMedia() {
        return "all";
    }

    /**
     * Returns this user agent's alternate style-sheet title.
     */
    public String getAlternateStyleSheet() {
        return null;
    }

    /**
     * Returns the user stylesheet 
     */
    public String getUserStyleSheetURI() {
        return null;
    }

    /**
     * Returns the XML parser to use
     */
    public String getXMLParserClassName() {
        return XMLResourceDescriptor.getXMLParserClassName();
    }

    /**
     * Returns <tt>false</tt>. The XML parser is not in validation mode.
     */
    public boolean isXMLParserValidating() {
        return false;
    }

    /**
     * Unsupported operation.
     */
    public EventDispatcher getEventDispatcher() {
        return null;
    }

    /**
     * Unsupported operation.
     */
    public void openLink(SVGAElement elt) { }

    /**
     * Unsupported operation.
     */
    public void setSVGCursor(Cursor cursor) { }

    /**
     * Unsupported operation.
     */
    public void runThread(Thread t) { }

    /**
     * Unsupported operation.
     */
    public AffineTransform getTransform() {
        return null;
    }

    /**
     * Unsupported operation.
     */
    public Point getClientAreaLocationOnScreen() {
        return new Point();
    }

    /**
     * Tells whether the given feature is supported by this
     * user agent.
     */
    public boolean hasFeature(String s) {
        return FEATURES.contains(s);
    }

    protected Set extensions = new HashSet();

    /**
     * Tells whether the given extension is supported by this
     * user agent.
     */
    public boolean supportExtension(String s) {
        return extensions.contains(s);
    }

    /**
     * Lets the bridge tell the user agent that the following
     * ex   tension is supported by the bridge.  
     */
    public void registerExtension(BridgeExtension ext) {
        Iterator i = ext.getImplementedExtensions();
        while (i.hasNext())
            extensions.add(i.next());
    }


    /**
     * Notifies the UserAgent that the input element 
     * has been found in the document. This is sometimes
     * called, for example, to handle &lt;a&gt; or
     * &lt;title&gt; elements in a UserAgent-dependant
     * way.
     */
    public void handleElement(Element elt, Object data){
    }

    /**
     * Returns the security settings for the given script
     * type, script url and document url
     * 
     * @param scriptType type of script, as found in the 
     *        type attribute of the &lt;script&gt; element.
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    public ScriptSecurity getScriptSecurity(String scriptType,
                                            URL scriptURL,
                                            URL docURL){
        return new DefaultScriptSecurity(scriptType, scriptURL, docURL);
    }
    

}

