/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;

/**
 * This class implements the {@link org.w3c.dom.stylesheets.StyleSheet}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractStyleSheet implements StyleSheet {
    /**
     * Is the stylesheet disabled?
     */
    protected boolean disabled;

    /**
     * The owner node
     */
    protected Node ownerNode;

    /**
     * The parent stylesheet
     */
    protected StyleSheet parentStyleSheet;

    /**
     * The href attribute
     */
    protected String href;

    /**
     * The stylesheet title
     */
    protected String title;

    /**
     * The media list
     */
    protected MediaList media;

    /**
     * Creates a new stylesheet.
     * @param owner  the owner node
     * @param parent the parent stylesheet
     * @param href   the href attribute
     * @param title  the stylesheet title
     * @param media  the media list
     */
    protected AbstractStyleSheet(Node       owner,
				 StyleSheet parent,
				 String     href,
				 String     title,
				 MediaList  media) {
	ownerNode        = owner;
	parentStyleSheet = parent;
	this.href        = href;
	this.title       = title;
	this.media       = media;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getDisabled()}.
     */
    public boolean getDisabled() {
	return disabled;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#setDisabled(boolean)}.
     */
    public void setDisabled(boolean disabled) {
	this.disabled = disabled;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getOwnerNode()}.
     */
    public Node getOwnerNode() {
	return ownerNode;
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getParentStyleSheet()}.
     */
    public StyleSheet getParentStyleSheet() {
	return parentStyleSheet;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getHref()}.
     */
    public String  getHref() {
	return href;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getTitle()}.
     */
    public String getTitle() {
	return title;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.StyleSheet#getMedia()}.
     */
    public MediaList getMedia() {
	return media;
    }
}
