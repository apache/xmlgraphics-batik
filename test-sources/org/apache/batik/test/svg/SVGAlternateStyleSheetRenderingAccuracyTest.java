/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import org.apache.batik.dom.svg.SVGOMDocument;

import org.w3c.dom.Document;

/**
 * Checks for regressions in rendering of a document with a given
 * alternate stylesheet label.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGAlternateStyleSheetRenderingAccuracyTest
    extends SVGRenderingAccuracyTest {
    
    /**
     * The stylesheet to use for rendering.
     */
    protected String title;

    /**
     * Constructor.
     * @param svgURL the URL String for the SVG document being tested.
     * @param refImgURL the URL for the reference image.
     * @param t The stylesheet title to use.
     */
    public SVGAlternateStyleSheetRenderingAccuracyTest(String svgURL,
                                                       String refImgURL,
                                                       String t) {
        super(svgURL, refImgURL);
        title = t;
    }

    /**
     * Template method which subclasses can override if they
     * need to manipulate the DOM in some way before running 
     * the accuracy test. For example, this can be useful to 
     * test the alternate stylesheet support.
     */
    protected Document manipulateSVGDocument(Document doc) {
        // enable the stylesheet
        ((SVGOMDocument)doc).enableAlternateStyleSheet(title);
        return doc;
    }
}
