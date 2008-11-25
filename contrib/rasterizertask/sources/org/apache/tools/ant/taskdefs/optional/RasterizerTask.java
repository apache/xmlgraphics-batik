/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.tools.ant.taskdefs.optional;

// -- Ant classes ------------------------------------------------------------
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.JAXPUtils;

// -- Batik classes ----------------------------------------------------------
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;

// -- SAX classes ------------------------------------------------------------
import org.xml.sax.XMLReader;

// -- Java SDK classes -------------------------------------------------------
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;



/**
 * This Ant task can be used to convert SVG images to raster images.
 *
 * <p>Possible result raster image formats are PNG, JPEG, TIFF, and PDF. 
 * Batik {@link SVGConverter} is used to execute the conversion. You need
 * <em>Batik</em> to produce the first three raster image types and 
 * <em>FOP</em> to produce PDF.</p>
 *
 * @see SVGConverter SVGConverter
 * @see org.apache.batik.apps.rasterizer.Main Main
 *
 * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
 * @version $Id$
 */
public class RasterizerTask extends MatchingTask {

    // -- Constants ----------------------------------------------------------
    /**
     * Default quality value for JPEGs. This value is used when 
     * the user doesn't set the quality.
     */
    private static final float DEFAULT_QUALITY = 0.99f;
    /**
     * Magic string indicating that any JAXP conforming XML parser can 
     * be used.
     */
    private static final String JAXP_PARSER = "jaxp";

    // -- Variables ----------------------------------------------------------
    /** Result image type. The default is PNG. */
    protected DestinationType resultType = DestinationType.PNG;

    /** Output image height. */
    protected float height = Float.NaN;
    /** Output image width. */
    protected float width = Float.NaN;
    /** Maximum output image height. */
    protected float maxHeight = Float.NaN;
    /** Maximum output image width. */
    protected float maxWidth = Float.NaN;
    /** Output image quality. */
    protected float quality = Float.NaN;
    /** Output Area of Interest (AOI) area. */
    protected String area = null;
    /** Output background color. */
    protected String background = null;
    /** Media type of CSS file used to produce output images. */
    protected String mediaType = null;
    /** Output pixel size - dots per inch. */
    protected float dpi = Float.NaN;
    /** Output image language. */
    protected String language = null;
    /** XML parser class currently in use. */
    protected String readerClassName = XMLResourceDescriptor.getXMLParserClassName();


    /** Source image path. */
    protected File srcFile = null;
    /** Destination image path. */
    protected File destFile = null;
    /** Source directory of images. */
    protected File srcDir = null;
    /** Destination directory for output images. */
    protected File destDir = null;
    /** Contents of <code>fileset</code> elements. */
    protected Vector filesets = new Vector();

    /** Converter object used to convert SVG images to raster images. */
    protected SVGConverter converter;



    // -- Constructors -------------------------------------------------------
    /**
     * Initializes a new rasterizer task.
     */
    public RasterizerTask() {
        converter = new SVGConverter(new RasterizerTaskSVGConverterController(this));
    }



    // -- Methods required by Ant --------------------------------------------
    /**
     * Gets <code>result</code> attribute value. 
     *
     * <p>See the documentation for valid values.</p>
     *
     * @param type Attribute value.
     */
    public void setResult(ValidImageTypes type) {
        this.resultType = getResultType(type.getValue());
    }

    /**
     * Gets <code>height</code> attribute value.
     *
     * <p>The attribute is optional.</p>
     *
     * @param height Attribute value.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Gets <code>width</code> attribute value.
     *
     * <p>The attribute is optional.</p>
     *
     * @param width Attribute value.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Gets <code>maxheight</code> attribute value.
     *
     * <p>The attribute is optional.</p>
     *
     * @param height Attribute value.
     */
    public void setMaxheight(float height) {
        this.maxHeight = height;
    }

    /**
     * Gets <code>maxwidth</code> attribute value.
     *
     * <p>The attribute is optional.</p>
     *
     * @param width Attribute value.
     */
    public void setMaxwidth(float width) {
        this.maxWidth = width;
    }

