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

package org.apache.batik.test.svg;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.batik.test.DefaultTestReport;
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
public class SVGRenderingAccuracyTest extends AbstractRenderingAccuracyTest {
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
     * Validating parser class name
     */
    public static final String VALIDATING_PARSER
	= configuration.getString("validating.parser");

    /**
     * Controls whether or not the SVG file should be
     * validated. By default, no validation is used.
     */
    protected boolean validate = false;

    /**
     * The userLanguage for which the document should be tested.
     */
    protected String userLanguage;

    /**
     * Constructor.
     * @param svgURL the URL String for the SVG document being tested.
     * @param refImgURL the URL for the reference image.
     */
    public SVGRenderingAccuracyTest(String svgURL,
                                    String refImgURL){
        super(svgURL, refImgURL);
    }

    /**
     * For subclasses
     */
    protected SVGRenderingAccuracyTest(){
    }

    /**
     * If true, this test will use validation
     */
    public void setValidating(Boolean validate){
	if (validate == null){
	    throw new IllegalArgumentException();
	}
        this.validate = validate.booleanValue();
    }

    public boolean getValidating(){
        return validate;
    }

    /**
     * Sets the userLanguage
     */
    public void setUserLanguage(String userLanguage){
        this.userLanguage = userLanguage;
    }

    public String getUserLanguage(){
        return this.userLanguage;
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

    
    public TestReport encode(URL srcURL, FileOutputStream fos) {
        DefaultTestReport report = new DefaultTestReport(this);
        try{
            ImageTranscoder transcoder = getTestImageTranscoder();
            TranscoderInput src = new TranscoderInput(svgURL.toString());
            TranscoderOutput dst = new TranscoderOutput(fos);
            transcoder.transcode(src, dst);
            return null;
        }catch(TranscoderException e){
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
                
            report.setErrorCode(ERROR_CANNOT_TRANSCODE_SVG);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry
                (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                 Messages.formatMessage(ERROR_CANNOT_TRANSCODE_SVG,
                                        new String[]{svgURL.toString(), 
                                                     e.getClass().getName(),
                                                     e.getMessage(),
                                                     trace.toString()
                                        })) });
        }catch(Exception e){
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));

            report.setErrorCode(ERROR_CANNOT_TRANSCODE_SVG);
            report.setDescription(new TestReport.Entry[]{
                new TestReport.Entry
                (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                 Messages.formatMessage(ERROR_CANNOT_TRANSCODE_SVG,
                                        new String[]{svgURL.toString(), 
                                                     e.getClass().getName(),
                                                     e.getMessage(),
                                                     trace.toString()
                                        })) });
        }
        report.setPassed(false);
        return report;
    }

    /**
     * Returns the <tt>ImageTranscoder</tt> the Test should
     * use
     */
    public ImageTranscoder getTestImageTranscoder(){
        ImageTranscoder t = new InternalPNGTranscoder();
        t.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE,
                             Boolean.FALSE);
        t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR,
                             new Color(0,0,0,0));
        t.addTranscodingHint(PNGTranscoder.KEY_EXECUTE_ONLOAD,
                             Boolean.TRUE);

        if (validate){
            t.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_VALIDATING,
                                 Boolean.TRUE);
            t.addTranscodingHint(PNGTranscoder.KEY_XML_PARSER_CLASSNAME,
                                 VALIDATING_PARSER);
        }

        if (userLanguage != null){
            t.addTranscodingHint(PNGTranscoder.KEY_LANGUAGE, 
                                 userLanguage);
        }
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
