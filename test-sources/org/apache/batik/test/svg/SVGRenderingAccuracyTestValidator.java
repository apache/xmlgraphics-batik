/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import org.apache.batik.test.Test;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.TestReportValidator;

/**
 * Validates the operation of <tt>SVGRenderingAccuracyTest</tt>
 * by forcing specific test case situations and checking that
 * they are handled properly by the class.
 * 
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRenderingAccuracyTestValidator extends DefaultTestSuite {
    /**
     * Simple valid SVG content used for this test
     */
    private static final String validSVG 
        = "<svg width=\"450\" height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * Simple valid SVG content used for this test, small size
     */
    private static final String validSmallSVG 
        = "<svg width=\"45\" height=\"50\" viewBox=\"0 0 45 50\"> \n" +
        "    <rect x=\"2.5\" y=\"2.5\" width=\"40\" height=\"45\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * Simple valid SVG content used for this test, red rectangle
     */
    private static final String validRedSVG 
        = "<svg width=\"450\" height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"red\" /> \n" +
        "</svg>\n";

    /**
     * Simple invalid SVG content used for this test
     * (the error is that a double quote is missing at the 
     * end of the width attribute value.
     */
    private static final String invalidSVG 
        = "<svg width=\"450 height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * This test creates a sample SVG file dynamically and
     * valides that errors are generated for the
     * following cases:
     * 
     * + invalid SVG URL
     * + corrupted SVG image (i.e., cannot be transcoded to
     *   an image).
     * + invalid reference image URL
     * + valid reference image URL, but inexisting resource.
     * + reference image of different size than generated
     *   image.
     * + reference image different than the generated image
     * 
     * This test finally validates that the test
     * passes if the reference image corresponds to the
     * newly generated image.
     */
    public SVGRenderingAccuracyTestValidator(){
        addTest(new InvalidSVGURL());
        addTest(new InvalidSVGContent());
        addTest(new InvalidReferenceImageURL());
        addTest(new InexistingReferenceImage());
        addTest(new DifferentSizes());
        addTest(new SameSizeDifferentContent());
        addTest(new AccurateRendering());
    }
    
    /**
     * Creates a SVG source URL for the given svg content
     */
    public static URL createSVGSourceURL(String svgContent) throws Throwable{
        File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                           null);
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(svgContent);
        writer.close();
        return tmpFile.toURL();
    }

    /**
     * Helper method: creates a valid reference image
     */
    public static URL createValidReferenceImage(String svgContent) throws Throwable{
        TranscoderInput validSrc = new TranscoderInput(new StringReader(svgContent));
        
        File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                           SVGRenderingAccuracyTest.TEMP_FILE_SUFFIX);
        
        TranscoderOutput validDst 
            = new TranscoderOutput(new FileOutputStream(tmpFile));
        
        ImageTranscoder transcoder 
            = SVGRenderingAccuracyTest.getImageTranscoder();
        
        transcoder.transcode(validSrc, validDst);
        
        tmpFile.deleteOnExit();
        
        return tmpFile.toURL();
    }



    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * invalid URL for the source SVG. Checks that this 
     * error is reported as a failure.
     */
    static class InvalidSVGURL extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create an invalid URL for the SVG file
            URL invalidSVGURL = new URL("http",
                                        "dummyHost",
                                        "dummyFile.svg");

            // Create a valid reference image
            URL refImgURL = createValidReferenceImage(validSVG);

            Test t = new SVGRenderingAccuracyTest(invalidSVGURL,
                                                  refImgURL);

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_TRANSCODE_SVG);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with a
     * valid URL pointing to an invalid SVG document. Checks that this 
     * error is reported as a failure.
     */
    static class InvalidSVGContent extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create an SVG URL from invalid SVG content.
            URL validSVGURL = createSVGSourceURL(invalidSVG);
            
            // Create a valid reference image
            URL refImgURL = createValidReferenceImage(validSVG);

            Test t = new SVGRenderingAccuracyTest(validSVGURL,
                                                  refImgURL);
            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_TRANSCODE_SVG);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * valid URL for the source SVG but with an invalid 
     * URL for the reference image.
     */
    static class InvalidReferenceImageURL extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an invalid URL for the reference image.
            URL invalidReferenceImageURL = null;
            
            invalidReferenceImageURL = new URL("http",
                                               "dummyHost",
                                               "dummyFile.png");
            Test t = new SVGRenderingAccuracyTest(validSVGURL,
                                                  invalidReferenceImageURL);

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_IMAGE);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * valid URL for the source SVG valid 
     * URL for the reference image, but the reference image,
     * but the reference image does not exist
     */
    static class InexistingReferenceImage extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            // We use the createSVGSourceURL method to create
            // a File that the ImageLoader is not able to load.
            File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                               null);
            URL refImgURL = tmpFile.toURL();
            tmpFile.delete();
            
            Test t = new SVGRenderingAccuracyTest(validSVGURL,
                                                  refImgURL);

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_IMAGE);

            return super.runImpl();
        }
    }

    static class DifferentSizes extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            //
            // Create a valid SVG URL from valid SVG content.
            //
            URL validSVGURL = createSVGSourceURL(validSVG);

            //
            // Create an valid URL for the reference image.
            //
            URL validRefImageURL = createSVGSourceURL(validSmallSVG);
            
            //
            // Run test and check report
            //
            Test t = new SVGRenderingAccuracyTest(validSVGURL,
                                                  validRefImageURL);

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_SVG_RENDERING_NOT_ACCURATE);                      

            return super.runImpl();
        }
    }

    static class SameSizeDifferentContent extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            URL validRefImageURL = createSVGSourceURL(validRedSVG);
            

            Test t = new SVGRenderingAccuracyTest(validSVGURL,
                                                  validRefImageURL);

            setConfig(t,
                      false, 
                      SVGRenderingAccuracyTest.ERROR_SVG_RENDERING_NOT_ACCURATE);

            return super.runImpl();
        }
    }

    static class AccurateRendering extends TestReportValidator {
        public TestReport runImpl() throws Throwable {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            URL validRefImageURL = createValidReferenceImage(validSVG);

            setConfig(new SVGRenderingAccuracyTest(validSVGURL,
                                                   validRefImageURL),
                      true,
                      null);

            return super.runImpl();
        }
    }

}
