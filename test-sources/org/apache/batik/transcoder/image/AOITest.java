/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * Test the ImageTranscoder with the KEY_AOI transcoding hint.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$ 
 */
public class AOITest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /** The area of interest. */
    protected Rectangle2D aoi;

    /** The width of the image. */
    protected Float imgWidth;

    /** The height of the image. */
    protected Float imgHeight;

    /**
     * Constructs a new <tt>AOITest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param x the x coordinate of the area of interest
     * @param y the y coordinate of the area of interest
     * @param width the width of the area of interest
     * @param height the height of the area of interest
     */
    public AOITest(String inputURI, 
		   String refImageURI, 
		   Float x,
		   Float y,
		   Float width,
		   Float height) {
	this(inputURI, 
	     refImageURI, 
	     x, 
	     y, 
	     width, 
	     height, 
	     new Float(-1), 
	     new Float(-1));
    }

    /**
     * Constructs a new <tt>AOITest</tt>.
     *
     * @param inputURI the URI of the input image
     * @param the URI of the reference image
     * @param x the x coordinate of the area of interest
     * @param y the y coordinate of the area of interest
     * @param width the width of the area of interest
     * @param height the height of the area of interest
     * @param imgWidth the width of the image to generate
     * @param imgHeight the height of the image to generate
     */
    public AOITest(String inputURI, 
		   String refImageURI, 
		   Float x,
		   Float y,
		   Float width,
		   Float height,
		   Float imgWidth,
		   Float imgHeight) {
	this.inputURI = inputURI;
	this.refImageURI = refImageURI;
	this.aoi = new Rectangle2D.Float(x.floatValue(),
					 y.floatValue(),
					 width.floatValue(),
					 height.floatValue());
	this.imgWidth = imgWidth;
	this.imgHeight = imgHeight;
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
	Map hints = new HashMap(11);
	hints.put(ImageTranscoder.KEY_AOI, aoi);
	if (imgWidth.floatValue() > 0) {
	    hints.put(ImageTranscoder.KEY_WIDTH, imgWidth);
	}
	if (imgHeight.floatValue() > 0) {
	    hints.put(ImageTranscoder.KEY_HEIGHT, imgHeight);
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
