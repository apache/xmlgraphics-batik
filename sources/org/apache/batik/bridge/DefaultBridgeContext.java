/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.ConcreteGVTFactory;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.dom.svg.SVGDocumentLoader;
import org.apache.batik.bridge.BufferedDocumentLoader;
import org.apache.batik.bridge.BufferedDocumentLoader;
import org.apache.batik.gvt.filter.ConcreteGraphicsNodeRableFactory;
import org.apache.batik.script.ConcreteInterpreterPool;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.ConcreteGVTBuilder;
import org.apache.batik.bridge.DefaultBridgeContext;
import org.apache.batik.gvt.renderer.StaticRendererFactory;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.css.ViewCSS;

/**
 * A default bridge context.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DefaultBridgeContext extends SVGBridgeContext {

    public DefaultBridgeContext(UserAgent userAgent, SVGDocument svgDocument) {
        setDocumentLoader(new BufferedDocumentLoader
                    (new SVGDocumentLoader(userAgent.getXMLParserClassName())));
        setGVTFactory(ConcreteGVTFactory.getGVTFactoryImplementation());
        setParserFactory(new ParserFactory());
        setGraphicsNodeRableFactory(new ConcreteGraphicsNodeRableFactory());
        setUserAgent(userAgent);
        setGVTBuilder(new ConcreteGVTBuilder());
        setGraphicsNodeRenderContext(
            new StaticRendererFactory().getRenderContext());
        setViewCSS((ViewCSS) svgDocument.getRootElement());
        setInterpreterPool(new ConcreteInterpreterPool(svgDocument));
    }

    public DefaultBridgeContext(String parser, SVGDocument svgDocument) {
        // <!> deprecated
        setDocumentLoader(new BufferedDocumentLoader
                          (new SVGDocumentLoader(parser)));
        setGVTFactory(ConcreteGVTFactory.getGVTFactoryImplementation());
        setParserFactory(new ParserFactory());
        setGraphicsNodeRableFactory(new ConcreteGraphicsNodeRableFactory());
        setGVTBuilder(new ConcreteGVTBuilder());
        setUserAgent(new DefaultUserAgent(parser));
        setGraphicsNodeRenderContext(
            new StaticRendererFactory().getRenderContext());
        setViewCSS((ViewCSS) svgDocument.getRootElement());
        setInterpreterPool(new ConcreteInterpreterPool(svgDocument));
    }
}
