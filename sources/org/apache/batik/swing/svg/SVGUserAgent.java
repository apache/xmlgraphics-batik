/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import org.w3c.dom.Element;

/**
 * This interface must be implemented to provide client services to
 * a JSVGComponent.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGUserAgent {
    
    /**
     * Displays an error message.
     */
    void displayError(String message);
    
    /**
     * Displays an error resulting from the specified Exception.
     */
    void displayError(Exception ex);

    /**
     * Displays a message in the User Agent interface.
     * The given message is typically displayed in a status bar.
     */
    void displayMessage(String message);

    /**
     * Shows an alert dialog box.
     */
    void showAlert(String message);

    /**
     * Shows a prompt dialog box.
     */
    String showPrompt(String message);

    /**
     * Shows a prompt dialog box.
     */
    String showPrompt(String message, String defaultValue);

    /**
     * Shows a confirm dialog box.
     */
    boolean showConfirm(String message);

    /**
     * Returns a customized the pixel to mm factor.
     */
    float getPixelToMM();

    /**
     * Returns the language settings.
     */
    String getLanguages();

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    String getUserStyleSheetURI();

    /**
     * Returns the class name of the XML parser.
     */
    String getXMLParserClassName();

    /**
     * Returns true if the XML parser must be in validation mode, false
     * otherwise.
     */
    boolean isXMLParserValidating();

    /**
     * Returns this user agent's CSS media.
     */
    String getMedia();

    /**
     * Returns this user agent's alternate style-sheet title.
     */
    String getAlternateStyleSheet();

    /**
     * Opens a link in a new component.
     * @param uri The document URI.
     * @param newc Whether the link should be activated in a new component.
     */
    void openLink(String uri, boolean newc);

    /**
     * Tells whether the given extension is supported by this
     * user agent.
     */
    boolean supportExtension(String s);

    /**
     * Notifies the UserAgent that the input element 
     * has been found in the document. This is sometimes
     * called, for example, to handle &lt;a&gt; or
     * &lt;title&gt; elements in a UserAgent-dependant
     * way.
     */
    void handleElement(Element elt, Object data);
}
