/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.apache.batik.swing.*;
import org.apache.batik.svggen.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;
import org.apache.batik.dom.svg.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import org.apache.batik.bridge.*;
import org.apache.batik.test.*;

/**
 * Checks that the content generated from the SVGGraphics2D and to which
 * an event handler has been added can be processed by Batik.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ShowGraphics2DOutput extends AbstractTest {
    public TestReport runImpl() throws Exception {

        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        SVGDocument doc = (SVGDocument)impl.createDocument(svgNS, "svg", null);
        
        SVGGraphics2D g = new SVGGraphics2D(doc);

        Shape circle = new Ellipse2D.Double(0,0,50,50);
        g.setPaint(Color.red);
        g.fill(circle);
        g.translate(60,0);
        g.setPaint(Color.green);
        g.fill(circle);
        g.translate(60,0);
        g.setPaint(Color.blue);
        g.fill(circle);
        g.setSVGCanvasSize(new Dimension(180,50));

        Element root = doc.getDocumentElement();

        // The following populates the document root with the 
        // generated SVG content.
        g.getRoot(root);

        root.setAttribute("onload", "System.out.println('hello')");

        // Now that the SVG file has been loaded, build
        // a GVT Tree from it
        TestUserAgent userAgent = new TestUserAgent();
        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(userAgent);
        ctx.setDynamic(true);

        builder.build(ctx, doc);
        BaseScriptingEnvironment scriptEnvironment 
            = new BaseScriptingEnvironment(ctx);
        scriptEnvironment.loadScripts();
        scriptEnvironment.dispatchSVGLoadEvent();

        if (!userAgent.failed) {
            return reportSuccess();
        } else {
            return reportError("Got exception while processing document");
        }
    }

    class TestUserAgent extends UserAgentAdapter {
        boolean failed;

        public void displayError(Exception e) {
            failed = true;
        } 
    }
}
