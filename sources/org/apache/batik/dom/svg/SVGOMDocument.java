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
     * The SVG element factories.
     */
    protected static HashTable factories = new HashTable();
    static {
        factories.put(SVG_A_TAG,
                      new AElementFactory());

        factories.put(SVG_ALT_GLYPH_TAG,
                      new AltGlyphElementFactory());

        factories.put(SVG_ALT_GLYPH_DEF_TAG,
                      new AltGlyphDefElementFactory());

        factories.put(SVG_ALT_GLYPH_ITEM_TAG,
                      new AltGlyphItemElementFactory());

        factories.put(SVG_ANIMATE_TAG,
                      new AnimateElementFactory());

        factories.put(SVG_ANIMATE_COLOR_TAG,
                      new AnimateColorElementFactory());

        factories.put(SVG_ANIMATE_MOTION_TAG,
                      new AnimateMotionElementFactory());

        factories.put(SVG_ANIMATE_TRANSFORM_TAG,
                      new AnimateTransformElementFactory());

        factories.put(SVG_CIRCLE_TAG,
                      new CircleElementFactory());

        factories.put(SVG_CLIP_PATH_TAG,
                      new ClipPathElementFactory());

        factories.put(SVG_COLOR_PROFILE_TAG,
                      new ColorProfileElementFactory());

        factories.put(SVG_CURSOR_TAG,
                      new CursorElementFactory());

        factories.put(SVG_DEFINITION_SRC_TAG,
                      new DefinitionSrcElementFactory());

        factories.put(SVG_DEFS_TAG,
                      new DefsElementFactory());

        factories.put(SVG_DESC_TAG,
                      new DescElementFactory());

        factories.put(SVG_ELLIPSE_TAG,
                      new EllipseElementFactory());

        factories.put(SVG_FE_BLEND_TAG,
                      new FeBlendElementFactory());

        factories.put(SVG_FE_COLOR_MATRIX_TAG,
                      new FeColorMatrixElementFactory());

        factories.put(SVG_FE_COMPONENT_TRANSFER_TAG,
                      new FeComponentTransferElementFactory());

        factories.put(SVG_FE_COMPOSITE_TAG,
                      new FeCompositeElementFactory());

        factories.put(SVG_FE_CONVOLVE_MATRIX_TAG,
                      new FeConvolveMatrixElementFactory());

        factories.put(SVG_FE_DIFFUSE_LIGHTING_TAG,
                      new FeDiffuseLightingElementFactory());

        factories.put(SVG_FE_DISPLACEMENT_MAP_TAG,
                      new FeDisplacementMapElementFactory());

        factories.put(SVG_FE_DISTANT_LIGHT_TAG,
                      new FeDistantLightElementFactory());

        factories.put(SVG_FE_FUNC_A_TAG,
                      new FeFuncAElementFactory());

        factories.put(SVG_FE_FUNC_R_TAG,
                      new FeFuncRElementFactory());

        factories.put(SVG_FE_FUNC_G_TAG,
                      new FeFuncGElementFactory());

        factories.put(SVG_FE_FUNC_B_TAG,
                      new FeFuncBElementFactory());

        factories.put(SVG_FE_FLOOD_TAG,
                      new FeFloodElementFactory());

        factories.put(SVG_FE_GAUSSIAN_BLUR_TAG,
                      new FeGaussianBlurElementFactory());

        factories.put(SVG_FE_IMAGE_TAG,
                      new FeImageElementFactory());

        factories.put(SVG_FE_MERGE_TAG,
                      new FeMergeElementFactory());

        factories.put(SVG_FE_MERGE_NODE_TAG,
                      new FeMergeNodeElementFactory());

        factories.put(SVG_FE_MORPHOLOGY_TAG,
                      new FeMorphologyElementFactory());

        factories.put(SVG_FE_OFFSET_TAG,
                      new FeOffsetElementFactory());

        factories.put(SVG_FE_POINT_LIGHT_TAG,
                      new FePointLightElementFactory());

        factories.put(SVG_FE_SPECULAR_LIGHTING_TAG,
                      new FeSpecularLightingElementFactory());

        factories.put(SVG_FE_SPOT_LIGHT_TAG,
                      new FeSpotLightElementFactory());

        factories.put(SVG_FE_TILE_TAG,
                      new FeTileElementFactory());

        factories.put(SVG_FE_TURBULENCE_TAG,
                      new FeTurbulenceElementFactory());

        factories.put(SVG_FONT_TAG,
                      new FontElementFactory());

        factories.put(SVG_FONT_FACE_TAG,
                      new FontFaceElementFactory());

        factories.put(SVG_FONT_FACE_FORMAT_TAG,
                      new FontFaceFormatElementFactory());

        factories.put(SVG_FONT_FACE_NAME_TAG,
                      new FontFaceNameElementFactory());

        factories.put(SVG_FONT_FACE_SRC_TAG,
                      new FontFaceSrcElementFactory());

        factories.put(SVG_FONT_FACE_URI_TAG,
                      new FontFaceUriElementFactory());

        factories.put(SVG_FOREIGN_OBJECT_TAG,
                      new ForeignObjectElementFactory());

        factories.put(SVG_FILTER_TAG,
                      new FilterElementFactory());

        factories.put(SVG_G_TAG,
                      new GElementFactory());

        factories.put(SVG_GLYPH_TAG,
                      new GlyphElementFactory());

        factories.put(SVG_GLYPH_REF_TAG,
                      new GlyphRefElementFactory());

        factories.put(SVG_HKERN_TAG,
                      new HkernElementFactory());

        factories.put(SVG_IMAGE_TAG,
                      new ImageElementFactory());

        factories.put(SVG_LINE_TAG,
                      new LineElementFactory());

        factories.put(SVG_LINEAR_GRADIENT_TAG,
                      new LinearGradientElementFactory());

        factories.put(SVG_MASK_TAG,
                      new MaskElementFactory());

        factories.put(SVG_MARKER_TAG,
                      new MarkerElementFactory());

        factories.put(SVG_METADATA_TAG,
                      new MetadataElementFactory());

        factories.put(SVG_MISSING_GLYPH_TAG,
                      new MissingGlyphElementFactory());

        factories.put(SVG_MPATH_TAG,
                      new MpathElementFactory());

        factories.put(SVG_PATH_TAG,
                      new PathElementFactory());

        factories.put(SVG_PATTERN_TAG,
                      new PatternElementFactory());

        factories.put(SVG_POLYGON_TAG,
                      new PolygonElementFactory());

        factories.put(SVG_POLYLINE_TAG,
                      new PolylineElementFactory());

        factories.put(SVG_RADIAL_GRADIENT_TAG,
                      new RadialGradientElementFactory());

        factories.put(SVG_RECT_TAG,
                      new RectElementFactory());

        factories.put(SVG_SET_TAG,
                      new SetElementFactory());

        factories.put(SVG_SCRIPT_TAG,
                      new ScriptElementFactory());

        factories.put(SVG_STOP_TAG,
                      new StopElementFactory());

        factories.put(SVG_STYLE_TAG,
                      new StyleElementFactory());

        factories.put(SVG_SVG_TAG,
                      new SvgElementFactory());

        factories.put(SVG_SWITCH_TAG,
                      new SwitchElementFactory());

        factories.put(SVG_SYMBOL_TAG,
                      new SymbolElementFactory());

        factories.put(SVG_TEXT_TAG,
                      new TextElementFactory());

        factories.put(TAG_TEXT_PATH,
                      new TextPathElementFactory());

        factories.put(SVG_TITLE_TAG,
                      new TitleElementFactory());

        factories.put(SVG_TREF_TAG,
                      new TrefElementFactory());

        factories.put(SVG_TSPAN_TAG,
                      new TspanElementFactory());

        factories.put(SVG_USE_TAG,
                      new UseElementFactory());

        factories.put(SVG_VIEW_TAG,
                      new ViewElementFactory());

        factories.put(SVG_VKERN_TAG,
                      new VkernElementFactory());
    }

    /**
     * Allows the user to register a new element factory.
     */
    public static void registerCustomElementFactory(String namespaceURI,
                                                    String localName,
                                                    ElementFactory factory) {
        if (customFactories == null) {
            customFactories = new HashTable();
        }
        HashTable ht = (HashTable)customFactories.get(namespaceURI);
        if (ht == null) {
            customFactories.put(namespaceURI, ht = new HashTable());
        }
        ht.put(localName, factory);
    }

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
     * The string representing the referrer.
     */
    protected String referrer;

    /**
     * Creates a new uninitialized document.
     */
    protected SVGOMDocument() {
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
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMDocument();
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
        return referrer;
    }

    /**
     * Sets the referrer string.
     */
    public void setReferrer(String s) {
        referrer = s;
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
        return (url == null) ? null : url.toString();
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
            return ef.create(DOMUtilities.getPrefix(qualifiedName), this);
        }
        if (namespaceURI != null) {
            if (customFactories != null) {
                HashTable ht = (HashTable)customFactories.get(namespaceURI);
                if (ht != null) {
                    String name = DOMUtilities.getLocalName(qualifiedName);
                    ElementFactory cef = (ElementFactory)ht.get(name);
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
     * This interface represents a factory of elements.
     */
    protected interface ElementFactory {
        /**
         * Creates an instance of the associated element type.
         */
        Element create(String prefix, Document doc);
    }

    /**
     * To create a 'a' element.
     */
    protected static class AElementFactory implements ElementFactory {
        public AElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMAElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'altGlyph' element.
     */
    protected static class AltGlyphElementFactory implements ElementFactory {
        public AltGlyphElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ALT_GLYPH_TAG);
        }
    }

    /**
     * To create a 'altGlyphDef' element.
     */
    protected static class AltGlyphDefElementFactory implements ElementFactory {
        public AltGlyphDefElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ALT_GLYPH_DEF_TAG);
        }
    }

    /**
     * To create a 'altGlyphItem' element.
     */
    protected static class AltGlyphItemElementFactory implements ElementFactory {
        public AltGlyphItemElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ALT_GLYPH_ITEM_TAG);
        }
    }

    /**
     * To create a 'animate' element.
     */
    protected static class AnimateElementFactory implements ElementFactory {
        public AnimateElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ANIMATE_TAG);
        }
    }

    /**
     * To create a 'animateColor' element.
     */
    protected static class AnimateColorElementFactory implements ElementFactory {
        public AnimateColorElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ANIMATE_COLOR_TAG);
        }
    }

    /**
     * To create a 'animateMotion' element.
     */
    protected static class AnimateMotionElementFactory implements ElementFactory {
        public AnimateMotionElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ANIMATE_MOTION_TAG);
        }
    }

    /**
     * To create a 'animateTransform' element.
     */
    protected static class AnimateTransformElementFactory implements ElementFactory {
        public AnimateTransformElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_ANIMATE_TRANSFORM_TAG);
        }
    }

    /**
     * To create a 'circle' element.
     */
    protected static class CircleElementFactory implements ElementFactory {
        public CircleElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMCircleElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'clip-path' element.
     */
    protected static class ClipPathElementFactory implements ElementFactory {
        public ClipPathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMClipPathElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'color-profile' element.
     */
    protected static class ColorProfileElementFactory implements ElementFactory {
        public ColorProfileElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_COLOR_PROFILE_TAG);
        }
    }

    /**
     * To create a 'cursor' element.
     */
    protected static class CursorElementFactory implements ElementFactory {
        public CursorElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_CURSOR_TAG);
        }
    }

    /**
     * To create a 'definition-src' element.
     */
    protected static class DefinitionSrcElementFactory implements ElementFactory {
        public DefinitionSrcElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_DEFINITION_SRC_TAG);
        }
    }

    /**
     * To create a 'defs' element.
     */
    protected static class DefsElementFactory implements ElementFactory {
        public DefsElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMDefsElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'desc' element.
     */
    protected static class DescElementFactory implements ElementFactory {
        public DescElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMDescElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'ellipse' element.
     */
    protected static class EllipseElementFactory implements ElementFactory {
        public EllipseElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMEllipseElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feBlend' element.
     */
    protected static class FeBlendElementFactory implements ElementFactory {
        public FeBlendElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEBlendElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feColorMatrix' element.
     */
    protected static class FeColorMatrixElementFactory implements ElementFactory {
        public FeColorMatrixElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEColorMatrixElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feComponentTransfer' element.
     */
    protected static class FeComponentTransferElementFactory
        implements ElementFactory {
        public FeComponentTransferElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEComponentTransferElement(prefix,
                                                       (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feConvolveMatrix' element.
     */
    protected static class FeConvolveMatrixElementFactory implements ElementFactory {
        public FeConvolveMatrixElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEConvolveMatrixElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feComposite' element.
     */
    protected static class FeCompositeElementFactory
        implements ElementFactory {
        public FeCompositeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFECompositeElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feDiffuseLighting' element.
     */
    protected static class FeDiffuseLightingElementFactory implements ElementFactory {
        public FeDiffuseLightingElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDiffuseLightingElement(prefix,
                                                     (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feDisplacementMap' element.
     */
    protected static class FeDisplacementMapElementFactory implements ElementFactory {
        public FeDisplacementMapElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDisplacementMapElement(prefix,
                                                     (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feDistantLight' element.
     */
    protected static class FeDistantLightElementFactory implements ElementFactory {
        public FeDistantLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDistantLightElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feFlood' element.
     */
    protected static class FeFloodElementFactory implements ElementFactory {
        public FeFloodElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFloodElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feFuncA' element.
     */
    protected static class FeFuncAElementFactory implements ElementFactory {
        public FeFuncAElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncAElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feFuncR' element.
     */
    protected static class FeFuncRElementFactory implements ElementFactory {
        public FeFuncRElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncRElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feFuncG' element.
     */
    protected static class FeFuncGElementFactory implements ElementFactory {
        public FeFuncGElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncGElement(prefix, (AbstractDocument)doc);
        }
    }


    /**
     * To create a 'feFuncB' element.
     */
    protected static class FeFuncBElementFactory implements ElementFactory {
        public FeFuncBElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncBElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feGaussianBlur' element.
     */
    protected static class FeGaussianBlurElementFactory implements ElementFactory {
        public FeGaussianBlurElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEGaussianBlurElement(prefix,
                                                  (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feImage' element.
     */
    protected static class FeImageElementFactory implements ElementFactory {
        public FeImageElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEImageElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feMerge' element.
     */
    protected static class FeMergeElementFactory implements ElementFactory {
        public FeMergeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMergeElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feMergeNode' element.
     */
    protected static class FeMergeNodeElementFactory implements ElementFactory {
        public FeMergeNodeElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMergeNodeElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feMorphology' element.
     */
    protected static class FeMorphologyElementFactory implements ElementFactory {
        public FeMorphologyElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMorphologyElement(prefix,
                                                (AbstractDocument)doc);
        }
    }


    /**
     * To create a 'feOffset' element.
     */
    protected static class FeOffsetElementFactory implements ElementFactory {
        public FeOffsetElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEOffsetElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'fePointLight' element.
     */
    protected static class FePointLightElementFactory implements ElementFactory {
        public FePointLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFEPointLightElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feSpecularLighting' element.
     */
    protected static class FeSpecularLightingElementFactory implements ElementFactory {
        public FeSpecularLightingElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFESpecularLightingElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feSpotLight' element.
     */
    protected static class FeSpotLightElementFactory implements ElementFactory {
        public FeSpotLightElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFESpotLightElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feTile' element.
     */
    protected static class FeTileElementFactory implements ElementFactory {
        public FeTileElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFETileElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'feTurbulence' element
     */
    protected static class FeTurbulenceElementFactory implements ElementFactory{
        public FeTurbulenceElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFETurbulenceElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'font' element.
     */
    protected static class FontElementFactory implements ElementFactory {
        public FontElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_TAG);
        }
    }

    /**
     * To create a 'font-face' element.
     */
    protected static class FontFaceElementFactory implements ElementFactory {
        public FontFaceElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_FACE_TAG);
        }
    }

    /**
     * To create a 'font-face-format' element.
     */
    protected static class FontFaceFormatElementFactory implements ElementFactory {
        public FontFaceFormatElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_FACE_FORMAT_TAG);
        }
    }

    /**
     * To create a 'font-face-name' element.
     */
    protected static class FontFaceNameElementFactory implements ElementFactory {
        public FontFaceNameElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_FACE_NAME_TAG);
        }
    }

    /**
     * To create a 'font-face-src' element.
     */
    protected static class FontFaceSrcElementFactory implements ElementFactory {
        public FontFaceSrcElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_FACE_SRC_TAG);
        }
    }

    /**
     * To create a 'font-face-uri' element.
     */
    protected static class FontFaceUriElementFactory implements ElementFactory {
        public FontFaceUriElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FONT_FACE_URI_TAG);
        }
    }

    /**
     * To create a 'filter' element.
     */
    protected static class FilterElementFactory implements ElementFactory {
        public FilterElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMFilterElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'foreignObject' element.
     */
    protected static class ForeignObjectElementFactory implements ElementFactory {
        public ForeignObjectElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_FOREIGN_OBJECT_TAG);
        }
    }

    /**
     * To create a 'g' element.
     */
    protected static class GElementFactory implements ElementFactory {
        public GElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMGElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'glyph' element.
     */
    protected static class GlyphElementFactory implements ElementFactory {
        public GlyphElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_GLYPH_TAG);
        }
    }

    /**
     * To create a 'glyphRef' element.
     */
    protected static class GlyphRefElementFactory implements ElementFactory {
        public GlyphRefElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_GLYPH_REF_TAG);
        }
    }

    /**
     * To create a 'hkern' element.
     */
    protected static class HkernElementFactory implements ElementFactory {
        public HkernElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_HKERN_TAG);
        }
    }

    /**
     * To create a 'image' element.
     */
    protected static class ImageElementFactory implements ElementFactory {
        public ImageElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMImageElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'line' element.
     */
    protected static class LineElementFactory implements ElementFactory {
        public LineElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMLineElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'linearGradient' element.
     */
    protected static class LinearGradientElementFactory implements ElementFactory {
        public LinearGradientElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMLinearGradientElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'mask' element.
     */
    protected static class MaskElementFactory implements ElementFactory {
        public MaskElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMMaskElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'marker' element.
     */
    protected static class MarkerElementFactory implements ElementFactory {
        public MarkerElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMMarkerElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'metadata' element.
     */
    protected static class MetadataElementFactory implements ElementFactory {
        public MetadataElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMMetadataElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'missing-glyph' element.
     */
    protected static class MissingGlyphElementFactory implements ElementFactory {
        public MissingGlyphElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_MISSING_GLYPH_TAG);
        }
    }

    /**
     * To create a 'mpath' element.
     */
    protected static class MpathElementFactory implements ElementFactory {
        public MpathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_MPATH_TAG);
        }
    }

    /**
     * To create a 'path' element.
     */
    protected static class PathElementFactory implements ElementFactory {
        public PathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMPathElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'pattern' element.
     */
    protected static class PatternElementFactory implements ElementFactory {
        public PatternElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMPatternElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'polygon' element.
     */
    protected static class PolygonElementFactory implements ElementFactory {
        public PolygonElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMPolygonElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'polyline' element.
     */
    protected static class PolylineElementFactory implements ElementFactory {
        public PolylineElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMPolylineElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'radialGradient' element.
     */
    protected static class RadialGradientElementFactory implements ElementFactory {
        public RadialGradientElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMRadialGradientElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'rect' element.
     */
    protected static class RectElementFactory implements ElementFactory {
        public RectElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMRectElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'set' element.
     */
    protected static class SetElementFactory implements ElementFactory {
        public SetElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_SET_TAG);
        }
    }

    /**
     * To create a 'script' element.
     */
    protected static class ScriptElementFactory implements ElementFactory {
        public ScriptElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMScriptElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'stop' element.
     */
    protected static class StopElementFactory implements ElementFactory {
        public StopElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMStopElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'style' element.
     */
    protected static class StyleElementFactory implements ElementFactory {
        public StyleElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMStyleElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create an 'svg' element.
     */
    protected static class SvgElementFactory implements ElementFactory {
        public SvgElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMSVGElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'switch' element.
     */
    protected static class SwitchElementFactory implements ElementFactory {
        public SwitchElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMSwitchElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'symbol' element.
     */
    protected static class SymbolElementFactory implements ElementFactory {
        public SymbolElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMSymbolElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'text' element.
     */
    protected static class TextElementFactory implements ElementFactory {
        public TextElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMTextElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'textPath' element.
     */
    protected static class TextPathElementFactory implements ElementFactory {
        public TextPathElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMTextPathElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'title' element.
     */
    protected static class TitleElementFactory implements ElementFactory {
        public TitleElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMTitleElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'tref' element.
     */
    protected static class TrefElementFactory implements ElementFactory {
        public TrefElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMTRefElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'tspan' element.
     */
    protected static class TspanElementFactory implements ElementFactory {
        public TspanElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMTSpanElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'use' element.
     */
    protected static class UseElementFactory implements ElementFactory {
        public UseElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMUseElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'view' element.
     */
    protected static class ViewElementFactory implements ElementFactory {
        public ViewElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_VIEW_TAG);
        }
    }

    /**
     * To create a 'vkern' element.
     */
    protected static class VkernElementFactory implements ElementFactory {
        public VkernElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SVGOMToBeImplementedElement(prefix,
                                                   (AbstractDocument)doc,
                                                   SVG_VKERN_TAG);
        }
    }

}
