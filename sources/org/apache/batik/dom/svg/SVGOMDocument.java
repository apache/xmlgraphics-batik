/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.batik.css.DOMStyleSheetList;
import org.apache.batik.css.ElementWithID;
import org.apache.batik.css.svg.SVGViewCSS;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.GenericAttr;
import org.apache.batik.dom.GenericAttrNS;
import org.apache.batik.dom.GenericCDATASection;
import org.apache.batik.dom.GenericComment;
import org.apache.batik.dom.GenericDocumentFragment;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.GenericElementNS;
import org.apache.batik.dom.GenericEntityReference;
import org.apache.batik.dom.GenericProcessingInstruction;
import org.apache.batik.dom.GenericText;
import org.apache.batik.dom.StyleSheetProcessingInstruction;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.apache.batik.dom.util.OverrideStyleElement;
import org.apache.batik.dom.util.XMLSupport;

import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

/**
 * This class implements {@link org.w3c.dom.svg.SVGDocument}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMDocument
    extends    AbstractDocument
    implements SVGDocument,
               DocumentCSS,
               DocumentView,
               SVGConstants
{
    /**
     * The error messages bundle class name.
     */
    protected final static String RESOURCES =
        "org.apache.batik.dom.svg.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    /**
     * The url of the document.
     */
    protected URL url;

    /**
     * Is this document immutable?
     */
    protected boolean readonly;

    /**
     * The default view.
     */
    protected AbstractView defaultView;

    /**
     * The document context.
     */
    protected SVGContext context;

    /**
     * The element factories.
     */
    protected HashTable factories = new HashTable();
    {
        factories.put(TAG_A,                new AElementFactory());
        factories.put(TAG_CIRCLE,           new CircleElementFactory());
        factories.put(TAG_DEFS,             new DefsElementFactory());
        factories.put(TAG_DESC,             new DescElementFactory());
        factories.put(TAG_ELLIPSE,          new EllipseElementFactory());
        factories.put(TAG_FE_BLEND,              new FeBlendElementFactory());
        factories.put(TAG_FE_COLOR_MATRIX,       new FeColorMatrixElementFactory());
        factories.put(TAG_FE_COMPONENT_TRANSFER, new FeComponentTransferElementFactory());
        factories.put(TAG_FE_DIFFUSE_LIGHTING,   new FeDiffuseLightingElementFactory());
        factories.put(TAG_FE_DISPLACEMENT_MAP,   new FeDisplacementMapElementFactory());
        factories.put(TAG_FE_FLOOD,         new FeFloodElementFactory());
        factories.put(TAG_FE_GAUSSIAN_BLUR, new FeGaussianBlurElementFactory());
        factories.put(TAG_FE_IMAGE,         new FeImageElementFactory());
        factories.put(TAG_FE_MERGE,         new FeMergeElementFactory());
        factories.put(TAG_FE_MORPHOLOGY,    new FeMorphologyElementFactory());
        factories.put(TAG_FE_OFFSET,        new FeOffsetElementFactory());
        factories.put(TAG_FE_SPECULAR_LIGHTING,  new FeSpecularLightingElementFactory());
        factories.put(TAG_FE_TILE,          new FeTileElementFactory());
        factories.put(TAG_FE_TURBULENCE,    new FeTurbulenceElementFactory());
        factories.put(TAG_FILTER,           new FilterElementFactory());
        factories.put(TAG_G,                new GElementFactory());
        factories.put(TAG_IMAGE,            new ImageElementFactory());
        factories.put(TAG_LINE,             new LineElementFactory());
        factories.put(TAG_PATH,             new PathElementFactory());
        factories.put(TAG_POLYGON,          new PolygonElementFactory());
        factories.put(TAG_POLYLINE,         new PolylineElementFactory());
        factories.put(TAG_RECT,             new RectElementFactory());
        factories.put(TAG_STYLE,            new StyleElementFactory());
        factories.put(TAG_SVG,              new SvgElementFactory());
        factories.put(TAG_SWITCH,           new SwitchElementFactory());
        factories.put(TAG_SYMBOL,           new SymbolElementFactory());
        factories.put(TAG_TEXT,             new TextElementFactory());
        factories.put(TAG_TITLE,            new TitleElementFactory());
        factories.put(TAG_USE,              new UseElementFactory());
    }

    /**
     * Creates a new uninitialized document.
     */
    public SVGOMDocument() {
    }

    /**
     * Creates a new document.
     */
    public SVGOMDocument(DocumentType dt,
                         DOMImplementation impl) {
        super(impl);
        if (dt != null) {
            appendChild(dt);
        }
    }

    /**
     * Returns this document context.
     */
    public SVGContext getSVGContext() {
        return context;
    }

    /**
     * Sets the document context.
     */
    public void setSVGContext(SVGContext ctx) {
        context = ctx;
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public  void setLocale(Locale l) {
        super.setLocale(l);
        localizableSupport.setLocale(l);
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        try {
            return super.formatMessage(key, args);
        } catch (MissingResourceException e) {
            return localizableSupport.formatMessage(key, args);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGDocument#getTitle()}.
     */
    public String getTitle() {
        String result = "";
        SVGSVGElement elt = getRootElement();
        boolean preserve = false;
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String name = (n.getPrefix() == null)
                    ? n.getNodeName()
                    : n.getLocalName();
                if (name.equals("title")) {
                    preserve =
                        ((SVGLangSpace)n).getXMLspace().equals("preserve");
                    for (n = n.getFirstChild();
                         n != null;
                         n = n.getNextSibling()) {
                        if (n.getNodeType() == Node.TEXT_NODE) {
                            result += n.getNodeValue();
                        }
                    }
                    break;
                }
            }
        }
        return (preserve)
            ? XMLSupport.preserveXMLSpace(result)
            : XMLSupport.defaultXMLSpace(result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGDocument#setTitle(String)}.
     */
    public void setTitle(String s) {
        throw new RuntimeException(" !!! TODO: SVGOMDocument.setTitle()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGDocument#getReferrer()}.
     */
    public String getReferrer() {
        throw new RuntimeException(" !!! TODO: SVGOMDocument.getReferrer()");
    }

    /**
     * Sets the referrer string.
     */
    public void setReferrer(String s) {
        throw new RuntimeException(" !!! TODO: SVGOMDocument.setReferrer(String)");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGDocument#getDomain()}.
     */
    public String getDomain() {
        return (url == null) ? null : url.getHost();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGDocument#getRootElement()}.
     */
    public SVGSVGElement getRootElement() {
        return (SVGSVGElement)getDocumentElement();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGDocument#getURL()}
     */
    public String getURL() {
        return url.toString();
    }

    /**
     * Returns the URI of the document.
     */
    public URL getURLObject() {
        return url;
    }

    /**
     * Sets the URI of the document.
     */
    public void setURLObject(URL url) {
        this.url = url;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementById(String)}.
     */
    public Element getElementById(String elementId) {
        if (elementId == null || elementId.equals("")) {
            return null;
        }
        Element e = getDocumentElement();
        if (e == null) {
            return null;
        }
        return getById(elementId, e);
    }

    /**
     * An auxiliary method used by getElementById.
     */
    protected static Element getById(String id, Node node) {
        if (!(node instanceof ElementWithID)) {
            return null;
        }

        ElementWithID e = (ElementWithID)node;
        if (e.getID().equals(id)) {
            return (Element)e;
        }
        for (Node n = node.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            Element result = getById(id, n);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElement(String)}.
     */
    public Element createElement(String tagName) throws DOMException {
        return new GenericElement(tagName.intern(), this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createDocumentFragment()}.
     */
    public DocumentFragment createDocumentFragment() {
        return new GenericDocumentFragment(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createTextNode(String)}.
     */
    public Text createTextNode(String data) {
        return new GenericText(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createComment(String)}.
     */
    public Comment createComment(String data) {
        return new GenericComment(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createCDATASection(String)}
     */
    public CDATASection createCDATASection(String data) throws DOMException {
        return new GenericCDATASection(data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createProcessingInstruction(String,String)}.
     * @return a StyleSheetProcessingInstruction if target is
     *         "xml-stylesheet" or a GenericProcessingInstruction otherwise.
     */
    public ProcessingInstruction createProcessingInstruction(String target,
                                                             String data)
        throws DOMException {
        if ("xml-stylesheet".equals(target)) {
            return new StyleSheetProcessingInstruction
                (data, this, (StyleSheetFactory)getImplementation());
        }
        return new GenericProcessingInstruction(target, data, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttribute(String)}.
     */
    public Attr createAttribute(String name) throws DOMException {
        return new GenericAttr(name.intern(), this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createEntityReference(String)}.
     */
    public EntityReference createEntityReference(String name)
        throws DOMException {
        return new GenericEntityReference(name, this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createAttributeNS(String,String)}.
     */
    public Attr createAttributeNS(String namespaceURI, String qualifiedName)
        throws DOMException {
        if (namespaceURI == null) {
            return new GenericAttr(qualifiedName.intern(), this);
        } else {
            return new GenericAttrNS(namespaceURI.intern(),
                                     qualifiedName.intern(),
                                     this);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#createElementNS(String,String)}.
     */
    public Element createElementNS(String namespaceURI, String qualifiedName)
        throws DOMException {

        if (!SVGDOMImplementation.SVG_NAMESPACE_URI.equals(namespaceURI)) {
            if (namespaceURI == null) {
                return new GenericElement(qualifiedName.intern(), this);
            } else {
                return new GenericElementNS(namespaceURI.intern(),
                                            qualifiedName.intern(),
                                            this);
            }
        }
        String name = DOMUtilities.getLocalName(qualifiedName);
        ElementFactory ef = (ElementFactory)factories.get(name);
        if (ef == null) {
            throw createDOMException(DOMException.NOT_FOUND_ERR,
                                     "invalid.element",
                                     new Object[] { namespaceURI,
                                                    qualifiedName });
        }
        return ef.create(DOMUtilities.getPrefix(qualifiedName));
    }

    // AbstractDocument ///////////////////////////////////////////////

    /**
     * Tests whether the event dispatching must be done.
     */
    public boolean getEventsEnabled() {
        return eventsEnabled;
    }

    /**
     * Sets the eventsEnabled property.
     */
    public void setEventsEnabled(boolean b) {
        eventsEnabled = b;
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
        readonly = v;
    }

    // DocumentStyle /////////////////////////////////////////////////////////

    DOMStyleSheetList styleSheets;

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.stylesheets.DocumentStyle#getStyleSheets()}.
     */
    public StyleSheetList getStyleSheets() {
        if (styleSheets == null) {
            getStyleSheets(this, styleSheets = new DOMStyleSheetList());
        }
        return styleSheets;
    }

    /**
     * An auxiliary method for getStyleSheets.
     */
    protected static void getStyleSheets(Node n, DOMStyleSheetList l) {
        if (n instanceof LinkStyle) {
            l.append(((LinkStyle)n).getSheet());
        }
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
            getStyleSheets(c, l);
        }
    }

    // DocumentView ///////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.views.DocumentView#getDefaultView()}.
     * @return a ViewCSS object.
     */
    public AbstractView getDefaultView() {
        if (defaultView == null) {
            if (context == null) {
                context = new DefaultSVGContext();
            }
            defaultView = new SVGViewCSS(this, context);
        }
        return defaultView;
    }

    // DocumentCSS ////////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.DocumentCSS#getOverrideStyle(Element,String)}.
     */
    public CSSStyleDeclaration getOverrideStyle(Element elt,
                                                String pseudoElt) {
        if (elt instanceof OverrideStyleElement) {
            OverrideStyleElement ose = (OverrideStyleElement)elt;
            return ose.hasOverrideStyle(pseudoElt)
                ? null
                : ose.getOverrideStyle(pseudoElt);
        }
        return null;
    }

    // The element factories /////////////////////////////////////////////////

    /**
     * This interface represents a factory of elements.
     */
    protected interface ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        Element create(String prefix);
    }

    /**
     * To create a 'a' element.
     */
    protected class AElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMAElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'circle' element.
     */
    protected class CircleElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMCircleElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'defs' element.
     */
    protected class DefsElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMDefsElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'desc' element.
     */
    protected class DescElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMDescElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'ellipse' element.
     */
    protected class EllipseElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMEllipseElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feBlend' element.
     */
    protected class FeBlendElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_BLEND);
        }
    }

    /**
     * To create a 'feColorMatrix' element.
     */
    protected class FeColorMatrixElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_COLOR_MATRIX);
        }
    }

    /**
     * To create a 'feComponentTransfer' element.
     */
    protected class FeComponentTransferElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_COMPONENT_TRANSFER);
        }
    }

    /**
     * To create a 'feDiffuseLighting' element.
     */
    protected class FeDiffuseLightingElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_DIFFUSE_LIGHTING);
        }
    }

    /**
     * To create a 'feDisplacementMap' element.
     */
    protected class FeDisplacementMapElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_DISPLACEMENT_MAP);
        }
    }

    /**
     * To create a 'feFlood' element.
     */
    protected class FeFloodElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_FLOOD);
        }
    }

    /**
     * To create a 'feGaussianBlur' element.
     */
    protected class FeGaussianBlurElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEGaussianBlurElement(prefix, 
                                                  SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feImage' element.
     */
    protected class FeImageElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_IMAGE);
        }
    }

    /**
     * To create a 'feMerge' element.
     */
    protected class FeMergeElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_MERGE);
        }
    }

    /**
     * To create a 'feMorphology' element.
     */
    protected class FeMorphologyElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_MORPHOLOGY);
        }
    }


    /**
     * To create a 'feOffset' element.
     */
    protected class FeOffsetElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_OFFSET);
        }
    }

    /**
     * To create a 'feSpecularLighting' element.
     */
    protected class FeSpecularLightingElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_SPECULAR_LIGHTING);
        }
    }

    /**
     * To create a 'feTile' element.
     */
    protected class FeTileElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_TILE);
        }
    }

    /**
     * To create a 'feTurbulence' element
     */
    protected class FeTurbulenceElementFactory implements ElementFactory{
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_FE_TURBULENCE);
        }
    }

    /**
     * To create a 'filter' element.
     */
    protected class FilterElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFilterElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'g' element.
     */
    protected class GElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMGElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'image' element.
     */
    protected class ImageElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMImageElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'line' element.
     */
    protected class LineElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMLineElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'path' element.
     */
    protected class PathElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPathElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'polygon' element.
     */
    protected class PolygonElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPolygonElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'polyline' element.
     */
    protected class PolylineElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPolylineElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'rect' element.
     */
    protected class RectElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMRectElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'style' element.
     */
    protected class StyleElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMStyleElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create an 'svg' element.
     */
    protected class SvgElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMSVGElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'switch' element.
     */
    protected class SwitchElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMSwitchElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'symbol' element.
     */
    protected class SymbolElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMSymbolElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'text' element.
     */
    protected class TextElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTextElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'title' element.
     */
    protected class TitleElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTitleElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'use' element.
     */
    protected class UseElementFactory implements ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMUseElement(prefix, SVGOMDocument.this);
        }
    }
}
