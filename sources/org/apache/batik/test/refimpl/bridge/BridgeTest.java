/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.refimpl.bridge;

import java.awt.*;
import javax.swing.*;
import org.w3c.dom.*;
import org.apache.batik.gvt.*;
import org.w3c.dom.svg.*;
import org.xml.sax.*;

import org.apache.batik.dom.svg.*;
import org.apache.batik.css.*;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.util.SVGConstants;

public class BridgeTest extends JFrame {

    /**
     * SAX Implementation used to parse SVG documents
     */
    private static final String SAX_PARSER
        = "org.apache.xerces.parsers.SAXParser";

    /**
     * SAC Implementation used to parse CSS data
     */
    private static final String SAC_PARSER = "org.w3c.flute.parser.Parser";

    public BridgeTest(GraphicsNode root) {
        SwingStaticViewer v = new SwingStaticViewer(root);
        getContentPane().add(v, BorderLayout.CENTER);
    }

    public static void main(String [] args) {
        CSSDocumentHandler.setParserClassName(SAC_PARSER);
        SVGDocumentFactory documentFactory = new SVGDocumentFactory(SAX_PARSER);

        // build the DOM document
        SVGDocument svgDocument = null;
        try {
            String entityURI = args[0];
            System.out.println("Loading ... : " + entityURI);
            svgDocument = documentFactory.createDocument
                (SVGConstants.SVG_NAMESPACE_URI, new InputSource(entityURI));
        } catch(SAXException e){
            e.printStackTrace();
            return;
        }

        // create the default SVG context
        BridgeContext ctx
            = new org.apache.batik.refimpl.bridge.SVGBridgeContext();

        // set the GVT implementation we want to use
        GVTFactory gvt
            = org.apache.batik.refimpl.gvt.ConcreteGVTFactory.getGVTFactoryImplementation();
        ctx.setGVTFactory(gvt);

        GVTBuilder gvtBuilder
            = new org.apache.batik.refimpl.bridge.ConcreteGVTBuilder();

        // set the Parser implementation we want to use
        ParserFactory parser
            = new org.apache.batik.refimpl.parser.ParserFactory();
        ctx.setParserFactory(parser);
        JFrame frame = new BridgeTest(gvtBuilder.build(ctx, svgDocument));
        frame.setSize(400, 400);
        frame.show();
    }
}
