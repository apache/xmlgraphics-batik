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

package org.apache.batik.extension.svg;

import org.apache.batik.css.engine.value.svg.OpacityManager;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.DomExtension;
import org.apache.batik.dom.svg.ExtensibleSVGDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is a Service interface for classes that want to extend the
 * functionality of the Dom, to support new tags in the rendering tree.
 */
public class BatikDomExtension 
    implements DomExtension, BatikExtConstants {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority() { return 1f; }

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor() {
        return "Thomas DeWeese";
    }

    /**
     * This should contain a contact address (usually an e-mail address).
     */
    public String getContactAddress() {
        return "deweese@apache.org";
    }

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL() {
        return "http://xml.apache.org/batik";
    }

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription() {
        return "Example extension to standard SVG shape tags";
    }

    /**
     * This method should update the DomContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The DomContext instance to be updated
     */
    public void registerTags(ExtensibleSVGDOMImplementation di) {
        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_REGULAR_POLYGON_TAG,
             new BatikRegularPolygonElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_STAR_TAG,
             new BatikStarElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG,
             new BatikHistogramNormalizationElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_MULTI_IMAGE_TAG,
             new BatikMultiImageElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_SOLID_COLOR_TAG,
             new SolidColorElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_TEXT_TAG,
             new FlowTextElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_DIV_TAG,
             new FlowDivElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_PARA_TAG,
             new FlowParaElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_REGION_BREAK_TAG,
             new FlowRegionBreakElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_LINE_TAG,
             new FlowLineElementFactory());

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_FLOW_SPAN_TAG,
             new FlowSpanElementFactory());

        di.registerCustomCSSValueManager
            (new SVGColorManager(BATIK_EXT_SOLID_COLOR_PROPERTY));

        di.registerCustomCSSValueManager
            (new OpacityManager(BATIK_EXT_SOLID_OPACITY_PROPERTY, true));

        di.registerCustomElementFactory
            (BATIK_EXT_NAMESPACE_URI,
             BATIK_EXT_COLOR_SWITCH_TAG,
             new ColorSwitchElementFactory());
    }

    /**
     * To create a 'regularPolygon' element.
     */
    protected static class BatikRegularPolygonElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikRegularPolygonElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikRegularPolygonElement
                (prefix, (AbstractDocument)doc);
        }
    }


    /**
     * To create a 'star' element.
     */
    protected static class BatikStarElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikStarElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikStarElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'histogramNormalization' element.
     */
    protected static class BatikHistogramNormalizationElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikHistogramNormalizationElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikHistogramNormalizationElement
                (prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'multiImage' element.
     */
    protected static class BatikMultiImageElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public BatikMultiImageElementFactory() {}
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new BatikMultiImageElement
                (prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'solidColor' element.
     */
    protected static class SolidColorElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public SolidColorElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new SolidColorElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'solidColor' element.
     */
    protected static class ColorSwitchElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public ColorSwitchElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new ColorSwitchElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowText' element.
     */
    protected static class FlowTextElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowTextElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowTextElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowDiv' element.
     */
    protected static class FlowDivElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowDivElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowDivElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowPara' element.
     */
    protected static class FlowParaElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowParaElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowParaElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowRegionBreak' element.
     */
    protected static class FlowRegionBreakElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowRegionBreakElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowRegionBreakElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowLine' element.
     */
    protected static class FlowLineElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowLineElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowLineElement(prefix, (AbstractDocument)doc);
        }
    }

    /**
     * To create a 'flowSpan' element.
     */
    protected static class FlowSpanElementFactory 
        implements SVGDOMImplementation.ElementFactory {
        public FlowSpanElementFactory() {
        }
        /**
         * Creates an instance of the associated element type.
         */
        public Element create(String prefix, Document doc) {
            return new FlowSpanElement(prefix, (AbstractDocument)doc);
        }
    }
}
