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
 * Test the ImageTranscoder with the KEY_DEFAULT_FONT_FAMILY transcoding hint.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$ 
 */
public class DefaultFontFamilyTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /** The default font-family to use. */
    protected String defaultFontFamily;

    /**
     * Constructs a new <tt>DefaultFontFamilyTest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param defaultFontFamily the default font-family to use
     */
    public DefaultFontFamilyTest(String inputURI, 
                                 String refImageURI, 
                                 String defaultFontFamily) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
	this.defaultFontFamily = defaultFontFamily;
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
	hints.put(ImageTranscoder.KEY_DEFAULT_FONT_FAMILY, defaultFontFamily);
	return hints;
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
	return createBufferedImageData(resolveURL(refImageURI));
    }
}
