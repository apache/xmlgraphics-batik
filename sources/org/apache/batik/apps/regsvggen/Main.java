/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.regsvggen;

import java.io.*;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintStream;
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.Calendar;
import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.net.MalformedURLException;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.ext.awt.image.ImageLoader;
import org.apache.batik.svggen.*;

import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.apache.crimson.tree.*;
import org.apache.crimson.parser.*;

import org.xml.sax.InputSource;

import org.w3c.dom.*;


/**
 * A regression checking tool for SVG generator test cases.
 *
 * @author <a href="mailto:spei@cs.uiowa.edu">Sheng Pei</a>
 * @version $Id$
 */
public class Main {

    //
    // The following section defines the Directory names
    //

    /** The regsvggen root directory name. */
    static final String REGSVGGEN_DIRECTORY_NAME = "regsvggen";

    /** This directory contains images transcoded from SVG files
     *  that're from SVG Generator
     */
    static final String REGSVGGEN_REF_DIRECTORY_NAME = "ref";

    /** This directory contains images encoded directly from
     *  the BufferedImage
     */
    static final String REGSVGGEN_NEW_DIRECTORY_NAME = "new";

    /** This directory contains the svg files that're generated
     *  dynamically by the Classes which use Java codes embedded
     *  in the XML test cases
     */
    static final String REGSVGGEN_SVG_DIRECTORY_NAME = "svg";

    /** This directory contains the xml test cases */
    static final String REGSVGGEN_XML_DIRECTORY_NAME = "xml";

    /** This sub directory contains the difference images. */
    static final String REGSVGGEN_DIFF_DIRECTORY_NAME = "diff";

    /** This sub directory contains the temp class files. */
    static final String REGSVGGEN_CLASSES_DIRECTORY_NAME = "classes";

    //
    // The following section defines the Report file
    //

    /** The regsvggen report file name */
    static final String REGSVGGEN_REPORT_FILE_NAME = "report";
    /** The regsvggen report file extension */
    static final String REGSVGGEN_REPORT_FILE_EXT = "html";
    /** The regsvggen report head string     */
    static final String REGSVGGEN_REPORT_HEAD_STRING = "<!doctype html public " + "\"" + "-//w3c//dtd html 4.0 transitional//en" + "\"" + "> <html> <body> <center>Batik Regression Guard Tool(Regsvggen) Report</center>";
    /** The regsvggen report end string     */
    static final String REGSVGGEN_REPORT_END_STRING = "</body> </html>";
    /** The regsvggen report no-regression string */
    static final String REGSVGGEN_REPORT_NO_REGRESSION_STRING ="<p>There's no file that has regression.<br>";

    static final String CLASSPATH_SEPARATOR = System.getProperty("path.separator", ";");
    static final String FILE_SEPARATOR = System.getProperty("file.separator", "/");

    //
    // The following section defines the Class constants and variables
    //

    private static final String EOL = "\n";
    private static final String IMPORT = "import";
    private static final String ATTR_TEXT_AS_SHAPES = "textAsShapes";
    private static final String TEST_PACKAGE = "usecases";

    private static final String testPackage = "package " + TEST_PACKAGE + ";\n\n";
    private static final String testImports = "import java.awt.*; \n import java.awt.image.*; \n import org.apache.batik.apps.regsvggen.Painter; \n import java.awt.geom.*; \n import java.awt.font.*; \n";
    private static final String testClassDec = " implements Painter {\n\n\tstatic final String IMAGE_DIR=\"/work/doc/svg/src/usecases/html/images/\";\n\n\tpublic void paint(Graphics2D g){";
    private static final String testFinish = "\tpublic Dimension getSize(){ \n\t\treturn new Dimension(300, 400); \n\t} \n\n}";
    private static final String testOutName = "CompileOut";
    private static final String classDir = "." + FILE_SEPARATOR + "regsvggen" + FILE_SEPARATOR + "classes";

    /**
     * The CSS parser class name key.
     */
    public final static String CSS_PARSER_CLASS_NAME =
        "org.w3c.flute.parser.Parser";

