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

import java.io.IOException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;

import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;

/**
 * Test the ImageTranscoder input with a DOM tree.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ParametrizedDOMTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /**
     * Constructs a new <tt>ParametrizedDOMTest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     */
    public ParametrizedDOMTest(String inputURI, String refImageURI) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
    }

    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
	try {
	    String parser = XMLResourceDescriptor.getXMLParserClassName();
	    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
	    Document doc = f.createDocument(resolveURL(inputURI).toString());
	    return new TranscoderInput(doc);
	} catch (IOException ex) {
            throw new IllegalArgumentException(inputURI);
	}
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
	return createBufferedImageData(resolveURL(refImageURI));
    }
}
