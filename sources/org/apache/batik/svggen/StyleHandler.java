/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Map;
import org.w3c.dom.Element;

/**
 * The <code>StyleHandler</code> interface allows you to specialize
 * how the style will be set on an SVG <code>Element</code>.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface StyleHandler {
    /**
     * Sets the style described by <code>styleMap</code> on the given
     * <code>element</code>.
     * @param element the SVG <code>Element</code> to be styled.
     * @param styleMap the <code>Map</code> containing pairs of style
     * property names, style values.
     */
    public void setStyle(Element element, Map styleMap,
                         SVGGeneratorContext generatorContext);
}