    /**
     * MAIN
     */
    public static void main(String [] args) {

        // Setting the Fonts

        GraphicsEnvironment env;
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontNames[] = env.getAvailableFontFamilyNames();
        int nFonts = fontNames != null ? fontNames.length : 0;
        for(int i=0; i<nFonts; i++){
            System.out.print(".");
            // display("Initializing the fonts...");
            //fonts.put(fontNames[i], fontNames[i]);
        }
        System.out.println();

        if (args.length != 1) {
            usage(System.err);
            exit(1);
        }
        if (args[0].equals("-help")) {
            usage(System.out);
            exit(0);
        } else if (args[0].equals("-init")) {
            init();
        }
        else if (args[0].equals("-reset")) {
            reset();
        } else if (args[0].equals("-go")) {
            setUp();
            generateGraphics();
            generateRefImages();
            diffImage();
            cleanUp();
        } else {
            usage(System.err);
            exit(1);
        }
    }

    /**
     * INITILIZATION
     *
     * Creates the regsvggen directory and its sub directories.
     */
    public static void init() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        if (!f.exists()) {
            display("Creating "+f.getAbsolutePath());
            f.mkdir();

            File ff = new File(f, REGSVGGEN_XML_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());

            ff = new File(f, REGSVGGEN_SVG_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());

            ff = new File(f, REGSVGGEN_DIFF_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating "+ff.getAbsolutePath());

            ff = new File(f, REGSVGGEN_NEW_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating temp directory: "+ff.getAbsolutePath());

            exit(0);
        } else {
            error(f.getAbsolutePath()+" already exists");
            exit(2);
        }
    }

    /**
     * GO
     *
     * The main processing unit, devided into four parts:
     * 1. setUp()             --  generate temp directories: ref and new
     * 2. generateGraphics()  --  load XML file, create Classes, generate
     *                              PNG image in "new" and SVG file in "svg"
     * 3. generateRefImages() --  generate PNG image from SVG files into "ref"
     * 4. diffImage()         --  compare the images between "ref" and "new",
     *                              generate reports
     * 5. cleanUp()           --  delete temp files and directories
     */

    public static void setUp(){

        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        if(!f.exists()){
            display("The regsvggen hasn't been initilized. Use -init");
            exit(2);
        }
        else {
            File ff = new File(f, REGSVGGEN_REF_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating temp directory: "+ff.getAbsolutePath());

            ff = new File(f, REGSVGGEN_CLASSES_DIRECTORY_NAME);
            ff.mkdir();
            display("Creating temp directory:"+ff.getAbsolutePath());

        }
    }
    public static void generateGraphics(){
        File xmlDir = new File(getXmlDirectory());
        File [] xmlFiles = xmlDir.listFiles();
        if (xmlFiles == null){
            error("No XML files has been found in "+
                  xmlDir.getAbsolutePath());
            exit(2);
        }
        File xmlFile;
        File nowImage;
        String testFileName;
        String testName;
        String testImageName;
        String dClassName;
        for (int i=0; i<xmlFiles.length; i++){
            xmlFile = xmlFiles[i];
            testFileName = xmlFile.getAbsolutePath();
            testName = getImageName(xmlFile.getName());
            if (!testFileName.endsWith(".xml")){continue;}
            try {
                dClassName = compileTestClass(testFileName);
                nowImage = new File(getNewDirectory(), testName);
                testImageName = nowImage.getAbsolutePath();
                createBufImage(dClassName, testImageName);
                nowImage = new File(getSvgDirectory(), testName);
                testImageName = nowImage.getAbsolutePath().substring(0, testImageName.length() - 4) + ".svg";
                createSvgFile(dClassName, testImageName);
            }
            catch (Exception e){
                display("Error when loading classes: " + e.toString());
            }
        }
    }
    /**
     * This method will do the following:
     * 1. Load the test case XML file specified by testFileName
     * 2. Extract the Java code in the XML file and merge a Java source file
     * 3. Compile the temparory Java source file and get the Class name
     */
    public static String compileTestClass(String testFileName)
    throws Exception{
        //
        // Load xml test file, using crimson
        //

        XMLReaderImpl parser = new XMLReaderImpl();
        XmlDocumentBuilder builder = new XmlDocumentBuilder();
        parser.setContentHandler(builder);

        try {
            FileReader xmlReader = new FileReader(testFileName);
            InputSource is = new InputSource(xmlReader);
            parser.parse(is);
        }
        catch (Exception e){
            display("Error: " + e.toString());
        }
        // Get document DOM
        Document testDoc = builder.getDocument();

        //
        // Get Test Title
        //
        Element root = testDoc.getDocumentElement();

        //
        // Get text convertion strategy. Defaults to false.
        //
        String textAsShapesAttr = root.getAttribute(ATTR_TEXT_AS_SHAPES);
        if(textAsShapesAttr == null) textAsShapesAttr = "false";

        //
        // Get Test Description
        //
        /*
        NodeList testDescriptions = root.getElementsByTagName(DESCRIPTION);
        String descriptions[] = new String[testDescriptions.getLength()];
        for(int i=0; i<descriptions.length; i++)
            descriptions[i] = testDescriptions.item(i).getFirstChild().getNodeValue();
        */
        //
        // Get node that contains the Java Code to be compiled
        //
        NodeList codeNodes = testDoc.getElementsByTagName("javaCode");
        StringBuffer javaCode = new StringBuffer();
        int nCodeNodes = codeNodes.getLength();
        int nCD = 0;
        for(int i=0; i<nCodeNodes; i++){
            Element codeNode = (Element)codeNodes.item(i);
            NodeList codeDefs = codeNode.getChildNodes();
            int nCodeDefs = codeDefs.getLength();
            for(int j=0; j<nCodeDefs; j++){
                Node codeDef = codeDefs.item(j);
                display("Child of type: " + codeDef.getNodeType());
                javaCode.append(codeDef.getNodeValue());
                javaCode.append("\n");
            }
        }

        display("Found : " + nCodeNodes + " javaCode elements and " + nCD + " CDATA sections");

        //
        // Get additional imports
        //
        NodeList importNodes = testDoc.getElementsByTagName(IMPORT);
        StringBuffer imports = new StringBuffer();
        int nImports = importNodes.getLength();
        for(int i=0; i<nImports; i++){
            Element importNode = (Element)importNodes.item(i);
            imports.append(importNode.getFirstChild().getNodeValue());
            imports.append("\n");
        }

        display("Imports: " + imports.toString());

        // Create a temporary file for compiling the test
        File tmpSourceFile = File.createTempFile("SVGGeneratorTest", ".java");
        String className = tmpSourceFile.getName().substring(0, tmpSourceFile.getName().length() - 5);
        // Write source in temporary file
        Writer ow = new OutputStreamWriter(new FileOutputStream(tmpSourceFile));
        ow.write(testPackage);
        ow.write(testImports);
        ow.write(imports.toString());
        ow.write("public class " + className + testClassDec);
        ow.write(javaCode.toString());
        ow.write("\n\t}\n\n\tpublic boolean isTextAsShapes(){ return " + textAsShapesAttr.equalsIgnoreCase("true") + "; }\n\n");
        ow.write(testFinish);
        ow.flush();
        ow.close();

        // Compile test now
        String command = "javac -classpath ";
        command += System.getProperty("java.class.path") + CLASSPATH_SEPARATOR +  tmpSourceFile.getParent();
        command += " -d " + classDir + " " + tmpSourceFile.getAbsolutePath();

        display(command);
        final Process process = Runtime.getRuntime().exec(command);

        // In a separate thread, read the compilation output
        final StringWriter compileOutput = new StringWriter();

        Thread traceProcessTh = new Thread(){
            public void run(){
                InputStream pis = process.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(pis));
                try{
                    String line = null;
                    while((line = reader.readLine()) != null){
                        compileOutput.write(testOutName);
                        compileOutput.write(" : ");
                        compileOutput.write(line);
                        compileOutput.write(EOL);
                    }
                    /* The following will generate a file for compilation output
                    compileOutput.write(testOutName + " compilation finished" + EOL);
                    File f = new File(REGSVGGEN_DIRECTORY_NAME);
                    File compileout = new File(f,"compile");
                    PrintWriter writer = new PrintWriter(new FileWriter(compileout));
                    writer.print(compileOutput.toString());
                    writer.close();
                    */
                }catch(IOException ioe){
                    compileOutput.write(testOutName + " compilation finished" + EOL);
                }
           }
        };

        traceProcessTh.start();
        process.waitFor();

        //
        // Clean up source file
        //
        tmpSourceFile.delete();

        // Return back the qualified class name
        String qualifiedClassName = TEST_PACKAGE + "." + className;
        return qualifiedClassName;
    }


    /**
     *  This method will generate PNG file directly
     *  from the BufferedImage, the image files will
     *  be stored in the "new" directory
     */
    public static void createBufImage(String className, String testImageName)
    throws Exception{
        Class cl = Class.forName(className);
        display("Loading class: " + className);
        Painter painter = null;
        try{
            painter = (Painter)cl.newInstance();
        }catch(Exception e){
            exit(1);
        }
        // Begin encoding to PNG image file
        ImageTranscoder transcoder = getTranscoder();

        Dimension size = painter.getSize();
        BufferedImage buf = transcoder.createImage(size.width, size.height);
        Graphics2D g = buf.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                               RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        Shape clip = new Rectangle(0, 0, 300, 400);
        clip = new GeneralPath(clip);
        g.setClip(clip);

        painter.paint(g);
        g.dispose();

        try{
            transcoder.writeImage(buf,
                                  new TranscoderOutput(new FileOutputStream(testImageName)));
        }
        catch(Exception e){
        }
    }

    /**
     *  This method will generate SVG files using the
     *  SVGGraphics2D, SVG files will be in "svg" directory
     */
    public static void createSvgFile(String className, String testImageName)
    throws Exception{
        Class cl = Class.forName(className);
        display("Loading class: " + className);
        Painter painter = null;
        try{
            painter = (Painter)cl.newInstance();
        }catch(Exception e){
            exit(1);
        }
        // First, create an instance of org.w3c.dom.Document

        CSSDocumentHandler.setParserClassName(CSS_PARSER_CLASS_NAME);
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String namespaceURI = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document domFactory = impl.createDocument(namespaceURI, "svg", null);

        // Second, create an instance of the generator and paint to it
        ExtensionHandler extensionHandler
            = new DefaultExtensionHandler();
        SVGGraphics2D svggen = new SVGGraphics2D(domFactory);
        Dimension size = painter.getSize();
        svggen.setSVGCanvasSize(size);
        painter.paint(svggen);

        // Third, find out whether to use XML attributes or CSS properties
        // and write the SVG content to the output file
        boolean useCss = false;
        svggen.stream(testImageName, useCss);
    }
    /**
     * This method will generate PNG files from corresponding
     * SVG files under "svg" directory, the image files will
     * be in "ref" directory
     */
    public static void generateRefImages() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        if (!f.exists()) {
            error("regsvggen is not initialized. use -init first");
        }
        File ff = new File(f, REGSVGGEN_REF_DIRECTORY_NAME);
        if (ff.list().length != 0) {
            error("The reference directory "+ff.getAbsolutePath()+
                  " is not empty. Use -reset first.");
            exit(3);
        }
        generateImages(getRefDirectory(), "images from SVG files");
    }

