/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.regard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.refimpl.transcoder.ImageTranscoder;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.refimpl.transcoder.PngTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.util.awt.image.ImageLoader;
import org.xml.sax.InputSource;

/**
 * A diffing tool.
 *
 * @author <a href="mailto:spei@cs.uiowa.edu">Sheng Pei</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version  $Id$
 */
public class Main {

    /** The regard directory name. */
    static final String REGARD_DIRECTORY_NAME = "regard";
    /** The regard sub directory that contains the reference images. */
    static final String REGARD_REF_DIRECTORY_NAME = "ref";
    /** The regard sub directory that contains the new images. */
    static final String REGARD_NEW_DIRECTORY_NAME = "new";
    /** The regard sub directory that contains the svg examples. */
    static final String REGARD_SVG_DIRECTORY_NAME = "svg";
    /** The regard sub directory that contains the difference images. */
    static final String REGARD_DIFF_DIRECTORY_NAME = "diff";
    /** The regard report file name */
    static final String REGARD_REPORT_FILE_NAME = "report";
    /** The regard report file extension */
    static final String REGARD_REPORT_FILE_EXT = "html";

    /** The regard report head string     */
    static final String REGARD_REPORT_HEAD_STRING = "<!doctype html public " + "\"" + "-//w3c//dtd html 4.0 transitional//en" + "\"" + "> <html> <body> <center>Batik Regression Guard Tool(Regard) Report</center>";
    /** The regard report end string     */
    static final String REGARD_REPORT_END_STRING = "</body> </html>";
    /** The regard report no-regression string */
    static final String REGARD_REPORT_NO_REGRESSION_STRING ="<p>There's no file that has regression.<br>";

    /**
     * MAIN
     */
    public static void main(String [] args) {
        if (args.length != 1) {
            usage(System.err);
            exit(1);
        }
        if (args[0].equals("-help")) {
            usage(System.out);
            exit(0);
        } else if (args[0].equals("-init")) {
            init();
        } else if (args[0].equals("-reset")) {
            reset();
        } else if (args[0].equals("-ref")) {
            generateRefImages();
        } else if (args[0].equals("-new")) {
            generateNewImages();
        } else if (args[0].equals("-diff")) {
            diffImage();
        } else {
            usage(System.err);
            exit(1);
        }
    }


    /**
     * INITILIZATION
     *
     * Creates the regard directory and its three sub directory if needed.
     */
    public static void init() {
        File f = new File(REGARD_DIRECTORY_NAME);
        if (!f.exists()) {
            display("Creating "+f.getAbsolutePath());
            f.mkdir();

            File ff = new File(f, REGARD_NEW_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());

            ff = new File(f, REGARD_REF_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());

            ff = new File(f, REGARD_SVG_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());
            exit(0);
        } else {
            error(f.getAbsolutePath()+" already exists");
            exit(2);
        }
    }

