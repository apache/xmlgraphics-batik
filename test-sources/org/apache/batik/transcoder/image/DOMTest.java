/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 

import java.io.ByteArrayOutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;

import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;

/**
 * Test the ImageTranscoder input with a DOM tree.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DOMTest extends AbstractImageTranscoderTest {

    /**
     * Constructs a new <tt>DOMTest</tt>.
     */
    public DOMTest() {
    }

    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	Document doc = impl.createDocument(svgNS, "svg", null);

	Element root = doc.getDocumentElement();

	root.setAttributeNS(null, "width", "400");
	root.setAttributeNS(null, "height", "400");

	Element r = doc.createElementNS(svgNS, "rect");
	r.setAttributeNS(null, "x", "0");
	r.setAttributeNS(null, "y", "0");
	r.setAttributeNS(null, "width", "400");
	r.setAttributeNS(null, "height", "400");
	r.setAttributeNS(null, "style", "fill:black");
	root.appendChild(r);

	r = doc.createElementNS(svgNS, "rect");
	r.setAttributeNS(null, "x", "100");
	r.setAttributeNS(null, "y", "50");
	r.setAttributeNS(null, "width", "100");
	r.setAttributeNS(null, "height", "50");
	r.setAttributeNS(null, "style", "stroke:red; fill:none");
	root.appendChild(r);

	return new TranscoderInput(doc);
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
        try {
            BufferedImage img = new BufferedImage
                (400, 400, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, 400, 400);
            g2d.setColor(Color.red);
            g2d.drawRect(100, 50, 100, 50);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            PNGTranscoder t = new PNGTranscoder();
            TranscoderOutput output = new TranscoderOutput(ostream);
            t.writeImage(img, output);
            return ostream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("DOMTest error");
        }
    }
}