    static void generateImages(String outputDirectory, String desc) {
        File destDir = new File(outputDirectory);
        File svgDir = new File(getSvgDirectory());
        ImageTranscoder transcoder = getTranscoder();
        File [] samples = svgDir.listFiles();
        if (samples == null) {
            error("No SVG files has been found in "+
                  svgDir.getAbsolutePath());
            exit(2);
        }
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
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void diffImage() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        if (!f.exists()) {
            error("regsvggen is not initialized. use -init first");
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
    }

    public static void generateReport(){
        String time = computeTimeStamp();
        String reportFileName = REGSVGGEN_REPORT_FILE_NAME + "_" + time + "." + REGSVGGEN_REPORT_FILE_EXT;
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        File reportFile = new File(f, reportFileName);
        display("Creating report...");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            String content = new String();
            content += REGSVGGEN_REPORT_HEAD_STRING;
            content += REGSVGGEN_REPORT_NO_REGRESSION_STRING;
            content += "<p>Reported On:";
            content += time;
            content += REGSVGGEN_REPORT_END_STRING;
            writer.print(content);
            writer.close();
            display("Report finished");
        }
        catch(IOException e){
        }

    }

    public static void generateReport(File [] refImages, int [] badIndex, int bi){
        // create report file and the difference directory
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        String time = computeTimeStamp();
        String reportFileName = REGSVGGEN_REPORT_FILE_NAME + "_" + time + "." + REGSVGGEN_REPORT_FILE_EXT;
        File reportFile = new File(f, reportFileName);
        File diffDir = new File(f, REGSVGGEN_DIFF_DIRECTORY_NAME);
        diffDir.mkdir();
        display("Creating "+diffDir.getAbsolutePath());

        // get the new directory reference
        File newDir = new File(getNewDirectory());

        // begin to prepare the report and difference image files
        display("Creating report...");
        String content = new String();

        content += REGSVGGEN_REPORT_HEAD_STRING;
        content += "<p>There're ";
        content += String.valueOf(bi);
        content += " file(s) that has/have regression:<p>";

        for(int i=0; i<bi; i++){
            File refImg = refImages[badIndex[i]];
            File newImg = new File(newDir, refImg.getName());
            File diffImg = new File(diffDir, refImg.getName());
            BufferedImage bfRef = ImageLoader.loadImage(refImg, BufferedImage.TYPE_INT_ARGB);
            BufferedImage bfNew = ImageLoader.loadImage(newImg, BufferedImage.TYPE_INT_ARGB);
            if ((bfRef.getWidth() != bfNew.getWidth()) ||
                 (bfRef.getHeight() != bfNew.getHeight())){
                display("Error: image size changed!");
                String s = "<br>" + String.valueOf(i+1) + ".  " + refImg.getName() +  ": image size changed!";
                content += s;
                continue;
            }
            ImageTranscoder transcoder = getTranscoder();
            BufferedImage bfDiff = transcoder.createImage(3*bfRef.getWidth(), bfRef.getHeight());
            Graphics2D g = bfDiff.createGraphics();
            //g.setPaint(Color.white);
            //g.fillRect(0, 0, bfDiff.getWidth(), bfDiff.getHeight());
            g.dispose();
            diffBufferedImage(bfRef.getRaster(), bfNew.getRaster(), bfDiff.getRaster());
            try{
                transcoder.writeImage(bfDiff,
                                      new TranscoderOutput(new FileOutputStream(diffImg)));
            }
            catch(Exception e){
            }
            display(String.valueOf(i+1) + ". " + "Creating the difference image file of " + refImg.getName());
            try{
                String s = diffImg.toURL().toString();
                content += "<br>" + String.valueOf(i+1) + ". " + "<a href=" + "\"" + s + "\"" +">" + "<img src=" + "\"" + s + "\"" + " height=40 width=90" + " />" + "</a>";
            }
            catch(MalformedURLException e){
            }
        }
        content += "<p>Reported On: ";
        content += time;
        content += REGSVGGEN_REPORT_END_STRING;
        try{
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            writer.print(content);
            writer.close();
        }
        catch (IOException e){}

        display("Report finished");
    }

