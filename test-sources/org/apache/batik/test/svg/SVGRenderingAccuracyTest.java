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
import java.io.StringWriter;
import java.io.PrintWriter;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.rendered.CompositeRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;

import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.renderable.Filter;

import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGEncodeParam;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.batik.util.ParsedURL;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestReport;

import org.w3c.dom.Document;


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
public class SVGRenderingAccuracyTest extends AbstractTest {
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
     * {1} = Exception class
     * {2} = Exception message
     * {3} = Stack trace.
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
     * Entry describing the reference/generated image file
     */
    public static final String ENTRY_KEY_REFERENCE_GENERATED_IMAGE_URI
        = "SVGRenderingAccuracyTest.entry.key.reference.generated.image.file";

    /**
     * Entry describing the generated difference image
     */
    public static final String ENTRY_KEY_DIFFERENCE_IMAGE
        = "SVGRenderingAccuracyTest.entry.key.difference.image";

    /**
     * Entry describing that an internal error occured while
     * generating the test failure description
     */
    public static final String ENTRY_KEY_INTERNAL_ERROR
        = "SVGRenderingAccuracyTest.entry.key.internal.error";

    /**
     * Messages expressing that comparison images could not be
     * created:
     * {0} : exception class
     * {1} : exception message
     * {2} : exception stack trace.
     */
    public static final String COULD_NOT_GENERATE_COMPARISON_IMAGES
        = "SVGRenderingAccuracyTest.message.error.could.not.generate.comparison.images";

    /**
     * Messages expressing that an image could not be loaded.
     * {0} : URL for the reference image.
     */
    public static final String COULD_NOT_LOAD_IMAGE
        = "SVGRenderingAccuracyTest.message.error.could.not.load.image";

    /**
     * Message expressing that the variation URL could not be open
     * {0} : URL 
     */
    public static final String COULD_NOT_OPEN_VARIATION_URL 
        = "SVGRenderingAccuracyTest.message.warning.could.not.open.variation.url";

    /**
     * The gui resources file name
     */
    public final static String CONFIGURATION_RESOURCES =
        "org.apache.batik.test.svg.resources.Configuration";

    /**
     * Suffix used for comparison images
     */
    public final static String IMAGE_TYPE_COMPARISON = "_cmp";

    /**
     * Suffix used for diff images
     */
    public final static String IMAGE_TYPE_DIFF = "_diff";

    /**
     * Suffix used for saved images (e.g., comparison and diff images)
     */
    public final static String IMAGE_FILE_EXTENSION = ".png";

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
     * The URL of a file containing an 'accepted' 
     * variation from the reference image.
     */
    protected URL variationURL;

    /**
     * The File where the newly computed variation 
     * should be saved if different from the 
     * variationURL
     */
    protected File saveVariation;

    /**
     * Temporary directory
     */
    protected static File tempDirectory;

    /**
     * Returns the temporary directory
     */
    public static File getTempDirectory(){
        if(tempDirectory == null){
            String tmpDir = System.getProperty("java.io.tmpdir");
            if(tmpDir == null){
                throw new Error();
            }

            tempDirectory = new File(tmpDir);
            if(!tempDirectory.exists()){
                throw new Error();
            }
        }
        return tempDirectory;
    }

    /**
     * Constructor.
     * @param svgURL the URL String for the SVG document being tested.
     * @param refImgURL the URL for the reference image.
     */
    public SVGRenderingAccuracyTest(String svgURL,
                                    String refImgURL){
        if(svgURL == null){
            throw new IllegalArgumentException();
        }

        if(refImgURL == null){
            throw new IllegalArgumentException();
        }

        this.svgURL = resolveURL(svgURL);
        this.refImgURL = resolveURL(refImgURL);
    }
    
    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    protected URL resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if(f.getParentFile().exists()){
            try{
                return f.toURL();
            }catch(MalformedURLException e){
                throw new IllegalArgumentException();
            }
        }
        
