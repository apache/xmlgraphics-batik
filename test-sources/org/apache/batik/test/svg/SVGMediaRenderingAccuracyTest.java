/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import org.apache.batik.css.AbstractViewCSS;

import org.w3c.dom.Document;

import org.w3c.dom.views.DocumentView;

/**
 * Checks for regressions in rendering of a document with a given
 * media.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGMediaRenderingAccuracyTest extends SVGRenderingAccuracyTest {
    
    /**
     * The media to use for rendering.
     */
    protected String media;

    /**
     * Constructor.
     * @param svgURL the URL String for the SVG document being tested.
     * @param refImgURL the URL for the reference image.
     * @param m The media to use.
     */
    public SVGMediaRenderingAccuracyTest(String svgURL,
                                         String refImgURL,
                                         String m) {
        super(svgURL, refImgURL);
        media = m;
    }

    /**
     * Template method which subclasses can override if they
     * need to manipulate the DOM in some way before running 
     * the accuracy test. For example, this can be useful to 
     * test the alternate stylesheet support.
     */
    protected Document manipulateSVGDocument(Document doc) {
        // set the media type
        AbstractViewCSS view;
        view = (AbstractViewCSS)((DocumentView)document).getDefaultView();
        view.setMedia(media);
        
        return doc;
    }
}