    /**
     * Gets <code>quality</code> attribute value.
     *
     * <p>The value have to be a float between 0 and 1 excluded. 
     * The attribute is optional.</p>
     *
     * @param quality Attribute value.
     */
    public void setQuality(float quality) {
        this.quality = quality;
    }

    /**
     * Gets <code>area</code> attribute value.
     *
     * <p>The value have to be four integers separated with whitespaces or
     * commas.
     * The attribute is optional.</p>
     *
     * @param area Attribute value.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Gets <code>bg</code> attribute value.
     *
     * <p>The value have to be three or four integers separated with 
     * whitespaces or commas.
     * The attribute is optional.</p>
     *
     * @param bg Attribute value.
     */
    public void setBg(String bg) {
        this.background = bg;
    }

    /**
     * Gets <code>media</code> attribute value.
     *
     * <p>The value have to a media type defined in CSS2 specifications.
     * Only visual media group is supported.</p>
     *
     * @param media Attribute value.
     */
    public void setMedia(ValidMediaTypes media) {
        this.mediaType = media.getValue();
    }

    /**
     * Gets <code>dpi</code> attribute value.
     *
     * <p>The value have to be a positive float. 
     * The attribute is optional.</p>
     *
     * @param dpi Attribute value.
     */
    public void setDpi(float dpi) {
        this.dpi = dpi;
    }

    /**
     * Gets <code>lang</code> attribute value.
     *
     * <p>See SVG specification for valid values.
     * The attribute is optional.</p>
     *
     * @param lang Attribute value.
     */
    public void setLang(String language) {
        this.language = language;
    }

    /**
     * Sets classname of an XML parser.
     * The attribute is optional.
     *
     * @param value Java classname of an XML parser.
     */
    public void setClassname(String value) {
        this.readerClassName = value;
    }

    /**
     * Gets <code>src</code> attribute value.
     *
     * <p>One of the following have to have a value: this attribute, 
     * <code>srcdir</code> attribute or <code>fileset</code> element.</p>
     *
     * @param file Attribute value.
     */
    public void setSrc(File file) {
        this.srcFile = file;
    }

    /**
     * Gets <code>dest</code> attribute value.
     *
     * <p>The attribute have to have a value when 
     * <code>src</code> attribute has a value.</p>
     *
     * @param file Attribute value.
     */
    public void setDest(File file) {
        this.destFile = file;
    }

    /**
     * Gets <code>srcdir</code> attribute value.
     *
     * <p>If <code>srcfile</code> attribute doesn't have a value then 
     * either this attribute have to have a value or the element have
     * to contain <code>fileset</code> elements.</p>
     *
     * @param dir Attribute value.
     */
    public void setSrcdir(File dir) {
        this.srcDir = dir;
    }

    /**
     * Gets <code>destdir</code> attribute value.
     *
     * <p>This attribute have to have a value if either 
     * <code>srcdir</code> attribute or <code>fileset</code> elements 
     * exists.</p>
     *
     * @param dir Attribute value.
     */
    public void setDestdir(File dir) {
        this.destDir = dir;
    }