        // url is not a file. It must be a regular URL...
        try{
            return new URL(url);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(url);
        }
    }

    /**
     * Sets the File where the variation from the reference image should be
     * stored
     */
    public void setSaveVariation(File saveVariation){
        this.saveVariation = saveVariation;
    }

    public File getSaveVariation(){
        return saveVariation;
    }

    public String getVariationURL(){
        return variationURL.toString();
    }

    /**
     * Sets the URL where an acceptable variation fron the reference 
     * image can be found.
     */
    public void setVariationURL(String variationURL){
        this.variationURL = resolveURL(variationURL);
    }

    /**
     * Returns this <tt>Test</tt>'s name. The name is the 
     * URL of the SVG being rendered.
     */
    public String getName(){
        return svgURL.toString();
    }

    /**
     * Template method which subclasses can override if they
     * need to manipulate the DOM in some way before running 
     * the accuracy test. For example, this can be useful to 
     * test the alternate stylesheet support.
     */
    protected Document manipulateSVGDocument(Document doc) {
        return doc;
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

        ImageTranscoder transcoder = getTestImageTranscoder();
        TranscoderInput src = new TranscoderInput(svgURL.toString());
        TranscoderOutput dst = new TranscoderOutput(tmpFileOS);
        
        try{
            transcoder.transcode(src, dst);
        }catch(TranscoderException e){
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
                
            report.setErrorCode(ERROR_CANNOT_TRANSCODE_SVG);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_TRANSCODE_SVG,
                                                 new String[]{svgURL.toString(), 
                                                              e.getClass().getName(),
                                                              e.getMessage(),
                                                              trace.toString()
                                                 })) });
            report.setPassed(false);
            return report;
        }catch(Exception e){
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));

            report.setErrorCode(ERROR_CANNOT_TRANSCODE_SVG);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_CANNOT_TRANSCODE_SVG,
                                                 new String[]{svgURL.toString(), 
                                                              e.getClass().getName(),
                                                              e.getMessage(),
                                                              trace.toString()
                                                 })) });
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
                                                 new Object[]{refImgURL.toString(), 
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
            accurate = compare(refStream, newStream);
        } catch(IOException e) {
            report.setErrorCode(ERROR_ERROR_WHILE_COMPARING_FILES);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                          Messages.formatMessage(ERROR_ERROR_WHILE_COMPARING_FILES,
                                                 new Object[]{refImgURL.toString(), 
                                                              tmpFile.getAbsolutePath(),
                                                              e.getMessage()}))});
            report.setPassed(false);
            return report;
        }


        //
        // If the files differ here, it means that even the variation does 
        // not account for the difference return an error
        //
        if(!accurate){
            try{
                BufferedImage ref = getImage(refImgURL);
                BufferedImage gen = getImage(tmpFile);
                BufferedImage diff = buildDiffImage(ref, gen);

                //
                // If there is an accepted variation, check if it equals the
                // computed difference.
                //
                if(variationURL != null){
                    File tmpDiff = imageToFile(diff, IMAGE_TYPE_DIFF);

                    InputStream variationURLStream = null;
                    try{
                        variationURLStream = variationURL.openStream();
                    }catch(IOException e){
                        // Could not open variationURL stream. Just trace that
                        System.err.println(Messages.formatMessage(COULD_NOT_OPEN_VARIATION_URL,
                                                                  new Object[]{variationURL.toString()}));
                    }

                    if(variationURLStream != null){
                        InputStream refDiffStream =
                            new BufferedInputStream(variationURLStream);
                        
                        InputStream tmpDiffStream =
                            new BufferedInputStream(new FileInputStream(tmpDiff));
                        
                        if(compare(refDiffStream, tmpDiffStream)){
                            // We accept the generated result.
                            accurate = true;
                        }
                    }
                }

                if(!accurate){

                    if(saveVariation != null){
                        // There is a computed variation different from the 
                        // referenced variation and there is a place where the new 
                        // variation should be saved.
                        saveImage(diff, saveVariation);
                    }
                    
                    // Build two images:
                    // a. One with the reference image and the newly generated image
                    // b. One with the difference between the two images and the set of 
                    //    different pixels.
                    BufferedImage cmp = makeCompareImage(ref, gen);
                    File cmpFile = imageToFile(cmp, IMAGE_TYPE_COMPARISON);
                    File diffFile = imageToFile(diff, IMAGE_TYPE_DIFF);
                    
                    report.setErrorCode(ERROR_SVG_RENDERING_NOT_ACCURATE);
                    
                    report.setDescription(new TestReport.Entry[]{
                        new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                                             Messages.formatMessage(ERROR_SVG_RENDERING_NOT_ACCURATE, null)),
                        new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_REFERENCE_GENERATED_IMAGE_URI, null),
                                             cmpFile),
                        new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_DIFFERENCE_IMAGE, null),
                                             diffFile) });

                
                    report.setPassed(false);
                    return report;
                }
            }catch(Exception e){
                report.setErrorCode(ERROR_SVG_RENDERING_NOT_ACCURATE);
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                
                report.setDescription(new TestReport.Entry[]{
                    new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                                         Messages.formatMessage(ERROR_SVG_RENDERING_NOT_ACCURATE, null)),
                    new TestReport.Entry(Messages.formatMessage(ENTRY_KEY_INTERNAL_ERROR, null),
                                         Messages.formatMessage(COULD_NOT_GENERATE_COMPARISON_IMAGES, 
                                                                new Object[]{e.getClass().getName(),
                                                                             e.getMessage(),
                                                                             trace.toString()})) });
                    report.setPassed(false);
                    return report;
            }
        }


        //
        // Yahooooooo! everything worked out well.
        //
        report.setPassed(true);
        return report;
    }

    /**
     * Compare the two input streams
     */
    protected boolean compare(InputStream refStream,
                              InputStream newStream)
        throws IOException{
        int b, nb;
        boolean accurate;
        do {
            b = refStream.read();
            nb = newStream.read();
        } while (b != -1 && nb != -1 && b == nb);
        refStream.close();
        newStream.close();
        return (b == nb);
    }

    /**
     * Saves an image in a given File
     */
    protected void saveImage(BufferedImage img, File imgFile)
        throws IOException {
        if(!imgFile.exists()){
            imgFile.createNewFile();
        }

        PNGImageEncoder encoder 
            = new PNGImageEncoder(new FileOutputStream(imgFile),
                                  PNGEncodeParam.getDefaultEncodeParam(img));
        
        encoder.encode(img);
    }

    /**
     * Builds a new BufferedImage that is the difference between the two input images
     */
    protected BufferedImage buildDiffImage(BufferedImage ref,
                                           BufferedImage gen){
        BufferedImage diff = new BufferedImage(ref.getWidth(),
                                               ref.getHeight(),
                                               BufferedImage.TYPE_INT_ARGB);

        Vector src = new Vector();
        src.addElement(new BufferedImageCachableRed(ref));
        src.addElement(new BufferedImageCachableRed(gen));

        CompositeRed cr = new CompositeRed(src,
                                           CompositeRule.ARITHMETIC(0, 10, -10, 0.5f));

        cr.copyToRaster(diff.getRaster());
        return diff;
    }

    /**
     * Loads an image from a File
     */
    protected BufferedImage getImage(File file) 
        throws Exception {
        return getImage(file.toURL());
    }

    /**
     * Loads an image from a URL
     */
    protected BufferedImage getImage(URL url) 
        throws IOException {
        ImageTagRegistry reg = ImageTagRegistry.getRegistry();
        Filter filt = reg.readURL(new ParsedURL(url));
        if(filt == null)
            throw new IOException(Messages.formatMessage
                                  (COULD_NOT_LOAD_IMAGE,
                                   new Object[]{url.toString()}));

        RenderedImage red = filt.createDefaultRendering();
        if(red == null)
            throw new IOException(Messages.formatMessage
                                  (COULD_NOT_LOAD_IMAGE,
                                   new Object[]{url.toString()}));
        
        BufferedImage img = new BufferedImage(red.getWidth(),
                                              red.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        red.copyData(img.getRaster());

        return img;
    }

    /**
     *
     */
    protected BufferedImage makeCompareImage(BufferedImage ref,
                                             BufferedImage gen){
        BufferedImage cmp = new BufferedImage(ref.getWidth()*2,
                                              ref.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = cmp.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, cmp.getWidth(), cmp.getHeight());
        g.drawImage(ref, 0, 0, null);
        g.translate(ref.getWidth(), 0);
        g.drawImage(gen, 0, 0, null);
        g.dispose();

        return cmp;
    }

    /**
     * Creates a File into which the input image is
     * saved.
     * If there is a "file" component in the SVG url,
     * then a temporary file is created with that 
     * name and the imageType suffix in the temp 
     * directory of the test-reports directory. 
     */
    protected File imageToFile(BufferedImage img,
                               String imageType)
        throws IOException {
        String file = getURLFile(svgURL);

        File imageFile = null;
        if( !"".equals(file) ){
            imageFile = makeTempFileName(file, imageType);
        }
        else{
            imageFile = makeRandomFileName(imageType);
        }
        
        imageFile.deleteOnExit();

        PNGImageEncoder encoder 
            = new PNGImageEncoder(new FileOutputStream(imageFile),
                                  PNGEncodeParam.getDefaultEncodeParam(img));
        
        encoder.encode(img);
        
        return imageFile;
        
    }

    /**
     * Extracts the file portion of the URL
     */
    protected String getURLFile(URL url){
        String path = url.getPath();
        int n = path.lastIndexOf('/');
        if(n == -1){
            return path;
        }
        else{
            if(n<path.length()){
                return path.substring(n+1, path.length());
            }
            else{
                return "";
            }
        }
    }

    protected File makeTempFileName(String svgFileName,
                                    String imageType){
        int dotIndex = svgFileName.lastIndexOf('.');
        if( dotIndex == -1){
            return getNextTempFileName(svgFileName + imageType);
        }
        else{
            return getNextTempFileName
                (svgFileName.substring(0, dotIndex) +
                 imageType + IMAGE_FILE_EXTENSION);
        }
    }
    
    protected File getNextTempFileName(String fileName){
        File f = new File(getTempDirectory(), fileName);
        if(!f.exists()){
            return f;
        }
        else{
            return getNextTempFileName(fileName,
                                       1);
        } 
    }
    
    protected File getNextTempFileName(String fileName,
                                       int instance){
        // First, create a 'versioned' file name
        int n = fileName.lastIndexOf('.');
        String iFileName = fileName + instance;
        if(n != -1){
            iFileName = fileName.substring(0, n) + instance
                + fileName.substring(n, fileName.length());
        }
        
        File r = new File(getTempDirectory(), iFileName);
        if(!r.exists()){
            return r;
        }
        else{
            return getNextTempFileName(fileName,
                                       instance + 1);
        }
    }
    
    /**
     * Creates a temporary File into which the input image is
     * saved.
     */
    protected File makeRandomFileName(String imageType)
        throws IOException {
        
        return File.createTempFile(TEMP_FILE_PREFIX,
                                   TEMP_FILE_SUFFIX + imageType,
                                   null);
    }
    
    /**
     * Returns the <tt>ImageTranscoder</tt> the Test should
     * use
     */
    public ImageTranscoder getTestImageTranscoder(){
        ImageTranscoder t = new InternalPNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                             PARSER_CLASS_NAME);
        t.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE,
                             new Boolean(false));
        return t;
    }

    /**
     * Inner class which derives from the PNGTranscoder and calls the 
     * manipulateSVGDocument just before encoding happens.
     */
    protected class InternalPNGTranscoder extends PNGTranscoder{
        /**
         * Transcodes the specified Document as an image in the specified output.
         *
         * @param document the document to transcode
         * @param uri the uri of the document or null if any
         * @param output the ouput where to transcode
         * @exception TranscoderException if an error occured while transcoding
         */
        protected void transcode(Document document,
                                 String uri,
                                 TranscoderOutput output)
            throws TranscoderException {
            SVGRenderingAccuracyTest.this.manipulateSVGDocument(document);
            super.transcode(document, uri, output);
        }
    }
}
