/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import org.apache.batik.test.svg.JSVGRenderingAccuracyTest;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

/**
 * Test setDocument on JSVGComponent with non-Batik SVGOMDocument.
 *
 * This test constructs a generic Document with SVG content then it
 * ensures that when this is passed to JSVGComponet.setDocument it is
 * properly imported to an SVGOMDocument and rendered from there.
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class SetSVGDocumentTest extends JSVGRenderingAccuracyTest {
    public SetSVGDocumentTest() {
    }
    protected String[] breakSVGFile(String svgFile){
        if(svgFile == null) {
            throw new IllegalArgumentException(svgFile);
        }

        String [] ret = new String[3];
        ret[0] = "test-resources/org/apache/batik/test/svg/";
        ret[1] = "SetSVGDocumentTest";
        ret[2] = ".svg";
        return ret;
    }

    /* JSVGCanvasHandler.Delegate Interface */
    public boolean canvasInit(JSVGCanvas canvas) {
        DOMImplementation impl = 
            GenericDOMImplementation.getDOMImplementation();
        Document doc = impl.createDocument(SVGConstants.SVG_NAMESPACE_URI, 
                                           SVGConstants.SVG_SVG_TAG, null);
        Element e = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI, 
                                        SVGConstants.SVG_RECT_TAG);
        e.setAttribute("x", "10");
        e.setAttribute("y", "10");
        e.setAttribute("width", "100");
        e.setAttribute("height", "50");
        e.setAttribute("fill", "crimson");
        doc.getDocumentElement().appendChild(e);

        e = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI, 
                                SVGConstants.SVG_CIRCLE_TAG);
        e.setAttribute("cx", "55");
        e.setAttribute("cy", "35");
        e.setAttribute("r", "30");
        e.setAttribute("fill", "gold");
        doc.getDocumentElement().appendChild(e);
        
        canvas.setDocument(doc);
        return false; // We didn't trigger a load event.
    }

    public boolean canvasUpdated(JSVGCanvas canvas) {
        return true;
    }
};
