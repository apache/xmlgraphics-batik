/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;

import java.awt.Dimension;
import java.awt.Font;

import java.net.URL;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

/**
 * This test validates that the SVGGraphics2D generates the same result 
 * with the two versions of its getRoot method.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class GetRootTest extends AbstractTest implements SVGConstants {
    public static final Dimension CANVAS_SIZE
        = new Dimension(300, 400);

    public static final String ERROR_DIFFERENT_SVG_OUTPUT
        = "GetRootTest.error.different.svg.output";

    public static final String ENTRY_KEY_NO_ARG_OUTPUT 
        = "GetRootTest.entry.key.no.arg.output";

    public static final String ENTRY_KEY_SVG_ARG_OUTPUT
        = "GetRootTest.entry.key.svg.arg.output";

    public TestReport runImpl() throws Exception {
        // First, use the no-argument getRoot

        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
        String namespaceURI = SVGConstants.SVG_NAMESPACE_URI;
        Document domFactory = impl.createDocument(namespaceURI, SVG_SVG_TAG, null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
        GraphicContextDefaults defaults 
            = new GraphicContextDefaults();
        defaults.font = new Font("Arial", Font.PLAIN, 12);
        ctx.setGraphicContextDefaults(defaults);
        SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);

        g2d.setSVGCanvasSize(CANVAS_SIZE);

        Painter painter = new BasicShapes();
        painter.paint(g2d);

        StringWriter swA = new StringWriter();
        g2d.stream(g2d.getRoot(), swA);

        // Now, use the getRoot with argument
        domFactory = impl.createDocument(namespaceURI, SVG_SVG_TAG, null);
        ctx = SVGGeneratorContext.createDefault(domFactory);
        ctx.setGraphicContextDefaults(defaults);
        g2d = new SVGGraphics2D(ctx, false);

        g2d.setSVGCanvasSize(CANVAS_SIZE);

        painter.paint(g2d);

        StringWriter swB = new StringWriter();
        g2d.stream(g2d.getRoot(domFactory.getDocumentElement()),
                   swB);

        // Compare the two output: they should be identical
        if (swA.toString().equals(swB.toString())) {
            return reportSuccess();
        } else {
            TestReport report = reportError(ERROR_DIFFERENT_SVG_OUTPUT);
            report.addDescriptionEntry(ENTRY_KEY_NO_ARG_OUTPUT,
                                       swA.toString());
            report.addDescriptionEntry(ENTRY_KEY_SVG_ARG_OUTPUT,
                                       swB.toString());
            return report;
        }
    }
}
