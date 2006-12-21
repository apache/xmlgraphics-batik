/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.batik.dom.anim.AnimationTarget;
import org.apache.batik.dom.anim.AnimationTargetListener;
import org.apache.batik.anim.values.AnimatableBooleanValue;
import org.apache.batik.anim.values.AnimatableIntegerValue;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.anim.values.AnimatableLengthListValue;
import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableNumberListValue;
import org.apache.batik.anim.values.AnimatableNumberOptionalNumberValue;
import org.apache.batik.anim.values.AnimatablePathDataValue;
import org.apache.batik.anim.values.AnimatablePreserveAspectRatioValue;
import org.apache.batik.anim.values.AnimatablePointListValue;
import org.apache.batik.anim.values.AnimatableRectValue;
import org.apache.batik.anim.values.AnimatableStringValue;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSNavigableNode;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.dom.util.DoublyIndexedTable;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.parser.PathArrayProducer;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedPathData;
import org.w3c.dom.svg.SVGAnimatedPoints;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLengthList;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformList;

/**
 * This class implements the {@link SVGElement} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMElement
    extends    AbstractElement
    implements SVGElement,
               SVGConstants,
               ExtendedTraitAccess,
               AnimationTarget {

    /**
     * Table mapping XML attribute names to TraitInformation objects.
     */
    protected static DoublyIndexedTable xmlTraitInformation;
    static {
        DoublyIndexedTable t = new DoublyIndexedTable();
        t.put(null, SVG_ID_ATTRIBUTE,
                new TraitInformation(false, SVGTypes.TYPE_CDATA));
        t.put(XML_NAMESPACE_URI, XML_BASE_ATTRIBUTE,
                new TraitInformation(false, SVGTypes.TYPE_URI));
        t.put(XML_NAMESPACE_URI, XML_SPACE_ATTRIBUTE,
                new TraitInformation(false, SVGTypes.TYPE_IDENT));
        t.put(XML_NAMESPACE_URI, XML_ID_ATTRIBUTE,
                new TraitInformation(false, SVGTypes.TYPE_CDATA));
        t.put(XML_NAMESPACE_URI, XML_LANG_ATTRIBUTE,
                new TraitInformation(false, SVGTypes.TYPE_LANG));
        xmlTraitInformation = t;
    }

    /**
     * Is this element immutable?
     */
    protected transient boolean readonly;

    /**
     * The element prefix.
     */
    protected String prefix;

    /**
     * The SVG context to get SVG specific informations.
     */
    protected transient SVGContext svgContext;

    /**
     * Table mapping namespaceURI/local name pairs to {@link LinkedList}s
     * of {@link AnimationTargetListener}s.
     */
    protected DoublyIndexedTable targetListeners;

    /**
     * The context used to resolve the units.
     */
    protected UnitProcessor.Context unitContext;

    /**
     * Creates a new Element object.
     */
    protected SVGOMElement() {
    }

    /**
     * Creates a new Element object.
     * @param prefix The namespace prefix.
     * @param owner  The owner document.
     */
    protected SVGOMElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        // initializeLiveAttributes();
    }

    /**
     * Initializes all live attributes for this element.
     */
    protected void initializeAllLiveAttributes() {
        // initializeLiveAttributes();
    }

    /**
     * Initializes the live attribute values of this element.
     */
    private void initializeLiveAttributes(boolean rec) {
        // If live attributes are added here, make sure to uncomment the
        // call to initializeLiveAttributes in the constructor and
        // initializeAllLiveAttributes method above.
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getId()}.
     */
    public String getId() {
        return super.getId();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setId(String)}.
     */
    public void setId(String id) {
        Attr a = getIdAttribute();
        if (a == null) {
            setAttributeNS(null, "id", id);
        } else {
            a.setNodeValue(id);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getXMLbase()}.
     */
    public String getXMLbase() {
        return getAttributeNS(XML_NAMESPACE_URI, XML_BASE_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#setXMLbase(String)}.
     */
    public void setXMLbase(String xmlbase) throws DOMException {
        setAttributeNS(XML_NAMESPACE_URI, XML_BASE_QNAME, xmlbase);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getOwnerSVGElement()}.
     */
    public SVGSVGElement getOwnerSVGElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this);
             e != null;
             e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGSVGElement) {
                return (SVGSVGElement)e;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGElement#getViewportElement()}.
     */
    public SVGElement getViewportElement() {
        for (Element e = CSSEngine.getParentCSSStylableElement(this);
             e != null;
             e = CSSEngine.getParentCSSStylableElement(e)) {
            if (e instanceof SVGFitToViewBox) {
                return (SVGElement)e;
            }
        }
        return null;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getNodeName()}.
     */
    public String getNodeName() {
        if (prefix == null || prefix.equals("")) {
            return getLocalName();
        }

        return prefix + ':' + getLocalName();
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
    }

    /**
     * <b>DOM</b>: Implements {@link Node#setPrefix(String)}.
     */
    public void setPrefix(String prefix) throws DOMException {
        if (isReadonly()) {
            throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     "readonly.node",
                                     new Object[] { new Integer(getNodeType()),
                                                    getNodeName() });
        }
        if (prefix != null &&
            !prefix.equals("") &&
            !DOMUtilities.isValidName(prefix)) {
            throw createDOMException(DOMException.INVALID_CHARACTER_ERR,
                                     "prefix",
                                     new Object[] { new Integer(getNodeType()),
                                                    getNodeName(),
                                                    prefix });
        }
        this.prefix = prefix;
    }

    /**
     * Returns the xml:base attribute value of the given element,
     * resolving any dependency on parent bases if needed.
     * Follows shadow trees when moving to parent nodes.
     */
    protected String getCascadedXMLBase(Node node) {
        String base = null;
        Node n = node.getParentNode();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                base = getCascadedXMLBase((Element) n);
                break;
            }
            if (n instanceof CSSNavigableNode) {
                n = ((CSSNavigableNode) n).getCSSParentNode();
            } else {
                n = n.getParentNode();
            }
        }
        if (base == null) {
            AbstractDocument doc;
            if (node.getNodeType() == Node.DOCUMENT_NODE) {
                doc = (AbstractDocument) node;
            } else {
                doc = (AbstractDocument) node.getOwnerDocument();
            }
            base = doc.getDocumentURI();
        }
        while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
            node = node.getParentNode();
        }
        if (node == null) {
            return base;
        }
        Element e = (Element) node;
        Attr attr = e.getAttributeNodeNS(XML_NAMESPACE_URI, XML_BASE_ATTRIBUTE);
        if (attr != null) {
            if (base == null) {
                base = attr.getNodeValue();
            } else {
                base = new ParsedURL(base, attr.getNodeValue()).toString();
            }
        }
        return base;
    }

    // SVGContext ////////////////////////////////////////////////////

    /**
     * Sets the SVG context to use to get SVG specific informations.
     *
     * @param ctx the SVG context
     */
    public void setSVGContext(SVGContext ctx) {
        svgContext = ctx;
    }

    /**
     * Returns the SVG context used to get SVG specific informations.
     */
    public SVGContext getSVGContext() {
        return svgContext;
    }

    // ExtendedNode //////////////////////////////////////////////////

    /**
     * Creates an SVGException with the appropriate error message.
     */
    public SVGException createSVGException(short type,
                                           String key,
                                           Object [] args) {
        try {
            return new SVGOMException
                (type, getCurrentDocument().formatMessage(key, args));
        } catch (Exception e) {
            return new SVGOMException(type, key);
        }
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

    /**
     * Returns the table of TraitInformation objects for this element.
     */
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    /**
     * Creates a new {@link SVGOMAnimatedTransformList} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedTransformList createLiveAnimatedTransformList
            (String ns, String ln, String def) {
        SVGOMAnimatedTransformList v =
            new SVGOMAnimatedTransformList(this, ns, ln, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedBoolean} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedBoolean createLiveAnimatedBoolean
            (String ns, String ln, boolean def) {
        SVGOMAnimatedBoolean v =
            new SVGOMAnimatedBoolean(this, ns, ln, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedString} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedString createLiveAnimatedString
            (String ns, String ln) {
        SVGOMAnimatedString v =
            new SVGOMAnimatedString(this, ns, ln);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedPreserveAspectRatio} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedPreserveAspectRatio
            createLiveAnimatedPreserveAspectRatio() {
        SVGOMAnimatedPreserveAspectRatio v =
            new SVGOMAnimatedPreserveAspectRatio(this);
        liveAttributeValues.put(null, SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedMarkerOrientValue} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedMarkerOrientValue
            createLiveAnimatedMarkerOrientValue(String ns, String ln) {
        SVGOMAnimatedMarkerOrientValue v =
            new SVGOMAnimatedMarkerOrientValue(this, ns, ln);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedPathData} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedPathData
            createLiveAnimatedPathData(String ns, String ln, String def) {
        SVGOMAnimatedPathData v =
            new SVGOMAnimatedPathData(this, ns, ln, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedNumber} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedNumber createLiveAnimatedNumber
            (String ns, String ln, float def) {
        return createLiveAnimatedNumber(ns, ln, def, false);
    }

    /**
     * Creates a new {@link SVGOMAnimatedNumber} that can be parsed as a
     * percentage and stores it in this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedNumber createLiveAnimatedNumber
            (String ns, String ln, float def, boolean allowPercentage) {
        SVGOMAnimatedNumber v =
            new SVGOMAnimatedNumber(this, ns, ln, def, allowPercentage);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedNumberList} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedNumberList createLiveAnimatedNumberList
            (String ns, String ln, String def, boolean canEmpty) {
        SVGOMAnimatedNumberList v =
            new SVGOMAnimatedNumberList(this, ns, ln, def, canEmpty);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedPoints} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedPoints createLiveAnimatedPoints
            (String ns, String ln, String def) {
        SVGOMAnimatedPoints v =
            new SVGOMAnimatedPoints(this, ns, ln, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedLengthList} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedLengthList createLiveAnimatedLengthList
            (String ns, String ln, String def, boolean emptyAllowed,
             short dir) {
        SVGOMAnimatedLengthList v =
            new SVGOMAnimatedLengthList(this, ns, ln, def, emptyAllowed, dir);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedInteger} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedInteger createLiveAnimatedInteger
            (String ns, String ln, int def) {
        SVGOMAnimatedInteger v =
            new SVGOMAnimatedInteger(this, ns, ln, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedEnumeration} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedEnumeration createLiveAnimatedEnumeration
            (String ns, String ln, String[] val, short def) {
        SVGOMAnimatedEnumeration v =
            new SVGOMAnimatedEnumeration(this, ns, ln, val, def);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    /**
     * Creates a new {@link SVGOMAnimatedLength} and stores it in
     * this element's LiveAttributeValue table.
     */
    protected SVGOMAnimatedLength createLiveAnimatedLength
            (String ns, String ln, String val, short dir, boolean nonneg) {
        SVGOMAnimatedLength v =
            new SVGOMAnimatedLength(this, ns, ln, val, dir, nonneg);
        liveAttributeValues.put(ns, ln, v);
        v.addAnimatedAttributeListener
            (((SVGOMDocument) ownerDocument).getAnimatedAttributeListener());
        return v;
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given CSS property is available on this element.
     */
    public boolean hasProperty(String pn) {
        AbstractStylableDocument doc = (AbstractStylableDocument) ownerDocument;
        CSSEngine eng = doc.getCSSEngine();
        return eng.getPropertyIndex(pn) != -1
            || eng.getShorthandIndex(pn) != -1;
    }

    /**
     * Returns whether the given trait is available on this element.
     */
    public boolean hasTrait(String ns, String ln) {
        // XXX no traits yet
        return false;
    }

    /**
     * Returns whether the given CSS property is animatable.
     */
    public boolean isPropertyAnimatable(String pn) {
        AbstractStylableDocument doc = (AbstractStylableDocument) ownerDocument;
        CSSEngine eng = doc.getCSSEngine();
        int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            ValueManager[] vms = eng.getValueManagers();
            return vms[idx].isAnimatableProperty();
        }
        idx = eng.getShorthandIndex(pn);
        if (idx != -1) {
            ShorthandManager[] sms = eng.getShorthandManagers();
            return sms[idx].isAnimatableProperty();
        }
        return false;
    }

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public final boolean isAttributeAnimatable(String ns, String ln) {
        DoublyIndexedTable t = getTraitInformationTable();
        TraitInformation ti = (TraitInformation) t.get(ns, ln);
        if (ti != null) {
            return ti.isAnimatable();
        }
        return false;
    }

    /**
     * Returns whether the given CSS property is additive.
     */
    public boolean isPropertyAdditive(String pn) {
        AbstractStylableDocument doc = (AbstractStylableDocument) ownerDocument;
        CSSEngine eng = doc.getCSSEngine();
        int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            ValueManager[] vms = eng.getValueManagers();
            return vms[idx].isAdditiveProperty();
        }
        idx = eng.getShorthandIndex(pn);
        if (idx != -1) {
            ShorthandManager[] sms = eng.getShorthandManagers();
            return sms[idx].isAdditiveProperty();
        }
        return false;
    }

    /**
     * Returns whether the given XML attribute is additive.
     */
    public boolean isAttributeAdditive(String ns, String ln) {
        return false;
    }

    /**
     * Returns whether the given trait is animatable.
     */
    public boolean isTraitAnimatable(String ns, String tn) {
        // XXX no traits yet
        return false;
    }

    /**
     * Returns whether the given trait is additive.
     */
    public boolean isTraitAdditive(String ns, String tn) {
        // XXX no traits yet
        return false;
    }

    /**
     * Returns the type of the given property.
     */
    public int getPropertyType(String pn) {
        AbstractStylableDocument doc =
            (AbstractStylableDocument) ownerDocument;
        CSSEngine eng = doc.getCSSEngine();
        int idx = eng.getPropertyIndex(pn);
        if (idx != -1) {
            ValueManager[] vms = eng.getValueManagers();
            return vms[idx].getPropertyType();
        }
        return SVGTypes.TYPE_UNKNOWN;
    }

    /**
     * Returns the type of the given attribute.
     */
    public final int getAttributeType(String ns, String ln) {
        DoublyIndexedTable t = getTraitInformationTable();
        TraitInformation ti = (TraitInformation) t.get(ns, ln);
        if (ti != null) {
            return ti.getType();
        }
        return SVGTypes.TYPE_UNKNOWN;
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Returns the element.
     */
    public Element getElement() {
        return this;
    }

    /**
     * Updates a property value in this target.  Ignored for non-stylable
     * elements.  Overridden in {@link SVGStylableElement} to actually update
     * properties.
     */
    public void updatePropertyValue(String pn, AnimatableValue val) {
    }

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
    }

    /**
     * Updates a 'other' animation value in this target.
     */
    public void updateOtherValue(String type, AnimatableValue val) {
    }

    /**
     * Returns the underlying value of an animatable XML attribute.
     */
    public AnimatableValue getUnderlyingValue(String ns, String ln) {
        return null;
    }

    /**
     * Returns an AnimatableNumberOptionalNumberValue for the base value of
     * the given two SVGAnimatedIntegers.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedInteger n,
                                           SVGAnimatedInteger on) {
        return new AnimatableNumberOptionalNumberValue
            (this, n.getBaseVal(), on.getBaseVal());
    }

    /**
     * Returns an AnimatableNumberOptionalNumberValue for the base value of
     * the given two SVGAnimatedNumbers.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedNumber n,
                                           SVGAnimatedNumber on) {
        return new AnimatableNumberOptionalNumberValue
            (this, n.getBaseVal(), on.getBaseVal());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedBoolean.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedBoolean a) {
        return new AnimatableBooleanValue(this, a.getBaseVal());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedBoolean.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedString a) {
        return new AnimatableStringValue(this, a.getBaseVal());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedBoolean.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedNumber a) {
        return new AnimatableNumberValue(this, a.getBaseVal());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedInteger.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedInteger a) {
        return new AnimatableIntegerValue(this, a.getBaseVal());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedEnumeration.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedEnumeration a) {
        SVGOMAnimatedEnumeration ae = (SVGOMAnimatedEnumeration) a;
        return new AnimatableStringValue(this, ae.getBaseValAsString());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedLength.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedLength a,
                                           short pcInterp) {
        SVGLength l = a.getBaseVal();
        return new AnimatableLengthValue(this, l.getUnitType(),
                                         l.getValueInSpecifiedUnits(),
                                         pcInterp);
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedLengthList.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedLengthList a,
                                           short pcInterp) {
        SVGLengthList ll = a.getBaseVal();
        int n = ll.getNumberOfItems();
        short[] types = new short[n];
        float[] values = new float[n];
        for (int i = 0; i < n; i++) {
            SVGLength l = ll.getItem(i);
            types[i] = l.getUnitType();
            values[i] = l.getValueInSpecifiedUnits();
        }
        return new AnimatableLengthListValue(this, types, values, pcInterp);
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedTransformList.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedTransformList a) {
        SVGTransformList tl = a.getBaseVal();
        int n = tl.getNumberOfItems();
        Vector v = new Vector(n);
        for (int i = 0; i < n; i++) {
            v.add((AbstractSVGTransform) tl.getItem(i));
        }
        return new AnimatableTransformListValue(this, v);
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedPreserveAspectRatio.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedPreserveAspectRatio a) {
        SVGPreserveAspectRatio par = a.getBaseVal();
        return new AnimatablePreserveAspectRatioValue(this, par.getAlign(),
                                                      par.getMeetOrSlice());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedNumberList.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedNumberList a) {
        SVGNumberList nl = a.getBaseVal();
        int n = nl.getNumberOfItems();
        float[] numbers = new float[n];
        for (int i = 0; i < n; i++) {
            numbers[i] = nl.getItem(n).getValue();
        }
        return new AnimatableNumberListValue(this, numbers);
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedPoints.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedPoints a) {
        SVGPointList pl = a.getPoints();
        int n = pl.getNumberOfItems();
        float[] points = new float[n * 2];
        for (int i = 0; i < n; i++) {
            SVGPoint p = pl.getItem(i);
            points[i * 2] = p.getX();
            points[i * 2 + 1] = p.getY();
        }
        return new AnimatablePointListValue(this, points);
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedPathData.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedPathData a) {
        SVGPathSegList psl = a.getPathSegList();
        PathArrayProducer pp = new PathArrayProducer();
        SVGAnimatedPathDataSupport.handlePathSegList(psl, pp);
        return new AnimatablePathDataValue(this, pp.getPathCommands(),
                                           pp.getPathParameters());
    }

    /**
     * Returns an AnimatableValue for the base value of the given
     * SVGAnimatedRect.
     */
    protected AnimatableValue getBaseValue(SVGAnimatedRect a) {
        SVGRect r = a.getBaseVal();
        return new AnimatableRectValue(this, r.getX(), r.getY(), r.getWidth(),
                                       r.getHeight());
    }

    /**
     * Updates an {@link SVGOMAnimatedBoolean} with the given
     * {@link AnimatableValue}.
     */
    protected void updateBooleanAttributeValue(SVGAnimatedBoolean a,
                                               AnimatableValue val) {
        SVGOMAnimatedBoolean ab = (SVGOMAnimatedBoolean) a;
        if (val == null) {
            ab.resetAnimatedValue();
        } else {
            AnimatableBooleanValue animBoolean = (AnimatableBooleanValue) val;
            ab.setAnimatedValue(animBoolean.getValue());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedString} with the given
     * {@link AnimatableValue}.
     */
    protected void updateStringAttributeValue(SVGAnimatedString a,
                                              AnimatableValue val) {
        SVGOMAnimatedString as = (SVGOMAnimatedString) a;
        if (val == null) {
            as.resetAnimatedValue();
        } else {
            AnimatableStringValue animString = (AnimatableStringValue) val;
            as.setAnimatedValue(animString.getString());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedNumber} with the given
     * {@link AnimatableValue}.
     */
    protected void updateNumberAttributeValue(SVGAnimatedNumber a,
                                              AnimatableValue val) {
        SVGOMAnimatedNumber an = (SVGOMAnimatedNumber) a;
        if (val == null) {
            an.resetAnimatedValue();
        } else {
            AnimatableNumberValue animNumber = (AnimatableNumberValue) val;
            an.setAnimatedValue(animNumber.getValue());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedInteger} with the given
     * {@link AnimatableValue}.
     */
    protected void updateIntegerAttributeValue(SVGAnimatedInteger a,
                                               AnimatableValue val) {
        SVGOMAnimatedInteger ai = (SVGOMAnimatedInteger) a;
        if (val == null) {
            ai.resetAnimatedValue();
        } else {
            AnimatableIntegerValue animInteger = (AnimatableIntegerValue) val;
            ai.setAnimatedValue(animInteger.getValue());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedEnumeration} with the given
     * {@link AnimatableValue}.
     */
    protected void updateEnumerationAttributeValue(SVGAnimatedEnumeration a,
                                                   AnimatableValue val) {
        SVGOMAnimatedEnumeration ae = (SVGOMAnimatedEnumeration) a;
        if (val == null) {
            ae.resetAnimatedValue();
        } else {
            AnimatableStringValue animString = (AnimatableStringValue) val;
            ae.setAnimatedValue(animString.getString());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedLength} with the given
     * {@link AnimatableValue}.
     */
    protected void updateLengthAttributeValue(SVGAnimatedLength a,
                                              AnimatableValue val) {
        AbstractSVGAnimatedLength al = (AbstractSVGAnimatedLength) a;
        if (val == null) {
            al.resetAnimatedValue();
        } else {
            AnimatableLengthValue animLength = (AnimatableLengthValue) val;
            al.setAnimatedValue(animLength.getLengthType(),
                                animLength.getLengthValue());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedLengthList} with the given
     * {@link AnimatableValue}.
     */
    protected void updateLengthListAttributeValue(SVGAnimatedLengthList a,
                                                  AnimatableValue val) {
        SVGOMAnimatedLengthList all = (SVGOMAnimatedLengthList) a;
        if (val == null) {
            all.resetAnimatedValue();
        } else {
            AnimatableLengthListValue animLengthList =
                (AnimatableLengthListValue) val;
            all.setAnimatedValue(animLengthList.getLengthTypes(),
                                 animLengthList.getLengthValues());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedTransformList} with the given
     * {@link AnimatableValue}.
     */
    protected void updateTransformListAttributeValue(SVGAnimatedTransformList a,
                                                     AnimatableValue val) {
        SVGOMAnimatedTransformList atl = (SVGOMAnimatedTransformList) a;
        if (val == null) {
            atl.resetAnimatedValue();
        } else {
            AnimatableTransformListValue animTransformList =
                (AnimatableTransformListValue) val;
            atl.setAnimatedValue(animTransformList.getTransforms());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedPreserveAspectRatio} with the given
     * {@link AnimatableValue}.
     */
    protected void updatePreserveAspectRatioAttributeValue
            (SVGAnimatedPreserveAspectRatio a, AnimatableValue val) {
        SVGOMAnimatedPreserveAspectRatio par =
            (SVGOMAnimatedPreserveAspectRatio) a;
        if (val == null) {
            par.resetAnimatedValue();
        } else {
            AnimatablePreserveAspectRatioValue animPreserveAspectRatio =
                (AnimatablePreserveAspectRatioValue) val;
            par.setAnimatedValue(animPreserveAspectRatio.getAlign(),
                                 animPreserveAspectRatio.getMeetOrSlice());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedNumberList} with the given
     * {@link AnimatableValue}.
     */
    protected void updateNumberListAttributeValue(SVGAnimatedNumberList a,
                                                  AnimatableValue val) {
        SVGOMAnimatedNumberList anl = (SVGOMAnimatedNumberList) a;
        if (val == null) {
            anl.resetAnimatedValue();
        } else {
            AnimatableNumberListValue animNumberList =
                (AnimatableNumberListValue) val;
            anl.setAnimatedValue(animNumberList.getNumbers());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedPoints} with the given
     * {@link AnimatableValue}.
     */
    protected void updatePointsAttributeValue(SVGAnimatedPoints a,
                                              AnimatableValue val) {
        SVGOMAnimatedPoints ap = (SVGOMAnimatedPoints) a;
        if (val == null) {
            ap.resetAnimatedValue();
        } else {
            AnimatablePointListValue animPointList =
                (AnimatablePointListValue) val;
            ap.setAnimatedValue(animPointList.getNumbers());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedPathData} with the given
     * {@link AnimatableValue}.
     */
    protected void updatePathDataAttributeValue(SVGAnimatedPathData a,
                                                AnimatableValue val) {
        SVGOMAnimatedPathData apd = (SVGOMAnimatedPathData) a;
        if (val == null) {
            apd.resetAnimatedValue();
        } else {
            AnimatablePathDataValue animPathData =
                (AnimatablePathDataValue) val;
            apd.setAnimatedValue(animPathData.getCommands(),
                                 animPathData.getParameters());
        }
    }

    /**
     * Updates an {@link SVGOMAnimatedRect} with the given
     * {@link AnimatableValue}.
     */
    protected void updateRectAttributeValue(SVGAnimatedRect a,
                                            AnimatableValue val) {
        SVGOMAnimatedRect ar = (SVGOMAnimatedRect) a;
        if (val == null) {
            ar.resetAnimatedValue();
        } else {
            AnimatableRectValue animRect = (AnimatableRectValue) val;
            ar.setAnimatedValue(animRect.getX(), animRect.getY(),
                                animRect.getWidth(), animRect.getHeight());
        }
    }

    /**
     * Gets how percentage values are interpreted by the given attribute
     * or property.
     */
    public short getPercentageInterpretation(String ns, String an,
                                             boolean isCSS) {
        if (isCSS || ns == null) {
            if (an.equals(CSSConstants.CSS_BASELINE_SHIFT_PROPERTY)
                    || an.equals(CSSConstants.CSS_FONT_SIZE_PROPERTY)) {
                return PERCENTAGE_FONT_SIZE;
            }
        }
        if (!isCSS) {
            DoublyIndexedTable t = getTraitInformationTable();
            TraitInformation ti = (TraitInformation) t.get(ns, an);
            if (ti != null) {
                return ti.getPercentageInterpretation();
            }
            return PERCENTAGE_VIEWPORT_SIZE;
        }
        // Default for properties.
        return PERCENTAGE_VIEWPORT_SIZE;
    }

    /**
     * Gets how percentage values are interpreted by the given attribute.
     */
    protected final short getAttributePercentageInterpretation(String ns, String ln) {
        return PERCENTAGE_VIEWPORT_SIZE;
    }

    /**
     * Returns whether color interpolations should be done in linear RGB
     * color space rather than sRGB.  Overriden in {@link SVGStylableElement}
     * to actually look up the 'color-interpolation' property.
     */
    public boolean useLinearRGBColorInterpolation() {
        return false;
    }

    /**
     * Converts the given SVG length into user units.
     * @param v the SVG length value
     * @param type the SVG length units (one of the
     *             {@link SVGLength}.SVG_LENGTH_* constants)
     * @param pcInterp how to interpretet percentage values (one of the
     *             {@link SVGContext}.PERCENTAGE_* constants)
     * @return the SVG value in user units
     */
    public float svgToUserSpace(float v, short type, short pcInterp) {
        if (unitContext == null) {
            unitContext = new UnitContext();
        }
        if (pcInterp == PERCENTAGE_FONT_SIZE
                && type == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            // XXX
            return 0f;
        } else {
            return UnitProcessor.svgToUserSpace(v, type, (short) (3 - pcInterp),
                                                unitContext);
        }
    }

    /**
     * Adds a listener for changes to the given attribute value.
     */
    public void addTargetListener(String ns, String an, boolean isCSS,
                                  AnimationTargetListener l) {
        if (!isCSS) {
            if (targetListeners == null) {
                targetListeners = new DoublyIndexedTable();
            }
            LinkedList ll = (LinkedList) targetListeners.get(ns, an);
            if (ll == null) {
                ll = new LinkedList();
                targetListeners.put(ns, an, ll);
            }
            ll.add(l);
        }
    }

    /**
     * Removes a listener for changes to the given attribute value.
     */
    public void removeTargetListener(String ns, String an, boolean isCSS,
                                     AnimationTargetListener l) {
        if (!isCSS) {
            LinkedList ll = (LinkedList) targetListeners.get(ns, an);
            ll.remove(l);
        }
    }

    /**
     * Fires the listeners registered for changes to the base value of the
     * given attribute.
     */
    void fireBaseAttributeListeners(String ns, String ln) {
        if (targetListeners != null) {
            LinkedList ll = (LinkedList) targetListeners.get(ns, ln);
            Iterator it = ll.iterator();
            while (it.hasNext()) {
                AnimationTargetListener l = (AnimationTargetListener) it.next();
                l.baseValueChanged(this, ns, ln, false);
            }
        }
    }

    // Importation/Cloning ///////////////////////////////////////////

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        SVGOMElement e = (SVGOMElement)n;
        e.prefix = prefix;
        e.initializeAllLiveAttributes();
        return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        SVGOMElement e = (SVGOMElement)n;
        e.prefix = prefix;
        e.initializeAllLiveAttributes();
        return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
        super.copyInto(n);
        SVGOMElement e = (SVGOMElement)n;
        e.prefix = prefix;
        e.initializeAllLiveAttributes();
        return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        SVGOMElement e = (SVGOMElement)n;
        e.prefix = prefix;
        e.initializeAllLiveAttributes();
        return n;
    }

    /**
     * To resolve the units.
     */
    protected class UnitContext implements UnitProcessor.Context {

        /**
         * Returns the element.
         */
        public Element getElement() {
            return SVGOMElement.this;
        }

        /**
         * Returns the size of a px CSS unit in millimeters.
         */
        public float getPixelUnitToMillimeter() {
            return getSVGContext().getPixelUnitToMillimeter();
        }

        /**
         * Returns the size of a px CSS unit in millimeters.
         * This will be removed after next release.
         * @see #getPixelUnitToMillimeter()
         */
        public float getPixelToMM() {
            return getPixelUnitToMillimeter();
        }

        /**
         * Returns the font-size value.
         */
        public float getFontSize() {
            return getSVGContext().getFontSize();
        }

        /**
         * Returns the x-height value.
         */
        public float getXHeight() {
            return 0.5f;
        }

        /**
         * Returns the viewport width used to compute units.
         */
        public float getViewportWidth() {
            return getSVGContext().getViewportWidth();
        }

        /**
         * Returns the viewport height used to compute units.
         */
        public float getViewportHeight() {
            return getSVGContext().getViewportHeight();
        }
    }
}
