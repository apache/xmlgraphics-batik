/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.apache.batik.refimpl.gvt.ConcreteGVTFactory;
import org.apache.batik.refimpl.parser.ParserFactory;
import org.apache.batik.dom.svg.SVGDocumentLoader;
import org.apache.batik.refimpl.bridge.BufferedDocumentLoader;
import org.apache.batik.refimpl.bridge.BufferedDocumentLoader;
import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.refimpl.script.ConcreteInterpreterPool;

import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.css.ViewCSS;

/**
 * A default bridge context.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DefaultBridgeContext extends SVGBridgeContext {

    public DefaultBridgeContext(String parser, SVGDocument svgDocument) {
        setDocumentLoader(new BufferedDocumentLoader
                          (new SVGDocumentLoader(parser)));
        setGVTFactory(ConcreteGVTFactory.getGVTFactoryImplementation());
        setParserFactory(new ParserFactory());
        setUserAgent(new DefaultUserAgent());
        setViewCSS((ViewCSS) svgDocument.getRootElement());
        setGraphicsNodeRableFactory(new ConcreteGraphicsNodeRableFactory());
        setInterpreterPool(new ConcreteInterpreterPool());
    }
}
