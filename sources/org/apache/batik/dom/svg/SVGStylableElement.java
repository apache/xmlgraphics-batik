/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.dom.svg;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.css.dom.CSSOMSVGColor;
import org.apache.batik.css.dom.CSSOMSVGPaint;
import org.apache.batik.css.dom.CSSOMSVGStyleDeclaration;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedString;

/**
 * This class provides a common superclass for elements which implement
 * SVGStylable.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGStylableElement
    extends SVGOMElement
    implements CSSStylableElement {

    /**
     * The computed style map.
     */
    protected StyleMap computedStyleMap;

    /**
     * Creates a new SVGStylableElement object.
     */
    protected SVGStylableElement() {
    }

    /**
     * Creates a new SVGStylableElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGStylableElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }
    
    // CSSStylableElement //////////////////////////////////////////
    
    /**
     * Returns the computed style of this element/pseudo-element.
     */
    public StyleMap getComputedStyleMap(String pseudoElement) {
        return computedStyleMap;
    }

    /**
     * Sets the computed style of this element/pseudo-element.
     */
    public void setComputedStyleMap(String pseudoElement, StyleMap sm) {
        computedStyleMap = sm;
    }

    /**
     * Returns the ID of this element.
     */
    public String getXMLId() {
        return getAttributeNS(null, "id");
    }

    /**
     * Returns the class of this element.
     */
    public String getCSSClass() {
        return getAttributeNS(null, "class");
    }

    /**
     * Returns the CSS base URL of this element.
     */
    public URL getCSSBase() {
        try {
            String bu = XMLBaseSupport.getCascadedXMLBase(this);
            if (bu == null) {
                return null;
            }
            return new URL(bu);
        } catch (MalformedURLException e) {
            // !!! TODO
            e.printStackTrace();
            throw new InternalError();
        }
    }

    /**
     * Tells whether this element is an instance of the given pseudo
     * class.
     */
    public boolean isPseudoInstanceOf(String pseudoClass) {
        if (pseudoClass.equals("first-child")) {
            Node n = getPreviousSibling();
            while (n != null && n.getNodeType() != ELEMENT_NODE) {
                n = n.getPreviousSibling();
            }
            return n == null;
        }
        return false;
    }

    // SVGStylable support ///////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGStylable#getStyle()}.
     */
    public CSSStyleDeclaration getStyle() {
        CSSStyleDeclaration result =
            (CSSStyleDeclaration)getLiveAttributeValue(null,
                                                       SVG_STYLE_ATTRIBUTE);
        if (result == null) {
            CSSEngine eng = ((SVGOMDocument)getOwnerDocument()).getCSSEngine();
            result = new StyleDeclaration(eng);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getPresentationAttribute(String)}.
     */
    public CSSValue getPresentationAttribute(String name) {
        CSSValue result = (CSSValue)getLiveAttributeValue(null, name);
        if (result != null)
            return result;

        CSSEngine eng = ((SVGOMDocument)getOwnerDocument()).getCSSEngine();
        int idx = eng.getPropertyIndex(name);
        if (idx == -1) 
            return null;

        if (idx > SVGCSSEngine.FINAL_INDEX) {
            if (eng.getValueManagers()[idx] instanceof SVGPaintManager) {
                result = new PresentationAttributePaintValue(eng, name);
            }
            if (eng.getValueManagers()[idx] instanceof SVGColorManager) {
                result = new PresentationAttributeColorValue(eng, name);
            }
        } else {
            switch (idx) {
            case SVGCSSEngine.FILL_INDEX:
            case SVGCSSEngine.STROKE_INDEX:
                result = new PresentationAttributePaintValue(eng, name);
                break;
                
            case SVGCSSEngine.FLOOD_COLOR_INDEX:
            case SVGCSSEngine.LIGHTING_COLOR_INDEX:
            case SVGCSSEngine.STOP_COLOR_INDEX:
                result = new PresentationAttributeColorValue(eng, name);
                break;
                
            default:
                result = new PresentationAttributeValue(eng, name);
            }
        }
        putLiveAttributeValue(null, name, (LiveAttributeValue)result);
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGStylable#getClassName()}.
     */
    public SVGAnimatedString getClassName() {
        return getAnimatedStringAttribute(null, SVG_CLASS_ATTRIBUTE);
    }

    /**
     * To manage a presentation attribute value.
     */
    public class PresentationAttributeValue
        extends CSSOMValue
        implements LiveAttributeValue,
                   CSSOMValue.ValueProvider {

        /**
         * The CSS engine.
         */
        protected CSSEngine cssEngine;

        /**
         * The property name.
         */
        protected String property;

        /**
         * The value.
         */
        protected Value value;

        /**
         * Whether the mutation comes from this object.
         */
        protected boolean mutate;

        /**
         * Creates a new PresentationAttributeValue.
         */
        public PresentationAttributeValue(CSSEngine eng, String prop) {
            super(null);
            valueProvider = this;
            setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return PresentationAttributeValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        value = cssEngine.parsePropertyValue
                            (SVGStylableElement.this, property, text);
                        mutate = true;
                        setAttributeNS(null, property, text);
                        mutate = false;
                    }
                });

            cssEngine = eng;
            property = prop;

            Attr attr = getAttributeNodeNS(null, prop);
            if (attr != null) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, prop, attr.getValue());
            }
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            if (value == null) {
                throw new DOMException(DOMException.INVALID_STATE_ERR, "");
            }
            return value;
        }

        // LiveAttributeValue //////////////////////////////////////

        /**
         * Called when an Attr node has been added.
         */
        public void attrAdded(Attr node, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been modified.
         */
        public void attrModified(Attr node, String oldv, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been removed.
         */
        public void attrRemoved(Attr node, String oldv) {
            if (!mutate) {
                value = null;
            }
        }
    }

    /**
     * To manage a presentation attribute SVGColor value.
     */
    public class PresentationAttributeColorValue
        extends CSSOMSVGColor
        implements LiveAttributeValue,
                   CSSOMSVGColor.ValueProvider {

        /**
         * The CSS engine.
         */
        protected CSSEngine cssEngine;

        /**
         * The property name.
         */
        protected String property;

        /**
         * The value.
         */
        protected Value value;

        /**
         * Whether the mutation comes from this object.
         */
        protected boolean mutate;

        /**
         * Creates a new PresentationAttributeColorValue.
         */
        public PresentationAttributeColorValue(CSSEngine eng, String prop) {
            super(null);
            valueProvider = this;
            setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return PresentationAttributeColorValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        value = cssEngine.parsePropertyValue
                            (SVGStylableElement.this, property, text);
                        mutate = true;
                        setAttributeNS(null, property, text);
                        mutate = false;
                    }
                });

            cssEngine = eng;
            property = prop;

            Attr attr = getAttributeNodeNS(null, prop);
            if (attr != null) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, prop, attr.getValue());
            }
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            if (value == null) {
                throw new DOMException(DOMException.INVALID_STATE_ERR, "");
            }
            return value;
        }

        // LiveAttributeValue //////////////////////////////////////

        /**
         * Called when an Attr node has been added.
         */
        public void attrAdded(Attr node, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been modified.
         */
        public void attrModified(Attr node, String oldv, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been removed.
         */
        public void attrRemoved(Attr node, String oldv) {
            if (!mutate) {
                value = null;
            }
        }
    }

    /**
     * To manage a presentation attribute SVGPaint value.
     */
    public class PresentationAttributePaintValue
        extends CSSOMSVGPaint
        implements LiveAttributeValue,
                   CSSOMSVGPaint.ValueProvider {

        /**
         * The CSS engine.
         */
        protected CSSEngine cssEngine;

        /**
         * The property name.
         */
        protected String property;

        /**
         * The value.
         */
        protected Value value;

        /**
         * Whether the mutation comes from this object.
         */
        protected boolean mutate;

        /**
         * Creates a new PresentationAttributeColorValue.
         */
        public PresentationAttributePaintValue(CSSEngine eng, String prop) {
            super(null);
            valueProvider = this;
            setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return PresentationAttributePaintValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        value = cssEngine.parsePropertyValue
                            (SVGStylableElement.this, property, text);
                        mutate = true;
                        setAttributeNS(null, property, text);
                        mutate = false;
                    }
                });


            cssEngine = eng;
            property = prop;

            Attr attr = getAttributeNodeNS(null, prop);
            if (attr != null) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, prop, attr.getValue());
            }
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            if (value == null) {
                throw new DOMException(DOMException.INVALID_STATE_ERR, "");
            }
            return value;
        }

        // LiveAttributeValue //////////////////////////////////////

        /**
         * Called when an Attr node has been added.
         */
        public void attrAdded(Attr node, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been modified.
         */
        public void attrModified(Attr node, String oldv, String newv) {
            if (!mutate) {
                value = cssEngine.parsePropertyValue
                    (SVGStylableElement.this, property, newv);
            }
        }

        /**
         * Called when an Attr node has been removed.
         */
        public void attrRemoved(Attr node, String oldv) {
            if (!mutate) {
                value = null;
            }
        }
    }

    /**
     * This class represents the 'style' attribute.
     */
    public class StyleDeclaration
        extends CSSOMSVGStyleDeclaration
        implements LiveAttributeValue,
                   CSSOMSVGStyleDeclaration.ValueProvider,
                   CSSOMSVGStyleDeclaration.ModificationHandler,
                   CSSEngine.MainPropertyReceiver {
        
        /**
         * The associated CSS object.
         */
        protected org.apache.batik.css.engine.StyleDeclaration declaration;

        /**
         * Whether the mutation comes from this object.
         */
        protected boolean mutate;

        /**
         * Creates a new StyleDeclaration.
         */
        public StyleDeclaration(CSSEngine eng) {
            super(null, null, eng);
            valueProvider = this;
            setModificationHandler(this);

            declaration = cssEngine.parseStyleDeclaration
                (SVGStylableElement.this,
                 getAttributeNS(null, SVG_STYLE_ATTRIBUTE));
        }

        // ValueProvider ////////////////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue(String name) {
            int idx = cssEngine.getPropertyIndex(name);
            for (int i = 0; i < declaration.size(); i++) {
                if (idx == declaration.getIndex(i)) {
                    return declaration.getValue(i);
                }
            }
            return null;
        }

        /**
         * Tells whether the given property is important.
         */
        public boolean isImportant(String name) {
            int idx = cssEngine.getPropertyIndex(name);
            for (int i = 0; i < declaration.size(); i++) {
                if (idx == declaration.getIndex(i)) {
                    return declaration.getPriority(i);
                }
            }
            return false;
        }

        /**
         * Returns the text of the declaration.
         */
        public String getText() {
            return declaration.toString(cssEngine);
        }

        /**
         * Returns the length of the declaration.
         */
        public int getLength() {
            return declaration.size();
        }

        /**
         * Returns the value at the given.
         */
        public String item(int idx) {
            return cssEngine.getPropertyName(declaration.getIndex(idx));
        }

        // LiveAttributeValue //////////////////////////////////////

        /**
         * Called when an Attr node has been added.
         */
        public void attrAdded(Attr node, String newv) {
            if (!mutate) {
                declaration = cssEngine.parseStyleDeclaration
                    (SVGStylableElement.this, newv);
            }
        }

        /**
         * Called when an Attr node has been modified.
         */
        public void attrModified(Attr node, String oldv, String newv) {
            if (!mutate) {
                declaration = cssEngine.parseStyleDeclaration
                    (SVGStylableElement.this, newv);
            }
        }

        /**
         * Called when an Attr node has been removed.
         */
        public void attrRemoved(Attr node, String oldv) {
            if (!mutate) {
                declaration =
                    new org.apache.batik.css.engine.StyleDeclaration();
            }
        }

        // ModificationHandler ////////////////////////////////////

        /**
         * Called when the value text has changed.
         */
        public void textChanged(String text) throws DOMException {
            declaration = cssEngine.parseStyleDeclaration
                (SVGStylableElement.this, text);
            mutate = true;
            setAttributeNS(null, SVG_STYLE_ATTRIBUTE, text);
            mutate = false;
        }

        /**
         * Called when a property was removed.
         */
        public void propertyRemoved(String name) throws DOMException {
            int idx = cssEngine.getPropertyIndex(name);
            for (int i = 0; i < declaration.size(); i++) {
                if (idx == declaration.getIndex(i)) {
                    declaration.remove(i);
                    mutate = true;
                    setAttributeNS(null, SVG_STYLE_ATTRIBUTE,
                                   declaration.toString(cssEngine));
                    mutate = false;
                    return;
                }
            }
        }

        public void setMainProperty(String name, Value v, boolean important) {
            int idx = cssEngine.getPropertyIndex(name);
            if (idx == -1) 
                return;   // unknown property

            int i=0;
            for (; i < declaration.size(); i++) {
                if (idx == declaration.getIndex(i))
                    break;
            }
            if (i < declaration.size()) 
                declaration.put(i, v, idx, important);
            else
                declaration.append(v, idx, important);
        }

        /**
         * Called when a property was changed.
         */
        public void propertyChanged(String name, String value, String prio)
            throws DOMException {
            boolean important = ((prio != null) && (prio.length() >0));
            cssEngine.setMainProperties(SVGStylableElement.this,
                                        this, name, value, important);
            mutate = true;
            setAttributeNS(null, SVG_STYLE_ATTRIBUTE,
                           declaration.toString(cssEngine));
            mutate = false;
        }
    }
}
