/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.Rectangle2D;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

/**
 * Checks that no NullPointerException is thrown by default
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DoubleString extends AbstractTest {
    public TestReport runImpl() throws Exception {
        // Get a DOMImplementation
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        
        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);
        
        SVGGraphics2D g = new SVGGraphics2D(document);
        Rectangle2D r = new Rectangle2D.Float(0.5f, 0.5f, 2.33f, 2.33f);
        g.fill(r);

        return reportSuccess();
    }
}