    public static void reset(){
        File dir = new File(getSvgDirectory());
        File [] images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            display("Deleting svg files "+images[i].getName());
            images[i].delete();
        }

        dir = new File(getDiffDirectory());
        images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            display("Deleting diff files "+images[i].getName());
            images[i].delete();
        }

        dir = new File(getRefDirectory());
        images = dir.listFiles();
        if(images != null){
            for (int i=0; i < images.length; ++i) {
                display("Deleting diff files "+images[i].getName());
                images[i].delete();
            }
        }

    }
    public static void cleanUp(){

        File dir = new File(getRefDirectory());
        File [] images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            //display("Deleting svg image files "+images[i].getName());
            images[i].delete();
        }
        dir.delete();

        /*dir = new File(getNewDirectory());
        images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            //display("Deleting bufferedimage files "+images[i].getName());
            images[i].delete();
        }
        dir.delete();*/

        dir = new File(getClassesDirectory());
        images = dir.listFiles();
        for (int i=0; i < images.length; ++i) {
            images[i].delete();
        }
        dir.delete();

        exit(0);
    }


    //
    // Convenient methods
    //
    static String getImageName(String uri) {
        if (uri.endsWith(".svg")) {
            uri = uri.substring(0, uri.lastIndexOf(".svg"));
            uri += ".png";
        }
        else if (uri.endsWith(".xml")) {
            uri = uri.substring(0, uri.lastIndexOf(".xml"));
            uri += ".png";
        }
        return uri;
    }

    /**
     * Writes an image.
     * @param transcoder the transcoder to use to generate the image
     * @param inputURI the URI of the SVG file
     * @param outputURI the URI of the image to generate
     */
    public static void writeImage(ImageTranscoder transcoder,
                                  String inputURI,
                                  String output) {
        try {
            OutputStream ostream =
                new BufferedOutputStream(new FileOutputStream(output));
            transcoder.transcode(new TranscoderInput(inputURI),
                                 new TranscoderOutput(ostream));
            ostream.flush();
            ostream.close();
        } catch(IOException ex) {
            error("while writing "+inputURI+" to "+output+"\n"+ex.getMessage());
        } catch(Exception ex) {
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
     * This is the core method to calculate the difference of the two
     * input images and save the comparison into the output image
     * @param ref the reference image in BufferedImage
     * @param new the new image in BufferedImage
     * @param diff the difference image in BufferedImage
     */

    public static void diffBufferedImage(Raster ref, Raster cmp, Raster diff){
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

        for (int i=0; i<h; i++){
            rp = refOff + i*refScanStride;
            np = cmpOff + i*cmpScanStride;
            dp0 = diffOff + i*diffScanStride;
            dp1 = diffOff + i*diffScanStride + w;
            dp2 = diffOff + i*diffScanStride + 2*w;

            for (int j=0; j<w; j++){
                refPixel = refPixels[rp];
                cmpPixel = cmpPixels[np];
                diffPixel = cmpPixel - refPixel;
                diffPixels[dp0] = refPixel;
                diffPixels[dp1] = cmpPixel;
                if(diffPixel != 0){
                    diffPixel = 0xff000000;
                }
                diffPixels[dp2] = diffPixel;
                rp++;
                np++;
                dp0++;
                dp1++;
                dp2++;
            }
        }
    }

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
     * Returns the transcoder to use.
     */
    public static ImageTranscoder getTranscoder() {
        ImageTranscoder t = new PNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                             "org.apache.crimson.parser.XMLReaderImpl");
        return t;
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
        out.println("usage: regsvggen [-help|-init|-reset|-go]");
        out.println("-help   Display this message");
        out.println("-init   Initialize regsvggen");
        out.println("-reset  Remove the reference and new images");
        out.println("-go    Process the test cases and produce report");
    }

    /**
     * Exits the application with the specified code.
     * @param code the exit code
     */
    public static void exit(int code) {
        System.exit(code);
    }

    /**
     * Returns the directory where the XML test cases are stored.
     */
    public static String getXmlDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_XML_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the reference images are stored.
     */
    public static String getRefDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_REF_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the new images are stored.
     */
    public static String getNewDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_NEW_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where temporary classes are stored.
     */
    public static String getClassesDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_CLASSES_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the difference images are stored.
     */
    public static String getDiffDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_DIFF_DIRECTORY_NAME).getAbsolutePath();
    }

    /**
     * Returns the directory where the svg files are stored.
     */
    public static String getSvgDirectory() {
        File f = new File(REGSVGGEN_DIRECTORY_NAME);
        return new File(f, REGSVGGEN_SVG_DIRECTORY_NAME).getAbsolutePath();
    }

}

