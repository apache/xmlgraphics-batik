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
 * Test the ImageTranscoder with the KEY_ALTERNATE_STYLESHEET transcoding hint.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$ 
 */
public class AlternateStylesheetTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /** The alternate stylesheet to use. */
    protected String alternateStylesheet;

    /**
     * Constructs a new <tt>AlternateStylesheetTest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param alternateStylesheet the alternate stylesheet CSS media
     */
    public AlternateStylesheetTest(String inputURI, 
				   String refImageURI, 
				   String alternateStylesheet) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
	this.alternateStylesheet = alternateStylesheet;
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
	hints.put(ImageTranscoder.KEY_ALTERNATE_STYLESHEET, 
		  alternateStylesheet);
	return hints;
    }

    /**
     * Returns the reference image for this test.
     */
    protected BufferedImage getReferenceImage() {
	return createBufferedImage(resolveURL(refImageURI));
    }
}
