/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import org.apache.batik.gvt.event.EventDispatcher;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAElement;

/**
 * An interface that provides access to the User Agent informations
 * needed by the bridge.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface UserAgent {

    // <!> FIXME: TO BE REMOVED
    /**
     * Returns the event dispatcher to use.
     */
    public EventDispatcher getEventDispatcher();

    /**
     * Returns the default size of the viewport.
     */
    public Dimension2D getViewportSize();

    /**
     * Displays an error resulting from the specified Exception.
     */
    public void displayError(Exception ex);

    /**
     * Displays a message in the User Agent interface.
     */
    public void displayMessage(String message);

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM();

    /**
     * Returns the language settings.
     */
    public String getLanguages();

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    public String getUserStyleSheetURI();

    /**
     * Opens a link.
     * @param elt The activated link element.
     */
    public void openLink(SVGAElement elt);

    /**
     * Informs the user agent to change the cursor.
     * @param cursor the new cursor
     */
    public void setSVGCursor(Cursor cursor);

    /**
     * Returns the class name of the XML parser.
     */
    public String getXMLParserClassName();

    /**
     * Returns the <code>AffineTransform</code> currently
     * applied to the drawing by the UserAgent.
     */
    public AffineTransform getTransform();

    /**
     * Returns the location on the screen of the
     * client area in the UserAgent.
     */
    public Point getClientAreaLocationOnScreen();

    /**
     * Tells whether the given feature is supported by this
     * user agent.
     */
    public boolean hasFeature(String s);

    /**
     * Tells whether the given extension is supported by this
     * user agent.
     */
    public boolean supportExtension(String s);

    /**
     * Lets the bridge tell the user agent that the following
     * extension is supported by the bridge.
     */
    public void registerExtension(BridgeExtension ext);
}
