/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;

import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.renderable.Filter;

import org.apache.batik.util.ParsedURL;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

/**
 * The base class for the ImageTranscoder tests.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractImageTranscoderTest extends AbstractTest {

    /**
     * Error when the reference image and the generated image is different.
     */
    public static final String ERROR_IMAGE_DIFFER =
	"AbstractImageTranscoderTest.error.image.differ";

    /**
     * Error when an exception occured while transcoding.
     */
    public static final String ERROR_TRANSCODING =
	"AbstractImageTranscoderTest.error.transcoder.exception";

    /**
     * Constructs a new <tt>AbstractImageTranscoderTest</tt>.
     */
    public AbstractImageTranscoderTest() {
    }

    /**
     * Runs this test. This method will only throw exceptions if some aspect of
     * the test's internal operation fails.
     */
    public TestReport runImpl() throws Exception {
	DefaultTestReport report = new DefaultTestReport(this);

	try {
	    DiffImageTranscoder transcoder = 
		new DiffImageTranscoder(getReferenceImage());

	    Map hints = createTranscodingHints();
	    if (hints != null) {
		transcoder.setTranscodingHints(hints);
	    }

	    TranscoderInput input = createTranscoderInput();
	    transcoder.transcode(input, null);
	    
	    if (!transcoder.isIdentical()) {
		report.setErrorCode(ERROR_IMAGE_DIFFER);
		report.addDescriptionEntry(ERROR_IMAGE_DIFFER, "");
		report.setPassed(false);
	    }
	} catch (TranscoderException ex) {
	    report.setErrorCode(ERROR_TRANSCODING);
	    report.addDescriptionEntry(ERROR_TRANSCODING, toString(ex));
            ex.printStackTrace();
	    report.setPassed(false);
	}
	
	return report;
    }

    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected abstract TranscoderInput createTranscoderInput();

    /**
     * Creates a Map that contains additional transcoding hints.
     */
    protected Map createTranscodingHints() {
	return null;
    }

    /**
     * Returns the reference image for this test.
     */
    protected abstract BufferedImage getReferenceImage();

    //////////////////////////////////////////////////////////////////////////
    // Convenient methods
    //////////////////////////////////////////////////////////////////////////

    /**
     * Gives the specified exception as a string.
     */
    public static String toString(Exception ex) {
	StringWriter trace = new StringWriter();
	ex.printStackTrace(new PrintWriter(trace));
	return trace.toString();
    }

    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    public static URL resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if (f.exists()) {
            try {
                return f.toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(url);
            }
        }
        // url is not a file. It must be a regular URL...
        try {
            return new URL(url);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException(url);
        }
    }

    /**
     * Loads an image from a URL
     */
    public static BufferedImage createBufferedImage(URL url) {
        ImageTagRegistry reg = ImageTagRegistry.getRegistry();
        Filter filt = reg.readURL(new ParsedURL(url));
        if (filt == null) {
            return null;
        }
	
        RenderedImage red = filt.createDefaultRendering();
        if (red == null) {
            return null;
        }
        
        BufferedImage img = new BufferedImage(red.getWidth(),
                                              red.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        red.copyData(img.getRaster());
        return img;
    }

    /**
     * A custom ImageTranscoder for testing.
     */
    protected static class DiffImageTranscoder extends ImageTranscoder {

	/** The result of the image comparaison. */
	protected boolean state;

	/** The reference image. */
	protected BufferedImage refImg;

	/**
	 * Constructs a new <tt>DiffImageTranscoder</tt>.
	 *
	 * @param refImg the reference image
	 * @param report the test report into which errors have been sent
	 */
	public DiffImageTranscoder(BufferedImage refImg) {
	    this.refImg = refImg;
	}

	/**
	 * Creates a new image with the specified dimension.
	 * @param w the image width in pixels
	 * @param h the image height in pixels
	 */
	public BufferedImage createImage(int w, int h) {
	    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Compares the specified image with the reference image and set the
	 * state flag.
	 *
	 * @param img the image to write
	 * @param output the output (ignored)
	 * @param TranscoderException if an error occured while storing the
	 * image 
	 */
	public void writeImage(BufferedImage img, TranscoderOutput output)
	    throws TranscoderException {

	    compareImage(img);
	}

	/**
	 * Compares both source and result images and set the state flag.
	 */
	protected void compareImage(BufferedImage img) {
	    // compare the resulting image with the reference image
	    // state = true if refImg is the same than img

	    if ((img.getType() != BufferedImage.TYPE_INT_ARGB) ||
		(refImg.getType() != BufferedImage.TYPE_INT_ARGB)) {
		throw new IllegalArgumentException("Different Image type");
	    }
	    int minX = refImg.getMinX();
	    int minY = refImg.getMinY();
	    int w = refImg.getWidth();
	    int h = refImg.getHeight();
	    if ((img.getMinX() != minX) ||
		(img.getMinY() != minY) ||
		(img.getWidth() != w) ||
		(img.getHeight() != h)) {
		showDiff(img);
		state = false;
		return;
	    }

	    for (int y = minY; y < minY+h; ++y) {
		for (int x = minX; x < minX+w; ++x) {
		    if (img.getRGB(x, y) != refImg.getRGB(x, y)) {

			showDiff(img);

			state = false;
			return;
		    }
		}
	    }
	    state = true;
	}

	private void showDiff(BufferedImage img) {
	    javax.swing.JFrame frame = new javax.swing.JFrame();

	    frame.getContentPane().add
		(new javax.swing.JLabel(new javax.swing.ImageIcon(img)),
		 java.awt.BorderLayout.EAST);
	    frame.getContentPane().add
		(new javax.swing.JLabel(new javax.swing.ImageIcon(refImg)),
		 java.awt.BorderLayout.WEST);
	    
	    frame.pack();
	    frame.show();
	}

	/**
	 * Returns true if the reference image is the same than the generated
	 * image, false otherwise.  
	 */
	public boolean isIdentical() {
	    return state;
	}
    }
}
