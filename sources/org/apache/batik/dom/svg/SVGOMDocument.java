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
import org.w3c.dom.stylesheets.StyleSheet;
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
               SVGConstants {

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
     * The custom elements factories.
     */
    protected static HashTable customFactories;

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
        factories.put(TAG_A,
                      new AElementFactory());

        factories.put(TAG_ANIMATE,
                      new AnimateElementFactory());

        factories.put(TAG_CIRCLE,
                      new CircleElementFactory());

        factories.put(TAG_CLIP_PATH,
                      new ClipPathElementFactory());

        factories.put(TAG_DEFS,
                      new DefsElementFactory());

        factories.put(TAG_DESC,
                      new DescElementFactory());

        factories.put(TAG_ELLIPSE,
                      new EllipseElementFactory());

        factories.put(TAG_FE_BLEND,
                      new FeBlendElementFactory());

        factories.put(TAG_FE_COLOR_MATRIX,
                      new FeColorMatrixElementFactory());

        factories.put(TAG_FE_COMPONENT_TRANSFER,
                      new FeComponentTransferElementFactory());

        factories.put(TAG_FE_COMPOSITE,
                      new FeCompositeElementFactory());

        factories.put(TAG_FE_CONVOLVE_MATRIX,
                      new FeConvolveMatrixElementFactory());

        factories.put(TAG_FE_DIFFUSE_LIGHTING,
                      new FeDiffuseLightingElementFactory());

        factories.put(TAG_FE_DISPLACEMENT_MAP,
                      new FeDisplacementMapElementFactory());

        factories.put(TAG_FE_DISTANT_LIGHT,
                      new FeDistantLightElementFactory());

        factories.put(TAG_FE_FUNC_A,
                      new FeFuncAElementFactory());

        factories.put(TAG_FE_FUNC_R,
                      new FeFuncRElementFactory());

        factories.put(TAG_FE_FUNC_G,
                      new FeFuncGElementFactory());

        factories.put(TAG_FE_FUNC_B,
                      new FeFuncBElementFactory());

        factories.put(TAG_FE_FLOOD,
                      new FeFloodElementFactory());

        factories.put(TAG_FE_GAUSSIAN_BLUR,
                      new FeGaussianBlurElementFactory());

        factories.put(TAG_FE_IMAGE,
                      new FeImageElementFactory());

        factories.put(TAG_FE_MERGE,
                      new FeMergeElementFactory());

        factories.put(TAG_FE_MERGE_NODE,
                      new FeMergeNodeElementFactory());

        factories.put(TAG_FE_MORPHOLOGY,
                      new FeMorphologyElementFactory());

        factories.put(TAG_FE_OFFSET,
                      new FeOffsetElementFactory());

        factories.put(TAG_FE_POINT_LIGHT,
                      new FePointLightElementFactory());

        factories.put(TAG_FE_SPECULAR_LIGHTING,
                      new FeSpecularLightingElementFactory());

        factories.put(TAG_FE_SPOT_LIGHT,
                      new FeSpotLightElementFactory());

        factories.put(TAG_FE_TILE,
                      new FeTileElementFactory());

        factories.put(TAG_FE_TURBULENCE,
                      new FeTurbulenceElementFactory());

        factories.put(TAG_FILTER,
                      new FilterElementFactory());

        factories.put(TAG_G,
                      new GElementFactory());

        factories.put(TAG_IMAGE,
                      new ImageElementFactory());

        factories.put(TAG_LINE,
                      new LineElementFactory());

        factories.put(TAG_LINEAR_GRADIENT,
                      new LinearGradientElementFactory());

        factories.put(TAG_MASK,
                      new MaskElementFactory());

        factories.put(TAG_METADATA,
                      new MetadataElementFactory());

        factories.put(TAG_PATH,
                      new PathElementFactory());

        factories.put(TAG_PATTERN,
                      new PatternElementFactory());

        factories.put(TAG_POLYGON,
                      new PolygonElementFactory());

        factories.put(TAG_POLYLINE,
                      new PolylineElementFactory());

        factories.put(TAG_RADIAL_GRADIENT,
                      new RadialGradientElementFactory());

        factories.put(TAG_RECT,
                      new RectElementFactory());

        factories.put(TAG_SCRIPT,
                      new ScriptElementFactory());

        factories.put(TAG_STOP,
                      new StopElementFactory());

        factories.put(TAG_STYLE,
                      new StyleElementFactory());

        factories.put(TAG_SVG,
                      new SvgElementFactory());

        factories.put(TAG_SWITCH,
                      new SwitchElementFactory());

        factories.put(TAG_SYMBOL,
                      new SymbolElementFactory());

        factories.put(TAG_TEXT,
                      new TextElementFactory());

        factories.put(TAG_TEXT_PATH,
                      new TextPathElementFactory());

        factories.put(TAG_TITLE,
                      new TitleElementFactory());

        factories.put(TAG_TREF,
                      new TrefElementFactory());

        factories.put(TAG_TSPAN,
                      new TspanElementFactory());

        factories.put(TAG_USE,
                      new UseElementFactory());
    }

    /**
     * Allows the user to register a new element factory.
     */
    public static void registerCustomElementFactory(String namespaceURI,
                                                    String localName,
                                                    CustomElementFactory factory) {
        if (customFactories == null) {
            customFactories = new HashTable();
        }
        HashTable ht = (HashTable)customFactories.get(namespaceURI);
        if (ht == null) {
            ht = new HashTable();
        }
        ht.put(localName, factory);
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

        if (SVGDOMImplementation.SVG_NAMESPACE_URI.equals(namespaceURI)) {
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
        if (namespaceURI != null) {
            if (customFactories != null) {
                HashTable ht = (HashTable)customFactories.get(namespaceURI);
                if (ht != null) {
                    String name = DOMUtilities.getLocalName(qualifiedName);
                    CustomElementFactory cef = (CustomElementFactory)ht.get(name);
                    if (cef != null) {
                        return cef.create(DOMUtilities.getPrefix(qualifiedName), this);
                    }
                }
            }
            return new GenericElementNS(namespaceURI.intern(),
                                        qualifiedName.intern(),
                                        this);
        } else {
            return new GenericElement(qualifiedName.intern(), this);
        }
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
            StyleSheet ss = ((LinkStyle)n).getSheet();
            if (ss != null) {
                l.append(ss);
            }
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
            SVGDOMImplementation impl =
                (SVGDOMImplementation)getImplementation();
            ((SVGViewCSS)defaultView).setUserAgentStyleSheet
                (impl.getUserAgentStyleSheet());
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
     * This interface represents a factory of custom elements.
     */
    public interface CustomElementFactory {
        /**
         * Creates an instance of a custom element.
         */
        Element create(String prefix, Document doc);
    }

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
        public AElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMAElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'animate' element.
     */
    protected class AnimateElementFactory implements ElementFactory {
        public AnimateElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   SVGOMDocument.this,
                                                   TAG_ANIMATE);
        }
    }

    /**
     * To create a 'circle' element.
     */
    protected class CircleElementFactory implements ElementFactory {
        public CircleElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMCircleElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'clip-path' element.
     */
    protected class ClipPathElementFactory implements ElementFactory {
        public ClipPathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMClipPathElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'defs' element.
     */
    protected class DefsElementFactory implements ElementFactory {
        public DefsElementFactory() {}
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
        public DescElementFactory() {}
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
        public EllipseElementFactory() {}
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
        public FeBlendElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEBlendElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feColorMatrix' element.
     */
    protected class FeColorMatrixElementFactory implements ElementFactory {
        public FeColorMatrixElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEColorMatrixElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feComponentTransfer' element.
     */
    protected class FeComponentTransferElementFactory
        implements ElementFactory {
        public FeComponentTransferElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEComponentTransferElement(prefix,
                                                       SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feConvolveMatrix' element.
     */
    protected class FeConvolveMatrixElementFactory implements ElementFactory {
        public FeConvolveMatrixElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEConvolveMatrixElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feComposite' element.
     */
    protected class FeCompositeElementFactory
        implements ElementFactory {
        public FeCompositeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFECompositeElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feDiffuseLighting' element.
     */
    protected class FeDiffuseLightingElementFactory implements ElementFactory {
        public FeDiffuseLightingElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEDiffuseLightingElement(prefix,
                                                     SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feDisplacementMap' element.
     */
    protected class FeDisplacementMapElementFactory implements ElementFactory {
        public FeDisplacementMapElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEDisplacementMapElement(prefix,
                                                     SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feDistantLight' element.
     */
    protected class FeDistantLightElementFactory implements ElementFactory {
        public FeDistantLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEDistantLightElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feFlood' element.
     */
    protected class FeFloodElementFactory implements ElementFactory {
        public FeFloodElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEFloodElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feFuncA' element.
     */
    protected class FeFuncAElementFactory implements ElementFactory {
        public FeFuncAElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEFuncAElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feFuncR' element.
     */
    protected class FeFuncRElementFactory implements ElementFactory {
        public FeFuncRElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEFuncRElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feFuncG' element.
     */
    protected class FeFuncGElementFactory implements ElementFactory {
        public FeFuncGElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEFuncGElement(prefix, SVGOMDocument.this);
        }
    }


    /**
     * To create a 'feFuncB' element.
     */
    protected class FeFuncBElementFactory implements ElementFactory {
        public FeFuncBElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEFuncBElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feGaussianBlur' element.
     */
    protected class FeGaussianBlurElementFactory implements ElementFactory {
        public FeGaussianBlurElementFactory() {}
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
        public FeImageElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEImageElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feMerge' element.
     */
    protected class FeMergeElementFactory implements ElementFactory {
        public FeMergeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEMergeElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feMergeNode' element.
     */
    protected class FeMergeNodeElementFactory implements ElementFactory {
        public FeMergeNodeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEMergeNodeElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feMorphology' element.
     */
    protected class FeMorphologyElementFactory implements ElementFactory {
        public FeMorphologyElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEMorphologyElement(prefix,
                                                SVGOMDocument.this);
        }
    }


    /**
     * To create a 'feOffset' element.
     */
    protected class FeOffsetElementFactory implements ElementFactory {
        public FeOffsetElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEOffsetElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'fePointLight' element.
     */
    protected class FePointLightElementFactory implements ElementFactory {
        public FePointLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFEPointLightElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feSpecularLighting' element.
     */
    protected class FeSpecularLightingElementFactory implements ElementFactory {
        public FeSpecularLightingElementFactory() {}
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
     * To create a 'feSpotLight' element.
     */
    protected class FeSpotLightElementFactory implements ElementFactory {
        public FeSpotLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMFESpotLightElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'feTile' element.
     */
    protected class FeTileElementFactory implements ElementFactory {
        public FeTileElementFactory() {}
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
        public FeTurbulenceElementFactory() {}
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
        public FilterElementFactory() {}
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
        public GElementFactory() {}
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
        public ImageElementFactory() {}
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
        public LineElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMLineElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'linearGradient' element.
     */
    protected class LinearGradientElementFactory implements ElementFactory {
        public LinearGradientElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMLinearGradientElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'mask' element.
     */
    protected class MaskElementFactory implements ElementFactory {
        public MaskElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMMaskElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'metadata' element.
     */
    protected class MetadataElementFactory implements ElementFactory {
        public MetadataElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMMetadataElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'path' element.
     */
    protected class PathElementFactory implements ElementFactory {
        public PathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPathElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'pattern' element.
     */
    protected class PatternElementFactory implements ElementFactory {
        public PatternElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPatternElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'polygon' element.
     */
    protected class PolygonElementFactory implements ElementFactory {
        public PolygonElementFactory() {}
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
        public PolylineElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMPolylineElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'radialGradient' element.
     */
    protected class RadialGradientElementFactory implements ElementFactory {
        public RadialGradientElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMRadialGradientElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'rect' element.
     */
    protected class RectElementFactory implements ElementFactory {
        public RectElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMRectElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'script' element.
     */
    protected class ScriptElementFactory implements ElementFactory {
        public ScriptElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMScriptElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'stop' element.
     */
    protected class StopElementFactory implements ElementFactory {
        public StopElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMStopElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'style' element.
     */
    protected class StyleElementFactory implements ElementFactory {
        public StyleElementFactory() {}
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
        public SvgElementFactory() {}
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
        public SwitchElementFactory() {}
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
        public SymbolElementFactory() {}
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
        public TextElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTextElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'textPath' element.
     */
    protected class TextPathElementFactory implements ElementFactory {
        public TextPathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTextPathElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'title' element.
     */
    protected class TitleElementFactory implements ElementFactory {
        public TitleElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTitleElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'tref' element.
     */
    protected class TrefElementFactory implements ElementFactory {
        public TrefElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTRefElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'tspan' element.
     */
    protected class TspanElementFactory implements ElementFactory {
        public TspanElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMTSpanElement(prefix, SVGOMDocument.this);
        }
    }

    /**
     * To create a 'use' element.
     */
    protected class UseElementFactory implements ElementFactory {
        public UseElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix) {
            return new SVGOMUseElement(prefix, SVGOMDocument.this);
        }
    }
}