    /**
     * Reads <code>fileset</code> elements.
     *
     * <p><code>fileset</code> elements can be used when there are many files
     * to be rasterized.</p>
     *
     * @param set <code>fileset</code> elements
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Validates and sets input values and files, then starts the conversion 
     * process.
     *
     * <p>See Ant documentation to find out more about the meaning of
     * <code>execute</code> in Ant task.</p>
     *
     * @throws BuildException Parameters are not set correctly or the conversion fails.
     */
    public void execute() throws BuildException {

        String[] sources;        // Array of input files.

        // Store default XML parser information and set user class.
        String defaultParser = XMLResourceDescriptor.getXMLParserClassName();
        // Throws BuildException.
        XMLResourceDescriptor.setXMLParserClassName(getParserClassName(readerClassName));

        try {
            // Check file and directory values.
            if(this.srcFile != null) {
                if(this.destFile == null) {
                    throw new BuildException("dest attribute is not set.");
                }
            } else {
                if((this.srcDir == null) && (filesets.size() == 0)) {
                    throw new BuildException("No input files! Either srcdir or fileset have to be set.");
                }
                if(this.destDir == null) {
                    throw new BuildException("destdir attribute is not set!");
                }
            }

            // Throws BuildException.
            setRasterizingParameters();

            // Get and set source(s).
            sources = getSourceFiles();
            converter.setSources(sources);

            // Set destination.
            if(this.srcFile != null) {
                converter.setDst(this.destFile);
            } else {
                converter.setDst(this.destDir);
            }

            // Input filenames are stored in the converter and 
            // everything is ready for the conversion.

            log("Rasterizing " + sources.length + 
                (sources.length == 1 ? " image " : " images ") + 
                "from SVG to " + this.resultType.toString() + ".");

            try {
                converter.execute();
            } catch(SVGConverterException sce) {
                throw new BuildException(sce.getMessage());
            }
        } finally {
            // Restore default XML parser for the next execute.
            XMLResourceDescriptor.setXMLParserClassName(defaultParser);
        }
    }



    // -- Internal methods ---------------------------------------------------
    /**
     * Checks and sets parameter values to the converter.
     *
     * <p>Some invalid values are just swallowed and default values are 
     * used instead. This is done when the invalid value cannot
     * be correctly differentiated from the default value and the default
     * value doesn't cause any harm. <code>BuildException</code> is thrown
     * if the invalid value is clearly recognized.</p>
     * 
     * @throws BuildException Invalid parameter value.
     */
    protected void setRasterizingParameters() 
        throws BuildException {
        if(this.resultType != null) {
            converter.setDestinationType(this.resultType);
        } else {
            throw new BuildException("Unknown value in result parameter.");
        }
        // Set size values.
        if(!Float.isNaN(this.width)) {
            if(this.width < 0) {
                throw new BuildException("Value of width parameter must positive.");
            }
            converter.setWidth(this.width);
        }
        if(!Float.isNaN(this.height)) {
            if(this.height < 0) {
                throw new BuildException("Value of height parameter must positive.");
            }
            converter.setHeight(this.height);
        }
        // Set maximum size values.
        if(!Float.isNaN(this.maxWidth)) {
            if(this.maxWidth < 0) {
                throw new BuildException("Value of maxwidth parameter must positive.");
            }
            converter.setMaxWidth(this.maxWidth);
        }
        if(!Float.isNaN(this.maxHeight)) {
            if(this.maxHeight < 0) {
                throw new BuildException("Value of maxheight parameter must positive.");
            }
            converter.setMaxHeight(this.maxHeight);
        }
        // The quality is just swallowed if the result type is not correct.
        if(allowedToSetQuality(resultType)) {
            if(!Float.isNaN(this.quality)) {
                // Throws BuildException.
                converter.setQuality(getQuality(this.quality));
            } else {
                // Set something to quiet irritating error 
                // from JPEGTranscoder.
                converter.setQuality(DEFAULT_QUALITY);
            }
        }
        if(this.area != null) {
            // Throws BuildException.
            converter.setArea(getAreaOfInterest(this.area));
        }
        if(this.background != null) {
            // Throws BuildException.
            converter.setBackgroundColor(getBackgroundColor(this.background));
        }
        if(this.mediaType != null) {
            // Ant takes care of the correct media type values.
            converter.setMediaType(this.mediaType);
        }
        if(!Float.isNaN(this.dpi)) {
            if(this.dpi < 0) {
                throw new BuildException("Value of dpi parameter must positive.");
            }
            // The calculation is the same as 2.54/dpi*10 where
            converter.setPixelUnitToMillimeter(25.4f/this.dpi);
        }
        if(this.language != null) {
            converter.setLanguage(this.language);
        }
    }

