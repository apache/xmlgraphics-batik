/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts an custom Composite object into
 * a set of SVG properties and definitions.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGComposite
 */
public class SVGCustomComposite extends AbstractSVGConverter {
    /**
     * @param generatorContext for use by SVGCustomComposite to build Elements
     */
    public SVGCustomComposite(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        return toSVG(gc.getComposite());
    }

    /**
     * @param composite the Composite object to convert to SVG
     * @return an SVGCompositeDescriptor mapping the SVG
     *         composite equivalent to the input Composite.
     */
    public SVGCompositeDescriptor toSVG(Composite composite) {
        SVGCompositeDescriptor compositeDesc =
            (SVGCompositeDescriptor)descMap.get(composite);

        if (compositeDesc == null) {
            // First time this composite is used. Request handler
            // to do the convertion
            SVGCompositeDescriptor desc =
                generatorContext.
                extensionHandler.handleComposite(composite,
                                                 generatorContext);

            if (desc != null) {
                Element def = desc.getDef();
                if(def != null)
                    defSet.add(def);
                descMap.put(composite, desc);
            }
        }

        return compositeDesc;
    }
}
