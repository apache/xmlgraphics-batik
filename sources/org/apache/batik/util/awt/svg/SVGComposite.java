/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Composite object into
 * a set of SVG properties and definitions. Here is
 * how Composites are mapped to SVG:
 * + AlphaComposite.SRC_OVER with extra alpha is mapped
 *   to the opacity attribute
 * + AlphaComposite's other rules are translated into
 *   predefined filter effects.
 * + Custom Composite implementations are handled by the
 *   extension mechanism.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.SVGAlphaComposite
 */
public class SVGComposite implements SVGConverter{
    /**
     * All AlphaComposite convertion is handed to svgAlphaComposite
     */
    private SVGAlphaComposite svgAlphaComposite;

    /**
     * All custom Composite convertion is handed to svgCustomComposite
     */
    private SVGCustomComposite svgCustomComposite;

    /**
     * Used to create DOM elements
     */
    private Document domFactory;

    /**
     * @param domFactory used by the converter to create Element and other
     *        needed DOM objects
     * @param extensionHandler can be invoked to handle unknown Composite
     *        implementations.
     */
    public SVGComposite(Document domFactory, ExtensionHandler extensionHandler){
        this.svgAlphaComposite = new SVGAlphaComposite(domFactory);
        this.svgCustomComposite = new SVGCustomComposite(domFactory, extensionHandler);
        this.domFactory = domFactory;
    }

    /**
     * @param new extension handler this object should use
     */
    void setExtensionHandler(ExtensionHandler extensionHandler){
        this.svgCustomComposite = new SVGCustomComposite(domFactory, extensionHandler);
    }

    /**
     * @return Set of filter Elements defining the composites this
     *         Converter has processed since it was created.
     */
    public Set getDefinitionSet(){
        Set compositeDefs = new HashSet(svgAlphaComposite.getDefinitionSet());
        compositeDefs.addAll(svgCustomComposite.getDefinitionSet());
        return compositeDefs;
    }

    public SVGAlphaComposite getAlphaCompositeConverter(){
        return svgAlphaComposite;
    }

    public SVGCustomComposite getCustomCompositeConverter(){
        return svgCustomComposite;
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.util.awt.svg.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        return toSVG(gc.getComposite());
    }

    /**
     * @param composite Composite to be converted to SVG
     * @return an SVGCompositeDescriptor mapping the SVG composite
     *         equivalent of the input Composite
     */
    public SVGCompositeDescriptor toSVG(Composite composite){
        if(composite instanceof AlphaComposite)
            return svgAlphaComposite.toSVG((AlphaComposite)composite);
        else
            return svgCustomComposite.toSVG(composite);
    }
}
