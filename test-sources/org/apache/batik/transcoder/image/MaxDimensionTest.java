/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.transcoder.image;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.transcoder.TranscoderInput;

/**
 * Test the ImageTranscoder with the KEY_MAX_WIDTH and/or the KEY_MAX_HEIGHT 
 * transcoding hint.
 *
 * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
 * @version $Id$ 
 */
public class MaxDimensionTest extends AbstractImageTranscoderTest {

    //-- Variables -----------------------------------------------------------
    /** The URI of the input image. */
    protected String inputURI;
    /** The URI of the reference image. */
    protected String refImageURI;
    /** The maximum width of the image. */
    protected Float maxWidth = new Float(Float.NaN);
    /** The maximum height of the image. */
    protected Float maxHeight = new Float(Float.NaN);
    /** The width of the image. */
    protected Float width = new Float(Float.NaN);
    /** The height of the image. */
    protected Float height = new Float(Float.NaN);


    //-- Constructors --------------------------------------------------------
    /**
     * Constructs a new <tt>MaxDimensionTest</tt>.
     *
     * @param inputURI URI of the input image.
     * @param refImageURI URI of the reference image.
     * @param maxWidth Maximum image width (KEY_MAX_WIDTH value).
     * @param maxHeight Maximum image height (KEY_MAX_HEIGHT value).
     */
    public MaxDimensionTest(String inputURI, String refImageURI, Float maxWidth, Float maxHeight) {
        this.inputURI = inputURI;
        this.refImageURI = refImageURI;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    /**
     * Constructs a new <tt>MaxDimensionTest</tt>.
     *
     * @param inputURI URI of the input image.
     * @param refImageURI URI of the reference image.
     * @param maxWidth Maximum image width (KEY_MAX_WIDTH value).
     * @param maxHeight Maximum image height (KEY_MAX_HEIGHT value).
     * @param width Image width (KEY_WIDTH value).
     * @param height Image height (KEY_HEIGH value).
     */
    public MaxDimensionTest(String inputURI, String refImageURI, Float maxWidth, Float maxHeight, Float width, Float height) {
        this.inputURI = inputURI;
        this.refImageURI = refImageURI;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.width = width;
        this.height = height;
    }


    //-- Methods -------------------------------------------------------------
    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
        return new TranscoderInput(resolveURL(inputURI).toString());
    }
    
    /**
     * Creates a Map that contains additional transcoding hints.
     *
     * @return Transcoding hint values.
     */
    protected Map createTranscodingHints() {
        Map hints = new HashMap(7);
        if (!width.isNaN() && width.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_WIDTH, width);
        }
        if (!height.isNaN() && height.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_HEIGHT, height);
        }
        if (maxWidth.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_MAX_WIDTH, maxWidth);
        }
        if (maxHeight.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_MAX_HEIGHT, maxHeight);
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