    /**
     * Gets source files from the task parameters and child elements, 
     * combines those to a one list, and returns the list.
     *
     * @return Array of source filename strings.
     */
    protected String[] getSourceFiles() {

        List inputFiles = new ArrayList(); // Input files in temp list.

        if(this.srcFile != null) {
            // Only one source and destination file have been set.
            inputFiles.add(this.srcFile.getAbsolutePath());
        } else {
            // Unknown number of files have to be converted. destdir 
            // attribute and either srcdir attribute or fileset element 
            // have been set.

            // Read source files from the child patterns.
            // The value of srcdir attribute overrides the dir attribute in
            // fileset element.
            if(this.srcDir != null) {
                // fileset is declared in the super class.
                // Scan to get all the files in srcdir directory that 
                // should be in input files.
                fileset.setDir(this.srcDir);
                DirectoryScanner ds = fileset.getDirectoryScanner(project);
                String[] includedFiles = ds.getIncludedFiles();
                // Add file and its path to the input file vector.
                for (int j = 0 ; j < includedFiles.length ; j++) {
                    File newFile = new File(srcDir.getPath(), includedFiles[j]);
                    inputFiles.add(newFile.getAbsolutePath());
                }
            }
            // Read source files from child filesets.
            for (int i = 0 ; i < filesets.size() ; i++) {
                // Scan to get all the files in this fileset that 
                // should be in input files.
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                String[] includedFiles = ds.getIncludedFiles();
                // Add file and its path to the input file vector.
                for (int j = 0 ; j < includedFiles.length ; j++) {
                    File newFile = new File(fs.getDir(project).getPath(), includedFiles[j]);
                    inputFiles.add(newFile.getAbsolutePath());
                }
            }
        }

        // Convert List to array and return the array.
        return (String[])inputFiles.toArray(new String[0]);
    }

    /**
     * Returns the correct result image type object.
     *
     * @param type Result image type as a string.
     *
     * @return Result image type as an object or <code>null</code> if the parameter doesn't have corresponding object.
     */
    protected DestinationType getResultType(String type) {
        if(type.equals(DestinationType.PNG_STR)) {
            return DestinationType.PNG;
        } else if(type.equals(DestinationType.JPEG_STR)) {
            return DestinationType.JPEG;
        } else if(type.equals(DestinationType.TIFF_STR)) {
            return DestinationType.TIFF;
        } else if(type.equals(DestinationType.PDF_STR)) {
            return DestinationType.PDF;
        }
        return null;
    }

