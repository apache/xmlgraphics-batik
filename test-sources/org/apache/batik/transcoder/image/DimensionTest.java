/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * Test the ImageTranscoder with the KEY_WIDTH and/or the KEY_HEIGHT transcoding
 * hint.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$ 
 */
public class DimensionTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /** The width of the image. */
    protected Float width;

    /** The height of the image. */
    protected Float height;

    /**
     * Constructs a new <tt>DimensionTest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param width the image width
     * @param height the image height
     */
    public DimensionTest(String inputURI, 
			 String refImageURI, 
			 Float width,
			 Float height) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
	this.width = width;
	this.height = height;
    }

    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
	return new TranscoderInput(resolveURL(inputURI).toString());
    }
    
    /**
     * Creates a Map that contains additional transcoding hints.
     */
    protected Map createTranscodingHints() {
	Map hints = new HashMap(7);
	if (width.floatValue() > 0) {
	    hints.put(ImageTranscoder.KEY_WIDTH, width);
	}
	if (height.floatValue() > 0) {
	    hints.put(ImageTranscoder.KEY_HEIGHT, height);
	}
	return hints;
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
	return createBufferedImageData(resolveURL(refImageURI));
    }
}
