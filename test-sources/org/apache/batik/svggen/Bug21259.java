/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.io.*;
import org.apache.batik.svggen.*;
import org.apache.batik.transcoder.image.*;
import org.apache.batik.transcoder.*;
import org.apache.batik.dom.svg.*;
import org.w3c.dom.*;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

/**
 * Checks that the streamed root is not removed from its parent 
 * as shown by bug report 21259.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Bug21259 extends AbstractTest{
    public TestReport runImpl() throws Exception {
        Document document = 
            SVGDOMImplementation.getDOMImplementation()
            .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI , "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setComment("Test");
        SVGGraphics2D graphics = new SVGGraphics2D(ctx, false);
        graphics.setSVGCanvasSize(new Dimension(600, 400));

        graphics.setColor(Color.red);
        graphics.setBackground(Color.black);
        graphics.fill(new Rectangle(0,0,100,100));

        // Populate the Document's root with the content of the tree
        Element root = document.getDocumentElement();
        graphics.getRoot(root);
        Writer writer = new StringWriter();
        graphics.stream(root, writer);

        assertTrue(root.getParentNode() == document);
        return reportSuccess();
    }
}