    /**
     * Checks if the quality value can be set. Only result image type
     * is checked.
     *
     * @param type Result image type.
     *
     * @return <code>true</code> if the quality can be set and <code>false</code> otherwise.
     */
    protected boolean allowedToSetQuality(DestinationType type) {
        if(!type.toString().equals(DestinationType.JPEG_STR)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a valid quality value.
     *
     * @param quality Input quality value to be tested.
     *
     * @return Valid quality value.
     *
     * @throws BuildException Input quality value is not valid.
     */
    protected float getQuality(float quality) 
        throws BuildException {
        if((quality <= 0) || (quality >= 1)) {
            throw new BuildException("quality parameter value have to be between 0 and 1.");
        }
        return quality;
    }

    /**
     * Returns a valid Area of Interest (AOI) as a Rectangle2D object.
     *
     * @param area AOI input area.
     *
     * @return A valid AOI rectangle.
     *
     * @throws BuildException AOI area is invalid.
     */
    protected Rectangle2D getAreaOfInterest(String area) 
        throws BuildException {

        float x;            // Upper left x point value of the area.
        float y;            // Upper left y point value of the area.
        float width;        // Area width value.
        float height;       // Area height value.
        String token;       // A token from the input string.
        StringTokenizer tokenizer = new StringTokenizer(area, ", \t\n\r\f");
                            // Input string tokenizer.

        if(tokenizer.countTokens() != 4) {
            throw new BuildException("There must be four numbers in the area parameter: x, y, width, and height.");
        }
        try {
            x = Float.parseFloat(tokenizer.nextToken());
            y = Float.parseFloat(tokenizer.nextToken());
            width = Float.parseFloat(tokenizer.nextToken());
            height = Float.parseFloat(tokenizer.nextToken());
        } catch(NumberFormatException nfe) {
            throw new BuildException("Invalid area parameter value: " + nfe.toString());
        }

        // Negative values are not allowed.
        if((x < 0) || (y < 0) || (width < 0) || (height < 0)) {
            throw new BuildException("Negative values are not allowed in area parameter.");
        }

        return new Rectangle2D.Float(x, y, width, height);
    }

    /**
     * Returns a valid background color object.
     *
     * @param argb String containing color channel values.
     *
     * @return A valid background color.
     *
     * @throws BuildException Input value is invalid.
     */
    protected Color getBackgroundColor(String argb) 
        throws BuildException {

        int a;              // Value of the alpha channel.
        int r;              // Value of the red channel.
        int g;              // Value of the green channel.
        int b;              // Value of the blue channel.
        String token;       // A token from the input string.
        StringTokenizer tokenizer = new StringTokenizer(argb, ", \t\n\r\f");
                            // Input string tokenizer.

        try {
            if(tokenizer.countTokens() == 3) {
                // Default alpha channel is opaque.
                a = 255;
            } else if(tokenizer.countTokens() == 4) {
                a = Integer.parseInt(tokenizer.nextToken());
            } else {
                throw new BuildException("There must be either three or four numbers in bg parameter: (alpha,) red, green, and blue.");
            }
            r = Integer.parseInt(tokenizer.nextToken());
            g = Integer.parseInt(tokenizer.nextToken());
            b = Integer.parseInt(tokenizer.nextToken());
        } catch(NumberFormatException nfe) {
            throw new BuildException("Invalid bg parameter value: " + nfe.toString());
        }

        // Check that the values are valid.
        if((a < 0) ||(a > 255) || (r < 0) ||(r > 255) || 
           (g < 0) ||(g > 255) || (b < 0) ||(b > 255)) {
            throw new BuildException("bg parameter value is invalid. Numbers have to be between 0 and 255.");
        }

        return new Color(r, g, b, a);
    }

    /**
     * Returns name of an XML parser.
     * Magic string {@link #JAXP_PARSER} is also accepted.
     *
     * @param className Name of the XML parser class or a magic string.
     *
     * @return Name of an XML parser.
     *
     * @throws BuildException Unable to get the name of JAXP parser.
     */
    private String getParserClassName(final String className) {
        String name = className;
        if ((className == null) || className.equals(JAXP_PARSER)) {
            // Set first JAXP parser.
            // Throws BuildException.
            XMLReader reader = JAXPUtils.getXMLReader();
            name = reader.getClass().getName();
        }

        log("Using class '" + name + "' to parse SVG documents.", Project.MSG_VERBOSE);
        return name;
    }



    // -----------------------------------------------------------------------
    //   Inner classes
    // -----------------------------------------------------------------------

    /**
     * Defines the valid attribute values for <code>result</code> parameter.
     *
     * <p>See the Ant documentation for more information.</p>
     *
     * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
     * @version $Id$
     */
    public static class ValidImageTypes extends EnumeratedAttribute {

        /**
         * Defines valid image types.
         *
         * @return Array of valid values as strings.
         */
        public String[] getValues() {
            return new String[] 
                {DestinationType.PNG_STR, 
                DestinationType.JPEG_STR, 
                DestinationType.TIFF_STR, 
                DestinationType.PDF_STR};
        }
    }

    /**
     * Defines the valid attribute values for a media parameter.
     *
     * <p>See the Ant documentation for more information.</p>
     *
     * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
     * @version $Id$
     */
    public static class ValidMediaTypes extends EnumeratedAttribute {

        /**
         * Defines valid media types.
         *
         * <p>The types are defined in CSS2 specification.
         * Only visual media group is supported.</p>
         *
         * @return Array of valid values as strings.
         */
        public String[] getValues() {
            return new String[] {"all", "handheld", "print", 
                "projection", "screen", "tty", "tv"};
        }
    }

}
