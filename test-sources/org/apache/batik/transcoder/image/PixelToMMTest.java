/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * Test the ImageTranscoder with the KEY_PIXEL_TO_MM transcoding hint.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$ 
 */
public class PixelToMMTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /** The pixel to mm factor. */
    protected Float px2mm;

    /**
     * Constructs a new <tt>PixelToMMTest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param px2mm the pixel to mm conversion factor
     */
    public PixelToMMTest(String inputURI, 
			 String refImageURI, 
			 Float px2mm) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
	this.px2mm = px2mm;
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
	Map hints = new HashMap(3);
	hints.put(ImageTranscoder.KEY_PIXEL_TO_MM, px2mm);
	return hints;
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
	return createBufferedImageData(resolveURL(refImageURI));
    }
}
