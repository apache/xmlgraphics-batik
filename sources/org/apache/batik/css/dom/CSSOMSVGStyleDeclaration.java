/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.SVGCSSEngine;

import org.apache.batik.css.engine.value.Value;

import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents a SVG style declaration.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMSVGStyleDeclaration extends CSSOMStyleDeclaration {
    
    /**
     * The CSS engine.
     */
    protected CSSEngine cssEngine;

    /**
     * Creates a new CSSOMSVGStyleDeclaration.
     */
    public CSSOMSVGStyleDeclaration(ValueProvider vp,
                                    CSSRule parent,
                                    CSSEngine eng) {
        super(vp, parent);
        cssEngine = eng;
    }

    /**
     * Creates the CSS value associated with the given property.
     */
    protected CSSValue createCSSValue(String name) {
        int idx = cssEngine.getPropertyIndex(name);
        if (idx > SVGCSSEngine.WRITING_MODE_INDEX) {
            if (cssEngine.getValueManagers()[idx] instanceof SVGColorManager) {
                return new StyleDeclarationColorValue(name);
            }
            if (cssEngine.getValueManagers()[idx] instanceof SVGPaintManager) {
                return new StyleDeclarationPaintValue(name);
            }
        } else {
            switch (idx) {
            case SVGCSSEngine.FILL_INDEX:
            case SVGCSSEngine.STROKE_INDEX:
                return new StyleDeclarationColorValue(name);

            case SVGCSSEngine.FLOOD_COLOR_INDEX:
            case SVGCSSEngine.LIGHTING_COLOR_INDEX:
            case SVGCSSEngine.STOP_COLOR_INDEX:
                return new StyleDeclarationPaintValue(name);
            }
        }
        return super.createCSSValue(name);
    }

    /**
     * This class represents a CSS value returned by this declaration.
     */
    protected class StyleDeclarationColorValue
        extends CSSOMSVGColor
        implements CSSOMSVGColor.ValueProvider {
        
        /**
         * The property name.
         */
        protected String property;

        /**
         * Creates a new StyleDeclarationColorValue.
         */
        public StyleDeclarationColorValue(String prop) {
            super(null);
            valueProvider = this;
            setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return StyleDeclarationColorValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        if (values == null ||
                            values.get(this) == null ||
                            handler == null) {
                            throw new DOMException
                                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                        }
                        String prio = getPropertyPriority(property);
                        CSSOMSVGStyleDeclaration.this.
                            handler.propertyChanged(property, text, prio);
                    }
                });

            property = prop;
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            return CSSOMSVGStyleDeclaration.this.
                valueProvider.getValue(property);
        }

    }

    /**
     * This class represents a CSS value returned by this declaration.
     */
    protected class StyleDeclarationPaintValue
        extends CSSOMSVGPaint
        implements CSSOMSVGPaint.ValueProvider {
        
        /**
         * The property name.
         */
        protected String property;

        /**
         * Creates a new StyleDeclarationPaintValue.
         */
        public StyleDeclarationPaintValue(String prop) {
            super(null);
            valueProvider = this;
            setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return StyleDeclarationPaintValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        if (values == null ||
                            values.get(this) == null ||
                            handler == null) {
                            throw new DOMException
                                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                        }
                        String prio = getPropertyPriority(property);
                        CSSOMSVGStyleDeclaration.this.
                            handler.propertyChanged(property, text, prio);
                    }
                });

            property = prop;
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            return CSSOMSVGStyleDeclaration.this.
                valueProvider.getValue(property);
        }

    }
    
}
