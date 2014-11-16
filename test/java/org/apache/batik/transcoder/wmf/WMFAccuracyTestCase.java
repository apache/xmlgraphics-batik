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
package org.apache.batik.transcoder.wmf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.tosvg.WMFTranscoder;
import org.apache.batik.util.SVGConstants;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test validates that a given WMF file is properly converted to
 * an SVG document by comparing the generated SVG document to a known,
 * valid SVG reference.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
@Ignore
public class WMFAccuracyTestCase extends AbstractTest
    implements SVGConstants {

    public static final String GENERATOR_REFERENCE_BASE
        = "test-references/org/apache/batik/transcoder/wmf";
    public static final String CANDIDATE_REF_DIR = "candidate";

    public static final String WMF_EXTENSION  = ".wmf";
    public static final String SVG_EXTENSION  = ".svg";
    public static final char   PATH_SEPARATOR = '/';

    /**
     * Error when an error occurs while generating SVG
     * with the <code>SVGGraphics2D</code>
     * {0} = painter class name if painter not null. Null otherwise
     * {1} = exception class name
     * {2} = exception message
     * {3} = exception stack trace.
     */
    public static final String ERROR_CANNOT_GENERATE_SVG
        = "WMFAccuracyTest.error.cannot.generate.svg";

    /**
     * Error when the reference SVG file cannot be opened
     * {0} = URI of the reference image
     * {1} = IOException message
     */
    public static final String ERROR_CANNOT_OPEN_REFERENCE_SVG_FILE
        = "WMFAccuracyTest.error.cannot.open.reference.svg.file";

    /**
     * Error when there is an IOException while comparing the
     * reference SVG file with the newly generated SVG content
     * {0} = URI of the reference image
     * {1} = IOException message.
     */
    public static final String ERROR_ERROR_WHILE_COMPARING_FILES
        = "WMFAccuracyTest.error.while.comparing.files";

    /**
     * Error when the generated SVG is inaccurate
     */
    public static final String ERROR_GENERATED_SVG_INACCURATE
        = "WMFAccuracyTest.error.generated.svg.inaccurate";

    public static final String ENTRY_KEY_ERROR_DESCRIPTION
        = "WMFAccuracyTest.entry.key.error.description";

    public static final String ENTRY_KEY_LINE_NUMBER
        = "WMFAccuracyTest.entry.key.line.number";

    public static final String ENTRY_KEY_COLUMN_NUMBER
        = "WMFAccuracyTest.entry.key.column.number";

    public static final String ENTRY_KEY_COLUMN_EXPECTED_VALUE
        = "WMFAccuracyTest.entry.key.column.expected.value";

    public static final String ENTRY_KEY_COLUMN_FOUND_VALUE
        = "WMFAccuracyTest.entry.key.column.found.value";

    public static final String ENTRY_KEY_REFERENCE_LINE
        = "WMFAccuracyTest.entry.key.reference.line";

    public static final String ENTRY_KEY_NEW_LINE
        = "WMFAccuracyTest.entry.key.new.line";

    public static final String REF_IMAGE_PREFIX
        = "test-references/";

    /**
     * Painter which performs an arbitrary rendering
     * sequence.
     */
    private URL wmfURL;

    /**
     * Reference SVG URL
     */
    private URL refURL;

    /**
     * File where the generated SVG might be saved
     */
    private File saveSVG;

    protected String[] breakWMFFile(String wmfFile){
        if(wmfFile == null) {
            throw new IllegalArgumentException("Null WMF file given");
        }

        String [] ret = new String[3];

        if (wmfFile.endsWith(WMF_EXTENSION)) {
            ret[2] = WMF_EXTENSION;
        } else {
            throw new IllegalArgumentException
                ("WMF File must end in '.wmf': " + wmfFile);
        }

        wmfFile = wmfFile.substring(0, wmfFile.length()-ret[2].length());

        int fileNameStart = wmfFile.lastIndexOf(PATH_SEPARATOR);
        String wmfDir = "";
        if(fileNameStart != -1){
            if(wmfFile.length() < fileNameStart + 2){
                // Nothing after PATH_SEPARATOR
                throw new IllegalArgumentException
                    ("Nothing after '"+PATH_SEPARATOR+"': " + wmfFile);
            }
            wmfDir  = wmfFile.substring(0, fileNameStart + 1);
            wmfFile = wmfFile.substring(fileNameStart + 1);
        }
        ret[0] = wmfDir;
        ret[1] = wmfFile;
        return ret;
    }

    public WMFAccuracyTestCase(){
    }

    public void setId(String id){
        super.setId(id);
        setFile(id);
    }

    public void setFile(String id) {
        String wmfFile = id;

        String[] dirNfile = breakWMFFile(wmfFile);

        wmfURL = resolveURL(dirNfile[0]+dirNfile[1]+dirNfile[2]);
        refURL = resolveURL
            (GENERATOR_REFERENCE_BASE+"/"+dirNfile[1] +SVG_EXTENSION);
        saveSVG = new File(GENERATOR_REFERENCE_BASE+"/"+CANDIDATE_REF_DIR+"/"+
                           dirNfile[1]+SVG_EXTENSION);
        // System.err.println("WMFURL: " + wmfURL);
        // System.err.println("REFURL: " + refURL);
        // System.err.println("saveSVG: " + saveSVG);
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

    public File getSaveSVG(){
        return saveSVG;
    }

    public void setSaveSVG(File saveSVG){
        this.saveSVG = saveSVG;
    }

    /**
     * This method will only throw exceptions if some aspect
     * of the test's internal operation fails.
     */
    public TestReport runImpl() throws Exception {
        DefaultTestReport report = new DefaultTestReport(this);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            WMFTranscoder wmft = new WMFTranscoder();
            TranscoderInput input;
            input = new TranscoderInput(wmfURL.toString());

            OutputStreamWriter    outW;
            outW = new OutputStreamWriter(out, "UTF-8");
            TranscoderOutput      output = new TranscoderOutput(outW);

            wmft.transcode(input, output);
        } catch(Exception e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            report.setErrorCode(ERROR_CANNOT_GENERATE_SVG);
            String desc, msg;
            desc = Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null);
            msg  = Messages.formatMessage(ERROR_CANNOT_GENERATE_SVG,
                                          new String[]{ wmfURL.toString(),
                                                        e.getClass().getName(),
                                                        e.getMessage(),
                                                        trace.toString() });
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(desc, msg) });
            report.setPassed(false);
            return report;
        }

        BufferedReader genReader, refReader;
        try {
            InputStream is = refURL.openStream();
            Reader r = new InputStreamReader(is);
            refReader = new BufferedReader(r);
        } catch (Exception e) {
            report.setErrorCode(ERROR_CANNOT_OPEN_REFERENCE_SVG_FILE);
            String desc, msg, refURLStr;
            refURLStr = (refURL != null) ? refURL.toExternalForm() : "null";
            desc = Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null);
            msg  = Messages.formatMessage(ERROR_CANNOT_OPEN_REFERENCE_SVG_FILE,
                                          new Object[]{refURLStr,
                                                       e.getMessage()});
            report.setDescription( new TestReport.Entry[]{
                new TestReport.Entry(desc, msg) });
            report.setPassed(false);
            save(out.toByteArray());
            return report;
        }

        byte[] data = out.toByteArray();
        ByteArrayInputStream gen = new ByteArrayInputStream(data);
        Reader r = new InputStreamReader(gen);
        genReader = new BufferedReader(r);

        try {
            String refStr = refReader.readLine();
            String genStr = genReader.readLine();
            int line=1;
            while ((refStr != null) && (genStr != null)) {

                if (!refStr.equals(genStr))
                    break;

                refStr = refReader.readLine();
                genStr = genReader.readLine();
                line++;
            }
            if ((refStr == null) && (genStr == null)) {
                report.setPassed(true);
                return report;
            }

            save(out.toByteArray());
            int cn = computeColumnNumber(refStr, genStr);
            String expectedChar = "<eol>";
            if ((cn >= 0) && (refStr != null) && (refStr.length() > cn))
                expectedChar = refStr.substring(cn, cn+1);

            String foundChar = "<null>";
            if((cn >=0) && (genStr != null) && (genStr.length() > cn))
                foundChar = genStr.substring(cn, cn+1);

            if (expectedChar.equals(" ")) expectedChar = "' '";
            if (foundChar.equals(" "))    foundChar    = "' '";

            report.setErrorCode(ERROR_GENERATED_SVG_INACCURATE);
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_LINE_NUMBER),
                                       new Integer(line));
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_COLUMN_NUMBER),
                                       new Integer(cn));
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_COLUMN_EXPECTED_VALUE),
                                       expectedChar);
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_COLUMN_FOUND_VALUE),
                                       foundChar);
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_REFERENCE_LINE),
                                       refStr);
            report.addDescriptionEntry(fmtMsg(ENTRY_KEY_NEW_LINE), genStr);
            report.setPassed(false);
        } catch (Exception e) {
            String desc, msg;
            desc = Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null);
            msg  = Messages.formatMessage(ERROR_ERROR_WHILE_COMPARING_FILES,
                                          new Object[]{refURL.toExternalForm(),
                                                       e.getMessage()});
            report.setErrorCode(ERROR_ERROR_WHILE_COMPARING_FILES);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry(desc, msg)});
            report.setPassed(false);
            save(out.toByteArray());
            return report;
        }
        return report;
    }



    /**
     * Saves the byte array in the "saveSVG" file
     * if that file's parent directory exists.
     */
    protected void save(byte[] data) throws IOException{
        if(saveSVG == null){
            return;
        }

        FileOutputStream os = new FileOutputStream(saveSVG);
        os.write(data);
        os.close();
    }

    public int computeColumnNumber(String aStr, String bStr){
        if(aStr == null || bStr == null){
            return -1;
        }

        int n = aStr.length();
        int i = -1;
        for(i=0; i<n; i++){
            char a = aStr.charAt(i);
            if(i < bStr.length()){
                char b = bStr.charAt(i);
                if(a != b){
                    break;
                }
            }
            else {
                break;
            }
        }

        return i;
    }

    protected String fmtMsg(String str) {
        return Messages.formatMessage(str, null);
    }

}
