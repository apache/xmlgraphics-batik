/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.experiment;

import java.lang.ref.WeakReference;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGGraphicsElement;
import org.apache.batik.dom.svg.SVGURIReferenceSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGURIReference;
import org.apache.batik.dom.util.XLinkSupport;

/**
 * This class implements {@link org.w3c.dom.svg.SVGTextPathElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextPathElement
    extends    SVGGraphicsElement
    implements SVGURIReference {

    /**
     * Creates a new TextPathElement object.
     */
    protected TextPathElement() {
    }

    /**
     * Creates a new TextPathElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public TextPathElement(String prefix, Document owner) {
        super(prefix, (AbstractDocument)owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return Experiments.EXPERIMENT_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "textPath";
    }

    // XLink support //////////////////////////////////////////////////////

    /**
     * The SVGURIReference support.
     */
    protected SVGURIReferenceSupport uriReferenceSupport;

    /**
     * Returns uriReferenceSupport different from null.
     */
    protected final SVGURIReferenceSupport getSVGURIReferenceSupport() {
	if (uriReferenceSupport == null) {
	    uriReferenceSupport = new SVGURIReferenceSupport();
	}
	return uriReferenceSupport;
    }
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkType()}.
     */
    public String getXlinkType() {
        return XLinkSupport.getXLinkType(this);
    }
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkType(String)}.
     */
    public void setXlinkType(String str) {
        XLinkSupport.setXLinkType(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkRole()}.
     */
    public String getXlinkRole() {
        return XLinkSupport.getXLinkRole(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkRole(String)}.
     */
    public void setXlinkRole(String str) {
        XLinkSupport.setXLinkRole(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkArcRole()}.
     */
    public String getXlinkArcRole() {
        return XLinkSupport.getXLinkArcRole(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkArcRole(String)}.
     */
    public void setXlinkArcRole(String str) {
        XLinkSupport.setXLinkArcRole(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkTitle()}.
     */
    public String getXlinkTitle() {
        return XLinkSupport.getXLinkTitle(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkTitle(String)}.
     */
    public void setXlinkTitle(String str) {
        XLinkSupport.setXLinkTitle(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkShow()}.
     */
    public String getXlinkShow() {
        return XLinkSupport.getXLinkShow(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkShow(String)}.
     */
    public void setXlinkShow(String str) {
        XLinkSupport.setXLinkShow(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getXlinkActuate()}.
     */
    public String getXlinkActuate() {
        return XLinkSupport.getXLinkActuate(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#setXlinkActuate(String)}.
     */
    public void setXlinkActuate(String str) {
        XLinkSupport.setXLinkActuate(this, str);
    }

    /**
     * Returns the value of the 'xlink:href' attribute of the given element.
     */
    public String getXlinkHref() {
        return XLinkSupport.getXLinkHref(this);
    }

    /**
     * Sets the value of the 'xlink:href' attribute of the given element.
     */
    public void setXlinkHref(String str) {
        XLinkSupport.setXLinkHref(this, str);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGURIReference#getHref()}.
     */
    public SVGAnimatedString getHref() {
        return getSVGURIReferenceSupport().getHref(this);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new TextPathElement();
    }
}
