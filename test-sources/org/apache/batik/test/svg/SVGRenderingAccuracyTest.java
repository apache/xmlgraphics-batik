/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.net.URL;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestReport;


/**
 * Checks for regressions in rendering a specific SVG document.
 * The <tt>Test</tt> will rasterize and SVG document and 
 * compare it to a reference image. The test passes if the 
 * rasterized SVG and the reference image match exactly (i.e.,
 * all pixel values are the same).
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRenderingAccuracyTest implements Test{
    /**
     * Error when temp file cannot be created
     * {0} = IOException message
     */
    public static final String ERROR_CANNOT_CREATE_TEMP_FILE        
        = "SVGRenderingAccuracyTest.error.cannot.create.temp.file";

    /**
     * Error when temp file stream cannot be created
     * {0} = temp file's cannonical path
     * {1} = IOException message
     */
    public static final String ERROR_CANNOT_CREATE_TEMP_FILE_STREAM 
        = "SVGRenderingAccuracyTest.error.cannot.create.temp.file.stream";

    /**
     * Error when transcoding the SVG document generates an error
     * {0} = URI of the transcoded SVG file
     * {1} = TranscoderException message
     */
    public static final String ERROR_CANNOT_TRANSCODE_SVG           
        = "SVGRenderingAccuracyTest.error.cannot.transcode.svg";

    /**
     * Error when the reference image cannot be opened
     * {0} = URI of the reference image
     * {1} = IOException message
     */
    public static final String ERROR_CANNOT_OPEN_REFERENCE_IMAGE    
        = "SVGRenderingAccuracyTest.error.cannot.open.reference.image";

    /**
     * Error when the generated image cannot be read
     * {0} = Cannonical path of the temp generated image
     * {1} = IOException message
     */
    public static final String ERROR_CANNOT_OPEN_GENERATED_IMAGE    
        = "SVGRenderingAccuracyTest.error.cannot.open.genereted.image";

    /**
     * Error when there is an IOException while comparing the 
     * two reference raster image with the new raster image built
     * from the SVG.
     * {0} = URI of the reference image
     * {1} = Connical path for the temp generated image
     * {2} = IOException message.
     */
    public static final String ERROR_ERROR_WHILE_COMPARING_FILES    
        = "SVGRenderingAccuracyTest.error.while.comparing.files";

    /**
     * Error when the generated image from the SVG file differs from
     * the reference image.
     */
    public static final String ERROR_SVG_RENDERING_NOT_ACCURATE     
        = "SVGRenderingAccuracyTest.error.svg.rendering.not.accurate";

    /**
     * Entry describing the error
     */
    public static final String ENTRY_KEY_ERROR_DESCRIPTION 
        = "SVGRenderingAccuracyTest.entry.key.error.description";

    /**
     * Entry describing the reference image URL
     */
    public static final String ENTRY_KEY_REFERENCE_IMAGE_URI
        = "SVGRenderingAccuracyTest.entry.key.reference.image.uri";

    /**
     * Entry describing the generated image 
     */
    public static final String ENTRY_KEY_TEMPORARY_FILE
        = "SVGRenderingAccuracyTest.entry.key.temporary.file";

    /**
     * The gui resources file name
     */
    public final static String CONFIGURATION_RESOURCES =
        "org.apache.batik.test.svg.resources.Configuration";

    /**
     * The configuration resource bundle
     */
    protected static ResourceBundle configuration;
    static {
        configuration = ResourceBundle.getBundle(CONFIGURATION_RESOURCES, 
                                                 Locale.getDefault());
    }

    /**
     * Prefix for the temporary files created by Tests
     * of this class
     */
    public static final String TEMP_FILE_PREFIX 
        = configuration.getString("temp.file.prefix");

    /**
     * Suffix for the temporary files created by 
     * Tests of this class
     */
    public static final String TEMP_FILE_SUFFIX
        = configuration.getString("temp.file.suffix");

    /**
     * Parser class name
     */
    public static final String PARSER_CLASS_NAME 
        = configuration.getString("org.xml.sax.driver");

    /**
     * The URL where the SVG can be found.
     */
    protected URL svgURL;

    /**
     * The URL for the reference image
     */
    protected URL refImgURL;

    /**
     * Constructor.
     * @param svgURL the URL for the SVG document being tested.
     * @param refImgURL the URL for the reference image.
     */
    public SVGRenderingAccuracyTest(URL svgURL,
                                    URL refImgURL){
        if(svgURL == null){
            throw new IllegalArgumentException();
        }

        if(refImgURL == null){
            throw new IllegalArgumentException();
        }

        this.svgURL = svgURL;
        this.refImgURL = refImgURL;
    }

    /**
     * Returns this <tt>Test</tt>'s name. The name is the 
     * URL of the SVG being rendered.
     */
    public String getName(){
        return svgURL.toString();
    }

    /**
     * Requests this <tt>Test</tt> to run and produce a 
     * report.
     *
     */
    public TestReport run(){
        DefaultTestReport report 
            = new DefaultTestReport(this);

        //
        // Render the SVG image into a raster. We use the 
        // ImageTranscoder to convert the SVG into a raster in
        // a temporary file.
        //
        File tmpFile = null;

        try{
            tmpFile = File.createTempFile(TEMP_FILE_PREFIX,
                                          TEMP_FILE_SUFFIX,
                                          null);
            tmpFile.deleteOnExit();
        }catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_CREATE_TEMP_FILE);
            report.setDescription(new TestReport.Entry[] { 
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_CREATE_TEMP_FILE, 
                                                 new Object[]{e.getMessage()}))
                    });
            report.setPassed(false);
            return report;
        }

        FileOutputStream tmpFileOS = null;

        try{
            tmpFileOS = new FileOutputStream(tmpFile);
        }catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_CREATE_TEMP_FILE_STREAM);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_CREATE_TEMP_FILE_STREAM,
                                                 new String[]{tmpFile.getAbsolutePath(),
                                                              e.getMessage()})) });
            report.setPassed(false);
            return report;
        }

        ImageTranscoder transcoder = getImageTranscoder();
        TranscoderInput src = new TranscoderInput(svgURL.toExternalForm());
        TranscoderOutput dst = new TranscoderOutput(tmpFileOS);
        
        try{
            transcoder.transcode(src, dst);
        }catch(TranscoderException e){
            report.setErrorCode(ERROR_CANNOT_TRANSCODE_SVG);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_TRANSCODE_SVG,
                                                 new String[]{svgURL.toExternalForm(), 
                                                              e.getException().getMessage()})) });
            report.setPassed(false);
            return report;
        }

        //
        // Do a binary comparison of the encoded images.
        //
        InputStream refStream = null;
        InputStream newStream = null;
        try {
            refStream =
                new BufferedInputStream(refImgURL.openStream());
        }catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_OPEN_REFERENCE_IMAGE);
            report.setDescription( new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_OPEN_REFERENCE_IMAGE,
                                                 new Object[]{refImgURL.toExternalForm(), 
                                                              e.getMessage()})) });
            report.setPassed(false);
            tmpFile.delete();
            return report;
        }

        try{
            newStream =
                new BufferedInputStream(new FileInputStream(tmpFile));
        }catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_OPEN_GENERATED_IMAGE);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_OPEN_GENERATED_IMAGE,
                                                 new Object[]{tmpFile.getAbsolutePath(), 
                                                              e.getMessage()}))});
            report.setPassed(false);
            tmpFile.delete();
            return report;
        }


        boolean accurate = false;
        try{
            int b, nb;
            do {
                b = refStream.read();
                nb = newStream.read();
            } while (b != -1 && nb != -1 && b == nb);
            refStream.close();
            newStream.close();
            accurate = (b == nb);
        } catch(IOException e) {
            report.setErrorCode(ERROR_ERROR_WHILE_COMPARING_FILES);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_ERROR_WHILE_COMPARING_FILES,
                                                 new Object[]{refImgURL.toExternalForm(), 
                                                              tmpFile.getAbsolutePath(),
                                                              e.getMessage()}))});
            report.setPassed(false);
            return report;
        }

        //
        // If the files differ, return an error
        //
        if(!accurate){
            report.setErrorCode(ERROR_SVG_RENDERING_NOT_ACCURATE);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                                     Messages.formatMessage(ERROR_SVG_RENDERING_NOT_ACCURATE, null)),
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_REFERENCE_IMAGE_URI, null),
                                     refImgURL),
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_TEMPORARY_FILE, null),
                                     tmpFile) });
            report.setPassed(false);
            return report;
        }


        //
        // Yahooooooo! everything worked out well.
        //
        report.setPassed(true);
        return report;
    }

    /**
     * Returns the <tt>ImageTranscoder</tt> the Test should
     * use
     */
    public static ImageTranscoder getImageTranscoder(){
        ImageTranscoder t = new PNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                             PARSER_CLASS_NAME);
        return t;
    }
}