    /**
     * RESET
     *
     * Removes the reference images.
     */
    public static void reset() {
        File dir = new File(getRefDirectory());
        File [] images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            display("Deleting reference image "+images[i].getName());
            images[i].delete();
        }
        dir = new File(getNewDirectory());
        images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            display("Deleting new image "+images[i].getName());
            images[i].delete();
        }
        dir = new File(getDiffDirectory());
        images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            display("Deleting diff image "+images[i].getName());
            images[i].delete();
        }
        exit(0);
    }

    /**
     * REFERENCE
     *
     * Generates the reference images using the SVG files from the
     * 'svg' directory.
     */
    public static void generateRefImages() {
        File f = new File(REGARD_DIRECTORY_NAME);
        if (!f.exists()) {
            error("regard is not initialized. use -init first");
        }
        File ff = new File(f, REGARD_REF_DIRECTORY_NAME);
        if (ff.list().length != 0) {
            error("The reference directory "+ff.getAbsolutePath()+
                  " is not empty. Use -reset first.");
            exit(3);
        }
        generateImages(getRefDirectory(), "reference image for");
    }

    /**
     * NEW IMAGES
     *
     * Generates the new images using the SVG files from the 'svg'
     * directory.
     */
    public static void generateNewImages() {
        File f = new File(REGARD_DIRECTORY_NAME);
        if (!f.exists()) {
            error("regard is not initialized. use -init first");
        }
        generateImages(getNewDirectory(), "new image for");
    }

    static void generateImages(String outputDirectory, String desc) {
        File destDir = new File(outputDirectory);
        File svgDir = new File(getSamplesDirectory());
        Transcoder transcoder = getTranscoder();
        File [] samples = svgDir.listFiles();
        if (samples == null) {
            error("No SVG files has been found in "+
                  svgDir.getAbsolutePath());
            exit(2);
        }
        System.out.println("Generating images for " + samples.length + " samples");
        for (int i=0; i < samples.length; ++i) {
            File src = samples[i];
            if (!src.getName().endsWith(".svg")){continue;}
            File dest = new File(destDir, getImageName(src.getName()));
            display("Generating "+desc+" "+src.getName());
            try {
              writeImage(transcoder,
                         src.toURL().toString(),
                         dest.getAbsolutePath());
            } catch (MalformedURLException ex) {
                error("Bad URL for "+src.getAbsolutePath()+" or "+
                      dest.getAbsolutePath());
            }
        }
    }

    static String getImageName(String uri) {
        if (uri.endsWith(".svg")) {
            uri = uri.substring(0, uri.lastIndexOf(".svg"));
            uri += ".png";
        }
        return uri;
    }

    /**
     * DIFFERENCE
     *
     * Diffs the images from the 'ref' and the 'new' directories.
     */
    public static void diffImage() {
        File f = new File(REGARD_DIRECTORY_NAME);
        if (!f.exists()) {
            error("regard is not initialized. use -init first");
        }
        File refDir = new File(getRefDirectory());
        File newDir = new File(getNewDirectory());
        File [] refImages = refDir.listFiles();
        int [] badIndex = new int [refImages.length];
        int badFiles = 0;
        for (int i=0; i < refImages.length; ++i) {
            File refImg = refImages[i];
            File newImg = new File(newDir, refImg.getName());
            boolean ok =
                diffImage(refImg.getAbsolutePath(), newImg.getAbsolutePath());
            if (ok) {
                display("Diff: "+refImg.getName()+" ok");
            } else {
                badIndex[badFiles] = i;
                display("Diff: "+refImg.getName()+" has regression");
                badFiles++;
            }
        }
        display("Total: "+badFiles+"/"+refImages.length+" corrupted");

        // When there's regression, we'll produce the diff directory and
        // diff image files
        if(badFiles>0) {
            generateReport(refImages, badIndex, badFiles);
        }
        else{
            generateReport();
        }
        exit(0);
    }

    /*
     * REPORT
     */
    public static void generateReport(){
        String time = computeTimeStamp();
        String reportFileName = REGARD_REPORT_FILE_NAME + "_" + time + "." + REGARD_REPORT_FILE_EXT;
        File f = new File(REGARD_DIRECTORY_NAME);
        File reportFile = new File(f, reportFileName);
        display("Creating report...");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            String content = new String();
            content += REGARD_REPORT_HEAD_STRING;
            content += REGARD_REPORT_NO_REGRESSION_STRING;
            content += "<p>Reported On:";
            content += time;
            content += REGARD_REPORT_END_STRING;
            writer.print(content);
            writer.close();
            display("Report finished");
        }
        catch(IOException e){
        }

    }

    public static void generateReport(File [] refImages, int [] badIndex, int bi){
        // create report file and the difference directory
        File f = new File(REGARD_DIRECTORY_NAME);
        String time = computeTimeStamp();
        String reportFileName = REGARD_REPORT_FILE_NAME + "_" + time + "." + REGARD_REPORT_FILE_EXT;
        File reportFile = new File(f, reportFileName);
        File diffDir = new File(f, REGARD_DIFF_DIRECTORY_NAME);
        diffDir.mkdir();
        display("Creating "+diffDir.getAbsolutePath());

        // get the new directory reference
        File newDir = new File(getNewDirectory());

        // begin to prepare the report and difference image files
        display("Creating report...");
        String content = new String();

        content += REGARD_REPORT_HEAD_STRING;
        content += "<p>There're ";
        content += String.valueOf(bi);
        content += " file(s) that has/have regression:<p>";

        int count=1;

        for(int i=0; i<bi; i++){
            File refImg = refImages[badIndex[i]];
            File newImg = new File(newDir, refImg.getName());
            File diffImg = new File(diffDir, refImg.getName());
            BufferedImage bfRef = ImageLoader.loadImage(refImg, BufferedImage.TYPE_INT_ARGB);
            BufferedImage bfNew = ImageLoader.loadImage(newImg, BufferedImage.TYPE_INT_ARGB);
            if ((bfRef.getWidth() != bfNew.getWidth()) ||
                 (bfRef.getHeight() != bfNew.getHeight())){
                display("Fatal error, image size changed!");
                String s = "<br>" + count + ".  " + refImg.getName() +  
                    ": image size changed!";
                content += s;
                count++;
                continue;
            }

            ImageTranscoder transcoder
                = (ImageTranscoder)getTranscoder();
            BufferedImage bfDiff = transcoder.createImage(2*bfRef.getWidth(), 
                                                          2*bfRef.getHeight());

            Graphics2D g = bfDiff.createGraphics();
            // g.setPaint(transcoder.getBackgroundPaint());
            // g.fillRect(0, 0, bfDiff.getWidth(), bfDiff.getHeight());
            g.dispose();
            display(String.valueOf(i+1) + ". " + "Creating the difference image file of " + refImg.getName());

            boolean difference = 
                diffBufferedImage(bfRef.getRaster(), 
                                  bfNew.getRaster(), 
                                  bfDiff.getRaster());
            if (!difference) {
                display("   No difference in image content");
            } else {
                try{
                    transcoder.writeImage(bfDiff, 
                                          new FileOutputStream(diffImg));
                }
                catch(Exception e){
                }
                try{
                    String s = diffImg.toURL().toString();
                    content += "<br>" + count + ". " + 
                        "<a href=" + "\"" + s + "\"" +">" + 
                        "<img border=\"0\" hspace=\"0\" vspace=\"0\" " + 
                        "align=\"middle\" src=" + "\"" + s + "\"" + 
                        " height=100 width=90" + " />" + "</a>";
                }
                catch(MalformedURLException e){
                }
                count++;
            }
        }
        content += "<p>Reported On: ";
        content += time;
        content += REGARD_REPORT_END_STRING;
        try{
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            writer.print(content);
            writer.close();
        }
        catch (IOException e){}

        display("Report finished");
    }

    /**
     * This is the core method to calculate the difference of the two
     * input images and save the comparison into the output image
     * @param ref the reference image in BufferedImage
     * @param new the new image in BufferedImage
     * @param diff the difference image in BufferedImage
     */

    public static boolean diffBufferedImage(Raster ref, 
                                            Raster cmp, 
                                            Raster diff){
        final int w = ref.getWidth();
        final int h = ref.getHeight();

        // Access the integer buffer for each image.
        DataBufferInt refDB = (DataBufferInt)ref.getDataBuffer();
        DataBufferInt cmpDB = (DataBufferInt)cmp.getDataBuffer();
        DataBufferInt diffDB = (DataBufferInt)diff.getDataBuffer();

        // Offset defines where in the stack the real data begin
        final int refOff = refDB.getOffset();
        final int cmpOff = cmpDB.getOffset();
        final int diffOff = diffDB.getOffset();

        // Stride is the distance between two consecutive column elements,
        // in the one-dimention dataBuffer
        final int refScanStride = ((SinglePixelPackedSampleModel)
                                   ref.getSampleModel()).getScanlineStride();

        final int cmpScanStride = ((SinglePixelPackedSampleModel)
                                   cmp.getSampleModel()).getScanlineStride();

        final int diffScanStride = ((SinglePixelPackedSampleModel)
                                    diff.getSampleModel()).getScanlineStride();

        // Access the pixel value array
        final int refPixels[] = refDB.getBankData()[0];
        final int cmpPixels[] = cmpDB.getBankData()[0];
        final int diffPixels[] = diffDB.getBankData()[0];

        // source pointers pointing to pixels in Ref and Cmp
        int rp, np;
        // destination pointers pointing to three areas in the Diff
        int dp0, dp1, dp2;

        // pixel values
        int refPixel;
        int cmpPixel;
        int diffPixel;


        boolean ret = false;

        for (int i=0; i<h; i++){
            rp = refOff + i*refScanStride;
            np = cmpOff + i*cmpScanStride;
            dp0 = diffOff + i*diffScanStride;
            dp1 = diffOff + i*diffScanStride + w;
            dp2 = diffOff + (i+h)*diffScanStride;

            for (int j=0; j<w; j++){
                refPixel = refPixels[rp];
                cmpPixel = cmpPixels[np];
                diffPixel = cmpPixel - refPixel;
                diffPixels[dp0] = refPixel;
                diffPixels[dp1] = cmpPixel;
                diffPixels[dp2] = diffPixel;
                if (diffPixel != 0) ret = true;
                rp++;
                np++;
                dp0++;
                dp1++;
                dp2++;
            }
        }
        return ret;
    }

    //
    // Convenient methods
    //


    public static String computeTimeStamp(){
        String time = "";
        Calendar rightNow = Calendar.getInstance();
        time += String.valueOf(rightNow.get(Calendar.HOUR));
        time += "_";
        time += String.valueOf(rightNow.get(Calendar.MINUTE));
        time += "_";
        time += String.valueOf(rightNow.get(Calendar.MONTH));
        time += "_";
        time += String.valueOf(rightNow.get(Calendar.DATE));
        time += "_";
        time += String.valueOf(rightNow.get(Calendar.YEAR));
        return time;
    }


    /**
     * Writes an image.
     * @param transcoder the transcoder to use to generate the image
     * @param inputURI the URI of the SVG file
     * @param outputURI the URI of the image to generate
     */
    public static void writeImage(Transcoder transcoder,
                                  String inputURI,
                                  String output) {
        try {
            InputSource isource = new InputSource(inputURI);
            OutputStream ostream =
                new BufferedOutputStream(new FileOutputStream(output));
            ((ImageTranscoder)transcoder).transcodeToStream(isource, ostream);
            ostream.flush();
            ostream.close();
        } catch(IOException ex) {
            error("while writing "+inputURI+" to "+output+"\n"+ex.getMessage());
        } catch(TranscoderException ex) {
            error("while writing "+inputURI+" to "+output+"\n"+ex.getMessage());
        }
    }

    /**
     * Returns true if the specified images are the same, false otherwise.
     * @param refInputURI the reference image
     * @param newInputURI the new produced image
     */
    public static boolean diffImage(String refInputURI, String newInputURI) {
        try {
            InputStream refStream =
                new BufferedInputStream(new FileInputStream(refInputURI));
            InputStream newStream =
                new BufferedInputStream(new FileInputStream(newInputURI));
            int b, nb;
            do {
                b = refStream.read();
                nb = newStream.read();
            } while (b != -1 && nb != -1 && b == nb);
            refStream.close();
            newStream.close();
            return (b == nb);
        } catch(IOException ex) {
            System.out.println("Error while diffing "+refInputURI+
                               " and "+newInputURI);
            return false;
        }
    }

    /**
     * Returns the transcoder to use.
     */
    public static Transcoder getTranscoder() {
        return new PngTranscoder();
    }

    /**
     * Display the specified error message.
     * @param msg the error message to display
     */
    public static void error(String msg) {
        System.err.println("ERROR: "+msg);
    }

    /**
     * Display the specified message.
     * @param msg the message to display
     */
    public static void display(String msg) {
        System.out.println(msg);
    }

    /**
     * Shows the usage message.
     * @param out the stream where to write the usage message
     */
    public static void usage(PrintStream out) {
        out.println("usage: regard [-help|-init|-reset|-ref|-new|-diff]");
        out.println("-help   Display this message");
        out.println("-init   Initialize regard");
        out.println("-reset  Removes the reference and new images");
        out.println("-ref    Produces reference images");
        out.println("-new    Produces new images");
        out.println("-diff   Diff the reference and the new images");
    }

    /**
     * Exits the application with the specified code.
     * @param code the exit code
     */
    public static void exit(int code) {
        System.exit(code);
    }

    /**
     * Returns the directory where the reference images are stored.
     */
    public static String getRefDirectory() {
        File f = new File(REGARD_DIRECTORY_NAME);
        return new File(f, REGARD_REF_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the new images are stored.
     */
    public static String getNewDirectory() {
        File f = new File(REGARD_DIRECTORY_NAME);
        return new File(f, REGARD_NEW_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the SVG samples are stored.
     */
    public static String getSamplesDirectory() {
        File f = new File(REGARD_DIRECTORY_NAME);
        return new File(f, REGARD_SVG_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the difference images are stored.
     */
    public static String getDiffDirectory() {
        File f = new File(REGARD_DIRECTORY_NAME);
        return new File(f, REGARD_DIFF_DIRECTORY_NAME).getAbsolutePath();
    }
}

